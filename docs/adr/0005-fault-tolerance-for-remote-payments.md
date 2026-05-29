# 5. Fault tolerance for the payment-service call

Status: Accepted

## Context
Once billing is remote (ADR 0004), the network can fail, be slow or return errors. Synchronous calls couple
availability: if payment-service is down, ordering would be down too.

## Decision
`RemotePaymentProcessor` uses Resilience4j:
- **Retry** (3 attempts, exponential backoff). Safe because every call carries an `Idempotency-Key`.
- **Circuit breaker** to stop hammering a failing dependency and give it time to recover.
- **Fallback**: report the payment as failed so the saga cancels the order, rather than surfacing a 500.

## Consequences
- (+) Failures are contained; the monolith stays responsive.
- (-) A cancelled order during an outage is a business decision: some products may prefer "pending, retry later".
  That is addressed by the asynchronous saga in ADR 0006.
