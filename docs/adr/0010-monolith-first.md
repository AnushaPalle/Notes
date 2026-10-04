# ADR 0010: Keep a hexagonal monolith, extract only the notifier

## Status
Accepted

## Context
Section 9 of the course says architecture decisions should follow context, team size and rate of change, and that DDD bounded contexts show where seams are.

## Decision
- Ingestion, Insights and Alerting are bounded contexts inside one deployable, with ports as seams.
- Only Alerting delivery is extracted (serverless-style notifier) because it is stateless and bursty.
- Known gaps are documented in `docs/architecture/fit-matrix.md` rather than hidden.

## Consequences
- One transaction covers event store and read model; no distributed consistency issues.
- Extracting another context later means implementing a port with a remote adapter.
- Revisit if ingest volume needs independent scaling from queries.
