package com.blazeb.camel.inaction.route;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "demo.route", havingValue = "recipient-list")
public class RecipientListRoute extends RouteBuilder {

    @Override
    public void configure() {
        from("direct:dispatch")
                .routeId("recipient-list")
                .setHeader("recipients", constant("seda:warehouse,seda:billing"))
                .recipientList(header("recipients"))
                .log("Dispatched ${body}");

        from("seda:warehouse")
                .routeId("recipient-warehouse")
                .log("Warehouse got: ${body}");

        from("seda:billing")
                .routeId("recipient-billing")
                .log("Billing got: ${body}");
    }
}
