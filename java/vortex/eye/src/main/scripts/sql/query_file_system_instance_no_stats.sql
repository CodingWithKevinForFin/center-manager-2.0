SELECT 
  a.id as 'id',
  a.name as 'name',
  a.revision as 'revision',
  a.fs_type as 'type',
  a.machine_instance_id as 'machineInstanceId',
  a.now as 'now'
FROM FileSystemInstance a 
WHERE a.active 