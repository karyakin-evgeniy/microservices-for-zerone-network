-- liquibase formatted sql

-- changeset Eujen:1
ALTER TABLE post MODIFY post_text LONGTEXT NULL;

-- changeset Eujen:2
ALTER TABLE post add column updated datetime;