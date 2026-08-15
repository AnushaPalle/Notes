# 4. Asynchronous, idempotent ingestion with retry and dead letters

Status: Accepted

## Context
Producers should not wait for storage, bursts must not take the API down, and clients retry on timeouts, so the same
event will arrive more than once.

## Decision
- `POST /api/events` validates, enqueues and returns **202**. A single worker consumes in order.
- A bounded queue gives **back-pressure**: when full the API answers **503 + Retry-After**.
- Failures are retried (3 attempts, linear backoff) and then written to **dead_letters**
  (`GET /api/admin/dead-letters`).
- **Idempotency** comes from the unique `event_id`: a duplicate append is a no-op and is not projected or published.

## Consequences
- Fast acknowledgements and resilience to short storage outages.
- Delivery is at-least-once from the client and effectively-once in storage.
- The in-process queue is lost on crash. For durability across restarts use a broker (Kafka), which fits
  behind the `SubmitEventUseCase` port without changing the core.
