SELECT 
  id as 'id',
  user_name as 'user',
  pid as 'pid',
  parent_pid as 'parentPid',
  start_time as 'startTime',
  command as 'command',
  now as 'now',
  machine_instance_id as 'machineInstanceId'
FROM ProcessInstance WHERE id in (?{ids}) LIMIT ?{lim}
