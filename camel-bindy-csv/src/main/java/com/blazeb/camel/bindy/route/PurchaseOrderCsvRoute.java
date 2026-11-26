package com.blazeb.camel.bindy.route;

import java.math.BigDecimal;

import com.blazeb.camel.bindy.model.PurchaseOrder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.BindyType;
import org.springframework.stereotype.Component;

@Component
public class PurchaseOrderCsvRoute extends RouteBuilder {

    @Override
    public void configure() {
        from("timer:purchaseOrder?period={{bindy.timer.period}}&repeatCount=1")
                .routeId("purchase-order-csv")
                .process(exchange -> exchange.getIn().setBody(
                        new PurchaseOrder("AKC4433", new BigDecimal("157.79"), 3)))
                .log("Marshaling ${body}")
                .marshal().bindy(BindyType.Csv, PurchaseOrder.class)
                .to("file:data/out?fileName=purchase-order.csv&charset=utf-8")
                .log("Wrote purchase-order.csv");
    }
}
