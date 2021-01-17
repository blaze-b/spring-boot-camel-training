package com.mclebtec.demo.integration.camel.activemq.route;

import org.apache.camel.builder.RouteBuilder;

//@Component
public class SimpleRouteBuilder extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        // EIP Splitter
        // Split the content of the file into lines and the process it
        // sender file to queue
        from("file:data/input").split().tokenize("\n").to("activemq:queue:queue-test");
        // sender queue to file
        from("activemq:queue:queue-test").to("file:data/output");

        // --------------------------------------------------------------------------//
        // EIP- Content Based Router
        from("file:data/input").split().tokenize("\n").to("direct:test");
        // Content Based routing- Route the message based on the token it contains.
        // sender file to queue
        from("direct:test").choice().when(body().contains("Hello")).to("activemq:queue:queue-test1")
                .when(body().contains("favourite1")).to("activemq:queue:queue-test2").when(body().contains("hotel"))
                .to("activemq:queue:queue-test3").otherwise().to("activemq:queue:queue-test-otherwise");

        // sender queue to file
        from("activemq:queue:queue-test1").to("file:data/output");
        from("activemq:queue:queue-test2").to("file:data/output");
        from("activemq:queue:queue-test3").to("file:C:data/output");
        from("activemq:queue:queue-test-otherwise").to("file:data/output");

    }

}
