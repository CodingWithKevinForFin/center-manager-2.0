SELECT 
  id,
  revision,
  machine_instance_id as 'machineInstanceId',
  system_load_avg as 'systemLoadAvg',
  total_memory as 'totalMemory',
  used_memory as 'usedMemory',
  total_swap_memory as 'totalSwapMemory',
  used_swap_memory as 'usedSwapMemory',
  now as 'now'
FROM MachineInstanceStats WHERE 
  machine_instance_id in(?{machine_instance_id}) AND active=true