--liquibase formatted sql
--changeset k5va:seed-employees-table
insert into employees (first_name, last_name, age)
values
('John', 'Doe', 25),
( 'Jane', 'Doe', 30),
( 'John', 'Smith', 35),
( 'Jane', 'Smith', 40),
( 'John', 'Johnson', 45),
( 'Jane', 'Johnson', 50),
( 'John', 'Brown', 55),
( 'Jane', 'Brown', 60),
( 'John', 'Davis', 65),
( 'Jane', 'Davis', 70),
( 'John', 'Miller', 75),
( 'Jane', 'Miller', 80);

--rollback DROP TABLE employees;