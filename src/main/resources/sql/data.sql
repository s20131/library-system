CREATE PROCEDURE create_book(title text, series text, author_first_name text, author_last_name text, release_date date default CURRENT_DATE)
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
END; $$;


DO $$
    DECLARE library_id uuid;
    DECLARE author_id uuid;
    DECLARE ebook_id uuid;
    DECLARE user_id uuid;
BEGIN
    library_id := gen_random_uuid();
    author_id := gen_random_uuid();
    ebook_id := gen_random_uuid();
    user_id := gen_random_uuid();

    INSERT INTO library VALUES (library_id, 'Biblioteka Publiczna Wesoła - 1', 'Ulica', '1', '05-077', 'Warszawa');

    INSERT INTO author VALUES (author_id, 'Andrzej', 'Sapkowski');
    CALL create_book('Wieża Jaskółki', 'Wiedźmin', 'Andrzej', 'Sapkowski');

    INSERT INTO resource(id, title, author, release_date, series) VALUES (ebook_id, 'Pani Jeziora', author_id, CURRENT_DATE, 'Wiedźmin');
    INSERT INTO ebook VALUES (ebook_id, md5(random()::text)::bytea, 'EPUB', 25.123, 'kB');

    FOR i in 1..10 LOOP
        CALL create_book('Book ' || i, 'Series X', 'John', 'Doe');
    END LOOP;

    INSERT INTO copy (id, available, library_id, resource_id) VALUES (gen_random_uuid(), 3, library_id, ebook_id);

    INSERT INTO "user" VALUES (user_id, 'Jane', 'Doe', 'jane.doe@gmail.com', 'janedoe', '$2a$10$buxxCLdRTA3gPwZfLQS7NO921gcMhjRqZGyTaBDFqTQf9HKV3mizy'); --abc123
    INSERT INTO user_settings VALUES (user_id, true, false, 'kindle123@kindle.com');

    INSERT INTO storage (user_id, resource_id, added_at) VALUES (user_id, ebook_id, now());
END; $$;
