CREATE OR REPLACE FUNCTION get_penalty()
    RETURNS decimal
    LANGUAGE sql
AS '
    SELECT penalty_rate FROM config;
';

CREATE OR REPLACE PROCEDURE update_penalties()
    LANGUAGE plpgsql
AS '
    DECLARE
    BEGIN
        UPDATE rental SET penalty = COALESCE(penalty, 0) + get_penalty() WHERE status = ''ACTIVE'' AND finish < CURRENT_TIMESTAMP;
    END;
';
