package com.mclebtec.demo.integration.camel.cbr.route;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;

//@Component
public class OrderRouterOtherwise extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        // TODO Auto-generated method stub
        System.out.println("Entering-----OrderRouterOtherwise");
        // load file orders from data/inbox into the JMS queue
        from("file:data/inbox-cbr-otherwise?noop=true").to("direct:tests");

        // content-based router-otherwise
        from("direct:tests").choice().when(header("CamelFileName").endsWith(".xml")).to("activemq:queue:xmlOrders1")
                .when(header("CamelFileName").regex("^.*(csv|csl)$")).to("activemq:queue:csvOrders1").otherwise()
                .to("activemq:queue:badOrders1");

        // test that our route is working
        from("activemq:queue:xmlOrders1").process(new Processor() {
            public void process(Exchange exchange) throws Exception {
                System.out.println("Received XML order1: " + exchange.getIn().getHeader("CamelFileName"));
            }
        });

        from("activemq:queue:csvOrders1").process(new Processor() {
            public void process(Exchange exchange) throws Exception {
                System.out.println("Received CSV order1: " + exchange.getIn().getHeader("CamelFileName"));
            }
        });

        from("activemq:queue:badOrders1").process(new Processor() {
            public void process(Exchange exchange) throws Exception {
                System.out.println("Received bad order1: " + exchange.getIn().getHeader("CamelFileName"));
            }
        });
        Thread.sleep(10000);
        System.out.println("Exiting------OrderRouterOtherwise");
    }

}
