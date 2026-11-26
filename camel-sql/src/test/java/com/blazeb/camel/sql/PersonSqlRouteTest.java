package com.blazeb.camel.sql;

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

import java.util.List;
import java.util.Map;

@CamelSpringBootTest
@SpringBootTest
@UseAdviceWith
@DirtiesContext
class PersonSqlRouteTest {

    @Autowired
    private CamelContext camelContext;

    @Autowired
    private ProducerTemplate producerTemplate;

    @Test
    void routesVipNamesStartingWithA() throws Exception {
        AdviceWith.adviceWith(camelContext, "person-sql-poll", a -> {
            a.replaceFromWith("direct:person-poll");
            a.weaveByToUri("sql:*").replace().process(e -> e.getIn().setBody(
                    List.of(
                            Map.of("ID", 1L, "NAME", "Ada"),
                            Map.of("ID", 2L, "NAME", "Ben")
                    )
            ));
            a.weaveByToUri("seda:vipPersons").replace().to("mock:vip");
            a.weaveByToUri("seda:standardPersons").replace().to("mock:standard");
        });

        camelContext.start();

        MockEndpoint vip = camelContext.getEndpoint("mock:vip", MockEndpoint.class);
        MockEndpoint standard = camelContext.getEndpoint("mock:standard", MockEndpoint.class);
        vip.expectedMessageCount(1);
        standard.expectedMessageCount(1);

        producerTemplate.sendBody("direct:person-poll", null);

        MockEndpoint.assertIsSatisfied(camelContext);
    }
}
