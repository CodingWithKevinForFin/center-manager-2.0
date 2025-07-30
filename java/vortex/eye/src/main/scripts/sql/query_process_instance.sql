SELECT 
  a.id as 'id',
  a.user_name as 'user',
  a.pid as 'pid',
  a.parent_pid as 'parentPid',
  a.start_time as 'startTime',
  a.command as 'command',
  b.now as 'now',
  a.machine_instance_id as 'machineInstanceId',
  b.memory as 'memory',
  b.cpu_percent as 'cpuPercent'
FROM ProcessInstance a LEFT JOIN ProcessStats b ON a.id=b.process_id 
WHERE a.active=true and b.active
