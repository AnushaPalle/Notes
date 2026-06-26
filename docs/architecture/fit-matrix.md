# Architecture fit matrix

Scored 1 (poor) to 5 (great) for ShopFlow's current needs: small team, one deployable domain, one hot spot (payments).

| Style | Team size fit | Ops cost | Scaling hot spots | Consistency | Verdict |
|---|---|---|---|---|---|
| Plain layered monolith | 5 | 5 | 1 | 5 | Becomes a ball of mud without module rules |
| **Modular monolith** | 5 | 5 | 2 | 5 | Chosen as the default (ADR 0002) |
| SOA / ESB | 2 | 2 | 3 | 3 | Too heavy |
| **Microservices (selective)** | 2 | 2 | 5 | 2 | Only for payments, via Strangler (ADR 0004) |
| Serverless | 3 | 4 | 5 | 2 | Not a fit for a stateful order flow (used in PulseBoard instead) |

## Sync vs async

| Interaction | Choice | Reason |
|---|---|---|
| Order -> Users/Catalog | sync, in process | Need the answer now, no network |
| Order -> Payment (rest mode) | sync + retry + circuit breaker | Simple, but couples availability (ADR 0005) |
| Order -> Payment (kafka mode) | async saga | Decouples availability, eventual consistency (ADR 0006) |

## Quality attribute trade-offs

- **Consistency vs availability**: kafka mode accepts a PENDING window, in exchange for the order API staying up when payments are down.
- **Simplicity vs scalability**: the monolith stays; only the hot spot is split.
- **Security**: Kong enforces JWT, ACL and rate limiting at the edge (ADR 0007). Internal calls are not yet mTLS - a known gap.
