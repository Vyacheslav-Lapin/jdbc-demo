create table if not exists student
(
  id      uuid primary key,
  fio     varchar not null,
  groupId integer
)
