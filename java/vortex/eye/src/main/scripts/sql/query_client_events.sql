Select
  id,
  now,
  invoked_by as 'invokedBy',
  event_type as 'eventType',
  comment,
  message,
  target_machine_uid as 'targetMachineUid',
  params as '(STRINGMAP)params'
FROM ClientEvents order by now desc LIMIT ?{LIMIT}