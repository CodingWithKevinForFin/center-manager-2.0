SELECT 
  machine_instance_id as 'id',
  system_load_avg as 'systemLoadAverage',
  total_memory as 'totalMemory',
  used_memory as 'usedMemory',
  total_swap_memory as 'totalSwapMemory',
  used_swap_memory as 'usedSwapMemory',
  now as 'now'
FROM MachineInstanceStats WHERE 
  machine_instance_id in (?{ids}) LIMIT ?{lim}