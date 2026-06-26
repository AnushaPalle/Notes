# ADR 0010: Known gaps and what we would do next

Status: accepted

- Docker stack (Kong, Kafka, Zipkin, Prometheus) was run end to end with `scripts/demo.sh` in `local` and `kafka` modes; `rest` mode was not exercised in Docker.
- No outbox: a crash between DB commit and Kafka publish can lose an `OrderPlaced` message. Next step: transactional outbox.
- No mTLS between services; internal traffic trusts the network (Zero Trust gap).
- H2 in memory; move to PostgreSQL per service before any real use.
- Kong demo secrets are committed on purpose and must never be reused.
