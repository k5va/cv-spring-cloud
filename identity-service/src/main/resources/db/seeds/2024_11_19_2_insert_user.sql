--liquibase formatted sql
--changeset k5va:insert-default-user splitStatements:true endDelimiter:;
INSERT INTO users (id, name, email, password)
VALUES ('f7f5f79e-521b-46ed-8af8-2a9491c56a1f', 'John Doe', 'john@mail.com', '$2a$10$8408DFYmFgfb3055bJ4GmO.k3DJEGe04W2/TeAQcBkAY1UwNhYDde');
INSERT INTO user_roles (user_id, role_id)
VALUES ('f7f5f79e-521b-46ed-8af8-2a9491c56a1f', '84a9bffd-a390-487c-92a4-97dff0d10864');
INSERT INTO user_roles (user_id, role_id)
VALUES ('f7f5f79e-521b-46ed-8af8-2a9491c56a1f', '6c500d26-192e-4b3e-b722-5e9ab3aa9909');

--rollback DELETE FROM users WHERE id = 'f7f5f79e-521b-46ed-8af8-2a9491c56a1f';
--rollback DELETE FROM user_roles WHERE user_id = 'f7f5f79e-521b-46ed-8af8-2a9491c56a1f';