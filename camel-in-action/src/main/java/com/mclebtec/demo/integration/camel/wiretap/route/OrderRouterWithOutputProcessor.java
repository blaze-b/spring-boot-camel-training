package com.mclebtec.demo.integration.camel.wiretap.route;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;

//@Component
public class OrderRouterWithOutputProcessor extends RouteBuilder {

	@Override
	public void configure() throws Exception {
		// load file orders from src/data into the JMS queue
		System.out.println("***********Entering---OrderRouterWithOutputProcessor********************");
		from("file:data/inbox-output?noop=true").to("direct:testsssssss");

		// content-based router
		from("direct:testsssssss").process(new Processor() {
			public void process(Exchange exchange) throws Exception {
				System.out.println("Received order header: " + exchange.getIn().getHeader("CamelFileName"));
				System.out.println("Received order body: " + exchange.getIn().getBody(String.class));
			}
		}).choice().when(header("CamelFileName").endsWith(".xml")).to("activemq:queue:xmlOrders7")
				.when(header("CamelFileName").regex("^.*(csv|csl)$")).to("activemq:queue:csvOrders7").otherwise()
				.to("activemq:queue:badOrders7");
		Thread.sleep(2000);
		System.out.println("***********Exiting---OrderRouterWithOutputProcessor********************");
	}

}
