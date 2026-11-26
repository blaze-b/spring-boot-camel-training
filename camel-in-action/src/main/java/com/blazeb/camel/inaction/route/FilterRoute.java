package com.blazeb.camel.inaction.route;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "demo.route", havingValue = "filter")
public class FilterRoute extends RouteBuilder {

    @Override
    public void configure() {
        from("file:data/inbox-filter?noop=true")
                .routeId("filter-ingest")
                .choice()
                    .when(header("CamelFileName").endsWith(".xml"))
                        .to("seda:xmlOrdersFilter")
                    .otherwise()
                        .to("seda:badOrdersFilter")
                .end();

        from("seda:xmlOrdersFilter")
                .routeId("filter-xpath")
                .filter(xpath("/order[not(@test)]"))
                .log("Accepted XML order: ${header.CamelFileName}");
    }
}
