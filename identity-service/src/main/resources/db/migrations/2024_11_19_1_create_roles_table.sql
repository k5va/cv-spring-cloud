--liquibase formatted sql
--changeset k5va:create-roles-table
CREATE TABLE roles
(
    id      UUID PRIMARY KEY,
    role    VARCHAR(32) NOT NULL UNIQUE
);

-- rollback DROP TABLE roles

-- changeset k5va:create-user-roles-table
CREATE TABLE user_roles
(
    id      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id),
    role_id UUID NOT NULL REFERENCES roles(id)
);

--rollback DROP TABLE user_roles