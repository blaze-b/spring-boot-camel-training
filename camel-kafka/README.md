# Chapter 4: Camel Kafka

Produce and consume Kafka messages with the Camel Kafka component (Java DSL).

## What you learn

- Timer-driven producer writing to a Kafka topic
- Consumer group subscription with `autoOffsetReset`
- Externalising brokers / topic / group via env vars

## How to run

Start Kafka first (Chapter 6 `docker-compose` includes a KRaft broker), then:

```bash
cd camel-kafka
export KAFKA_BROKERS=localhost:9092
mvn spring-boot:run
```

Optional overrides: `KAFKA_TOPIC`, `KAFKA_GROUP_ID`.

## Try it

1. Watch UUID messages appear in producer and consumer logs.
2. Change `kafka.topic` and restart both sides of the conversation.
3. Use Kafdrop (Chapter 6 compose profile) to inspect the topic.

## Tests

Smoke tests replace `kafka:` endpoints with mocks so `mvn test` does not need a broker.

```bash
mvn test
```

Previous: [Chapter 3: Bindy CSV](../camel-bindy-csv/) · Next: [Chapter 5: ActiveMQ JMS](../activemq-jms/)
