CREATE FUNCTION create_book(title text, series text, author_first_name text, author_last_name text, release_date date default CURRENT_DATE)
RETURNS uuid
LANGUAGE plpgsql
AS $$
DECLARE
    author_id uuid;
    book_id uuid;
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
    INSERT INTO resource(id, title, author, release_date, series) VALUES (book_id, title, author_id, release_date, series);
    INSERT INTO book VALUES (book_id, md5(random()::text));
    RETURN book_id;
END; $$;


DO $$
    DECLARE library_id uuid;
    DECLARE author_id uuid;
    DECLARE book_id uuid;
    DECLARE ebook_id uuid;
    DECLARE user_id uuid;
BEGIN
    library_id := gen_random_uuid();
    author_id := gen_random_uuid();
    ebook_id := gen_random_uuid();
    user_id := uuid('45d859ad-d03d-4a3b-ba59-31ae20a99992');

    INSERT INTO library VALUES (library_id, 'Biblioteka Publiczna - 1', 'Ulica', '1', '01-011', 'Warszawa', st_point(52.21644380014136, 21.23889248476246));
    INSERT INTO library VALUES (gen_random_uuid(), 'Biblioteka Publiczna - 2', 'Ulica', '2', '02-022', 'Warszawa', st_point(52.25069576608673, 21.222656961936707));
    INSERT INTO library VALUES (gen_random_uuid(), 'Biblioteka Publiczna - 3', 'Ulica', '3', '03-033', 'Warszawa', st_point(52.24268951970279, 21.02521989189416));

    INSERT INTO author VALUES (author_id, 'Andrzej', 'Sapkowski');
    book_id := create_book('Wieża Jaskółki', 'Wiedźmin', 'Andrzej', 'Sapkowski');

    INSERT INTO resource(id, title, author, release_date, series) VALUES (ebook_id, 'Pani Jeziora', author_id, CURRENT_DATE, 'Wiedźmin');
    INSERT INTO ebook VALUES (ebook_id, md5(random()::text)::bytea, 'EPUB', 25.123, 'kB');

    FOR i in 1..10 LOOP
        PERFORM create_book(title => 'Book ' || i, series => 'Series X', author_first_name => 'John', author_last_name => 'Doe');
    END LOOP;

    INSERT INTO copy (library_id, resource_id, available) VALUES (library_id, ebook_id, 3);
    INSERT INTO copy (library_id, resource_id, available) VALUES (library_id, book_id, 3);

    INSERT INTO "user" VALUES (user_id, 'Jane', 'Doe', 'jane.doe@gmail.com', 'janedoe', '$2a$12$ca76zYFD.OyRZYIwEmnlxO2FMBipevX0dB/r.ga2eZ21lgCWNsTj6'); --abc123!#
    INSERT INTO user_settings VALUES (user_id, true, false, 'kindle123@kindle.com');

    INSERT INTO storage (user_id, resource_id, since) VALUES (user_id, book_id, now());
    INSERT INTO storage (user_id, resource_id, since) VALUES (user_id, ebook_id, now() - INTERVAL '1 DAY');
END; $$;
