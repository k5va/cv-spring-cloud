--liquibase formatted sql
--changeset k5va:create-employees-table
CREATE TABLE employees
(
    id BIGSERIAL PRIMARY KEY NOT NULL,
    first_name VARCHAR(16) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    age INT NOT NULL
);

--rollback DROP TABLE employees;