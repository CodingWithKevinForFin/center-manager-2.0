SELECT 
  a.id as 'id',
  a.user_name as 'user',
  a.pid as 'pid',
  a.parent_pid as 'parentPid',
  a.start_time as 'startTime',
  a.command as 'command',
  a.now as 'now',
  a.machine_instance_id as 'machineInstanceId'
FROM ProcessInstance a 
WHERE a.active=true 
