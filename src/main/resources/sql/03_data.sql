-- DATABASE FEATURE - transaction, for-loop
DO $$
    DECLARE library_id_1 uuid;
    DECLARE library_id_2 uuid;
    DECLARE library_id_3 uuid;
    DECLARE book_id_1 uuid;
    DECLARE book_id_2 uuid;
    DECLARE book_loop_id uuid;
    DECLARE ebook_id_1 uuid;
    DECLARE ebook_id_2 uuid;
    DECLARE ebook_id_3 uuid;
    DECLARE ebook_id_4 uuid;
    DECLARE user_id uuid;
    DECLARE librarian_id uuid;
BEGIN
    library_id_1 := gen_random_uuid();
    library_id_2 := gen_random_uuid();
    library_id_3 := gen_random_uuid();
    user_id := uuid('45d859ad-d03d-4a3b-ba59-31ae20a99992');
    librarian_id := uuid('59a2a4de-8864-41ec-a30d-6333472e066d');

    INSERT INTO library VALUES
    (library_id_1, 'Biblioteka Publiczna w Dzielnicy Wesoła - Filia Nr 1', 'Jana Pawła II', '25', '05-077', 'Warszawa', st_flipcoordinates(st_point(52.21644380014136, 21.23889248476246))),
    (library_id_2, 'Biblioteka Dzielnicowa Warszawa Wesoła', '1 Praskiego Pułku', '31', '05-075', 'Warszawa', st_flipcoordinates(st_point(52.24838074876337, 21.22411093615883))),
    (library_id_3, 'Biblioteka Uniwersytecka w Warszawie', 'Dobra', '56/66', '00-312', 'Warszawa', st_flipcoordinates(st_point(52.24268951970279, 21.02521989189416)));

    CALL internal.create_book('Wieża Jaskółki', 'Wiedźmin', 'Andrzej', 'Sapkowski', book_id_1, '1997-06-27', 'Powieść z gatunku fantasy, napisana przez Andrzeja Sapkowskiego, wydana w 1997. Jest czwartą z pięciu części sagi o wiedźminie tego autora.', '/covers/wieza-jaskolki.jpeg');
    CALL internal.create_book('Gra o tron', 'Pieśń Lodu i Ognia', 'George R. R.', 'Martin', book_id_2, '1996-08-01', 'Powieść z gatunku fantasy, pierwszy tom sagi Pieśń lodu i ognia George’a R.R. Martina. Pierwsze wydanie w języku angielskim miało premierę 1 sierpnia 1996 roku[1]. Polski przekład ukazał się w roku 1998 nakładem wydawnictwa Zysk i S-ka. Akcja książki toczy się w fikcyjnym świecie na kontynentach Westeros i Essos, gdzie pory roku mogą trwać wiele lat. Na podstawie powieści powstał serial Gra o tron realizowany przez telewizję HBO, a także gra fabularna, gra planszowa oraz gry komputerowe.', '/covers/gra-o-tron.webp');
    CALL internal.create_ebook('Pani Jeziora', 'Wiedźmin', 'Andrzej', 'Sapkowski', ebook_id_1, '1999-03-15', 'Powieść z gatunku fantasy, napisana przez Andrzeja Sapkowskiego, wydana w 1999. Jest ostatnią z pięciu części sagi o wiedźminie.', '/covers/pani-jeziora.jpeg');
    CALL internal.create_ebook('Gra o tron', 'Pieśń Lodu i Ognia', 'George R. R.', 'Martin', ebook_id_2, '1996-08-01', 'Powieść z gatunku fantasy, pierwszy tom sagi Pieśń lodu i ognia George’a R.R. Martina. Pierwsze wydanie w języku angielskim miało premierę 1 sierpnia 1996 roku[1]. Polski przekład ukazał się w roku 1998 nakładem wydawnictwa Zysk i S-ka. Akcja książki toczy się w fikcyjnym świecie na kontynentach Westeros i Essos, gdzie pory roku mogą trwać wiele lat. Na podstawie powieści powstał serial Gra o tron realizowany przez telewizję HBO, a także gra fabularna, gra planszowa oraz gry komputerowe.', '/covers/gra-o-tron.webp');
    CALL internal.create_ebook('Boska Komedia', null, 'Dante', 'Alighieri', ebook_id_3, '1321-01-01', E'Boska Komedia to dzieło życia włoskiego poety Dante Alighieriego, którym chciał zapewnić nieśmiertelność, oddać hołd swojej zmarłej ukochanej Beatrycze oraz ostrzec lud średniowieczny przed konsekwencjami grzesznego życia. Poemat tryptyk — składający się z trzech ksiąg — Piekła, Czyśćca i Raju, po których wędruje alter ego autora, oprowadzane przez Wergiliusza, św. Bernarda z Clairvaux oraz ukochaną Beatrycze. Alighieri nie tylko jako bohater, ale i jako narrator krytykuje zaświaty, będące alegorią życia doczesnego. W całym utworze wyraźnie widać fascynację cyframi, zwłaszcza 3, będącą symbolem Trójcy Świętej. Księga pierwsza, Piekło, charakteryzuje się drastycznymi opisami wydarzeń i właśnie dzięki niej mówi się o dantejskich scenach.', '/covers/boska-komedia.jpeg', '/ebooks/boska-komedia.epub');
    CALL internal.create_ebook('Wesele', null, 'Stanisław', 'Wyspiański', ebook_id_4, '1901-03-16', 'Wystawiony po raz pierwszy na krakowskiej scenie w 1901 r. dramat Stanisława Wyspiańskiego Wesele wywołał spore poruszenie, niemal skandal. Głównym powodem był fakt wprowadzenia na scenę autentycznych, rozpoznawalnych w środowisku postaci pod ich własnymi imionami oraz odwołanie do rzeczywistego wydarzenia, mianowicie wesela poety Lucjana Rydla z chłopką Jadwigą Mikołajczykówną, które miało miejsce zaledwie rok wcześniej. Jednakże w tekście dramatu znaleźć można więcej spraw niepokojących niż tylko pożywka do plotek towarzyskich. Wyspiański wybrał szczególne wesele, będące triumfem młodopolskiej chłopomanii i świętowaniem uroczystego zaślubienia elity społecznej z wsią w osobach państwa młodych. Potraktował to wesele jako okazję do konfrontacji różnych klas, grup społecznych, obyczajów i systemów wartości; okazuje się dzięki temu, co łączy księdza z arendarzem Żydem, a co dzieli, mimo pozorów zbratania, gospodarza weselnej chałupy, zanurzonego w inteligenckim świecie wyobrażeń, z jego chłopskimi sąsiadami. W trakcie zabawy, może wskutek spożycia znacznej ilości mocnych trunków, pojawiają się ni to duchy, ni to symboliczne uosobienia lęków i marzeń poszczególnych postaci. W efekcie uzyskany zostaje obraz przedstawiający słabość mitów narodowych, jednostronnych i niemających mocy jednoczącej; naród, który nie jest zdolny do walki o własne państwo; marazm społeczeństwa i uwiąd warstw tradycyjnie uznawanych za predestynowane do przywództwa.', '/covers/wesele.jpg', '/ebooks/wesele.pdf');

    FOR i in 1..2 LOOP
        CALL internal.create_book(title => 'Book ' || i, series => 'Series X', author_first_name => 'John', author_last_name => 'Doe', book_id => book_loop_id);
        INSERT INTO copy VALUES (library_id_1, book_loop_id, 2);
    END LOOP;
    FOR i in 3..4 LOOP
        CALL internal.create_book(title => 'Book ' || i, series => 'Series Y', author_first_name => 'John', author_last_name => 'Doe', book_id => book_loop_id);
        INSERT INTO copy VALUES (library_id_1, book_loop_id, 0);
    END LOOP;

    INSERT INTO copy VALUES (library_id_1, ebook_id_1, 3),
                            (library_id_1, ebook_id_3, 2),
                            (library_id_1, book_id_1,  3),
                            (library_id_2, ebook_id_1, 2),
                            (library_id_2, ebook_id_4, 1),
                            (library_id_2, book_id_2,  4),
                            (library_id_3, ebook_id_1, 1),
                            (library_id_3, book_id_1,  0),
                            (library_id_3, book_id_2,  5);

    INSERT INTO "user" VALUES
    (user_id, 'Jane', 'Doe', 'jane.doe@gmail.com', 'janedoe', '$2a$12$ca76zYFD.OyRZYIwEmnlxO2FMBipevX0dB/r.ga2eZ21lgCWNsTj6'), --abc123!#
    (librarian_id, 'Michael', 'Scott', 'michael.scott@gmail.com', 'michaels', '$2a$12$ca76zYFD.OyRZYIwEmnlxO2FMBipevX0dB/r.ga2eZ21lgCWNsTj6'); --abc123!#

    INSERT INTO user_settings VALUES (user_id, true, false, 'kindle123@kindle.com'),
                                     (librarian_id, false, false, null);

    INSERT INTO library_card (user_id, expiration, is_active) VALUES
    (user_id, CURRENT_TIMESTAMP + interval '365 days', true),
    (librarian_id, CURRENT_TIMESTAMP + interval '3 days', false),
    (librarian_id, CURRENT_TIMESTAMP + interval '365 days', true);

    INSERT INTO librarian VALUES (librarian_id, library_id_1, true),
                                 (librarian_id, library_id_2, false),
                                 (librarian_id, library_id_3, false);

    INSERT INTO storage VALUES (user_id, book_id_1, now()),
                               (user_id, ebook_id_1, now() - interval '1 day');

    INSERT INTO rental VALUES
    (gen_random_uuid(), user_id, ebook_id_1, library_id_1, CURRENT_TIMESTAMP - interval '2 days', CURRENT_TIMESTAMP + interval '12 days', 'ACTIVE', null),
    (gen_random_uuid(), user_id, book_id_1, library_id_1, CURRENT_TIMESTAMP - interval '1 days', CURRENT_TIMESTAMP + interval '1 days', 'RESERVED_TO_BORROW', null),
    (gen_random_uuid(), user_id, book_loop_id, library_id_1, CURRENT_TIMESTAMP - interval '21 days', CURRENT_TIMESTAMP - interval '7 days', 'PROLONGED', 7 * 2.50);
END; $$;
