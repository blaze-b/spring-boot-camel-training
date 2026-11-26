package com.blazeb.camel.kafka;

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
@DirtiesContext
@TestPropertySource(properties = {
        "kafka.demo.enabled=true",
        "camel.springboot.main-run-controller=false"
})
class KafkaDemoRouteTest {

    @Autowired
    private CamelContext camelContext;

    @Autowired
    private ProducerTemplate producerTemplate;

    @Test
    void producerSendsBodyToKafkaEndpoint() throws Exception {
        AdviceWith.adviceWith(camelContext, "kafka-producer", a -> {
            a.replaceFromWith("direct:kafka-producer");
            a.weaveByToUri("kafka:*").replace().to("mock:kafka");
        });
        AdviceWith.adviceWith(camelContext, "kafka-consumer", a ->
                a.replaceFromWith("direct:kafka-consumer"));

        camelContext.start();

        MockEndpoint kafka = camelContext.getEndpoint("mock:kafka", MockEndpoint.class);
        kafka.expectedMessageCount(1);
        kafka.message(0).body().isInstanceOf(String.class);

        producerTemplate.sendBody("direct:kafka-producer", null);

        MockEndpoint.assertIsSatisfied(camelContext);
    }
}
