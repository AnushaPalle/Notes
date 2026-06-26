# ShopFlow

An e-commerce backend that evolves on purpose: **modular monolith -> strangled microservices**.
Built while studying *Mastering Software Architecture*.

Patterns practiced: Layered/Clean architecture, Modular monolith (DDD bounded contexts),
Event-driven (domain events, Saga), Strangler migration, Microservices behind an API gateway (Kong),
resilience (retry, circuit breaker), observability (metrics, tracing, health probes).

## Layout

- `shopflow-contracts` - wire contracts shared by both services (Kafka messages)
- `shopflow-monolith` - users, catalog, orders, billing modules (ArchUnit enforces boundaries)
- `payment-service` - extracted payments service
- `docker/` + `docker-compose.yml` - Kong, Kafka, Zipkin, Prometheus
- `docs/adr` - decisions, `docs/architecture` - C4, bounded contexts, fit matrix

## Run the tests

```bash
mvn -B test
```

## Run the stack

```bash
mvn -B -DskipTests package
docker compose up --build
./scripts/demo.sh
```

Billing mode: `PAYMENTS_MODE=local|rest|kafka` (default `local`).

| URL | What |
|---|---|
| http://localhost:8000 | Kong proxy (JWT required) |
| http://localhost:8080/actuator/prometheus | monolith metrics |
| http://localhost:9411 | Zipkin |
| http://localhost:9090 | Prometheus |
| localhost:29092 | Kafka broker (host access) |

## Status

Unit and integration tests pass. The Docker stack (Kong JWT/ACL, monolith, payment-service, Kafka, Zipkin, Prometheus) was run end to end with `scripts/demo.sh` in `local` and `kafka` modes. `rest` mode was not exercised in Docker.
