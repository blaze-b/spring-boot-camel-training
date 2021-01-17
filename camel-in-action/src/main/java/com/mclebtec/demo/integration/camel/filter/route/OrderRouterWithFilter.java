package com.mclebtec.demo.integration.camel.filter.route;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;

//@Component
public class OrderRouterWithFilter extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        // TODO Auto-generated method stub
        // load file orders from src/data into the JMS queue
        System.out.println("Entering-----OrderRouterWithFilter");
        from("file:data/inbox-filter?noop=true").to("direct:testsss");

        // content-based router
        from("direct:testsss").choice().when(header("CamelFileName").endsWith(".xml")).to("activemq:queue:xmlOrders3")
                .when(header("CamelFileName").regex("^.*(csv|csl)$")).to("activemq:queue:csvOrders3").otherwise()
                .to("activemq:queue:badOrders3");
        // lets filter out the test messages
        //test message will not be received
        //Filter
        from("activemq:queue:xmlOrders3").filter(xpath("/order[not(@test)]"))
                .process(new Processor() {
                    public void process(Exchange exchange) throws Exception {
                        System.out.println("Received XML order3: "
                                + exchange.getIn().getHeader("CamelFileName"));
                    }
                });
        Thread.sleep(10000);
        System.out.println("Exiting------OrderRouterWithFilter");
    }

}
