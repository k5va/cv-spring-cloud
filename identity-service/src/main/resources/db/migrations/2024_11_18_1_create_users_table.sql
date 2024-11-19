--liquibase formatted sql
--changeset k5va:create-users-table
CREATE TABLE users
(
    id UUID PRIMARY KEY NOT NULL,
    name VARCHAR(32) NOT NULL,
    email VARCHAR(16) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

--rollback DROP TABLE users CASCADE;