CREATE INDEX available_resources_idx ON resource(id, title, author, release_date, description, series) WHERE status = 'AVAILABLE';

CREATE INDEX author_covering_idx ON author(first_name, last_name) INCLUDE (id);

-- CREATE INDEX book_isbn_hash_idx ON book USING HASH(isbn); -- only example as this is already unique column

CREATE INDEX settings_idx ON internal.config USING GIST(settings);

-- CREATE INDEX library_card_idx ON library_card USING BRIN(number); -- only example as this is already unique column