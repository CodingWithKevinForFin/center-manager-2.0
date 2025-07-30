UPDATE DbObject set active=false where active=true and id=?{id};
INSERT INTO DbObject
(
  active,
  id,
  revision,
  now,
  name,
  db_database_id,
  definition,
  object_type
) VALUES (
  ?{active},
  ?{id},
  ?{revision},
  ?{now},
  ?{name},
  ?{db_database_id},
  ?{definition},
  ?{object_type}
);