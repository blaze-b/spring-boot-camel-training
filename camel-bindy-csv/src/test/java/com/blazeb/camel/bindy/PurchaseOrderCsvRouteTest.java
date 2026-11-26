package com.blazeb.camel.bindy;

import com.blazeb.camel.bindy.model.PurchaseOrder;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWith;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.model.dataformat.BindyType;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.UseAdviceWith;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertTrue;

@CamelSpringBootTest
@SpringBootTest
@UseAdviceWith
@DirtiesContext
class PurchaseOrderCsvRouteTest {

    @Autowired
    private CamelContext camelContext;

    @Autowired
    private ProducerTemplate producerTemplate;

    @Test
    void marshalsPurchaseOrderToCsv() throws Exception {
        AdviceWith.adviceWith(camelContext, "purchase-order-csv", a -> {
            a.replaceFromWith("direct:purchase-order");
            a.weaveByToUri("file:*").replace().to("mock:file");
        });
        AdviceWith.adviceWith(camelContext, "catalogue-csv", a -> a.replaceFromWith("direct:catalogue-skip"));
        AdviceWith.adviceWith(camelContext, "catalogue-csv-read", a -> a.replaceFromWith("direct:catalogue-read-skip"));

        camelContext.start();

        MockEndpoint file = camelContext.getEndpoint("mock:file", MockEndpoint.class);
        file.expectedMessageCount(1);

        producerTemplate.sendBody("direct:purchase-order",
                new PurchaseOrder("AKC4433", new BigDecimal("157.79"), 3));

        MockEndpoint.assertIsSatisfied(camelContext);
        String csv = file.getExchanges().get(0).getIn().getBody(String.class);
        assertTrue(csv.contains("AKC4433"));
        assertTrue(csv.contains("157.79"));
    }
}
