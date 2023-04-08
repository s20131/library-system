INSERT INTO library VALUES (gen_random_uuid(), 'Biblioteka Publiczna Wesoła - 1', 'Ulica', '1', '05-077', 'Warszawa'),
                           (gen_random_uuid(), 'Biblioteka Publiczna Wesoła - 2', 'Ulica', '2a', '05-077', 'Warszawa'),
                           (gen_random_uuid(), 'Biblioteka Publiczna Wesoła - 4', 'Ulica', '3b', '05-077', 'Warszawa');


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
    DECLARE author1_id uuid;
    DECLARE ebook1_id uuid;
    DECLARE user_id uuid;
BEGIN
    author1_id := gen_random_uuid();
    ebook1_id := gen_random_uuid();
    user_id := gen_random_uuid();

    INSERT INTO author VALUES (author1_id, 'Andrzej', 'Sapkowski');
    CALL create_book('Wieża Jaskółki', 'Wiedźmin', 'Andrzej', 'Sapkowski');
    INSERT INTO resource(id, title, author, release_date, series) VALUES (ebook1_id, 'Pani Jeziora', author1_id, CURRENT_DATE, 'Wiedźmin');
    INSERT INTO ebook VALUES (ebook1_id, md5(random()::text)::bytea, 'EPUB', 25.123, 'kB');
    INSERT INTO "user" VALUES (user_id, 'Jane', 'Doe', 'jane.doe@gmail.com', 'janedoe', 'abc123');
    INSERT INTO user_settings VALUES (user_id, true, false, 'kindle123@kindle.com');

    FOR i in 1..15 LOOP
        CALL create_book('Book ' || i, 'Series X', 'John', 'Doe');
    END LOOP;
END; $$;
