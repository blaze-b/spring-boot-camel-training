package com.blazeb.camel.inaction.route;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "demo.route", havingValue = "wiretap")
public class WireTapRoute extends RouteBuilder {

    @Override
    public void configure() {
        from("direct:orders")
                .routeId("wiretap-main")
                .wireTap("seda:audit")
                .log("Processing order body=${body}")
                .to("seda:fulfilled");

        from("seda:audit")
                .routeId("wiretap-audit")
                .log("Audit copy: ${body}");

        from("seda:fulfilled")
                .routeId("wiretap-fulfilled")
                .log("Fulfilled: ${body}");
    }
}
