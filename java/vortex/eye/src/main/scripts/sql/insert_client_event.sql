INSERT INTO ClientEvents
(
  id,
  now,
  invoked_by,
  event_type,
  comment,
  message,
  target_machine_uid,
  params
) VALUES (
  ?{id},
  ?{now},
  ?{invoked_by},
  ?{event_type},
  ?{comment},
  ?{message},
  ?{target_machine_uid},
  ?{params}
);          