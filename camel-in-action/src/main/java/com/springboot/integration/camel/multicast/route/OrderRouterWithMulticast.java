package com.springboot.integration.camel.multicast.route;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//@Component
public class OrderRouterWithMulticast extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        System.out.println("***********Entering---OrderRouterWithMulticast********************");
        // TODO Auto-generated method stub
        // load file orders from data/inbox-multicast into the JMS queue
        from("file:data/inbox-multicast?noop=true").to("direct:testssss");

        // content-based router
        from("direct:testssss").choice().when(header("CamelFileName").endsWith(".xml")).to("activemq:queue:xmlOrders4")
                .when(header("CamelFileName").regex("^.*(csv|csl)$")).to("activemq:queue:csvOrders4").otherwise()
                .to("activemq:queue:badOrders4");


        // Simple multicasting example sending to mutiple queues
        //from("activemq:queue:xmlOrders4").multicast().to("activemq:queue:accounting", "activemq:queue:production");

        // Parallel multicasting
        ExecutorService executor = Executors.newFixedThreadPool(16);
        from("activemq:queue:xmlOrders4").multicast().parallelProcessing().executorService(executor).to("activemq:queue:accounting",
                "activemq:queue:production");

        // test that our route is working
        from("activemq:queue:accounting").process(new Processor() {
            public void process(Exchange exchange) throws Exception {
                System.out.println("Accounting received order: " + exchange.getIn().getHeader("CamelFileName"));
            }
        });
        from("activemq:queue:production").process(new Processor() {
            public void process(Exchange exchange) throws Exception {
                System.out.println("Production received order: " + exchange.getIn().getHeader("CamelFileName"));
            }
        });
        Thread.sleep(10000);
        System.out.println("***********Exiting---OrderRouterWith-Multicast********************");
    }

}
