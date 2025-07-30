SELECT 
  id,
  revision,
  table_name as 'tableName',
  user_name as 'user',
  privilege_type as 'type',
  db_database_id as 'databaseId'
FROM DbPrivilege where active
