# 6. Server-Sent Events for the live feed

Status: Accepted

## Context
The dashboard must show events as they happen. Data flows one way (server to browser).

## Decision
- `GET /api/stream` serves Server-Sent Events. `SseHub` is an `EventPublisher` adapter, so the core only knows
  "publish this event".
- SSE over WebSocket: one-directional, plain HTTP, automatic reconnection in browsers, simple to proxy.
- `EventSource` cannot send headers, so `/api/stream` (only) also accepts `?apiKey=`.

## Consequences
- Trivial client code and no extra infrastructure.
- A key in a URL can end up in proxy and access logs. Acceptable for a learning project; production should use
  short-lived stream tokens or cookies, and the gateway should not log query strings for this path.
- Subscribers live in memory of one instance. Several instances would need a broker (Redis/Kafka) fan-out.
