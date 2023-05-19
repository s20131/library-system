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
    DECLARE library_id_1 uuid;
    DECLARE library_id_2 uuid;
    DECLARE library_id_3 uuid;
    DECLARE author_id uuid;
    DECLARE book_id uuid;
    DECLARE book_loop_id uuid;
    DECLARE ebook_id uuid;
    DECLARE user_id uuid;
BEGIN
    library_id_1 := gen_random_uuid();
    library_id_2 := gen_random_uuid();
    library_id_3 := gen_random_uuid();
    author_id := gen_random_uuid();
    ebook_id := gen_random_uuid();
    user_id := uuid('45d859ad-d03d-4a3b-ba59-31ae20a99992');

    INSERT INTO library VALUES (library_id_1, 'Biblioteka Publiczna w Dzielnicy Wesoła - Filia Nr 1', 'Jana Pawła II', '25', '05-077', 'Warszawa', st_point(21.23889248476246, 52.21644380014136));
    INSERT INTO library VALUES (library_id_2, 'Biblioteka Dzielnicowa Warszawa Wesoła', '1 Praskiego Pułku', '31', '05-075', 'Warszawa', st_point(21.22411093615883, 52.24838074876337));
    INSERT INTO library VALUES (library_id_3, 'Biblioteka Uniwersytecka w Warszawie', 'Dobra', '56/66', '00-312', 'Warszawa', st_point(21.02521989189416, 52.24268951970279));

    INSERT INTO author VALUES (author_id, 'Andrzej', 'Sapkowski');
    book_id := create_book('Wieża Jaskółki', 'Wiedźmin', 'Andrzej', 'Sapkowski');

    INSERT INTO resource(id, title, author, release_date, series) VALUES (ebook_id, 'Pani Jeziora', author_id, CURRENT_DATE, 'Wiedźmin');
    INSERT INTO ebook VALUES (ebook_id, md5(random()::text)::bytea, 'EPUB', 25.123, 'kB');

    FOR i in 1..5 LOOP
        book_loop_id := create_book(title => 'Book ' || i, series => 'Series X', author_first_name => 'John', author_last_name => 'Doe');
        IF i = 1 THEN
            INSERT INTO copy VALUES (library_id_1, book_loop_id, 0);
            CONTINUE;
        END IF;
        INSERT INTO copy VALUES (library_id_1, book_loop_id, 2);
    END LOOP;

    INSERT INTO copy VALUES (library_id_1, ebook_id, 3);
    INSERT INTO copy VALUES (library_id_2, ebook_id, 2);
    INSERT INTO copy VALUES (library_id_3, ebook_id, 1);
    INSERT INTO copy VALUES (library_id_1, book_id, 3);
    INSERT INTO copy VALUES (library_id_3, book_id, 0);

    INSERT INTO "user" VALUES (user_id, 'Jane', 'Doe', 'jane.doe@gmail.com', 'janedoe', '$2a$12$ca76zYFD.OyRZYIwEmnlxO2FMBipevX0dB/r.ga2eZ21lgCWNsTj6'); --abc123!#
    INSERT INTO user_settings VALUES (user_id, true, false, 'kindle123@kindle.com');

    INSERT INTO storage VALUES (user_id, book_id, now());
    INSERT INTO storage VALUES (user_id, ebook_id, now() - interval '1 day');

    INSERT INTO rental VALUES (gen_random_uuid(), user_id, ebook_id, library_id_1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + interval '14 days', 'ACTIVE', null);
    INSERT INTO rental VALUES (gen_random_uuid(), user_id, book_id, library_id_1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + interval '2 days', 'RESERVED_TO_BORROW', null);
END; $$;
