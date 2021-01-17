package com.springboot.integration.camel.wiretap.route;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

//@Component
public class OrderRouterWithWireTap extends RouteBuilder{

	@Override
	public void configure() throws Exception {
		// load file orders from data/inbox-wiretap into the queue
		System.out.println("***********Entering---OrderRouterWithWireTap********************");
		from("file:data/inbox-wiretap?noop=true").to("direct:testssssssss");

		// content-based router
		from("direct:testssssssss").wireTap("activemq:queue:orderAudit").choice()
				.when(header("CamelFileName").endsWith(".xml")).to("activemq:queue:xmlOrders8")
				.when(header("CamelFileName").regex("^.*(csv|csl)$")).to("activemq:queue:csvOrders8").otherwise()
				.to("activemq:queue:badOrders8");

		// test that our route is working
		from("activemq:queue:orderAudit").process(new Processor() {
			public void process(Exchange exchange) throws Exception {
				System.out.println("Received OrderAudit: " + exchange.getIn().getHeader("CamelFileName"));
				System.out.println("Received OrderAudit: " + exchange.getIn().getBody(String.class));
			}
		});
		// test that our route is working
		from("activemq:queue:xmlOrders8").process(new Processor() {
			public void process(Exchange exchange) throws Exception {
				System.out.println("Received xmlOrders8: " + exchange.getIn().getHeader("CamelFileName"));
				System.out.println("Received xmlOrders8: " + exchange.getIn().getBody(String.class));
			}
		});
		Thread.sleep(2000);
		System.out.println("***********Entering---OrderRouterWithWireTap********************");
	}

}
