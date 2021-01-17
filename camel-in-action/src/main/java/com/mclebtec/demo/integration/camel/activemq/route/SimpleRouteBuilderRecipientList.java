package com.mclebtec.demo.integration.camel.activemq.route;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;

//@Component
public class SimpleRouteBuilderRecipientList extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        // TODO Auto-generated method stub
        System.out.println("---------SimpleRouteBuilderRecipientListENTER---------");
        from("file:data/input").split().tokenize("\n").to("direct:test");
        // Recipient List- Dynamically set the recipients of the exchange
        // by creating the queue name at runtime
        from("direct:test").process(new Processor() {
            public void process(Exchange exchange) throws Exception {
                String recipient = exchange.getIn().getBody().toString();
                System.out.println(recipient);
                log.info("**********************************");
                log.info("Send message '{}' to queue....", recipient);
                String recipientQueue = "activemq:queue:" + recipient;
                System.out.println(recipientQueue);
                log.info("**********************************");
                log.info("Send message '{}' to queue....", recipientQueue);
                exchange.getIn().setHeader("queue", recipientQueue);
                System.out.println("---------SimpleRouteBuilderRecipientListEXIT---------");
            }
        }).recipientList(header("queue"));

    }

}
