CREATE FUNCTION get_penalty()
RETURNS decimal
LANGUAGE sql
AS $$
    SELECT (settings -> 'penalty_rate')::decimal FROM config;
$$;

CREATE PROCEDURE update_penalties()
LANGUAGE plpgsql
AS $$
DECLARE
BEGIN
    UPDATE rental SET penalty = COALESCE(penalty, 0) + get_penalty() WHERE status = 'ACTIVE' AND finish < CURRENT_TIMESTAMP;
END;
$$;
