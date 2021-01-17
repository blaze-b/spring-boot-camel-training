package com.springboot.order.route;


import com.springboot.order.converter.PurchaseOrder;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PurchaseOrderRoute extends RouteBuilder {

    @Autowired
    private ProducerTemplate producerTemplate;

    @Override
    public void configure() throws Exception {
        System.out.println("Entering----PurchaseOrderComponent");
        from("timer://test?period=50000").process(new Processor() {
            public void process(Exchange exchange) throws Exception {
                // extract the name parameter from the Camel message which we want to use
                // when invoking the bean
                System.out.println("**********Entering***activemq:queue:productmq*********************");
                byte[] data = "##START##AKC4433   179.95    3##END##".getBytes();
                exchange.getOut().setBody(data);
                log.info("-------------Sending--PurchaseOrderRoute-----------------");
                producerTemplate.sendBody("activemq:queue:productmq", data);
                ;
            }
        });

        from("activemq:queue:productmq")
                .convertBodyTo(PurchaseOrder.class)
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        log.info("-------------Receiving--PurchaseOrderRoute-----------------");
                        String orderDetails = exchange.getIn().getBody(String.class);
                        System.out.println("-------------Received--orderDetails-----------------" + orderDetails);
                        PurchaseOrder order = exchange.getIn().getBody(PurchaseOrder.class);
                        System.out.println("-------------Received--orderDetails-----------------" + order);
                        System.out.println("-------------Received--orderDetailsName-----------------" + order.getName());
                        System.out.println("-------------Received--orderDetailsPrice-----------------" + order.getPrice().toString());
                        System.out.println("-------------Received--orderDetailsAmount-----------------" + order.getAmount());
                    }

                });
        Thread.sleep(2000);
        System.out.println("Exiting----PurchaseOrderComponent");
    }

}
