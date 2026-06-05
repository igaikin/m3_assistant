--CREATE DATABASE m3_db;
/*
DROP TABLE IF EXISTS qualificationClass;
DROP TABLE IF EXISTS roles;
DROP TABLE IF EXISTS profession;
DROP TABLE IF EXISTS trains;
DROP TABLE IF EXISTS users;

*/

CREATE TABLE IF NOT EXISTS qualificationClass
(
    id      BIGSERIAL PRIMARY KEY,
    classes INT,
    deleted BOOLEAN NOT NULL DEFAULT false
);

CREATE TABLE IF NOT EXISTS professions
(
    id         BIGSERIAL PRIMARY KEY,
    profession VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS roles
(
    id   BIGSERIAL PRIMARY KEY,
    role VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS trains
(
    id    BIGSERIAL PRIMARY KEY,
    train VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS users
(
    id                    BIGSERIAL PRIMARY KEY,
    avatar                VARCHAR,
    first_name            VARCHAR(50)                          NOT NULL,
    surname               VARCHAR(50)                          NOT NULL,
    patronymic            VARCHAR(50)                          NOT NULL,
    personnel_number      BIGINT                               NOT NULL,
    qualificationClass_id BIGINT REFERENCES qualificationClass NOT NULL,
    profession_id         BIGINT REFERENCES professions        NOT NULL,
    role_id               BIGINT REFERENCES roles              NOT NULL,
    train_id              BIGINT REFERENCES trains             NOT NULL,
    email                 VARCHAR(50)                          NOT NULL,
    password              VARCHAR(255)                          NOT NULL,
    deleted               BOOLEAN                              NOT NULL DEFAULT false
);
