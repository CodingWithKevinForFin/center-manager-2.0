UPDATE AgentInstance set active=false where id=?{id} and active;
INSERT INTO AgentInstance
(
  active,
  machine_instance_id,
  id,
  revision,
  now,
  connected_time,
  process_uid,
  disconnected_time,
  agent_version,
  start_time,
  f1_license_expires,
  remote_host,
  remote_port,
  disconnected_reason
) VALUES (
  ?{active},
  ?{machine_instance_id},
  ?{id},
  ?{revision},
  ?{now},
  ?{connected_time},
  ?{process_uid},
  ?{disconnected_time},
  ?{agent_version},
  ?{start_time},
  ?{f1_license_expires},
  ?{remote_host},
  ?{remote_port},
  ?{disconnected_reason}
);