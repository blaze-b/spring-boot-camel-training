package com.blazeb.camel.inaction.route;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "demo.route", havingValue = "file-copy")
public class FileCopyRoute extends RouteBuilder {

    @Override
    public void configure() {
        from("file:data/inbox?noop=true")
                .routeId("file-copy")
                .log("Copying ${header.CamelFileName}")
                .to("file:data/outbox");
    }
}
