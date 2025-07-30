SELECT 
  id as 'id',
  revision as 'revision',
  agent_id as 'agentId',
  machine_instance_id as 'machineInstanceId',         
  severity as 'severity',              
  subtype as 'subType',      
  status as 'status', 
  message as 'message',
  now as 'now'
FROM AgentEvents WHERE machine_instance_id in (?{ids}) LIMIT ?{lim}
