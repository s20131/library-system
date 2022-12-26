CREATE TABLE library (
    id            UUID  NOT NULL PRIMARY KEY,
    name          TEXT  NOT NULL,
    street_name   TEXT  NOT NULL,
    street_number TEXT  NOT NULL,
    postcode      TEXT  NOT NULL,
    city          TEXT  NOT NULL
);
