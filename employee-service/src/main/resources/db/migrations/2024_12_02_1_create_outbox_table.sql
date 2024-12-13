--liquibase formatted sql
--changeset k5va:create-outbox-table
CREATE TABLE outbox
(
    id BIGSERIAL PRIMARY KEY NOT NULL,
    payload VARCHAR NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

--rollback DROP TABLE outbox;