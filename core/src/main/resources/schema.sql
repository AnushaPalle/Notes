-- Write side: append-only log of facts. Rows are never updated or deleted.
CREATE TABLE IF NOT EXISTS events (
    seq         BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_id    VARCHAR(100)  NOT NULL UNIQUE,
    source      VARCHAR(100)  NOT NULL,
    type        VARCHAR(100)  NOT NULL,
    severity    VARCHAR(10)   NOT NULL,
    payload     VARCHAR(4000) NOT NULL,
    occurred_at VARCHAR(40)   NOT NULL
);

-- Read side: a projection that can be dropped and rebuilt from `events`.
CREATE TABLE IF NOT EXISTS stats (
    source VARCHAR(100) NOT NULL,
    type   VARCHAR(100) NOT NULL,
    total  BIGINT       NOT NULL,
    PRIMARY KEY (source, type)
);
