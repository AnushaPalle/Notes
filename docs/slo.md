# SLOs

| SLI | Objective | Measured by |
|-----|-----------|-------------|
| Ingest success ratio | >= 99% of ingests do not error over 30 days | `pulseboard_events_ingested_total{result="error"}` vs total |
| Dead-letter rate | 0 new dead letters in any 5 min window | `pulseboard_events_dead_lettered_total` |
| Availability | Core scrapeable 99.5% | `up{job="pulseboard-core"}` |
| Latency | p95 of `POST /api/events` < 300 ms | `http_server_requests_seconds` histogram |

Alerts live in `docker/prometheus/alerts.yml`. The error budget for 99% over 30 days is about 7.2 hours of full failure; burn it down before shipping risky changes.

## Three pillars

- **Metrics**: Micrometer to `/actuator/prometheus`, scraped by Prometheus.
- **Logs**: JSON via logstash-logback-encoder (profile `elk`) to Logstash, Elasticsearch, Kibana (index `pulseboard-logs-*`).
- **Traces**: not implemented; see ADR 0009.
