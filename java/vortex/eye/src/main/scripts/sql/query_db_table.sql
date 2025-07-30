SELECT 
  id,
  revision,
  name,
  description,
  comments,
  create_time as 'createTime',
  db_database_id as 'databaseId'
FROM DbTable where active
