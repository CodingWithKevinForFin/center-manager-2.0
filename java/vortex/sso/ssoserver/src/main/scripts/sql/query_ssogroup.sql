SELECT 
  id,
  revision,
  name,
  group_type as 'type',
  now
FROM 
  SsoGroup where active
