package com.blazeb.camel.enrolment.route;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

@Component
public class EnrolmentPipelineRoute extends RouteBuilder {

    @Override
    public void configure() {
        errorHandler(deadLetterChannel("seda:enrolment-dlq")
                .maximumRedeliveries(2)
                .redeliveryDelay(250)
                .useOriginalMessage()
                .logExhausted(true));

        from("timer:enrolmentPoll?period={{enrolment.poll.period}}")
                .routeId("enrolment-poll")
                .to("sql:SELECT id, student_id, course_code, enrolled_date, status, idempotency_key "
                        + "FROM enrolment WHERE status = 'PENDING' ORDER BY id "
                        + "LIMIT 50?dataSource=#dataSource")
                .split(body())
                .to("direct:process-enrolment");

        from("direct:process-enrolment")
                .routeId("enrolment-process")
                .process(this::mapRowToHeaders)
                .choice()
                    .when(simple("${header.studentId} == null || ${header.studentId} == ''"))
                        .throwException(new IllegalArgumentException("studentId missing"))
                .end()
                .choice()
                    .when(header("courseCode").startsWith("CRS-CAM-"))
                        .setHeader("enrolmentLane", constant("CAMEL_TRACK"))
                    .when(header("courseCode").startsWith("CRS-KFK-"))
                        .setHeader("enrolmentLane", constant("KAFKA_TRACK"))
                    .otherwise()
                        .setHeader("enrolmentLane", constant("GENERAL_TRACK"))
                .end()
                .process(this::buildEventBody)
                .to("direct:mark-processing");

        from("direct:mark-processing")
                .routeId("enrolment-mark-processing")
                .to("sql:UPDATE enrolment SET status = 'PROCESSING' WHERE id = :#id AND status = 'PENDING'"
                        + "?dataSource=#dataSource")
                .filter(simple("${body} > 0"))
                .setBody(exchangeProperty("enrolmentEvent"))
                .marshal().json(JsonLibrary.Jackson)
                .to("kafka:{{enrolment.kafka.topic}}?brokers={{enrolment.kafka.brokers}}")
                .to("sql:UPDATE enrolment SET status = 'PUBLISHED' WHERE id = :#id?dataSource=#dataSource")
                .log("Published enrolment id=${header.id} lane=${header.enrolmentLane}");

        from("kafka:{{enrolment.kafka.topic}}?brokers={{enrolment.kafka.brokers}}"
                + "&groupId={{enrolment.kafka.audit-group}}"
                + "&autoOffsetReset=earliest")
                .routeId("enrolment-audit-consumer")
                .unmarshal().json(JsonLibrary.Jackson, Map.class)
                .process(exchange -> {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> body = exchange.getIn().getBody(Map.class);
                    exchange.getIn().setHeader("id", body.get("id"));
                    exchange.getIn().setHeader("studentId", body.get("studentId"));
                    exchange.getIn().setHeader("courseCode", body.get("courseCode"));
                    exchange.getIn().setHeader("enrolmentLane", body.get("enrolmentLane"));
                    exchange.getIn().setHeader("enrolledDate", body.get("enrolledDate"));
                    exchange.getIn().setHeader("reportLine",
                            body.get("studentId") + " enrolled in " + body.get("courseCode"));
                })
                .to("sql:INSERT INTO enrolment_audit (enrolment_id, student_id, course_code, enrolled_date, lane, report_line) "
                        + "VALUES (:#id, :#studentId, :#courseCode, CAST(:#enrolledDate AS DATE), :#enrolmentLane, :#reportLine)"
                        + "?dataSource=#dataSource")
                .log("Audit written for enrolment ${header.id}");

        from("seda:enrolment-dlq")
                .routeId("enrolment-dlq")
                .log("DLQ enrolment headers=${headers} body=${body}")
                .filter(header("id").isNotNull())
                .to("sql:UPDATE enrolment SET status = 'FAILED' WHERE id = :#id?dataSource=#dataSource");
    }

    private void mapRowToHeaders(Exchange exchange) {
        @SuppressWarnings("unchecked")
        Map<String, Object> row = exchange.getIn().getBody(Map.class);
        exchange.getIn().setHeader("id", row.get("id"));
        exchange.getIn().setHeader("studentId", row.get("student_id"));
        Object course = row.get("course_code");
        exchange.getIn().setHeader("courseCode",
                course == null ? null : String.valueOf(course).toUpperCase(Locale.ROOT));
        Object enrolled = row.get("enrolled_date");
        exchange.getIn().setHeader("enrolledDate", enrolled == null ? null : enrolled.toString());
        exchange.getIn().setHeader("idempotencyKey", row.get("idempotency_key"));
    }

    private void buildEventBody(Exchange exchange) {
        Map<String, Object> event = new LinkedHashMap<>();
        event.put("id", exchange.getIn().getHeader("id"));
        event.put("studentId", exchange.getIn().getHeader("studentId"));
        event.put("courseCode", exchange.getIn().getHeader("courseCode"));
        event.put("enrolledDate", exchange.getIn().getHeader("enrolledDate"));
        event.put("enrolmentLane", exchange.getIn().getHeader("enrolmentLane"));
        event.put("idempotencyKey", exchange.getIn().getHeader("idempotencyKey"));
        exchange.setProperty("enrolmentEvent", event);
        exchange.getIn().setBody(event);
    }
}
