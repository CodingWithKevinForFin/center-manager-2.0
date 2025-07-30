UPDATE BackupFileInstance set active=false WHERE 0!=?{revision} AND active and id=?{id};
INSERT INTO BackupFileInstance
(
  active,
  id,
  now,
  revision,
  machine_instance_id,
  mask,
  modified_time,
  path,
  checksum,
  size,
  data_vvid,
  backup_id
) VALUES (
  ?{active},
  ?{id},
  ?{now},
  ?{revision},
  ?{machine_instance_id},
  ?{mask},
  ?{modified_time},
  ?{path},
  ?{checksum},
  ?{size},
  ?{data_vvid},
  ?{backup_id}
);