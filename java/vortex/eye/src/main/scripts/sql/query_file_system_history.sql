SELECT 
  id as 'id',
  name as 'name',
  revision as 'revision',
  now as 'now',
  fs_type as 'type',
  machine_instance_id as 'machineInstanceId'
FROM FileSystemInstance WHERE id in (?{ids}) LIMIT ?{lim}
