UPDATE DbTable set active=false where active=true and id=?{id};
INSERT INTO DbTable
(
  active,
  id,
  revision,
  now,
  name,
  db_database_id,
  description,
  comments,
  create_time
) VALUES (
  ?{active},
  ?{id},
  ?{revision},
  ?{now},
  ?{name},
  ?{db_database_id},
  ?{description},
  ?{comments},
  ?{create_time}
);
update DbColumn set active=false where ?{active}=false and active and db_table_id=?{id};
