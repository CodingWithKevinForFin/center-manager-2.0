SELECT 
  id as 'id',
  revision as 'revision',
  agent_id as 'user',
  second as 'second',
  minute as 'minute',
  hour as 'hour',
  day_of_month as 'dayOfMonth',
  month as 'month',
  day_of_week as 'dayOfWeek',
  timezone as 'timeZone',
  command as 'command',
  now as 'now',
  machine_instance_id as 'machineInstanceId'
FROM JobSchedules WHERE machine_instance_id in (?{ids}) LIMIT ?{lim}
