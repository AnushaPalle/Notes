# 8. Observability: metrics, traces, health

Status: Accepted

## Context
Async sagas and several services make failures hard to see. "You can't fix what you can't see."

## Decision
- **Metrics**: Micrometer + Prometheus (`/actuator/prometheus`), including business counters
  (`shopflow.orders.placed`, `shopflow.orders.cancelled`) and Resilience4j circuit-breaker metrics.
- **Tracing**: Micrometer Tracing (Brave) to Zipkin. Trace ids are in every log line and propagate over HTTP and
  Kafka (observation enabled), so one order can be followed end to end.
- **Health**: Kubernetes-style liveness/readiness probes under `/actuator/health`.

## Consequences
- (+) Symptoms are visible (error rate, saga cancellations, latency) and traces pinpoint the slow hop.
- (-) 100% trace sampling is for the demo; lower it in production.
- Next: SLOs (e.g. 99.5% of orders leave PENDING within 5 s) with alerts on burn rate; OpenTelemetry as the vendor-neutral
  successor to Brave.
