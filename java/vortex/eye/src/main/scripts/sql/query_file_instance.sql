SELECT 
  id as "id",
  revision as "revision",
  parent_id as "parentId",
  mask as "mask",
  modified_time as "modifiedTime",
  path as "path",
  checksum as "checksum",
  size as "size"
FROM 
  FileInstance where machine_instance_id=?{machine_instance_id} and active 
