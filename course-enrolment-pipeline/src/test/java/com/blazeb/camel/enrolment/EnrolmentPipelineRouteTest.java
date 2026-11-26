package com.blazeb.camel.enrolment;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWith;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.UseAdviceWith;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import java.util.Map;

@CamelSpringBootTest
@SpringBootTest
@UseAdviceWith
@DirtiesContext
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:enrolment;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.flyway.enabled=true",
        "enrolment.poll.period=60000",
        "enrolment.kafka.brokers=localhost:9092",
        "camel.springboot.main-run-controller=false"
})
class EnrolmentPipelineRouteTest {

    @Autowired
    private CamelContext camelContext;

    @Autowired
    private ProducerTemplate producerTemplate;

    @Test
    void contentBasedRoutingPublishesCamelTrack() throws Exception {
        AdviceWith.adviceWith(camelContext, "enrolment-poll", a -> a.replaceFromWith("direct:poll-skip"));
        AdviceWith.adviceWith(camelContext, "enrolment-audit-consumer", a -> a.replaceFromWith("direct:audit-skip"));
        AdviceWith.adviceWith(camelContext, "enrolment-mark-processing", a -> {
            a.interceptSendToEndpoint("sql:*").skipSendToOriginalEndpoint()
                    .setBody(a.constant(1));
            a.interceptSendToEndpoint("kafka:*").skipSendToOriginalEndpoint().to("mock:kafka");
        });

        camelContext.start();

        MockEndpoint kafka = camelContext.getEndpoint("mock:kafka", MockEndpoint.class);
        kafka.expectedMessageCount(1);
        kafka.message(0).body(String.class).contains("CRS-CAM-101");

        producerTemplate.sendBody("direct:process-enrolment", Map.of(
                "id", 1L,
                "student_id", "STU-1001",
                "course_code", "CRS-CAM-101",
                "enrolled_date", "2026-01-10",
                "status", "PENDING",
                "idempotency_key", "STU-1001|CRS-CAM-101|2026-01-10"
        ));

        MockEndpoint.assertIsSatisfied(camelContext);
    }
}
