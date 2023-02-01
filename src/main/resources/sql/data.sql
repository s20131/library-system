INSERT INTO library VALUES (gen_random_uuid(), 'Biblioteka Publiczna Wesoła - 1', 'Ulica', '1', '05-077', 'Warszawa'),
                           (gen_random_uuid(), 'Biblioteka Publiczna Wesoła - 2', 'Ulica', '2a', '05-077', 'Warszawa'),
                           (gen_random_uuid(), 'Biblioteka Publiczna Wesoła - 4', 'Ulica', '3b', '05-077', 'Warszawa');

DO '
    --DECLARE sapkowski_id UUID;
    DECLARE wiedzmin_id UUID;
    DECLARE user_id UUID;
BEGIN
    --sapkowski_id := gen_random_uuid();
    wiedzmin_id := gen_random_uuid();
    user_id := gen_random_uuid();

    --INSERT INTO author VALUES (sapkowski_id, ''Andrzej'', ''Sapkowski'');
    INSERT INTO series VALUES (''Wiedźmin'');
    INSERT INTO resource(id, title/*, author*/, release_date, series) VALUES (wiedzmin_id, ''Wieźa Jaskółki''/*, sapkowski_id*/, CURRENT_DATE, ''Wiedźmin'');
    INSERT INTO book VALUES (wiedzmin_id, md5(random()::text));
    INSERT INTO "user" VALUES (user_id, ''John'', ''Doe'', ''john.doe@gmail.com'', ''johndoe'', ''abc123'');
    INSERT INTO user_settings VALUES (user_id, true, false, ''kindle123@kindle.com'');
END;'
