--liquibase formatted sql
--changeset k5va:insert-default-roles splitStatements:true endDelimiter:;
INSERT INTO roles (id, role) VALUES ('84a9bffd-a390-487c-92a4-97dff0d10864', 'USER');
INSERT INTO roles (id, role) VALUES ('6c500d26-192e-4b3e-b722-5e9ab3aa9909', 'ADMIN');

--rollback DELETE FROM ROLES