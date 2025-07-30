UPDATE FileInstance set active=false WHERE 0!=?{revision} AND active and id=?{id};
INSERT INTO FileInstance
(
  active,
  id,
  now,
  revision,
  machine_instance_id,
  parent_id,
  mask,
  modified_time,
  path,
  checksum,
  size,
  data_id,
  name
) VALUES (
  ?{active},
  ?{id},
  ?{now},
  ?{revision},
  ?{machine_instance_id},
  ?{parent_id},
  ?{mask},
  ?{modified_time},
  ?{path},
  ?{checksum},
  ?{size},
  ?{data_id},
  ?{name}
);