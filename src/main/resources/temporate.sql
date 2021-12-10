INSERT INTO sink001
SELECT
    MINUTE(`timestamp`) as per_minute,
    SUM(`value`) as amt,
    COUNT(1) as cnt
FROM source001
GROUP BY MINUTE(`timestamp`);

INSERT INTO sink001
SELECT
    MINUTE(TUMBLE_START(`timestamp`, INTERVAL '5' SECOND)) as per_minute,
    SUM(`value`) as amt,
    COUNT(1) as cnt
FROM source001
GROUP BY TUMBLE(`timestamp`, INTERVAL '5' SECOND);

SELECT
    window_start,
    window_end,
    SUM(`value`)
FROM TABLE(TUMBLE(TABLE source001, DESCRIPTOR(`timestamp`), INTERVAL '10' SECONDS))
GROUP BY window_start, window_end;