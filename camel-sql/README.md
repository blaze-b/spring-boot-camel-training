# Chapter 2: Camel SQL

Use the Camel SQL component against an embedded H2 database: poll rows on a timer, then content-based route by name prefix.

## What you learn

- Wiring Spring `DataSource` into `sql:` endpoints
- Timer-driven database polling
- Splitting result rows and routing with a content-based router
- Seeding schema with `schema.sql` / `data.sql` (no Oracle driver)

## How to run

```bash
cd camel-sql
mvn spring-boot:run
```

H2 console (optional): http://localhost:8080/h2-console  
JDBC URL: `jdbc:h2:mem:camelsql`

## Try it

1. Watch VIP vs standard log lines for seeded names (`Ada`, `Asha`, `Alex` vs others).
2. Insert another row via the H2 console and wait for the next poll (`person.poll.period`).
3. Change the VIP predicate to a different letter and restart.

## Tests

```bash
mvn test
```

Previous: [Chapter 1: Camel in action](../camel-in-action/) · Next: [Chapter 3: Bindy CSV](../camel-bindy-csv/)
