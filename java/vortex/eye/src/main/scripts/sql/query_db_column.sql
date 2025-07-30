SELECT 
  id,
  revision,
  name,
  description,
  comments,
  mask,
  size,
  db_table_id as 'tableId',
  numeric_precision as 'precision',
  numeric_scale as 'scale',
  permissible_values as 'permissibleValues', 
  position,
  data_type as 'type'
FROM DbColumn where active
