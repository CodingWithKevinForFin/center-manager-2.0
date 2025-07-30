UPDATE DbPrivilege set active=false where active=true and id=?{id};
INSERT INTO DbPrivilege
(
  active,
  id,
  db_database_id,
  revision,
  now,
  user_name,
  table_name,
  privilege_type
) VALUES (
  ?{active},
  ?{id},
  ?{db_database_id},
  ?{revision},
  ?{now},
  ?{user_name},
  ?{table_name},
  ?{privilege_type}
);