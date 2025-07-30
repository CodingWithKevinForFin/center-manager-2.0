select
  id,
  member_id as 'memberId',
  now,
  event_type as 'type',
  message,
  ok,
  session,
  name,
  namespace,
  client_location as 'clientLocation'
FROM SsoUpdateEvents;