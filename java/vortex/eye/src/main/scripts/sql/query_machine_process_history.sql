SELECT 
  ProcessInstance.id as 'id',
  ProcessInstance.revision as 'revision',
  ProcessInstance.user_name as 'user',
  ProcessInstance.pid as 'pid',
  ProcessInstance.parent_pid as 'parentPid',
  ProcessInstance.start_time as 'startTime',
  ProcessInstance.command as 'command',
  ProcessInstance.now as 'now',
  ProcessInstance.machine_instance_id as 'machineInstanceId',
  ProcessStats.memory as 'memory',
  ProcessStats.cpu_percent as 'cpuPercent'
FROM ProcessInstance LEFT JOIN ProcessStats ON ProcessInstance.id = ProcessStats.process_id WHERE machine_instance_id in (?{ids}) LIMIT ?{lim}
