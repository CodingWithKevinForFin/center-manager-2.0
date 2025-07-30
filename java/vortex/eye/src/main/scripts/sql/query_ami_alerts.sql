select
  a.id,
  a.machine_instance_id as 'machineInstanceId', 
  a.ami_application_id as 'amiApplicationId',
  a.level as 'level',
  a.alert_type as 'type',
  a.alert_id as 'alertId',
  a.expires as 'expires',
  a.assigned_to as 'assignedTo',
  b.revision,
  b.now,
  b.params as '(PARAMSMAP)params'
from AmiAlert a,AmiAlertParams b where a.active and b.active and a.id=b.ami_alert_id and (a.expires<1 or a.expires>=?{now})