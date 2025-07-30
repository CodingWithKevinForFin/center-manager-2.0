UPDATE MachineInstance SET active=false WHERE active=true AND machine_uid=?{machine_uid};
INSERT INTO MachineInstance
(
  active,
  id,
  now,
  hostname,
  machine_uid,
  machine_start_time,
  os_version,
  os_name,
  os_architecture,
  revision,
  cpu_count,
  metadata
) values (
  ?{active},
  ?{id},
  ?{now},
  ?{hostname},
  ?{machine_uid},
  ?{machine_start_time},
  ?{os_version},
  ?{os_name},
  ?{os_architecture},
  ?{revision},
  ?{cpu_count},
  ?{metadata}
)

