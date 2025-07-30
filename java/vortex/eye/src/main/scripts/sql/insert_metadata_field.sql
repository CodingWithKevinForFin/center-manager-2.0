UPDATE MetadataFields set active=false where active=true and id=?{id};
INSERT INTO MetadataFields
(
  active,
  id,
  revision,
  now,
  description,
  target_types,
  value_type,
  required,
  key_code,
  title,
  max_length,
  enums,
  max_value,
  min_value
) VALUES (
  ?{active},
  ?{id},
  ?{revision},
  ?{now},
  ?{description},
  ?{target_types},
  ?{value_type},
  ?{required},
  ?{key_code},
  ?{title},
  ?{max_length},
  ?{enums},
  ?{max_value},
  ?{min_value}
);