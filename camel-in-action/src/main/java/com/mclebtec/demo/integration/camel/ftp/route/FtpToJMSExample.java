package com.mclebtec.demo.integration.camel.ftp.route;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;

//@Component
public class FtpToJMSExample extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        // TODO Auto-generated method stub
        System.out.println("***********Entering---FtpToJMSExample********************");

        // ftp://10.100.191.60:10021///URL set up done for the testing ftp testing

        // from("ftp://10.100.191.60:10021/?username=brian&password=1234")//homepath
        from("ftp://10.100.191.60:10021/FTP/IN/?username=brian&password=1234")// particular path consumption
                //parallel execution
                .process(new Processor() {
                    public void process(Exchange exchange) throws Exception {
                        System.out.println("We just downloaded: " + exchange.getIn().getHeader("CamelFileName"));
                        Thread.sleep(10000);
                    }
                }).to("activemq:queue:ftpQueueTest").stop().end();
        Thread.sleep(10000);
        System.out.println("***********Exiting---FtpToJMSExample********************");
    }

}
