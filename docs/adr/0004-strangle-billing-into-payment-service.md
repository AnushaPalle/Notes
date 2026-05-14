# 4. Strangle billing into a payment-service

Status: Accepted

## Context
Billing has the clearest seam in the monolith (ADR 0002/0003) and different scaling and compliance needs.
A big-bang rewrite is risky.

## Decision
Apply the **Strangler pattern**. Billing gets a `PaymentProcessor` port with two adapters:
`LocalPaymentProcessor` (original logic) and `RemotePaymentProcessor` (calls `payment-service` over REST).
`shopflow.payments.mode` (`local` | `rest`) routes traffic, so the new service can be switched on, verified and rolled back
without a deploy of different code.
`payment-service` honours an `Idempotency-Key` header, so client retries never double charge.

## Consequences
- (+) Incremental, reversible migration; the rest of the monolith is untouched.
- (+) payment-service scales and deploys independently.
- (-) A network hop appears inside the order flow: latency, partial failure, and a second database later.
  Failure handling is added in ADR 0005.
