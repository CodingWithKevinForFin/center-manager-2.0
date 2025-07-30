UPDATE NetAddressInstance SET active=false where active=true AND id=?{id};
INSERT INTO NetAddressInstance
(
  active,
  id,
  revision,
  now,
  machine_instance_id,
  link_name,
  address,
  broadcast,
  address_type,
  scope
) VALUES (
  ?{active},
  ?{id},
  ?{revision},
  ?{now},
  ?{machine_instance_id},
  ?{link_name},
  ?{address},
  ?{broadcast},
  ?{address_type},
  ?{scope}
);