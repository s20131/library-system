CREATE TABLE library (
    id            UUID  NOT NULL PRIMARY KEY,
    name          TEXT  NOT NULL,
    street_name   TEXT  NOT NULL,
    street_number TEXT  NOT NULL,
    postcode      TEXT  NOT NULL,
    city          TEXT  NOT NULL,
    location     POINT  NOT NULL,
);

CREATE TABLE "user" (
    id        UUID  NOT NULL PRIMARY KEY,
    firstName TEXT  NOT NULL,
    lastName  TEXT  NOT NULL,
    email     TEXT  NOT NULL,
    username  TEXT  NOT NULL,
    password  TEXT  NOT NULL,
);

CREATE TABLE author (
    id        UUID  NOT NULL PRIMARY KEY,
    firstName TEXT  NOT NULL,
    lastName  TEXT  NOT NULL
);

CREATE TYPE resource_status AS ENUM ('WITHDRAWN', 'AVAILABLE');

CREATE TABLE resource
    id                UUID  NOT NULL PRIMARY KEY,
    title             TEXT  NOT NULL,
    author            UUID  NOT NULL REFERENCES author,
    release_date      DATE  NOT NULL,
    description       TEXT  NOT NULL,
    series            TEXT,
    status RESOURCE_STATUS  NOT NULL,
);

CREATE TABLE book (
    resource_id UUID  NOT NULL PRIMARY KEY REFERENCES resource,
    isbn        TEXT  NOT NULL,
);

CREATE TYPE unit AS ENUM ('PDF', 'MOBI', 'EPUB');

CREATE TABLE e-book (
    resource_id UUID  NOT NULL PRIMARY KEY REFERENCES resource,
    format      TEXT  NOT NULL,
    content    BYTEA  NOT NULL,
    size     NUMERIC  NOT NULL,
    size_unit   UNIT  NOT NULL,
);

CREATE TABLE resource_categories (
    resource_id UUID  NOT NULL REFERENCES resource,
    category    TEXT  NOT NULL REFERENCES category,
    PRIMARY KEY (resource_id, category)
);

CREATE TABLE category (
    name TEXT  NOT NULL PRIMARY KEY
);

INSERT INTO category VALUES ('adventure'), ('fantasy'), ('crime'), ('romance');

CREATE TABLE copy (
    id         UUID  NOT NULL PRIMARY KEY,
    available   INT  NOT NULL,
    library_id  INT  NOT NULL REFERENCES library,
    resource_id INT  NOT NULL REFERENCES resource
);

CREATE TABLE cover (
    id         UUID  NOT NULL PRIMARY KEY REFERENCES resource,
    content   BYTEA  NOT NULL,
    media_type TEXT  NOT NULL
);

CREATE TYPE operation AS ENUM ('CREATE', 'UPDATE', 'DELETE');

CREATE TABLE e-book_audit (
    id            UUID  NOT NULL PRIMARY KEY,
    e-book_id      INT  NOT NULL,
    e-book_format TEXT  NOT NULL,
    who            INT  NOT NULL,
    what     OPERATION  NOT NULL,
    at       TIMESTAMP  NOT NULL,
    data         JSONB  NOT NULL,
    FOREIGN KEY (e-book_id, e-book_format) REFERENCES e-book (resource_id, format)
);

CREATE TABLE favourite_libraries (
    user_id    UUID  NOT NULL REFERENCES "user",
    library_id UUID  NOT NULL REFERENCES library,
    PRIMARY KEY (user_id, library_id)
);

CREATE TABLE librarian (
    id         UUID  NOT NULL REFERENCES "user",
    library_id UUID  NOT NULL REFERENCES library,
    PRIMARY KEY (id, library_id)
);

CREATE TABLE library_card (
    -- DEFAULT START FROM ...
    number     BIGINT  NOT NULL PRIMARY KEY,
    qr_code     BYTEA  NOT NULL,
    expiration   DATE  NOT NULL,
    is_active BOOLEAN  NOT NULL,
    user_id       INT  NOT NULL REFERENCES "user"
);

CREATE TYPE rent_status AS ENUM ('WAITING');

CREATE TABLE rental (
    id                INT  NOT NULL PRIMARY KEY,
    user_id          UUID  NOT NULL REFERENCES "user",
    copy_id          UUID  NOT NULL REFERENCES copy,
    start       TIMESTAMP  NOT NULL,
    finish      TIMESTAMP  NOT NULL,
    status    RENT_STATUS  NOT NULL,
    penalty DECIMAL(10,2)
);

CREATE TABLE reservation (
    id           INT  NOT NULL PRIMARY KEY,
    user_id     UUID  NOT NULL REFERENCES "user",
    copy_id     UUID  NOT NULL REFERENCES copy,
    start  TIMESTAMP  NOT NULL,
    finish TIMESTAMP  NOT NULL
);

CREATE TABLE settings (
    id                              UUID  NOT NULL PRIMARY KEY REFERENCES "user",
    send_end_of_rental_reminder  BOOLEAN  NOT NULL,
    send_when_available_reminder BOOLEAN  NOT NULL,
    kindle_email                    TEXT
);

CREATE TABLE storage (
    user_id     UUID  NOT NULL REFERENCES "user",
    resource_id UUID  NOT NULL REFERENCES resource,
    since  TIMESTAMP  NOT NULL,
    PRIMARY KEY (user_id, resource_id)
);
