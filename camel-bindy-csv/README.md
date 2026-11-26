# Chapter 3: Bindy CSV

Marshal and unmarshal CSV with Camel Bindy. Merges the near-identical public demos for bindy and CSV writing into one module.

## What you learn

- `@CsvRecord` / `@DataField` models
- `marshal().bindy(BindyType.Csv, ...)` for a single record
- `BindyCsvDataFormat` for a list of catalogue items
- Writing CSV to disk and reading it back

## How to run

```bash
cd camel-bindy-csv
mvn spring-boot:run
```

Output files appear under `data/out/`:

- `purchase-order.csv`
- `catalogue-items.csv`

## Try it

1. Change the purchase order amount and re-run; inspect the CSV.
2. Add another `CatalogueItem` to the list and confirm a new CSV row.
3. Point the read route at a hand-edited CSV and watch unmarshal logs.

## Tests

```bash
mvn test
```

Previous: [Chapter 2: Camel SQL](../camel-sql/) · Next: [Chapter 4: Camel Kafka](../camel-kafka/)
