# PulseBoard sequence diagrams

## 1. Ingest: async queue, CQRS write path, DLQ

```mermaid
sequenceDiagram
    participant C as Client
    participant API as EventController
    participant Q as AsyncIngestor (bounded queue)
    participant S as IngestService
    participant ES as EventStore
    participant RM as ReadModel
    participant PUB as Publishers (SSE, Notifier)
    participant DL as DeadLetterStore
    C->>API: POST /api/events (X-API-Key)
    API->>Q: submit(event)
    alt queue full
        API-->>C: 503 + Retry-After
    else accepted
        API-->>C: 202
    end
    Q->>S: ingest(event) [worker]
    rect rgb(235,245,255)
    note over S,RM: one transaction
    S->>ES: append(event)
    alt duplicate id
        ES-->>S: false, return DUPLICATE
    else new
        S->>RM: apply(event)
    end
    end
    S->>PUB: publish(event) (errors logged only)
    alt fails after max attempts
        Q->>DL: save(event, reason)
    end
```

## 2. CQRS read and replay

```mermaid
sequenceDiagram
    participant C as Client
    participant Q as QueryService
    participant RM as ReadModel (stats)
    participant ES as EventStore
    C->>Q: GET /api/stats
    Q->>RM: read projection
    RM-->>C: counts by source/type
    C->>Q: POST /api/admin/replay (admin key)
    Q->>RM: clear
    Q->>ES: read all events in order
    loop each event
        Q->>RM: apply(event)
    end
```

## 3. Webhook with HMAC

```mermaid
sequenceDiagram
    participant X as External system
    participant W as WebhookController
    participant H as HmacVerifier
    participant Q as AsyncIngestor
    X->>W: POST /api/webhooks/billing + X-Signature sha256=hex
    W->>H: verify(body, signature, secret)
    alt invalid
        W-->>X: 401
    else valid
        W->>Q: submit(event)
        W-->>X: 202
    end
```

## 4. Live stream and notifier fan-out

```mermaid
sequenceDiagram
    participant B as Browser
    participant Hub as SseHub
    participant I as IngestService
    participant N as NotifierEventPublisher
    participant F as notifier function
    B->>Hub: GET /api/stream?apiKey=...
    Hub-->>B: SSE connection kept open
    I->>Hub: publish(event)
    Hub-->>B: event pushed
    I->>N: publish(event)
    alt severity WARN or above
        N->>F: POST /notify
        F->>F: apply rules, deliver alert
    end
```

## 5. Dashboard MVVM

```mermaid
sequenceDiagram
    participant V as DashboardView
    participant VM as useDashboardViewModel
    participant St as dashboardState (pure reducer)
    participant API as api/client
    V->>VM: render, read state
    VM->>API: fetch stats + open stream
    API-->>VM: data / events
    VM->>St: reduce(state, action)
    St-->>VM: new state
    VM-->>V: re-render
    V->>VM: user action (filter)
```

## 6. Observability

```mermaid
sequenceDiagram
    participant S as MeteredIngestUseCase
    participant IS as IngestService
    participant M as MeterRegistry
    participant P as Prometheus
    participant L as Logstash/ES/Kibana
    S->>IS: ingest(event)
    IS-->>S: ACCEPTED/DUPLICATE or exception
    S->>M: counter pulseboard.events.ingested{result}
    P->>M: scrape /actuator/prometheus
    P->>P: evaluate alerts.yml (SLOs)
    IS-->>L: JSON logs (profile elk)
```

## Hexagon overview

```mermaid
flowchart LR
    subgraph in[Adapters in]
        REST[EventController / Webhook]
        SSEc[StreamController]
    end
    subgraph core[Application + Domain]
        UC[Use-case ports]
        SVC[IngestService / QueryService]
        DOM[ActivityEvent]
    end
    subgraph out[Adapters out]
        JDBC[JdbcEventStore / JdbcReadModel]
        HUB[SseHub]
        NOT[NotifierEventPublisher]
        DLS[JdbcDeadLetterStore]
    end
    REST --> UC --> SVC --> DOM
    SSEc --> HUB
    SVC --> JDBC
    SVC --> HUB
    SVC --> NOT
    SVC --> DLS
```
