-- liquibase formatted sql

-- changeset Rafit:1
CREATE TABLE IF NOT EXISTS dialog
(
    id                  BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_first          BIGINT     NULL,
    user_second         BIGINT     NULL
);





