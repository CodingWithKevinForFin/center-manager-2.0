UPDATE NetLinkInstance SET active=false where active=true AND id=?{id};
INSERT INTO NetLinkInstance
(
  active,
  id,
  revision,
  machine_instance_id,
  mac,
  broadcast,
  transmission_details,
  mtu,
  now,
  name,
  state
) VALUES (
  ?{active},
  ?{id},
  ?{revision},
  ?{machine_instance_id},
  ?{mac},
  ?{broadcast},
  ?{transmission_details},
  ?{mtu},
  ?{now},
  ?{name},
  ?{state}
);