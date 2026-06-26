# ShopFlow sequence diagrams

## 1. Gateway: JWT + ACL

```mermaid
sequenceDiagram
    participant C as Client
    participant K as Kong :8000
    participant M as Monolith
    C->>K: POST /products (no token)
    K-->>C: 401
    C->>K: POST /products (shopper JWT)
    K->>K: verify JWT, check ACL group
    K-->>C: 403
    C->>K: POST /products (admin JWT)
    K->>M: forward
    M-->>K: 201
    K-->>C: 201
```

## 2. Place order, local mode (in-process domain events)

```mermaid
sequenceDiagram
    participant C as Client
    participant O as Orders module
    participant B as Billing module
    C->>O: POST /orders
    O->>O: save order (PENDING)
    O-->>B: OrderPlaced (domain event)
    B->>B: PaymentProcessor.process
    B-->>O: PaymentCompleted / PaymentFailed
    O->>O: PAID or CANCELLED
    O-->>C: order
```

## 3. Place order, rest mode (strangler + resilience)

```mermaid
sequenceDiagram
    participant B as Billing
    participant R as Retry
    participant CB as CircuitBreaker
    participant P as payment-service
    B->>R: process(order)
    loop up to max attempts
        R->>CB: call
        alt circuit closed
            CB->>P: POST /payments (Idempotency-Key: order-id)
            P-->>CB: APPROVED / DECLINED
        else circuit open
            CB-->>R: fail fast
        end
    end
    R-->>B: outcome, or fallback "payment service unavailable"
    B-->>B: PaymentCompleted / PaymentFailed
```

## 4. Place order, kafka mode (choreographed saga)

```mermaid
sequenceDiagram
    participant C as Client
    participant O as Monolith Orders
    participant X as Kafka topics
    participant P as payment-service
    C->>O: POST /orders
    O->>O: save PENDING, commit
    O->>X: shopflow.order-placed.v1, key=orderId (AFTER_COMMIT)
    O-->>C: 201 PENDING
    X->>P: consumer group payment-service
    P->>P: charge idempotently (key order-id)
    P->>X: shopflow.payment-result.v1, key=orderId
    X->>O: consumer group shopflow-monolith
    alt success
        O->>O: PAID
    else failure
        O->>O: CANCELLED (compensation)
    end
```

## 5. Observability

```mermaid
sequenceDiagram
    participant M as Monolith
    participant P as payment-service
    participant Z as Zipkin
    participant PR as Prometheus
    M->>P: call (trace id propagated)
    M-->>Z: spans
    P-->>Z: spans
    PR->>M: scrape /actuator/prometheus
    PR->>P: scrape /actuator/prometheus
```
