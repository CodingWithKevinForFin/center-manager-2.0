INSERT INTO SsoUpdateEvents
(
  id,
  member_id,
  now,
  event_type,
  message,
  ok,
  session,
  name,
  namespace,
  client_location
) VALUES (
  ?{id},
  ?{member_id},
  ?{now},
  ?{event_type},
  ?{message},
  ?{ok},
  ?{session},
  ?{name},
  ?{namespace},
  ?{client_location}
);