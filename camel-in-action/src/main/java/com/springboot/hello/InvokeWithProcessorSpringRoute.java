package com.springboot.hello;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class InvokeWithProcessorSpringRoute extends RouteBuilder {

    private HelloBean hello;

    @Autowired
    public InvokeWithProcessorSpringRoute(HelloBean hello) {
        this.hello = hello;
    }

    @Autowired
    private ProducerTemplate producerTemplate;

    @Override
    public void configure() throws Exception {
        System.out.println("**********Entering***InvokeWithProcessorSpringRoute*********************");
        from("timer://test?period=50000").process(new Processor() {
            public void process(Exchange exchange) throws Exception {
                // extract the name parameter from the Camel message which we want to use
                // when invoking the bean
                System.out.println("**********Entering***activemq:queue:helloQueue*********************");
                String name = exchange.getIn().getBody(String.class);
                System.out.println(name);
                if (name == null || name.length() == 0)
                    name = "BONNY";
                // invoke the bean which should have been injected by Spring
                String answer = hello.hello(name);
                // store the reply from the bean on the
                exchange.getOut().setBody(answer);
                exchange.getOut().setHeader(name, answer);
                log.info("-------------Sending--HelloBean-----------------");
                producerTemplate.sendBodyAndHeader("activemq:queue:helloQueue", answer, "Name", "BONNY");
            }
        });

        from("activemq:queue:helloQueue").process(new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                // TODO Auto-generated method stub
                log.info("-------------Receiving--HelloBean-----------------");
                StringBuilder string = new StringBuilder();
                string.append(exchange.getIn().getHeader("Name")).append("\n")
                        .append(exchange.getIn().getBody(String.class)).toString();
                System.out.println(string);
            }
        });

        Thread.sleep(10000);
        System.out.println("**********Exiting***InvokeWithProcessorSpringRoute*********************");
    }

}
