-- DATAABSE FEATURE - TRANSACTION, FOR-LOOP
DO $$
    DECLARE library_id_1 uuid;
    DECLARE library_id_2 uuid;
    DECLARE library_id_3 uuid;
    DECLARE book_id_1 uuid;
    DECLARE book_id_2 uuid;
    DECLARE book_loop_id uuid;
    DECLARE ebook_id_1 uuid;
    DECLARE ebook_id_2 uuid;
    DECLARE user_id uuid;
BEGIN
    library_id_1 := gen_random_uuid();
    library_id_2 := gen_random_uuid();
    library_id_3 := gen_random_uuid();
    user_id := uuid('45d859ad-d03d-4a3b-ba59-31ae20a99992');

    INSERT INTO library VALUES (library_id_1, 'Biblioteka Publiczna w Dzielnicy Wesoła - Filia Nr 1', 'Jana Pawła II', '25', '05-077', 'Warszawa', st_flipcoordinates(st_point(52.21644380014136, 21.23889248476246)));
    INSERT INTO library VALUES (library_id_2, 'Biblioteka Dzielnicowa Warszawa Wesoła', '1 Praskiego Pułku', '31', '05-075', 'Warszawa', st_flipcoordinates(st_point(52.24838074876337, 21.22411093615883)));
    INSERT INTO library VALUES (library_id_3, 'Biblioteka Uniwersytecka w Warszawie', 'Dobra', '56/66', '00-312', 'Warszawa', st_flipcoordinates(st_point(52.24268951970279, 21.02521989189416)));

    CALL create_book('Wieża Jaskółki', 'Wiedźmin', 'Andrzej', 'Sapkowski', book_id_1, '1997-06-27', 'Powieść z gatunku fantasy, napisana przez Andrzeja Sapkowskiego, wydana w 1997. Jest czwartą z pięciu części sagi o wiedźminie tego autora.', '/covers/wieza-jaskolki.jpeg');
    CALL create_book('Gra o tron', 'Pieśń Lodu i Ognia', 'George R. R.', 'Martin', book_id_2, '1996-08-01', 'Powieść z gatunku fantasy, pierwszy tom sagi Pieśń lodu i ognia George’a R.R. Martina. Pierwsze wydanie w języku angielskim miało premierę 1 sierpnia 1996 roku[1]. Polski przekład ukazał się w roku 1998 nakładem wydawnictwa Zysk i S-ka. Akcja książki toczy się w fikcyjnym świecie na kontynentach Westeros i Essos, gdzie pory roku mogą trwać wiele lat. Na podstawie powieści powstał serial Gra o tron realizowany przez telewizję HBO, a także gra fabularna, gra planszowa oraz gry komputerowe.', '/covers/gra-o-tron.webp');
    CALL create_ebook('Pani Jeziora', 'Wiedźmin', 'Andrzej', 'Sapkowski', ebook_id_1, '1999-03-15', 'Powieść z gatunku fantasy, napisana przez Andrzeja Sapkowskiego, wydana w 1999. Jest ostatnią z pięciu części sagi o wiedźminie.', '/covers/pani-jeziora.jpeg');
    CALL create_ebook('Gra o tron', 'Pieśń Lodu i Ognia', 'George R. R.', 'Martin', ebook_id_2, '1996-08-01', 'Powieść z gatunku fantasy, pierwszy tom sagi Pieśń lodu i ognia George’a R.R. Martina. Pierwsze wydanie w języku angielskim miało premierę 1 sierpnia 1996 roku[1]. Polski przekład ukazał się w roku 1998 nakładem wydawnictwa Zysk i S-ka. Akcja książki toczy się w fikcyjnym świecie na kontynentach Westeros i Essos, gdzie pory roku mogą trwać wiele lat. Na podstawie powieści powstał serial Gra o tron realizowany przez telewizję HBO, a także gra fabularna, gra planszowa oraz gry komputerowe.', '/covers/gra-o-tron.webp');

    FOR i in 1..5 LOOP
        CALL create_book(title => 'Book ' || i, series => 'Series X', author_first_name => 'John', author_last_name => 'Doe', book_id => book_loop_id);
        IF i % 2 = 0 THEN
            INSERT INTO copy VALUES (library_id_1, book_loop_id, 0);
            CONTINUE;
        END IF;
        INSERT INTO copy VALUES (library_id_1, book_loop_id, 2);
    END LOOP;

    INSERT INTO copy VALUES (library_id_1, ebook_id_1, 3);
    INSERT INTO copy VALUES (library_id_2, ebook_id_1, 2);
    INSERT INTO copy VALUES (library_id_3, ebook_id_1, 1);
    INSERT INTO copy VALUES (library_id_1, book_id_1, 3);
    INSERT INTO copy VALUES (library_id_3, book_id_1, 0);
    INSERT INTO copy VALUES (library_id_2, book_id_2, 4);
    INSERT INTO copy VALUES (library_id_3, book_id_2, 5);

    INSERT INTO "user" VALUES (user_id, 'Jane', 'Doe', 'jane.doe@gmail.com', 'janedoe', '$2a$12$ca76zYFD.OyRZYIwEmnlxO2FMBipevX0dB/r.ga2eZ21lgCWNsTj6'); --abc123!#
    INSERT INTO user_settings VALUES (user_id, true, false, 'kindle123@kindle.com');

    INSERT INTO storage VALUES (user_id, book_id_1, now());
    INSERT INTO storage VALUES (user_id, ebook_id_1, now() - interval '1 day');

    INSERT INTO rental VALUES (gen_random_uuid(), user_id, ebook_id_1, library_id_1, CURRENT_TIMESTAMP - interval '2 days', CURRENT_TIMESTAMP + interval '12 days', 'ACTIVE', null);
    INSERT INTO rental VALUES (gen_random_uuid(), user_id, book_id_1, library_id_1, CURRENT_TIMESTAMP - interval '1 days', CURRENT_TIMESTAMP + interval '1 days', 'RESERVED_TO_BORROW', null);
    INSERT INTO rental VALUES (gen_random_uuid(), user_id, book_loop_id, library_id_1, CURRENT_TIMESTAMP - interval '21 days', CURRENT_TIMESTAMP - interval '7 days', 'PROLONGED', 7 * 2.50);
END; $$;
