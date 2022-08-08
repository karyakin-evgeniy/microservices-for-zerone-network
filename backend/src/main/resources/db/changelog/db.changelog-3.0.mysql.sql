-- liquibase formatted sql

-- changeset Ilia:1
CREATE TABLE IF NOT EXISTS support
(
    id        BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id   BIGINT,
    firstname VARCHAR(64),
    lastname  VARCHAR(64),
    email     VARCHAR(32),
    massage   VARCHAR(255),
    time      datetime,
    FOREIGN KEY (user_id) REFERENCES users (id)
);