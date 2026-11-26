package com.blazeb.camel.bindy.route;

import java.math.BigDecimal;
import java.util.List;

import com.blazeb.camel.bindy.model.CatalogueItem;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.bindy.csv.BindyCsvDataFormat;
import org.springframework.stereotype.Component;

@Component
public class CatalogueCsvRoute extends RouteBuilder {

    @Override
    public void configure() {
        BindyCsvDataFormat bindy = new BindyCsvDataFormat(CatalogueItem.class);

        from("timer:catalogue?period={{bindy.timer.period}}&repeatCount=1")
                .routeId("catalogue-csv")
                .process(exchange -> exchange.getIn().setBody(List.of(
                        new CatalogueItem("SKU-100", "Notebook", "stationery", new BigDecimal("4.50")),
                        new CatalogueItem("SKU-200", "USB Hub", "electronics", new BigDecimal("19.99")),
                        new CatalogueItem("SKU-300", "Desk Lamp", "home", new BigDecimal("24.00"))
                )))
                .marshal(bindy)
                .to("file:data/out?fileName=catalogue-items.csv&charset=utf-8")
                .log("Wrote catalogue-items.csv");

        from("file:data/out?fileName=catalogue-items.csv&noop=true&idempotent=true")
                .routeId("catalogue-csv-read")
                .unmarshal(bindy)
                .split(body())
                .log("Unmarshaled item: ${body}");
    }
}
