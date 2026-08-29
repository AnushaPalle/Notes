# Threat model (STRIDE)

Scope: PulseBoard core API (REST ingest, webhooks, admin, SSE stream) and its data.

| Threat | Example | Mitigation | Status |
|---|---|---|---|
| **S**poofing | Attacker posts events as another service | API key per caller; HMAC signature on webhooks; constant-time comparison | Done |
| **T**ampering | Webhook body modified in transit | HMAC over the raw body; TLS at the edge | Done / TLS is deployment |
| **R**epudiation | "We never sent that alert" | Append-only event log with ids and timestamps; structured logs | Done |
| **I**nformation disclosure | Reading events without credentials; keys in logs | All `/api/**` needs a key; keys never logged; secrets from env, no defaults | Done |
| **D**enial of service | Flood of events exhausts memory | Bounded queue, 503 + Retry-After, payload column limit | Done (rate limiting at the edge is next) |
| **E**levation of privilege | Normal key calls replay or dead-letter admin APIs | Separate admin keys, 403 otherwise | Done |

## Zero-trust notes

- No endpoint is trusted because of where the call comes from; every request is authenticated.
- With no keys configured the service rejects everything (fail closed).
- Replay of a captured webhook is harmless because ingestion is idempotent by `eventId`.

## Known gaps

- Static shared keys: no rotation workflow, no per-key rate limit, no key expiry.
- No mTLS between PulseBoard and the notifier.
- A key sent over plain HTTP is exposed; always terminate TLS in front.
