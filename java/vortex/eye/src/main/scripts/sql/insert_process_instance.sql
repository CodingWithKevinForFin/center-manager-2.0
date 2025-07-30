UPDATE ProcessInstance SET active=false where active=true AND id=?{id};
INSERT INTO ProcessInstance
(
  active,
  id,
  revision,
  now,
  machine_instance_id,
  start_time,
  end_time,
  user_name,
  pid,
  parent_pid,
  command
) VALUES (
  ?{active},
  ?{id},
  ?{revision},
  ?{now},
  ?{machine_instance_id},
  ?{start_time},
  ?{end_time},
  ?{user_name},
  ?{pid},
  ?{parent_pid},
  ?{command}
);