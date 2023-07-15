SET TIMEZONE TO 'Europe/Warsaw';

CREATE OR REPLACE PROCEDURE revoke_ebooks()
LANGUAGE plpgsql
AS '
DECLARE
BEGIN
    UPDATE rental
    SET status = ''FINISHED''
    WHERE status = ''ACTIVE''
    AND finish < ''2023-05-07 17:55:00.000''::timestamptz
    AND rental.resource_id IN (SELECT ebook.resource_id FROM ebook WHERE rental.resource_id = ebook.resource_id);
END;
';
