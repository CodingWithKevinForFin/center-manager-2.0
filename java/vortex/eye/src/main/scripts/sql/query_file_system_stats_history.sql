SELECT 
  file_system_instance_id as 'id',
  now,
  total_space as 'totalSpace',
  usable_space as 'usableSpace',
  free_space as 'freeSpace'
FROM FileSystemStats WHERE file_system_instance_id in (?{ids}) LIMIT ?{lim}
