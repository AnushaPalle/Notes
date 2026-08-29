# 5. API keys and signed webhooks, fail closed

Status: Accepted

## Context
The API accepts data from machines and exposes admin operations. We want authentication that is simple, has no
default credentials, and keeps the core free of framework security machinery for now.

## Decision
- A servlet filter at the web adapter authenticates `/api/**` with `X-API-Key`, with two roles: normal and admin.
- Third parties that cannot hold a key use `/api/webhooks/{source}` authenticated by `X-Signature: sha256=<hmac>`.
- Secrets come from environment variables; nothing configured means everything is rejected.
- Authentication lives in the adapter; the domain and application layers are unaware of it (hexagonal).

## Consequences
- Easy to understand and test; no default passwords to leak.
- Static keys are weaker than short-lived tokens. The next step is OAuth2/JWT (Spring Security resource server) or
  letting the API gateway (Kong, see ShopFlow) own authentication.
