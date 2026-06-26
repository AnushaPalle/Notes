# Bounded contexts (DDD)

| Context | Owns | Exposes (contract) | Consumes | Ubiquitous language |
|---|---|---|---|---|
| Users | user accounts | `UserQuery.exists` | - | User |
| Catalog | products, prices | `CatalogQuery.findBySku` | - | Product, SKU, Price |
| Orders | order lifecycle | `OrderPlaced` event | Users, Catalog (queries) | Order, Place, Cancel, Paid |
| Billing / Payments | payment attempts | `PaymentCompleted`, `PaymentFailed` events, `/payments` REST, Kafka messages | Orders (events) | Payment, Charge, Idempotency key |

## Context map

- Orders -> Users, Catalog: **customer/supplier** via synchronous in-process queries (read-only contracts).
- Orders <-> Billing: **published language** (events in the monolith, `OrderPlacedV1` / `PaymentResultV1` on the wire) -
  no shared domain model.
- Payment-service is an **anti-corruption layer** boundary: the monolith only sees `PaymentProcessor`.

## Aggregates

- `Order` is the aggregate root; state changes only through `markPaid()` / `cancel()`.
- The only cross-aggregate reference is by id (`userId`, `sku`), never by object.

## Why these boundaries

They follow change cadence and failure isolation: payments change for compliance reasons, need independent
scaling, and fail differently, so they were the first context extracted (ADR 0004).
