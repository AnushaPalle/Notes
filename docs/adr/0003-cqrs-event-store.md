# 3. CQRS with an append-only event store

Status: Accepted

## Context
The dashboard needs fast aggregates (counts by source and type), while ingestion needs a durable, simple write path.
We also want to change what we aggregate later without losing history.

## Decision
- **Write side**: `events` table, append-only, unique `event_id` (also gives idempotency).
- **Read side**: `stats` projection updated in the same transaction as the append.
- **Replay**: `POST /api/admin/replay` clears the projection and rebuilds it from the log.
- Both sides sit behind ports (`EventStore`, `ReadModel`), so they could later live in different databases.

## Consequences
- Reads are cheap and shaped for the UI; writes stay trivial.
- New projections can be added by replaying history.
- Same-transaction projection keeps it simple but couples both sides to one database. Splitting them would make the
  read model eventually consistent (an explicit trade-off we are not paying yet).
- Full event sourcing (state only from events) is not used for everything; only the activity log is a log of facts.
