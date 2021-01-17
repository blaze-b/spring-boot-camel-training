package com.mclebtec.demo.integration.camel.cbr.route;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;

//@Component
public class OrderRouterWithStop extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        // TODO Auto-generated method stub
        // load file orders from src/data into the JMS queue
        System.out.println("Entering-----OrderRouterWithStop");
        from("file:data/inbox-cbr-stop?noop=true").to("direct:testss");

        // content based routing
        from("direct:testss").choice().when(header("CamelFileName").endsWith(".xml")).to("activemq:queue:xmlOrders2")
                .when(header("CamelFileName").regex("^.*(csv|csl)$")).to("activemq:queue:csvOrders2").otherwise()
                .to("activemq:queue:badOrders2").stop().end().to("activemq:queue:continuedProcessing");

        from("activemq:queue:xmlOrders2").process(new Processor() {
            public void process(Exchange exchange) throws Exception {
                System.out.println("Received XML order2: " + exchange.getIn().getHeader("CamelFileName"));
            }
        });
        from("activemq:queue:csvOrders2").process(new Processor() {
            public void process(Exchange exchange) throws Exception {
                System.out.println("Received CSV order2: " + exchange.getIn().getHeader("CamelFileName"));
            }
        });
        from("activemq:queue:badOrders2").process(new Processor() {
            public void process(Exchange exchange) throws Exception {
                System.out.println("Received bad order2: " + exchange.getIn().getHeader("CamelFileName"));
            }
        });

        // test that our route is working
        from("activemq:queue:continuedProcessing").process(new Processor() {
            public void process(Exchange exchange) throws Exception {
                System.out.println("Received continued order2: " + exchange.getIn().getHeader("CamelFileName"));
            }
        });
        Thread.sleep(10000);
        System.out.println("Exiting------OrderRouterWithStop");
    }

}
