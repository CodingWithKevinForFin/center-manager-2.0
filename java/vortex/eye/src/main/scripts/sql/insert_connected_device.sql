UPDATE ConnectedDevices SET active=false WHERE active=true AND id=?{id};
INSERT INTO ConnectedDevices
(
  active,
  id,
  machine_instance_id,
  revision,
  now,
  host,
  ip
) values (
  ?{active},
  ?{id},
  ?{machine_instance_id},
  ?{revision},
  ?{now},
  ?{host},
  ?{ip}
)

