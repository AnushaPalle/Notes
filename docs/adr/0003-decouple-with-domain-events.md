# 3. Decouple orders and billing with domain events

Status: Accepted (supersedes the synchronous `PaymentGateway` call)

## Context
`orders` called `billing` directly. That couples the modules in time and in code, and makes billing hard to extract.

## Decision
`orders` publishes `OrderPlaced`; `billing` reacts and publishes `PaymentCompleted` / `PaymentFailed`;
`orders` reacts by marking the order paid or cancelling it (saga compensation).
Events live in each module's `contract` package. In the monolith they travel over Spring's in-process event bus.

## Consequences
- (+) Modules know nothing about each other's internals; billing can later move out of process.
- (+) Events are an audit trail of what happened.
- (-) Control flow is less obvious; needs good tests and tracing.
- (-) In-process synchronous delivery means everything shares one transaction. That changes once billing is remote (ADR 0004).
