select
  a.id,
  a.machine_instance_id as 'machineInstanceId', 
  a.host_ip as 'hostIp',
  a.app_id as 'appId',
  b.revision,
  b.now,
  b.params as '(PARAMSMAP)params'
from AmiApplication a,AmiApplicationParams b where a.active and b.active and a.id=b.ami_application_id