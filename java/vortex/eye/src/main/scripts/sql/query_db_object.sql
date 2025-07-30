SELECT 
  id,
  revision,
  name,
  object_type as 'type',
  definition,
  db_database_id as 'databaseId'
FROM DbObject where active
