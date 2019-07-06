create table TODO
(
  id BIGINT IDENTITY not null,
  title varchar(100) not null,
  completed boolean not null default false,
  order_number int
);