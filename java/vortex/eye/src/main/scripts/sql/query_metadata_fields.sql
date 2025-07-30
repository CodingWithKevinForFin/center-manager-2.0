SELECT 
  id,
  revision,
  now,
  description,
  target_types as 'targetTypes',
  value_type as 'valueType',
  required,
  key_code as 'keyCode',
  title,
  max_length as 'maxLength',
  enums as '(STRINGMAP)enums',
  max_value as 'maxValue',
  min_value as 'minValue'
FROM MetadataFields WHERE active