package com.blazeb.camel.inaction.route;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "demo.route", havingValue = "cbr", matchIfMissing = true)
public class ContentBasedRouterRoute extends RouteBuilder {

    @Override
    public void configure() {
        from("file:data/inbox-cbr?noop=true")
                .routeId("cbr-ingest")
                .to("seda:incomingOrders");

        from("seda:incomingOrders")
                .routeId("cbr-router")
                .choice()
                    .when(header("CamelFileName").endsWith(".xml"))
                        .to("seda:xmlOrders")
                    .when(header("CamelFileName").endsWith(".csv"))
                        .to("seda:csvOrders")
                    .otherwise()
                        .to("seda:badOrders")
                .end();

        from("seda:xmlOrders")
                .routeId("cbr-xml")
                .log("XML order: ${header.CamelFileName}");

        from("seda:csvOrders")
                .routeId("cbr-csv")
                .log("CSV order: ${header.CamelFileName}");

        from("seda:badOrders")
                .routeId("cbr-bad")
                .log("Rejected order: ${header.CamelFileName}");
    }
}
