CREATE OR REPLACE PROCEDURE revoke_ebooks()
LANGUAGE plpgsql
AS '
DECLARE
BEGIN
    UPDATE rental
    SET status = ''FINISHED''
    WHERE status = ''ACTIVE''
    AND finish < CURRENT_TIMESTAMP
    AND rental.resource_id IN (SELECT ebook.resource_id FROM ebook WHERE rental.resource_id = ebook.resource_id);
END;
';