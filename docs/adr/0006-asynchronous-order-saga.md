# 6. Asynchronous order saga over Kafka

Status: Accepted

## Context
Synchronous REST (ADR 0004/0005) still ties order placement to payment-service availability and latency.
Orders and payments now have separate data; a distributed transaction is not an option.

## Decision
Add a third mode, `shopflow.payments.mode=kafka`, implementing a **choreographed Saga**:
1. `orders` commits a `PENDING` order and, after commit, publishes `OrderPlacedV1` to `shopflow.order-placed.v1`.
2. `payment-service` (consumer group `payment-service`) charges idempotently and publishes `PaymentResultV1` to
   `shopflow.payment-result.v1`.
3. The monolith (consumer group `shopflow-monolith`) consumes the result: success marks the order `PAID`; failure
   triggers the compensating action `CANCELLED`.

Events are versioned records in the `shopflow-contracts` module, serialized as JSON without type headers (each
consumer declares its one expected type, so the wire format is not tied to Java class names).
Messages are keyed by `orderId`, so all events of one order land in the same partition and stay ordered.
Delivery is at-least-once, so consumers are idempotent (`Idempotency-Key = order-<id>`, `markPaid`/`cancel` are no-ops
once the order has left `PENDING`).

### Why Kafka
A replayable, partitioned log fits an event-driven system: consumers track offsets, new consumers can replay history,
and throughput scales with partitions. The trade-off is more operational weight than a simple queue broker.
The pet project runs a single KRaft node (no ZooKeeper), 3 partitions, replication factor 1.

## Consequences
- (+) Temporal decoupling and fault isolation; a payment outage only delays orders.
- (+) Durable, replayable event log and a natural audit trail.
- (-) Eventual consistency: clients must handle `PENDING`.
- (-) Harder to debug: needs tracing and correlation (ADR 0008).
- Not yet covered: a dead-letter topic (consumer errors use Spring Kafka's default retry then skip) and an outbox
  for guaranteed publish.
