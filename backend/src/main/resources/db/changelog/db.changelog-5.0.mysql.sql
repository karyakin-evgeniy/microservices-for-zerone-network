-- liquibase formatted sql

-- changeset alex:1
ALTER TABLE notification add column status INT after person_id;