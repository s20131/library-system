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
    UPDATE rental
    SET penalty = COALESCE(penalty, 0) + get_penalty(), status = 'PROLONGED'
    WHERE (status = 'ACTIVE' OR status = 'PROLONGED') AND finish < CURRENT_TIMESTAMP;
END;
$$;

CREATE PROCEDURE revoke_awaiting_resources()
LANGUAGE plpgsql
AS $$
DECLARE
BEGIN
    UPDATE rental
    SET status = 'CANCELLED'
    WHERE status = 'RESERVED_TO_BORROW' AND finish < CURRENT_TIMESTAMP;
END;
$$;
