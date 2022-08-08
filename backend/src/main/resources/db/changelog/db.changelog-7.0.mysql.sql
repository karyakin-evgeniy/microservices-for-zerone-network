-- liquibase formatted sql

-- changeset Alex:1
CREATE TABLE IF NOT EXISTS notification_settings
(
    id                  BIGINT PRIMARY KEY AUTO_INCREMENT,
    person_id           BIGINT     NULL,
    type                VARCHAR(255)     NULL
);
