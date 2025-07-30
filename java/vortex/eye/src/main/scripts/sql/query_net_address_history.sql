SELECT 
  id as 'id',
  revision as 'revision',
  link_name as 'linkName',
  address_type as 'type',
  scope as 'scope',
  address as 'address',
  broadcast as 'broadcast',
  now as 'now',
  machine_instance_id as 'machineInstanceId'
FROM NetAddressInstance WHERE id in (?{ids}) LIMIT ?{lim}
