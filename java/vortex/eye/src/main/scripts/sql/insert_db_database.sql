UPDATE DbDatabase set active=false where active=true and id=?{id};
INSERT INTO DbDatabase
(
  active,
  id,
  revision,
  now,
  db_server_id,
  name
) VALUES (
  ?{active},
  ?{id},
  ?{revision},
  ?{now},
  ?{db_server_id},
  ?{name}
);
update DbColumn    set active=false where ?{active}=false and active and db_table_id in (select id from DbTable where active and db_database_id=?{id});
update DbObject    set active=false where ?{active}=false and active and db_database_id=?{id};
update DbPrivilege set active=false where ?{active}=false and active and db_database_id=?{id};
update DbTable     set active=false where ?{active}=false and active and db_database_id=?{id};
