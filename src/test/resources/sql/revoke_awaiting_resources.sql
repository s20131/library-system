SET TIMEZONE TO 'Europe/Warsaw';

CREATE OR REPLACE PROCEDURE revoke_awaiting_resources()
LANGUAGE plpgsql
AS '
    DECLARE
    BEGIN
        UPDATE rental
        SET status = ''CANCELLED''
        WHERE status = ''RESERVED_TO_BORROW'' AND finish < ''2023-05-07 17:55:00.000''::timestamptz;
    END;
';
