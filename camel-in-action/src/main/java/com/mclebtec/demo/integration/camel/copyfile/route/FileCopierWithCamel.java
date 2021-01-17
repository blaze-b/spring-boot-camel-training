package com.mclebtec.demo.integration.camel.copyfile.route;

import org.apache.camel.builder.RouteBuilder;

//@Component
public class FileCopierWithCamel extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        // TODO Auto-generated method stub
        System.out.println("Entering-----FileCopierWithCamel");
        from("file:data/inbox?noop=true")
                .to("file:data/outbox");
        Thread.sleep(10000);
        System.out.println("Exiting------FileCopierWithCamel");
    }

}
