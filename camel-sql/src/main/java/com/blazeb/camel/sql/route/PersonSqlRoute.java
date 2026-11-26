package com.blazeb.camel.sql.route;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PersonSqlRoute extends RouteBuilder {

    @Override
    public void configure() {
        from("timer:personPoll?period={{person.poll.period}}")
                .routeId("person-sql-poll")
                .to("sql:SELECT ID, NAME FROM PERSON ORDER BY ID?dataSource=#dataSource")
                .split(body())
                .process(exchange -> {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> row = exchange.getIn().getBody(Map.class);
                    exchange.getIn().setHeader("personId", row.get("ID"));
                    exchange.getIn().setHeader("personName", row.get("NAME"));
                })
                .choice()
                    .when(header("personName").startsWith("A"))
                        .log("VIP lane: id=${header.personId} name=${header.personName}")
                        .to("seda:vipPersons")
                    .otherwise()
                        .log("Standard lane: id=${header.personId} name=${header.personName}")
                        .to("seda:standardPersons")
                .end();

        from("seda:vipPersons")
                .routeId("person-vip")
                .log("VIP processed: ${header.personName}");

        from("seda:standardPersons")
                .routeId("person-standard")
                .log("Standard processed: ${header.personName}");
    }
}
