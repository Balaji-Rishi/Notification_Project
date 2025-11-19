show databases;

use notifications_db;

show tables;

select * from notifications;

SELECT id, channel, recipient, status, retry_count, max_retries, last_error, next_attempt_at,
       processing_started_at, created_at, updated_at
FROM notifications WHERE id = 12;


SELECT id, recipient, status, retry_count, max_retries, last_error, next_attempt_at
FROM notifications WHERE id = 14;


UPDATE notifications
SET status = 'PROCESSING',
    processing_started_at = DATE_SUB(NOW(), INTERVAL 11 MINUTE)
WHERE id = 15;


