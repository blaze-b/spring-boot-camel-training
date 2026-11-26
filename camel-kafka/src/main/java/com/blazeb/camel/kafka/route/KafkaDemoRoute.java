package com.blazeb.camel.kafka.route;

import java.util.UUID;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "kafka.demo.enabled", havingValue = "true", matchIfMissing = true)
public class KafkaDemoRoute extends RouteBuilder {

    @Override
    public void configure() {
        from("timer:kafkaProducer?period={{kafka.producer.period}}")
                .routeId("kafka-producer")
                .process(exchange -> exchange.getIn().setBody(UUID.randomUUID().toString()))
                .log("Sending message ${body}")
                .to("kafka:{{kafka.topic}}?brokers={{kafka.brokers}}");

        from("kafka:{{kafka.topic}}?brokers={{kafka.brokers}}&groupId={{kafka.group-id}}"
                + "&autoOffsetReset=earliest")
                .routeId("kafka-consumer")
                .log("Received message ${body}");
    }
}
