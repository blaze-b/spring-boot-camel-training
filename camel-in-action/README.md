# Chapter 1: Camel in action

Learn core Camel EIPs with the Java DSL: file copy, content-based router, filter, multicast, wire tap, and recipient list.

## What you learn

- How `RouteBuilder` wires consumer → EIP → producer
- Content-based routing on file extension
- XPath filter to drop test messages
- Parallel multicast and recipient list
- Wire tap for non-blocking audit copies

SEDA endpoints stand in for a broker so the demos run without ActiveMQ.

## How to run

```bash
cd camel-in-action
# default demo is content-based router
mvn spring-boot:run

# other demos
mvn spring-boot:run -Dspring-boot.run.arguments=--demo.route=file-copy
mvn spring-boot:run -Dspring-boot.run.arguments=--demo.route=filter
mvn spring-boot:run -Dspring-boot.run.arguments=--demo.route=multicast
mvn spring-boot:run -Dspring-boot.run.arguments=--demo.route=wiretap
mvn spring-boot:run -Dspring-boot.run.arguments=--demo.route=recipient-list
```

Sample files live under `data/inbox*` (and mirrored in `src/main/resources/data`).

## Try it

1. Drop a `.xml` and a `.csv` into `data/inbox-cbr` and watch the CBR logs.
2. Switch to `filter` and confirm `testmessage.xml` is skipped.
3. Switch to `multicast` and confirm both accounting and production log lines.

## Tests

```bash
mvn test
```

Previous: [Chapter 0](../README.md) · Next: [Chapter 2: Camel SQL](../camel-sql/)
