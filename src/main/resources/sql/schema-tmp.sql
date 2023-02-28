CREATE TABLE series (
    name TEXT  PRIMARY KEY
);

CREATE TYPE resource_status AS ENUM ('WITHDRAWN', 'AVAILABLE');

CREATE TYPE ebook_format AS ENUM ('PDF', 'MOBI', 'EPUB');

CREATE TYPE size_unit AS ENUM ('kB');

CREATE TABLE library (
    id            UUID  NOT NULL,
    name          TEXT  NOT NULL,
    street_name   TEXT  NOT NULL,
    street_number TEXT  NOT NULL,
    postcode      TEXT  NOT NULL,
    city          TEXT  NOT NULL,

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

CREATE TABLE book (
    resource_id UUID  NOT NULL,
    isbn        TEXT  NOT NULL,

    PRIMARY KEY (resource_id),
    FOREIGN KEY (resource_id) REFERENCES resource
);

CREATE TABLE ebook (
    resource_id    UUID  NOT NULL,
    content       BYTEA  NOT NULL,
    format EBOOK_FORMAT  NOT NULL,
    size  DECIMAL(5, 2)  NOT NULL,
    size_unit SIZE_UNIT  NOT NULL,

    PRIMARY KEY (resource_id, format),
    FOREIGN KEY (resource_id) REFERENCES resource,

    CHECK (size > 0)
);

CREATE TABLE "user" (
    id         UUID  NOT NULL,
    first_name TEXT  NOT NULL,
    last_name  TEXT  NOT NULL,
    email      TEXT  NOT NULL,
    login      TEXT  NOT NULL,
    password   TEXT  NOT NULL,

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
