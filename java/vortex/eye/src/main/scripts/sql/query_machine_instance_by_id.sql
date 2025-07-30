SELECT 
  id,
  revision,
  machine_uid as 'machineUid',
  machine_start_time as 'systemStartTime',
  os_version as 'osVersion',
  os_name as 'osName',
  os_architecture as 'osArchitecture',
  cpu_count as 'cpuCount',
  hostname as 'hostName',
  now as 'now',
  metadata as '(STRINGMAP)metadata'
FROM MachineInstance WHERE 
  id in(?{ids}) LIMIT ?{lim}