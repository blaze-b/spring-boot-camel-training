package com.mclebtec.demo.integration.camel.activemq.route;

import org.apache.camel.builder.RouteBuilder;

//@Component
public class SimpleRouteBuilderMessageFilter extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        // TODO Auto-generated method stub
        // Message Filter is a type of Content Based routing.
        // If condition satisfied perform a task else discard it.
        from("file:data/input").split().tokenize("\n").to("direct:test");

        // Message Filter is a type of Content Based routing.
        // If condition satisfied perform a task else discard it.
        from("direct:test").filter(body().contains("favourite1")).to("activemq:queue:queue-messagefilter");

        from("activemq:queue:queue-messagefilter").to("file:data/output");
    }

}
