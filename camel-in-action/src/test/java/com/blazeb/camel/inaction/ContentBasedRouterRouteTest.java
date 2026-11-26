package com.blazeb.camel.inaction;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWith;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.UseAdviceWith;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

@CamelSpringBootTest
@SpringBootTest
@UseAdviceWith
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestPropertySource(properties = {
        "demo.route=cbr",
        "camel.springboot.main-run-controller=false"
})
class ContentBasedRouterRouteTest {

    @Autowired
    private CamelContext camelContext;

    @Autowired
    private ProducerTemplate producerTemplate;

    @Test
    void routesXmlOrdersByExtension() throws Exception {
        AdviceWith.adviceWith(camelContext, "cbr-ingest", a -> a.replaceFromWith("direct:cbr-ingest"));
        AdviceWith.adviceWith(camelContext, "cbr-xml", a -> a.weaveAddLast().to("mock:xml"));
        AdviceWith.adviceWith(camelContext, "cbr-csv", a -> a.weaveAddLast().to("mock:csv"));
        AdviceWith.adviceWith(camelContext, "cbr-bad", a -> a.weaveAddLast().to("mock:bad"));

        camelContext.start();

        MockEndpoint xml = camelContext.getEndpoint("mock:xml", MockEndpoint.class);
        MockEndpoint csv = camelContext.getEndpoint("mock:csv", MockEndpoint.class);
        MockEndpoint bad = camelContext.getEndpoint("mock:bad", MockEndpoint.class);
        xml.expectedMessageCount(1);
        csv.expectedMessageCount(1);
        bad.expectedMessageCount(1);

        producerTemplate.sendBodyAndHeader("direct:cbr-ingest", "<order/>", "CamelFileName", "a.xml");
        producerTemplate.sendBodyAndHeader("direct:cbr-ingest", "a,b", "CamelFileName", "b.csv");
        producerTemplate.sendBodyAndHeader("direct:cbr-ingest", "x", "CamelFileName", "c.bad");

        MockEndpoint.assertIsSatisfied(camelContext);
    }
}
