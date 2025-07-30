select
  a.id,
  a.machine_instance_id as 'machineInstanceId', 
  a.ami_application_id as 'amiApplicationId',
  a.associated_alert_id as 'associatedAlertId',
  a.object_type as 'type',
  a.object_id as 'objectId',
  a.expires as 'expires',
  b.revision,
  b.now,
  b.params as '(PARAMSMAP)params'
from AmiObject a,AmiObjectParams b where a.active and b.active and a.id=b.ami_object_id and (a.expires<1 or a.expires>=?{now})