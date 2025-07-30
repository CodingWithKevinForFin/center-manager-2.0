SELECT 
  a.id as 'id',
  a.name as 'name',
  a.revision as 'revision',
  a.fs_type as 'type',
  a.machine_instance_id as 'machineInstanceId',
  b.now as 'now',
  b.total_space as 'totalSpace',
  b.usable_space as 'usableSpace',
  b.free_space as 'freeSpace'
FROM FileSystemInstance a JOIN FileSystemStats b ON a.id=b.file_system_instance_id 
WHERE a.active and b.active