DROP OWNED BY test;

CREATE TABLE library (
    id            UUID  NOT NULL,
    name          TEXT  NOT NULL,
    street_name   TEXT  NOT NULL,
    street_number TEXT  NOT NULL,
    postcode      TEXT  NOT NULL,
    city          TEXT  NOT NULL,
 -- location     POINT  NOT NULL,

    PRIMARY KEY (id),
    CHECK (postcode ~ '^\d{2}-\d{3}$')
);

CREATE TABLE "user" (
    id         UUID  NOT NULL,
    first_name TEXT  NOT NULL,
    last_name  TEXT  NOT NULL,
    email      TEXT  NOT NULL,
    username   TEXT  NOT NULL,
    password   TEXT  NOT NULL,

    PRIMARY KEY (id)
);

CREATE TABLE author (
    id         UUID  NOT NULL,
    first_name TEXT  NOT NULL,
    last_name  TEXT  NOT NULL,

    PRIMARY KEY (id)
);

CREATE TABLE series (
    id TEXT  NOT NULL,

    PRIMARY KEY (id)
);

CREATE TYPE resource_status AS ENUM ('WITHDRAWN', 'AVAILABLE');

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

CREATE TABLE book (
    resource_id UUID  NOT NULL,
    isbn        TEXT  NOT NULL,

    PRIMARY KEY (resource_id),
    FOREIGN KEY (resource_id) REFERENCES resource
);

CREATE TYPE unit AS ENUM ('PDF', 'MOBI', 'EPUB');

CREATE TABLE e_book (
    resource_id UUID  NOT NULL,
    format      TEXT  NOT NULL,
    content    BYTEA  NOT NULL,
    size     NUMERIC  NOT NULL,
    size_unit   UNIT  NOT NULL,

    PRIMARY KEY (resource_id, format),
    FOREIGN KEY (resource_id) REFERENCES resource
);

CREATE TABLE category (
    name TEXT  NOT NULL,

    PRIMARY KEY (name)
);

INSERT INTO category VALUES ('adventure'), ('fantasy'), ('crime'), ('romance');

CREATE TABLE resource_categories (
    resource_id UUID  NOT NULL,
    category    TEXT  NOT NULL,

    PRIMARY KEY (resource_id, category),
    FOREIGN KEY (resource_id) REFERENCES resource,
    FOREIGN KEY (category) REFERENCES category
);

CREATE TABLE copy (
    id          UUID  NOT NULL,
    available    INT  NOT NULL,
    library_id  UUID  NOT NULL,
    resource_id UUID  NOT NULL,

    PRIMARY KEY (id),
    FOREIGN KEY (library_id) REFERENCES library,
    FOREIGN KEY (resource_id) REFERENCES resource
);

CREATE TABLE cover (
    id         UUID  NOT NULL,
    content   BYTEA  NOT NULL,
    media_type TEXT  NOT NULL,

    PRIMARY KEY (id),
    FOREIGN KEY (id) REFERENCES resource
);

CREATE TYPE operation AS ENUM ('CREATE', 'UPDATE', 'DELETE');

CREATE TABLE e_book_audit (
    id            UUID  NOT NULL,
    e_book_id     UUID  NOT NULL,
    e_book_format TEXT  NOT NULL,
    who            INT  NOT NULL,
    what     OPERATION  NOT NULL,
    at       TIMESTAMP  NOT NULL,
    data         JSONB  NOT NULL,

    PRIMARY KEY (id),
    FOREIGN KEY (e_book_id, e_book_format) REFERENCES e_book (resource_id, format)
);

CREATE TABLE favourite_libraries (
    user_id    UUID  NOT NULL,
    library_id UUID  NOT NULL,

    PRIMARY KEY (user_id, library_id),
    FOREIGN KEY (user_id) REFERENCES "user",
    FOREIGN KEY (library_id) REFERENCES library
);

CREATE TABLE librarian (
    user_id    UUID  NOT NULL,
    library_id UUID  NOT NULL,

    PRIMARY KEY (user_id, library_id),
    FOREIGN KEY (user_id) REFERENCES "user",
    FOREIGN KEY (library_id) REFERENCES library
);

CREATE SEQUENCE library_card_seq MINVALUE 1000000000;

CREATE TABLE library_card (
    number     BIGINT  NOT NULL DEFAULT nextval('library_card_seq'),
    qr_code     BYTEA  NOT NULL,
    expiration   DATE  NOT NULL,
    is_active BOOLEAN  NOT NULL,
    user_id      UUID  NOT NULL,

    PRIMARY KEY (number),
    FOREIGN KEY (user_id) REFERENCES "user"
);

CREATE TYPE rent_status AS ENUM ('WAITING');

CREATE TABLE rental (
    id                INT  NOT NULL,
    user_id          UUID  NOT NULL,
    copy_id          UUID  NOT NULL,
    start       TIMESTAMP  NOT NULL,
    finish      TIMESTAMP  NOT NULL,
    status    RENT_STATUS  NOT NULL,
    penalty DECIMAL(10,2)          ,

    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES "user",
    FOREIGN KEY (copy_id) REFERENCES copy
);

CREATE TABLE reservation (
    id           INT  NOT NULL,
    user_id     UUID  NOT NULL,
    copy_id     UUID  NOT NULL,
    start  TIMESTAMP  NOT NULL,
    finish TIMESTAMP  NOT NULL,

    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES "user",
    FOREIGN KEY (copy_id) REFERENCES copy
);

CREATE TABLE settings (
    user_id                         UUID  NOT NULL,
    send_end_of_rental_reminder  BOOLEAN  NOT NULL,
    send_when_available_reminder BOOLEAN  NOT NULL,
    kindle_email                    TEXT          ,

    PRIMARY KEY (user_id),
    FOREIGN KEY (user_id) REFERENCES "user"
);

CREATE TABLE storage (
    user_id     UUID  NOT NULL,
    resource_id UUID  NOT NULL,
    since  TIMESTAMP  NOT NULL,

    PRIMARY KEY (user_id, resource_id),
    FOREIGN KEY (user_id) REFERENCES "user",
    FOREIGN KEY (resource_id) REFERENCES resource
);
