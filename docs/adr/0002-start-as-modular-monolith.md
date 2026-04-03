# 2. Start as a modular monolith

Status: Accepted

## Context
Small team, unknown domain boundaries, need fast iteration. Microservices would add network latency,
distributed debugging and DevOps overhead before we understand where the seams are.

## Decision
Build one deployable (`shopflow-monolith`) split into modules that mirror bounded contexts:
`users`, `catalog`, `orders`, `billing`. Each module is layered: `web -> application -> domain`.
Modules talk only through each other's `contract` package (interfaces, DTOs, events).

## Consequences
- (+) Simple deployment, one transaction, easy testing and debugging.
- (+) Clear seams make later extraction (Strangler pattern) cheap.
- (-) Single scaling unit until modules are extracted.
- Boundaries are enforced by ArchUnit tests so the monolith does not decay into a big ball of mud.
