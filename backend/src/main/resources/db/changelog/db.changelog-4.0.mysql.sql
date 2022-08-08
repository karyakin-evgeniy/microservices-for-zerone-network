-- liquibase formatted sql

-- changeset rafit:1
ALTER TABLE message add column dialog_id BIGINT after recipient_id;
