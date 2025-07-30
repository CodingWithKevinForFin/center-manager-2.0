SELECT 
  a.id,
  a.revision,
  b.now,
  a.db_type as 'dbType',
  a.url,
  a.description,
  a.psw as 'password',
  a.machine_uid as 'machineUid',
  a.hints,
  a.server_port as 'serverPort',
  a.metadata as '(STRINGMAP)metadata',
  b.status,
  b.inspected_time as 'inspectedTime',
  b.message
FROM DbServer a join DbServerStatus b on a.id=b.dbserver_id where a.active and b.active
