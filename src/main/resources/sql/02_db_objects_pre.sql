-- DATABASE FEATURE - FUNCTION, PROCEDURAL LANGUAGE
CREATE FUNCTION get_image_type(filename text)
RETURNS text
LANGUAGE plpgsql
IMMUTABLE
AS $$
BEGIN
   RETURN concat('image/', substring(filename FROM '\w+$'));
END; $$;

---

CREATE FUNCTION bytes_to_kB(bytes int)
RETURNS DECIMAL
LANGUAGE plpgsql
IMMUTABLE
AS $$
BEGIN
    RETURN bytes / 1024;
END; $$;

---

-- DATABASE FEATURE - DYNAMIC SQL
CREATE FUNCTION refresh_view()
    RETURNS TRIGGER
    LANGUAGE plpgsql
AS $$
DECLARE
    view_name TEXT := tg_argv[0];
BEGIN
    EXECUTE format('REFRESH MATERIALIZED VIEW CONCURRENTLY ' || view_name || ';');
    RETURN NULL;
END; $$;

-- DATABASE FEATURE - STORED PROCEDURE, CONDITIONALS
CREATE PROCEDURE create_book(
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

    INSERT INTO series VALUES (series) ON CONFLICT DO NOTHING;

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

CREATE PROCEDURE create_ebook(
    title text,
    series text,
    author_first_name text,
    author_last_name text,
    out ebook_id uuid,
    release_date date default CURRENT_DATE,
    description text default null,
    cover_filepath text default null,
    content bytea default md5(random()::text)::bytea
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

    INSERT INTO series VALUES (series) ON CONFLICT DO NOTHING;

    ebook_id := gen_random_uuid();
    INSERT INTO resource(id, title, author, release_date, description, series) VALUES (ebook_id, title, author_id, release_date, description, series);
    INSERT INTO ebook VALUES (ebook_id, content, 'EPUB', pg_column_size(content), 'kB');

    IF cover_filepath IS NOT NULL THEN
        cover_mediatype := get_image_type(cover_filepath);
        cover := pg_read_binary_file(cover_filepath);
        INSERT INTO cover VALUES (ebook_id, cover, cover_mediatype);
    END IF;
END; $$;

---

-- DATABASE FEATURE - TRIGGER
CREATE TRIGGER refresh_books_search_view
AFTER INSERT OR UPDATE OR DELETE ON book
EXECUTE FUNCTION refresh_view('books_search_view');

CREATE TRIGGER refresh_ebooks_search_view
AFTER INSERT OR UPDATE OR DELETE ON ebook
EXECUTE FUNCTION refresh_view('ebooks_search_view');