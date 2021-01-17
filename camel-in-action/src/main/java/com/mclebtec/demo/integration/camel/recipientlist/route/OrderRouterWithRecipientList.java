package com.mclebtec.demo.integration.camel.recipientlist.route;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;

//@Component
public class OrderRouterWithRecipientList extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        // TODO Auto-generated method stub
        System.out.println("***********Entering---OrderRouterWithRecipientList********************");
        from("file:data/inbox-recipientlist?noop=true").to("direct:testsssss");

        // content-based router
        from("direct:testsssss")
                .choice()
                .when(header("CamelFileName").endsWith(".xml"))
                .to("activemq:queue:xmlOrders5")
                .when(header("CamelFileName").regex("^.*(csv|csl)$"))
                .to("activemq:queue:csvOrders5")
                .otherwise()
                .to("activemq:queue:badOrders5");

        from("activemq:queue:xmlOrders5")
                .setHeader("customer", xpath("/order/@customer"))
                .process(new Processor() {
                    public void process(Exchange exchange) throws Exception {
                        String recipients = "activemq:queue:accounting1";
                        System.out.println(recipients);
                        String customer = exchange.getIn().getHeader("customer", String.class);
                        System.out.println(customer);
                        if (customer.equals("honda")) {
                            recipients += ",activemq:queue:production1";
                        }
                        exchange.getIn().setHeader("recipients", recipients);
                    }
                })
                .recipientList(header("recipients"));

        // test that our route is working
        from("activemq:queue:accounting1").process(new Processor() {
            public void process(Exchange exchange) throws Exception {
                System.out.println("Accounting received order1: "
                        + exchange.getIn().getHeader("CamelFileName"));
            }
        });
        from("activemq:queue:production1").process(new Processor() {
            public void process(Exchange exchange) throws Exception {
                System.out.println("Production received order1: "
                        + exchange.getIn().getHeader("CamelFileName"));
            }
        });
        Thread.sleep(10000);
        System.out.println("***********Exiting---OrderRouterWithRecipientList********************");

    }

}
