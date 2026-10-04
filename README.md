# PulseBoard

A real-time activity and alerting platform: apps send activity events, PulseBoard stores them,
shows live dashboards and sends alerts. Built while studying *Mastering Software Architecture*.

## Patterns practiced

| Pattern | Where |
|---------|-------|
| Hexagonal (ports and adapters) | `core`, enforced by `ArchitectureTest` (ADR 0002) |
| CQRS + append-only event store, replayable read model | ADR 0003 |
| Event-driven / async ingest: idempotency, retry, DLQ | ADR 0004 |
| Security: API keys, HMAC webhooks, STRIDE threat model | ADR 0005, `docs/architecture/threat-model.md` |
| Server push (SSE) | ADR 0006 |
| Serverless-style function | `notifier`, ADR 0007 |
| MVVM vs MVC vs MVP | `dashboard`, ADR 0008 |
| Observability: metrics, ELK logs, SLOs | ADR 0009, `docs/slo.md` |

Decision records live in `docs/adr`; see also `docs/architecture/fit-matrix.md` (includes bounded contexts and known gaps).

## Layout

- `core/` Spring Boot 3 (Java 21) hexagonal service
- `notifier/` Node 20 Lambda-style alert function (also runnable as an HTTP server)
- `dashboard/` React + Vite UI
- `docker/`, `docker-compose.yml` core, notifier, ELK, Prometheus

## Run and test

```bash
# core
cd core && mvn test
PULSEBOARD_API_KEYS=demo-key PULSEBOARD_ADMIN_KEYS=demo-admin PULSEBOARD_WEBHOOK_SECRET=whsec_demo mvn spring-boot:run

# notifier
cd notifier && node --test && npm start

# dashboard (proxies /api to :8080)
cd dashboard && npm install && npm test && npm run dev

# full stack
docker compose up --build   # core :8080, notifier :3000, Kibana :5601, Prometheus :9090
```

Try it:

```bash
curl -X POST localhost:8080/api/events -H 'X-API-Key: demo-key' -H 'Content-Type: application/json' \
  -d '{"eventId":"e1","source":"billing","type":"payment.failed","severity":"ERROR"}'
curl -H 'X-API-Key: demo-key' localhost:8080/api/stats
curl -N 'localhost:8080/api/stream?apiKey=demo-key'
curl localhost:8080/actuator/prometheus | grep pulseboard
```

With no API keys configured every `/api` request is rejected (fail closed).

## Verification status

Unit and integration tests for `core`, `notifier` and `dashboard` pass, and `docker compose config` validates.
The `core`, `notifier` and Prometheus containers were run and smoke-tested (ingest, metrics, alert delivery, Prometheus scrape and rules). The ELK containers (Elasticsearch, Logstash, Kibana) were not run, so the `elk` log profile is untested.
