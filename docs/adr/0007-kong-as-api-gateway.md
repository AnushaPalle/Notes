# 7. Kong as the single entry point (API gateway)

Status: Accepted

## Context
Services must not each re-implement authentication, authorization, throttling and request hygiene.
Zero Trust: never trust the network, always verify identity, grant least privilege.

## Decision
Only Kong is exposed (port 8000). It runs DB-less from `docker/kong/kong.yml` (config as code) and enforces:
- **Authentication**: `jwt` plugin (HS256 for the demo; use asymmetric keys or an IdP in production).
- **Authorization / least privilege**: `acl` plugin, `shoppers` may read and order, only `admins` may create products.
- **Traffic control**: `rate-limiting` (30/min) and `request-size-limiting` (1 MB).
- **Observability/hygiene**: `correlation-id` header, `cors`.

## Consequences
- (+) One place for cross-cutting security policy; services stay simple.
- (+) Gateway config is versioned and reviewable.
- (-) The gateway is a critical path and a potential single point of failure: run several instances in production.
- (-) The internal network is still trusted in this demo. Next steps: mTLS between services and service-level auth.
- Demo secrets are in git on purpose. Never do this for real.
