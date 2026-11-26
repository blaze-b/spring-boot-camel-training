package com.blazeb.camel.inaction.route;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "demo.route", havingValue = "multicast")
public class MulticastRoute extends RouteBuilder {

    @Override
    public void configure() {
        from("file:data/inbox-multicast?noop=true")
                .routeId("multicast-ingest")
                .filter(header("CamelFileName").endsWith(".xml"))
                .to("seda:xmlOrdersMulticast");

        from("seda:xmlOrdersMulticast")
                .routeId("multicast-fanout")
                .multicast().parallelProcessing()
                    .to("seda:accounting", "seda:production")
                .end();

        from("seda:accounting")
                .routeId("multicast-accounting")
                .log("Accounting received: ${header.CamelFileName}");

        from("seda:production")
                .routeId("multicast-production")
                .log("Production received: ${header.CamelFileName}");
    }
}
