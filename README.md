# PulseBoard

A real-time activity and alerting platform: apps send activity events, PulseBoard stores them,
shows live dashboards and sends alerts. Built while studying *Mastering Software Architecture*.

Patterns practiced: Hexagonal architecture (ports and adapters), CQRS with an append-only event store and
replayable read model, Event-driven/async ingestion (idempotency, retry, dead letters), a serverless-style
notifier function, MVVM vs MVC vs MVP on the UI, plus security (API keys, signed webhooks, threat model)
and observability (metrics, structured logs in ELK, SLOs).

Every architectural decision is recorded in `docs/adr`.
