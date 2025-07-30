UPDATE DbColumn set active=false where active=true and id=?{id};
INSERT INTO DbColumn
(
  active,
  id,
  revision,
  now,
  name,
  db_table_id,
  description,
  comments,
  mask,
  size,
  numeric_precision,
  numeric_scale,
  permissible_values,
  position,
  data_type
) VALUES (
  ?{active},
  ?{id},
  ?{revision},
  ?{now},
  ?{name},
  ?{db_table_id},
  ?{description},
  ?{comments},
  ?{mask},
  ?{size},
  ?{numeric_precision},
  ?{numeric_scale},
  ?{permissible_values},
  ?{position},
  ?{data_type}
);