create table if not exists student
(
  id      uuid primary key,
  fio     varchar not null,
  groupId integer
);

create table if not exists employees
(
  first_name varchar primary key,
  last_name varchar not null,
  address varchar
);
