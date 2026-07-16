# 2. Hexagonal architecture for the core

Status: Accepted

## Context
PulseBoard has many ways in (REST, webhooks, SSE) and many ways out (database, push stream, notifier).
Those will change faster than the business rules.

## Decision
The core is hexagonal: `domain` and `application` know nothing about HTTP, JDBC or messaging.
They expose **inbound ports** (`port.in`) and need **outbound ports** (`port.out`).
Everything technical lives in `adapter.in.*` / `adapter.out.*`.
An ArchUnit test enforces the dependency direction.

## Consequences
- Core logic is unit-testable with plain fakes.
- Swapping storage (memory -> JDBC) touches adapters only.
- Slightly more files and indirection than a plain controller-service-repository stack.
