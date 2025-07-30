UPDATE SsoGroupAttribute set active=false where id=?{id} and active;
INSERT INTO SsoGroupAttribute
(
  active,
  id,
  now,
  revision,
  ssogroup_id,
  attribute_key,
  attribute_type,
  attribute_value
) VALUES (
  true,
  ?{id},
  ?{now},
  ?{revision},
  ?{group_id},
  ?{attribute_key},
  ?{attribute_type},
  ?{attribute_value}
);