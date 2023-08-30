-- DATABASE FEATURE - function
CREATE FUNCTION get_file_extension(filename text)
RETURNS text
LANGUAGE plpgsql
IMMUTABLE
AS $$
BEGIN
    RETURN substring(filename FROM '\w+$');
END; $$;

---

CREATE FUNCTION get_image_type(filename text)
RETURNS text
LANGUAGE plpgsql
IMMUTABLE
AS $$
BEGIN
    RETURN concat('image/', get_file_extension(filename));
END; $$;

---

-- DATABASE FEATURE - dynamic SQL, run functions with owner privileges
CREATE FUNCTION refresh_view()
RETURNS trigger
SECURITY DEFINER
LANGUAGE plpgsql
AS $$
DECLARE
    view_name TEXT := tg_argv[0];
BEGIN
    EXECUTE format('REFRESH MATERIALIZED VIEW CONCURRENTLY ' || view_name || ';');
    RETURN NULL;
END; $$;

---

CREATE FUNCTION generate_binary(length int)
RETURNS bytea
LANGUAGE sql
AS $$
    SELECT repeat(md5(random()::text), length)::bytea;
$$;

---

CREATE FUNCTION generate_between(min int, max int)
RETURNS int
LANGUAGE sql
AS $$
    SELECT (random() * (max - min + 1) + min)::int;
$$;

---

-- DATABASE FEATURE - derived table, exception
CREATE FUNCTION check_resource_id_uniqueness()
RETURNS trigger
LANGUAGE plpgsql
AS $$
DECLARE
    counter int;
BEGIN
    SELECT count(*) INTO counter FROM
    (
        SELECT resource_id FROM book
        INTERSECT
        SELECT resource_id FROM ebook
    ) AS result;

    IF counter > 0 THEN
        RAISE 'Duplicate resource id: %', NEW.resource_id USING ERRCODE = 'unique_violation';
    END IF;
    RETURN NULL;
END; $$;

---

-- DATABASE FEATURE - stored procedure, conditionals, procedural language
CREATE PROCEDURE internal.create_book(
    title text,
    series text,
    author_first_name text,
    author_last_name text,
    out book_id uuid,
    release_date date default CURRENT_DATE,
    description text default null,
    cover_filepath text default null
)
LANGUAGE plpgsql
AS $$
DECLARE
    author_id uuid;
    cover bytea;
    cover_mediatype text;
BEGIN
    SELECT id INTO author_id
    FROM author
    WHERE first_name=author_first_name AND last_name=author_last_name;

    IF found IS FALSE THEN
        INSERT INTO author(id, first_name, last_name)
        VALUES (gen_random_uuid(), author_first_name, author_last_name)
        RETURNING id INTO author_id;
    END IF;

    IF series IS NOT NULL THEN
        INSERT INTO series VALUES (series) ON CONFLICT DO NOTHING;
    END IF;

    book_id := gen_random_uuid();
    INSERT INTO resource(id, title, author, release_date, description, series) VALUES (book_id, title, author_id, release_date, description, series);
    INSERT INTO book VALUES (book_id, md5(random()::text));

    IF cover_filepath IS NOT NULL THEN
        cover_mediatype := get_image_type(cover_filepath);
        cover := pg_read_binary_file(cover_filepath);
        INSERT INTO cover VALUES (book_id, cover, cover_mediatype);
    END IF;
END; $$;

---

CREATE PROCEDURE internal.create_ebook(
    title text,
    series text,
    author_first_name text,
    author_last_name text,
    out ebook_id uuid,
    release_date date default CURRENT_DATE,
    description text default null,
    cover_filepath text default null,
    content_filepath text default null
)
LANGUAGE plpgsql
AS $$
DECLARE
    author_id uuid;
    cover bytea;
    cover_mediatype text;
    content bytea;
BEGIN
    SELECT id INTO author_id
    FROM author
    WHERE first_name=author_first_name AND last_name=author_last_name;

    IF found IS FALSE THEN
        INSERT INTO author(id, first_name, last_name)
        VALUES (gen_random_uuid(), author_first_name, author_last_name)
        RETURNING id INTO author_id;
    END IF;

    IF series IS NOT NULL THEN
        INSERT INTO series VALUES (series) ON CONFLICT DO NOTHING;
    END IF;

    ebook_id := gen_random_uuid();
    INSERT INTO resource(id, title, author, release_date, description, series) VALUES (ebook_id, title, author_id, release_date, description, series);

    IF content_filepath IS NULL THEN
        content := generate_binary(generate_between(1000, 10000));
        INSERT INTO ebook VALUES (ebook_id, content, 'PDF');
    ELSE
        content := pg_read_binary_file(content_filepath);
        INSERT INTO ebook VALUES (ebook_id, content, upper(get_file_extension(content_filepath))::ebook_format);
    END IF;

    IF cover_filepath IS NOT NULL THEN
        cover_mediatype := get_image_type(cover_filepath);
        cover := pg_read_binary_file(cover_filepath);
        INSERT INTO cover VALUES (ebook_id, cover, cover_mediatype);
    END IF;
END; $$;

---

-- DATABASE FEATURE - trigger
CREATE TRIGGER refresh_books_search_view
AFTER INSERT OR UPDATE OR DELETE ON book
EXECUTE FUNCTION refresh_view('books_search_view');

CREATE TRIGGER refresh_ebooks_search_view
AFTER INSERT OR UPDATE OR DELETE ON ebook
EXECUTE FUNCTION refresh_view('ebooks_search_view');

-- TODO could be refreshed on resource, author change

---

CREATE TRIGGER unique_book_resource_id_check
AFTER INSERT ON book
FOR EACH ROW
EXECUTE FUNCTION check_resource_id_uniqueness();

CREATE TRIGGER unique_ebook_resource_id_check
AFTER INSERT ON ebook
FOR EACH ROW
EXECUTE FUNCTION check_resource_id_uniqueness();

---

CREATE FUNCTION get_penalty()
RETURNS decimal
SECURITY DEFINER
LANGUAGE sql
AS $$
    SELECT (settings -> 'penalty_rate')::decimal FROM internal.config;
$$;

---

CREATE FUNCTION get_time()
RETURNS timestamp
SECURITY DEFINER
LANGUAGE sql
AS $$
    SELECT (settings -> 'mocked_time')::timestamp FROM internal.config;
$$;

---

CREATE PROCEDURE update_penalties()
LANGUAGE plpgsql
AS $$
DECLARE
BEGIN
    UPDATE rental
    SET penalty = COALESCE(penalty, 0) + get_penalty(), status = 'PROLONGED'
    WHERE (status = 'ACTIVE' OR status = 'PROLONGED')
      AND finish < get_time()
      AND rental.resource_id IN (SELECT book.resource_id FROM book WHERE rental.resource_id = book.resource_id);
END;
$$;

---

CREATE PROCEDURE revoke_awaiting_resources()
LANGUAGE plpgsql
AS $$
DECLARE
BEGIN
    UPDATE rental
    SET status = 'CANCELLED'
    WHERE status = 'RESERVED_TO_BORROW' AND finish < get_time();
END;
$$;

CREATE PROCEDURE revoke_ebooks()
LANGUAGE plpgsql
AS $$
DECLARE
BEGIN
    UPDATE rental
    SET status = 'FINISHED'
    WHERE status = 'ACTIVE'
      AND finish < get_time()
      AND rental.resource_id IN (SELECT ebook.resource_id FROM ebook WHERE rental.resource_id = ebook.resource_id);
END;
$$;

CREATE PROCEDURE internal.activate_resource(resource_id uuid)
LANGUAGE plpgsql
AS $$
DECLARE
BEGIN
    UPDATE resource
    SET status = 'AVAILABLE'
    WHERE id = resource_id;
END;
$$;

---

CREATE PROCEDURE internal.deactivate_resource(resource_id uuid)
LANGUAGE plpgsql
AS $$
DECLARE
BEGIN
    UPDATE resource
    SET status = 'WITHDRAWN'
    WHERE id = resource_id;
END;
$$;

---

-- DATABASE FEATURE - subquery
CREATE PROCEDURE internal.grant_librarian(u_id uuid, lib_id uuid)
LANGUAGE plpgsql
AS $$
DECLARE
BEGIN
    INSERT INTO librarian
    VALUES (
        u_id,
        lib_id,
        (
            SELECT
            CASE count(*)
                WHEN 0 THEN true
                ELSE false
            END
            FROM librarian l
            WHERE l.user_id = u_id
        )
    );
END;
$$;
