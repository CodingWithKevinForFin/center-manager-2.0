SELECT 
  id as 'id',
  revision as 'revision',
  local_pid as 'localPid',
  local_appname as 'localAppName',
  local_host as 'localHost',
  local_port as 'localPort',
  foreign_host as 'foreignHost',
  foreign_port as 'foreignPort',
  state as 'state',
  now as 'now',
  machine_instance_id as 'machineInstanceId'
FROM NetConnectionInstance WHERE id in (?{ids}) LIMIT ?{lim}
