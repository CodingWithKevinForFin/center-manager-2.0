SELECT 
  id,
  machine_instance_id as 'machineInstanceId',         
  level,
  now,
  start_time as 'timeGenerated',
  source,
  host,
  name,
  user_name as 'userName',
  message,
  duration,
  not_ended as 'notEnded',
  end_time as 'endTime'
FROM AgentMachineEventStats WHERE machine_instance_id in (?{ids}) LIMIT ?{lim}
