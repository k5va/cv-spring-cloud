--liquibase formatted sql
--changeset k5va:seed-employees-table
insert into employees (id, cv_id, first_name, last_name, age)
values
(1, 1, 'John', 'Doe', 25),
(2, 2, 'Jane', 'Doe', 30),
(3, 3, 'John', 'Smith', 35),
(4, 4, 'Jane', 'Smith', 40),
(5, 5, 'John', 'Johnson', 45),
(6, 6, 'Jane', 'Johnson', 50),
(7, 7, 'John', 'Williams', 55),
(8, 8, 'Jane', 'Williams', 60),
(9, 9, 'John', 'Brown', 65),
(10, 10, 'Jane', 'Brown', 70),
(11, 11, 'John', 'Jones', 75);

--rollback DROP TABLE employees;