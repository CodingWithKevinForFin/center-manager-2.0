SELECT 
  a.id,
  a.id as machineInstanceId,
  a.revision,
  a.machine_uid as 'machineUid',
  a.machine_start_time as 'systemStartTime',
  a.os_version as 'osVersion',
  a.os_name as 'osName',
  a.os_architecture as 'osArchitecture',
  a.cpu_count as 'cpuCount',
  a.hostname as 'hostName',
  a.metadata as '(STRINGMAP)metadata',
  b.now as 'now',
  b.system_load_avg as 'systemLoadAverage',
  b.total_memory as 'totalMemory',
  b.used_memory as 'usedMemory',
  b.total_swap_memory as 'totalSwapMemory',
  b.used_swap_memory as 'usedSwapMemory'
FROM MachineInstance a JOIN MachineInstanceStats b ON a.id=b.machine_instance_id
WHERE a.active and b.active