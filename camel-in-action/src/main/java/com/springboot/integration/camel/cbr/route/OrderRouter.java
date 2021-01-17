package com.springboot.integration.camel.cbr.route;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;

//@Component
public class OrderRouter extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        // TODO Auto-generated method stub
        // load file orders from src/data into the JMS queue
        System.out.println("Entering-----OrderRouter");
        // from("file:data/inbox-cbr?noop=true").to("activemq:queue:incomingOrders");//use
        // a queue
        from("file:data/inbox-cbr?noop=true").to("direct:test");// or use the test queue
        // content-based router
        from("direct:test")
                .choice()
                .when(header("CamelFileName").endsWith(".xml")).to("activemq:queue:xmlOrders")
                .when(header("CamelFileName").endsWith(".csv")).to("activemq:queue:csvOrders");

        // test that our route is working
        from("activemq:queue:xmlOrders").process(new Processor() {
            public void process(Exchange exchange) throws Exception {
                System.out.println("Received XML order: " + exchange.getIn().getHeader("CamelFileName"));
            }
        });

        from("activemq:queue:csvOrders").process(new Processor() {
            public void process(Exchange exchange) throws Exception {
                System.out.println("Received CSV order: " + exchange.getIn().getHeader("CamelFileName"));
            }
        });
        Thread.sleep(10000);
        System.out.println("Exiting------OrderRouter");
    }

}
