package com.mclebtec.demo.integration.camel.activemq.route;

import org.apache.camel.ConsumerTemplate;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class MainRouter extends RouteBuilder {

    @Autowired
    private ProducerTemplate producerTemplate;

    @Autowired
    private ConsumerTemplate consumerTemplate;

    @Override
    public void configure() throws Exception {
        // TODO Auto-generated method stub
        System.out.println("**********Entering***MainRouter*********************");
        // Producer route
        from("timer://test?period=50000").process(new Processor() {
            public void process(Exchange exchange) throws Exception {
                String message = UUID.randomUUID().toString();
                log.info("-------------Sending-------------------");
                log.info("Send message '{}' to queue....", message);
                //two ways of the jms declaration
                //producerTemplate.sendBody("activemq://test-queue", message);
                producerTemplate.sendBody("activemq:queue:CamelQueue", message);
            }
        });
        // ==========================================================================//
        // Consumer queue
        //from("activemq://test-queue").process(new Processor() {
        from("activemq:queue:CamelQueue").process(new Processor() {
            public void process(Exchange exchange) throws Exception {
                String message = exchange.getIn().getBody(String.class);
                log.info("-------------Receiving-------------------");
                log.info("Receive message '{}' from queue.", message);
            }
        });
        Thread.sleep(10000);
        System.out.println("***********Exiting***MainRouter********************");
    }

}
