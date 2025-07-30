SELECT 
  id,
  revision,
  name,
  now,
  db_server_id as 'dbServerId'
FROM DbDatabase where active
