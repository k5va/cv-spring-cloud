--liquibase formatted sql
--changeset k5va:add-outbox-type-field
ALTER TABLE outbox ADD COLUMN type VARCHAR(25) DEFAULT 'CV';

--rollback ALTER TABLE outbox DROP COLUMN type;