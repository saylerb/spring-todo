CREATE TABLE todo
(
  id BIGSERIAL primary key,
  title varchar(100) not null,
  completed boolean not null default false,
  order_number int
);