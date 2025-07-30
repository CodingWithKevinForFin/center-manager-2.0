UPDATE FileSystemStats SET active=false where active=true AND file_system_instance_id=?{id};
INSERT INTO FileSystemStats
(
  active,
  file_system_instance_id,
  now,
  total_space,
  usable_space,
  free_space
) VALUES (
  true,
  ?{id},
  ?{now},
  ?{total_space},
  ?{usable_space},
  ?{free_space}
);