UPDATE NetConnectionInstance SET active=false where active=true AND id=?{id};
INSERT INTO NetConnectionInstance
(
  active,
  id,
  revision,
  now,
  machine_instance_id,
  local_host,
  local_port,
  foreign_host,
  foreign_port,
  local_appname,
  local_pid,
  state
) VALUES (
  ?{active},
  ?{id},
  ?{revision},
  ?{now},
  ?{machine_instance_id},
  ?{local_host},
  ?{local_port},
  ?{foreign_host},
  ?{foreign_port},
  ?{local_appname},
  ?{local_pid},
  ?{state}
);