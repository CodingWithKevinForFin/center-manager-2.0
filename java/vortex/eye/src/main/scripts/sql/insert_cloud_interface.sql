UPDATE CloudInterfaces set active=false WHERE active and id=?{id};
INSERT INTO CloudInterfaces
(
  active,
  id,
  now,
  revision,
  description,
  user_name,
  password,
  key_contents,
  key_type,
  cloud_vendor_type,
  parameters
) VALUES (
  ?{active},
  ?{id},
  ?{now},
  ?{revision},
  ?{description},
  ?{user_name},
  ?{password},
  ?{key_contents},
  ?{key_type},
  ?{cloud_vendor_type},
  ?{parameters}
);