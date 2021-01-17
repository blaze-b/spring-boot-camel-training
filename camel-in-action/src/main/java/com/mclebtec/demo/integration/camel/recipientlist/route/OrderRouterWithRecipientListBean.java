package com.mclebtec.demo.integration.camel.recipientlist.route;

import com.mclebtec.demo.integration.camel.recipientlist.route.bean.RecipientListBean;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;

//@Component
public class OrderRouterWithRecipientListBean extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        // TODO Auto-generated method stub
        System.out.println("***********Entering---OrderRouterWithRecipientListBean********************");
        from("file:data/inbox-recipientlstbean?noop=true").to("direct:testssssss");

        // content-based router
        from("direct:testssssss").choice().when(header("CamelFileName").endsWith(".xml"))
                .to("activemq:queue:xmlOrders6").when(header("CamelFileName").regex("^.*(csv|csl)$"))
                .to("activemq:queue:csvOrders6").otherwise().to("activemq:queue:badOrders6");
        // Bean class
        from("activemq:queue:xmlOrders6").bean(RecipientListBean.class);

        // test that our route is working
        from("activemq:queue:accounting2").process(new Processor() {
            public void process(Exchange exchange) throws Exception {
                System.out.println("Accounting received order2: " + exchange.getIn().getHeader("CamelFileName"));
            }
        });
        from("activemq:queue:production2").process(new Processor() {
            public void process(Exchange exchange) throws Exception {
                System.out.println("Production received order2: " + exchange.getIn().getHeader("CamelFileName"));
            }
        });
        Thread.sleep(10000);
        System.out.println("***********Exiting---OrderRouterWithRecipientListBean********************");

    }

}
