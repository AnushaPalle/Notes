# 7. Alert rules as a serverless-style function

Status: Accepted

## Context
Alert rules are stateless, spiky (only WARN/ERROR events), and change often. The core should not redeploy to
change who gets paged.

## Decision
- A separate `notifier` function (Node.js) takes one event and decides which alerts to send. Handler signature is
  API Gateway/Lambda-shaped; `template.yaml` (AWS SAM) describes the deployment, `src/server.js` runs it locally.
- The core calls it through an outbound adapter (`NotifierEventPublisher`) only for WARN and above, with 2 s timeouts.
  The adapter exists only when `PULSEBOARD_NOTIFIER_URL` is set.
- Failures never affect ingestion; `IngestService` logs and moves on.

## Consequences
- Scale-to-zero and independent deploys for the part that is bursty and stateless.
- Extra network hop and a cold-start risk; alerts are best-effort (no retry or outbox yet).
- Not a good fit for the stateful, transactional core, which therefore stays a long-running service
  (see `docs/architecture/fit-matrix.md`).
