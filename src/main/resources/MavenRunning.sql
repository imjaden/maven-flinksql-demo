CREATE TABLE source001 (
    `userid` BIGINT,
    `value` BIGINT,
    `timestamp` TIMESTAMP(3),
    `proctime` as PROCTIME(),
     WATERMARK FOR `timestamp` as `timestamp` - INTERVAL '5' SECOND
) WITH (
    'connector' = 'kafka',
    'topic' = 'source001',
    'scan.startup.mode' = 'earliest-offset',
    'properties.bootstrap.servers' = '127.0.0.1:9092',
    'format' = 'json'
);

CREATE TABLE sink001 (
    per_minute BIGINT,
    amt BIGINT,
    cnt BIGINT,
    primary key (per_minute) not enforced
) WITH (
    'connector' = 'upsert-kafka',
    'topic' = 'sink001',
    'properties.bootstrap.servers' = '127.0.0.1:9092',
    'key.format' = 'json',
    'value.format' = 'json'
);

INSERT INTO sink001
SELECT
    MINUTE(`timestamp`) as per_minute,
    SUM(`value`) as amt,
    COUNT(1) as cnt
FROM source001
GROUP BY MINUTE(`timestamp`);