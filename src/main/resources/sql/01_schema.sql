CREATE EXTENSION hstore;

REVOKE CREATE ON SCHEMA public FROM public; -- unnecessary as it's turned off by default since postgres 15
-- DATABASE FEATURE - psql variable
REVOKE ALL ON DATABASE :DBNAME FROM public;

CREATE SCHEMA app;
-- DATABASE FEATURE - set configuration value
ALTER DATABASE :DBNAME SET search_path TO app, public, topology, tiger;
SET search_path TO app, public; -- needs to be repeated as the above doesn't apply to the current session
ALTER DATABASE :DBNAME SET timezone TO 'Europe/Warsaw';

-- DATABASE FEATURE - enum
CREATE TYPE ebook_format AS ENUM ('PDF', 'EPUB');

CREATE TABLE rental_status (
    name TEXT  PRIMARY KEY
);

INSERT INTO rental_status VALUES ('ACTIVE'), ('RESERVED_TO_BORROW'), ('PROLONGED'), ('CANCELLED'), ('FINISHED');

CREATE TABLE resource_status (
    name TEXT  PRIMARY KEY
);

INSERT INTO resource_status VALUES ('AVAILABLE'), ('WITHDRAWN'), ('WAITING_FOR_APPROVAL');

CREATE TABLE series (
    name TEXT  PRIMARY KEY
);

-- DATABASE FEATURE - postgis
CREATE TABLE library (
    id                        UUID  NOT NULL,
    name                      TEXT  NOT NULL,
    street_name               TEXT  NOT NULL,
    street_number             TEXT  NOT NULL,
    postcode                  TEXT  NOT NULL,
    city                      TEXT  NOT NULL,
    location GEOMETRY(Point, 4326)  NOT NULL,

    PRIMARY KEY (id),
    CHECK (postcode ~ '^\d{2}-\d{3}$')
);

CREATE TABLE author (
    id         UUID  NOT NULL,
    first_name TEXT  NOT NULL,
    last_name  TEXT  NOT NULL,

    PRIMARY KEY (id)
);

CREATE TABLE resource (
    id           UUID  NOT NULL,
    title        TEXT  NOT NULL,
    author       UUID  NOT NULL,
    release_date DATE  NOT NULL,
    description  TEXT          ,
    series       TEXT          ,
    status       TEXT  NOT NULL DEFAULT 'AVAILABLE',

    PRIMARY KEY (id),
    FOREIGN KEY (author) REFERENCES author,
    FOREIGN KEY (series) REFERENCES series,
    FOREIGN KEY (status) REFERENCES resource_status
);

-- DATABASE FEATURE - blob
CREATE TABLE cover (
    id         UUID  NOT NULL,
    content   BYTEA  NOT NULL,
    media_type TEXT  NOT NULL,

    PRIMARY KEY (id),
    FOREIGN KEY (id) REFERENCES resource
);

CREATE TABLE book (
    resource_id UUID  NOT NULL,
    isbn        TEXT  NOT NULL,

    UNIQUE (isbn),
    PRIMARY KEY (resource_id),
    FOREIGN KEY (resource_id) REFERENCES resource
);

CREATE TABLE ebook (
    resource_id    UUID  NOT NULL,
    content       BYTEA  NOT NULL,
    format EBOOK_FORMAT  NOT NULL,

    PRIMARY KEY (resource_id, format),
    FOREIGN KEY (resource_id) REFERENCES resource
);

CREATE TABLE copy (
    library_id  UUID  NOT NULL,
    resource_id UUID  NOT NULL,
    available    INT  NOT NULL,

    PRIMARY KEY (library_id, resource_id),
    FOREIGN KEY (library_id) REFERENCES library,
    FOREIGN KEY (resource_id) REFERENCES resource,

    CHECK (available >= 0)
);

CREATE TABLE "user" (
    id         UUID  NOT NULL,
    first_name TEXT  NOT NULL,
    last_name  TEXT  NOT NULL,
    email      TEXT  NOT NULL,
    username   TEXT  NOT NULL,
    password   TEXT  NOT NULL,

    UNIQUE (email),
    UNIQUE (username),
    PRIMARY KEY (id)
);

CREATE TABLE user_settings (
    user_id                         UUID  NOT NULL,
    send_end_of_rental_reminder  BOOLEAN  NOT NULL,
    send_when_available_reminder BOOLEAN  NOT NULL,
    kindle_email                    TEXT          ,

    PRIMARY KEY (user_id),
    FOREIGN KEY (user_id) REFERENCES "user"
);

-- DATABASE FEATURE - sequence
CREATE SEQUENCE library_card_seq MINVALUE 1000;

CREATE TABLE library_card (
    number     BIGINT  NOT NULL DEFAULT nextval('library_card_seq'),
    user_id      UUID  NOT NULL,
  --qr_code     BYTEA  NOT NULL,
    expiration   DATE  NOT NULL,
    is_active BOOLEAN  NOT NULL,

    PRIMARY KEY (number),
    FOREIGN KEY (user_id) REFERENCES "user"
);

-- DATABASE FEATURE - conditionally unique constraint
CREATE UNIQUE INDEX active_card_unique_idx ON library_card (user_id, is_active) WHERE is_active = true;

CREATE TABLE librarian (
    user_id     UUID  NOT NULL,
    library_id  UUID  NOT NULL,
    is_selected BOOL  NOT NULL,

    PRIMARY KEY (user_id, library_id),
    FOREIGN KEY (user_id) REFERENCES "user",
    FOREIGN KEY (library_id) REFERENCES library
);

CREATE UNIQUE INDEX selected_library_unique_idx ON librarian (user_id, is_selected) WHERE is_selected = true;

CREATE TABLE storage (
    user_id     UUID  NOT NULL,
    resource_id UUID  NOT NULL,
    since  TIMESTAMP  NOT NULL,

    PRIMARY KEY (user_id, resource_id),
    FOREIGN KEY (user_id) REFERENCES "user",
    FOREIGN KEY (resource_id) REFERENCES resource
);

-- DATABASE FEATURE - check constraint
CREATE TABLE rental (
    id                    UUID  NOT NULL,
    user_id               UUID  NOT NULL,
    resource_id           UUID  NOT NULL,
    library_id            UUID  NOT NULL,
    start            TIMESTAMP  NOT NULL,
    finish           TIMESTAMP  NOT NULL,
    status                TEXT  NOT NULL,
    penalty      DECIMAL(10,2)          ,

    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES "user",
    FOREIGN KEY (resource_id, library_id) REFERENCES copy (resource_id, library_id),
    FOREIGN KEY (status) REFERENCES rental_status,

    CHECK (
        CASE
            WHEN status = 'PROLONGED' THEN penalty IS NOT NULL
            WHEN status = 'FINISHED' THEN penalty IS NULL OR penalty IS NOT NULL
            ELSE penalty IS NULL
        END
    )
);

CREATE TABLE reservation (
    user_id     UUID  NOT NULL,
    resource_id UUID  NOT NULL,
    library_id  UUID  NOT NULL,
    start  TIMESTAMP  NOT NULL,
    finish TIMESTAMP  NOT NULL,

    -- TODO is such PK enough? less strict than dependencies, the data would be only upserted
    PRIMARY KEY (user_id, resource_id),
    FOREIGN KEY (user_id) REFERENCES "user",
    FOREIGN KEY (resource_id, library_id) REFERENCES copy (resource_id, library_id)
);

---

-- DATABASE FEATURE - materialized view, full-text search
CREATE MATERIALIZED VIEW books_search_view AS SELECT r.*, b.isbn, to_tsvector(concat_ws(', ', r.title, r.description, r.series, b.isbn, a.first_name, a.last_name)) AS tokens
FROM resource r
INNER JOIN book b ON r.id = b.resource_id
INNER JOIN author a on r.author = a.id;

-- DATABASE FEATURE - index
CREATE UNIQUE INDEX books_search_view_idx ON books_search_view (id);

---

CREATE MATERIALIZED VIEW ebooks_search_view AS SELECT r.*, e.content, e.format, to_tsvector(concat_ws(', ', r.title, r.description, r.series, e.format, a.first_name, a.last_name)) AS tokens
FROM resource r
INNER JOIN ebook e ON r.id = e.resource_id
INNER JOIN author a on r.author = a.id;

CREATE UNIQUE INDEX ebooks_search_view_idx ON ebooks_search_view (id);

---

-- DATABASE FEATURE - user + privileges
CREATE USER spring_app WITH PASSWORD 'test';
GRANT CONNECT ON DATABASE :DBNAME TO spring_app;
GRANT USAGE ON SCHEMA app TO spring_app;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA app TO spring_app;
ALTER DEFAULT PRIVILEGES FOR USER spring_app IN SCHEMA app GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO spring_app;
GRANT USAGE ON ALL SEQUENCES IN SCHEMA app TO spring_app;
ALTER DEFAULT PRIVILEGES IN SCHEMA app GRANT USAGE ON SEQUENCES TO spring_app;
GRANT EXECUTE ON ALL FUNCTIONS IN SCHEMA app TO spring_app;

-- DATABASE FEATURE - schema
CREATE SCHEMA internal;
CREATE TABLE internal.config (
    settings hstore
);

-- DATABASE FEATURE - key-value pairs
INSERT INTO internal.config VALUES ('penalty_rate => 2.50');
UPDATE internal.config SET settings['mocked_time'] = 'now()';
