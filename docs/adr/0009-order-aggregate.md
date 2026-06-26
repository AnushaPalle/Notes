# ADR 0009: Model Order as an aggregate with explicit state transitions

Status: accepted

## Context
Orders are changed by the payment flow (paid or cancelled) from several entry points (listener, saga consumer, REST).

## Decision
`Order` is an aggregate root. State changes go only through `markPaid()` and `cancel()` on the entity,
invoked from `OrderService`. Other contexts hold the order id, never the entity.

## Consequences
Invariants live in one place. Extracting Orders later only needs the id-based contracts to be turned into messages.
