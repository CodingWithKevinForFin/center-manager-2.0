SELECT 
  id as 'id',
  now as 'now',
  revision as 'revision',
  mask as 'mask',
  machine_instance_id as 'machineInstanceId',
  modified_time as 'modifiedTime',
  path as 'path',
  checksum as 'checksum',
  size as 'size',
  backup_id as 'backupId',
  data_vvid as 'dataVvid'
FROM 
  BackupFileInstance where id in (?{ids}) LIMIT ?{lim}
