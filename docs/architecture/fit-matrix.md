# Fit matrix: which architecture for which need

| Need | Choice here | Why | Alternative and when to switch |
|------|-------------|-----|--------------------------------|
| Isolate business rules from frameworks | Hexagonal core | Swap H2 for Postgres or SSE for Kafka without touching domain | Plain layered is fine for CRUD-only apps |
| Audit trail and replay | Event store + CQRS read model | Read model can be rebuilt after a bug | Simple CRUD table when history is not needed |
| Absorb bursts, survive downstream failure | In-process async queue, retry, DLQ | Cheap, no broker to operate | Kafka/other brokers when multiple instances or durability across restarts are required |
| Occasional alert delivery | Serverless-style notifier | Stateless, scales to zero, pay per use | Keep in-core if alerts are constant and latency critical |
| Live UI updates | SSE | One-way, simple, auto-reconnect | WebSocket for bidirectional traffic |
| UI structure | MVVM with hooks | State logic testable without DOM | MVC/MVP for non-reactive UIs |
| Operate it | Prometheus + ELK | Metrics for alerts, logs for diagnosis | Managed observability when ops cost matters |

## Monolith vs serverless trade-off

The core stays a single deployable (a modular monolith in hexagonal form) because ingest, projection and query share a transaction. Only the notifier, which is stateless and bursty, is a function. Splitting more would add network hops and consistency problems without a scaling need.

## Bounded contexts

- **Ingestion**: accepts, validates, de-duplicates events (`IngestUseCase`, `AsyncIngestor`).
- **Insights**: read model, stats, replay (`QueryUseCase`, `ReadModel`).
- **Alerting**: decides what is notable and delivers it (`EventPublisher`, notifier).

They communicate through the `EventPublisher` port, so Alerting could be extracted without changing Ingestion.

## Known gaps

- In-process queue loses queued events on crash (the event store is durable, the queue is not). Use an outbox or broker for at-least-once guarantees.
- H2 in memory by default; configure `DB_URL` for persistence.
- No distributed tracing, no rate limiting, API keys are static and not rotated.
- `?apiKey=` on the stream endpoint may leak into access logs (ADR 0006).
- ELK stack (Elasticsearch, Logstash, Kibana) and the `elk` log profile not run.
