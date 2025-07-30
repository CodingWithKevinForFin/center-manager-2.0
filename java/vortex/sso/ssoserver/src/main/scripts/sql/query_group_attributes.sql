SELECT 
  id,
  now as 'now',
  revision,
  ssogroup_id as 'groupId',
  attribute_key as 'key',
  attribute_type as 'type',
  attribute_value as 'value'
FROM 
  SsoGroupAttribute where active 