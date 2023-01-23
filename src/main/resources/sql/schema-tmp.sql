CREATE TABLE series (
    name TEXT  NOT NULL,

    PRIMARY KEY (name)
);

CREATE TYPE resource_status AS ENUM ('WITHDRAWN', 'AVAILABLE');

--CREATE TYPE content_type AS ENUM ('PDF', 'MOBI', 'EPUB');

CREATE TYPE size_unit AS ENUM ('kB', 'MB');

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

CREATE TABLE resource (
    id                UUID  NOT NULL,
    title             TEXT  NOT NULL,
    release_date      DATE  NOT NULL,
    description       TEXT          ,
    series            TEXT          ,
    status RESOURCE_STATUS  NOT NULL DEFAULT 'AVAILABLE',

    PRIMARY KEY (id),
    FOREIGN KEY (series) REFERENCES series
);

CREATE TABLE book (
    resource_id UUID  NOT NULL,
    isbn        TEXT  NOT NULL,

    PRIMARY KEY (resource_id),
    FOREIGN KEY (resource_id) REFERENCES resource
);

CREATE TABLE ebook (
    resource_id          UUID  NOT NULL,
  --format               TEXT  NOT NULL,
    content             BYTEA  NOT NULL,
  --content_type CONTENT_TYPE  NOT NULL,
    size              DECIMAL(5, 2)  NOT NULL,
  --size_unit       SIZE_UNIT  NOT NULL,

    PRIMARY KEY (resource_id/*, format*/),
    FOREIGN KEY (resource_id) REFERENCES resource,
    CHECK (size > 0)
);