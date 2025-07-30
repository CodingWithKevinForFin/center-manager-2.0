UPDATE FileSystemInstance SET active=false where active=true AND id=?{id};
INSERT INTO FileSystemInstance
(
  active,
  id,
  revision,
  machine_instance_id,
  now,
  fs_type,
  name
) VALUES (
  ?{active},
  ?{id},
  ?{revision},
  ?{machine_instance_id},
  ?{now},
  ?{fs_type},
  ?{name}
);