SELECT 
  id as 'id',
  revision as 'revision',
  host as 'host',
  ip as 'address'
FROM ConnectedDevices WHERE machine_instance_id=?{machine_instance_id} AND active=true
