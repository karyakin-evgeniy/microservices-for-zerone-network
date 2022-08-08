-- liquibase formatted sql

-- changeset alex:1
ALTER TABLE notification MODIFY notification_type VARCHAR(255) NULL;