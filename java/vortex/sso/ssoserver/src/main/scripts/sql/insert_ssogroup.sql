UPDATE SsoGroup set active=false where id=?{id} and active;
INSERT INTO SsoGroup (
  active,
  id,
  revision,
  name,
  group_type,
  now
) VALUES (
  ?{active},
  ?{id},
  ?{revision},
  ?{name},
  ?{groupType},
  ?{now}
);
