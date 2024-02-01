CREATE TABLE customer
(
    id                 BIGSERIAL PRIMARY KEY NOT NULL,
    name               varchar(255)          NOT NULL,
    surname            varchar(255)          NOT NULL,
    birth_date         date                  NOT NULL,
    gsm_number         varchar(255) UNIQUE   NOT NULL,
    balance            float8                NOT NULL,
    created_date       timestamp             NOT NULL,
    last_modified_date timestamp             NOT NULL,
    version            integer               NOT NULL
);
