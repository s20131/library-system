CREATE OR REPLACE FUNCTION get_penalty()
    RETURNS decimal
    LANGUAGE sql
AS '
    SELECT (settings -> ''penalty_rate'')::decimal FROM config;
';

CREATE OR REPLACE PROCEDURE update_penalties()
    LANGUAGE plpgsql
AS '
    DECLARE
    BEGIN
    UPDATE rental
    SET penalty = COALESCE(penalty, 0) + get_penalty(), status = ''PROLONGED''
    WHERE (status = ''ACTIVE'' OR status = ''PROLONGED'') AND finish < CURRENT_TIMESTAMP;
    END;
';
