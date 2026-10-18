-- V8__migrate_primary_keys_to_uuidv7.sql
-- Run only after manually clearing the three application tables.
-- This migration contains no data-deletion statements.

ALTER TABLE users
    MODIFY COLUMN id BINARY(16) NOT NULL;

ALTER TABLE idempotency_keys
    MODIFY COLUMN id BINARY(16) NOT NULL;

ALTER TABLE user_import_rejections
    MODIFY COLUMN id BINARY(16) NOT NULL;