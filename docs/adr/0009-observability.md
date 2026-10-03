# ADR 0009: Observability via decorators, Prometheus and ELK

## Status
Accepted

## Context
Section 7 of the course stresses that cloud-native systems need metrics, logs and traces. We want these without polluting the hexagonal core.

## Decision
- Metrics are added by **decorators** (`MeteredIngestUseCase`, `MeteredDeadLetterStore`) in `adapter.out.metrics`, marked `@Primary`. The domain and application layers stay free of Micrometer.
- Actuator exposes `health` (with liveness/readiness probes), `info` and `prometheus`. These are outside `/api`, so API-key auth does not apply; do not expose them publicly.
- Logs are structured JSON, shipped only under the `elk` profile so local runs stay readable.
- Prometheus alert rules encode the SLOs in `docs/slo.md`.

## Consequences
- Counters are cheap and testable. Decorators depend on concrete classes (`IngestService`, `JdbcDeadLetterStore`) to keep the proxied `@Transactional` bean as delegate.
- Distributed tracing is deferred: with one service plus a notifier it adds little. ShopFlow demonstrates Zipkin tracing across services.
- The TCP log appender drops logs if Logstash is down; acceptable for a pet project.
