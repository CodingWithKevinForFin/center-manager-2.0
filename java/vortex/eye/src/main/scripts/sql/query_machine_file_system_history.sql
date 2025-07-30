SELECT 
  id as 'id',
  name as 'name',
  revision as 'revision',
  now as 'now',
  machine_instance_id as 'machineInstanceId'
FROM FileSystemInstance WHERE machine_instance_id in (?{ids}) LIMIT ?{lim}
