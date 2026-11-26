# Chapter 5: ActiveMQ JMS (plain Spring)

Contrast chapter: the same queue messaging style as earlier Camel demos, but with **plain Spring JMS** (`JmsTemplate` + `@JmsListener`) instead of Camel routes.

## What you learn

- Spring Boot ActiveMQ starter configuration
- REST-triggered producer with `JmsTemplate`
- Annotation-driven consumer with `@JmsListener`
- Why Camel EIPs still matter once routing grows beyond send/receive

## Modules

| Module | Port | Role |
|--------|-----:|------|
| `activemq-producer` | 8083 | HTTP → queue |
| `activemq-receiver` | 8084 | queue → log |

## How to run

1. Start ActiveMQ Classic (or Artemis with an OpenWire connector) locally.
2. Export broker credentials (do **not** commit real passwords):

```bash
export ACTIVEMQ_BROKER_URL=tcp://localhost:61616
export ACTIVEMQ_USER=your-user
export ACTIVEMQ_PASSWORD=your-password
export ACTIVEMQ_QUEUE_NAME=learning-queue
```

3. Start receiver, then producer:

```bash
cd activemq-jms/activemq-receiver && mvn spring-boot:run
cd activemq-jms/activemq-producer && mvn spring-boot:run
```

4. Publish:

```bash
curl http://localhost:8083/api/message/hello-camel
curl http://localhost:8083/api/messages
```

## Try it

1. Compare this chapter with Chapter 1 SEDA routing — notice the lack of CBR/filter DSL here.
2. Add a second queue and dual `@JmsListener` methods; contrast with Camel recipient list.
3. Point both apps at Chapter 6 infrastructure ideas (externalise hosts via env only).

## Tests

```bash
mvn test
```

Previous: [Chapter 4: Camel Kafka](../camel-kafka/) · Next: [Chapter 6: Course enrolment pipeline](../course-enrolment-pipeline/)
