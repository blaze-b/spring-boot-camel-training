package com.springboot.order.route;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;


@Component
public class OrderRoute extends RouteBuilder {

    @Autowired
    private ProducerTemplate producerTemplate;

    protected AbstractXmlApplicationContext createApplicationContext() {
        // See this file for the route in Spring XML
        return new ClassPathXmlApplicationContext("config/RouteScope.xml");
    }

    @Override
    public void configure() throws Exception {
        System.out.println("**********Entering***OrderRoute*********************");
        testOrderOk();
        System.out.println("**********Exiting***OrderRoute*********************");
    }

    public void testOrderOk() throws Exception {
        from("file:data/inbox-orders").process(new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                producerTemplate.sendBodyAndHeader("activemq:queue:testBean",
                        "<?xml version=\"1.0\"?><order>" + "<amount>1</amount><name>Camel in Action</name></order>",
                        Exchange.FILE_NAME, "order.xml");
            }
        });

        from("activemq:queue:testBean").process(new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                String message = exchange.getIn().getBody(String.class);
                System.out.println("activemq:queue:testBean" + message);
            }
        });

        Thread.sleep(5000);
    }

}
