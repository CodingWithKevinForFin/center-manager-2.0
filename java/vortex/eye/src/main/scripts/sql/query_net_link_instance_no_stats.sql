SELECT 
  a.id as 'id',
  a.revision as 'revision',
  a.name as 'name',
  a.mac as 'mac',
  a.broadcast as 'broadcast',
  a.transmission_details as 'transmissionDetails',
  a.mtu as 'mtu',
  a.state as 'state',
  a.now as 'now',
  a.machine_instance_id as 'machineInstanceId'
FROM NetLinkInstance a WHERE a.active
