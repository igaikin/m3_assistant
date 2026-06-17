--CREATE DATABASE m3_db;
/*
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS qualification_classes;
DROP TABLE IF EXISTS roles;
DROP TABLE IF EXISTS professions;
DROP TABLE IF EXISTS trains;

*/

CREATE TABLE IF NOT EXISTS qualification_classes
(
    id                  BIGSERIAL PRIMARY KEY,
    qualification_class VARCHAR(1)
);

CREATE TABLE IF NOT EXISTS professions
(
    id         BIGSERIAL PRIMARY KEY,
    profession VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS roles
(
    id   BIGSERIAL PRIMARY KEY,
    role VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS trains
(
    id    BIGSERIAL PRIMARY KEY,
    train VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS users
(
    id                     BIGSERIAL PRIMARY KEY,
    avatar                 VARCHAR,
    first_name             VARCHAR(50)                                  NOT NULL,
    surname                VARCHAR(50)                                  NOT NULL,
    patronymic             VARCHAR(50)                                  NOT NULL,
    personnel_number       VARCHAR(5)                                   NOT NULL,
    qualification_class_id BIGINT REFERENCES qualification_classes (id) NOT NULL,
    profession_id          BIGINT REFERENCES professions (id)           NOT NULL,
    role_id                BIGINT REFERENCES roles (id)                 NOT NULL,
    train_id               BIGINT REFERENCES trains (id)                NOT NULL,
    email                  VARCHAR(100) UNIQUE                          NOT NULL,
    password               VARCHAR(255)                                 NOT NULL
);

CREATE TABLE file_alias
(
    id                 BIGSERIAL PRIMARY KEY,
    original_file_name VARCHAR(255) NOT NULL,
    alias_name         VARCHAR(255),
    target_folder      VARCHAR(255)
);

CREATE TABLE calendar_event (
                                id BIGSERIAL PRIMARY KEY,
                                title VARCHAR(255),
                                event_date DATE
);
