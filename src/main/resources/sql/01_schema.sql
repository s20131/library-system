-- DATABASE FEATURE - ENUM
CREATE TYPE resource_status AS ENUM ('WITHDRAWN', 'AVAILABLE');

CREATE TYPE ebook_format AS ENUM ('PDF', 'EPUB');

CREATE TABLE rental_status (
    name TEXT  PRIMARY KEY
);

INSERT INTO rental_status VALUES ('ACTIVE'), ('RESERVED_TO_BORROW'), ('PROLONGED'), ('CANCELED'), ('FINISHED');

CREATE TABLE series (
    name TEXT  PRIMARY KEY
);

-- DATABASE FEATURE - POSTGIS, CHECK CONSTRAINT
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
    id                UUID  NOT NULL,
    title             TEXT  NOT NULL,
    author            UUID  NOT NULL,
    release_date      DATE  NOT NULL,
    description       TEXT          ,
    series            TEXT          ,
    status RESOURCE_STATUS  NOT NULL DEFAULT 'AVAILABLE',

    PRIMARY KEY (id),
    FOREIGN KEY (author) REFERENCES author,
    FOREIGN KEY (series) REFERENCES series
);

-- DATABASE FEATURE - BLOB
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

-- DATABASE FEATURE - SEQUENCE
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

-- DATABASE FEATURE - CONDITIONAL UNIQUE CONSTRAINT
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
    FOREIGN KEY (status) REFERENCES rental_status
);

--TODO is such PK (less strict than dependencies) ok?
CREATE TABLE reservation (
    user_id     UUID  NOT NULL,
    resource_id UUID  NOT NULL,
    library_id  UUID  NOT NULL,
    start  TIMESTAMP  NOT NULL,
    finish TIMESTAMP  NOT NULL,

    -- TODO is such PK enough? the data would be only upserted
    PRIMARY KEY (user_id, resource_id),
    FOREIGN KEY (user_id) REFERENCES "user",
    FOREIGN KEY (resource_id, library_id) REFERENCES copy (resource_id, library_id)
);

---

-- TODO polish dictionary?
-- DATABASE FEATURE - MATERIALIZED VIEW, FULL-TEXT SEARCH
CREATE MATERIALIZED VIEW books_search_view AS SELECT r.*, b.isbn, to_tsvector(concat_ws(', ', r.title, r.description, r.series, b.isbn, a.first_name, a.last_name)) AS tokens
FROM resource r
INNER JOIN book b ON r.id = b.resource_id
INNER JOIN author a on r.author = a.id;

-- DATABASE FEATURE - INDEX
CREATE UNIQUE INDEX books_search_view_idx ON books_search_view (id);

---

CREATE MATERIALIZED VIEW ebooks_search_view AS SELECT r.*, e.content, e.format, to_tsvector(concat_ws(', ', r.title, r.description, r.series, e.format, a.first_name, a.last_name)) AS tokens
FROM resource r
INNER JOIN ebook e ON r.id = e.resource_id
INNER JOIN author a on r.author = a.id;

CREATE UNIQUE INDEX ebooks_search_view_idx ON ebooks_search_view (id);

---

-- TODO privileges + schema
CREATE USER spring_app;
