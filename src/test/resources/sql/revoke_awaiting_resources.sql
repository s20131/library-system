CREATE OR REPLACE PROCEDURE revoke_awaiting_resources()
LANGUAGE plpgsql
AS '
    DECLARE
    BEGIN
        UPDATE rental
        SET status = ''CANCELLED''
        WHERE status = ''RESERVED_TO_BORROW'' AND finish < CURRENT_TIMESTAMP;
    END;
';
