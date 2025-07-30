INSERT INTO TailFileEvent
(
  id,
  event_type,
  tail_file_id,
  checksum,
  file_position,
  data,
  now
) VALUES (
  ?{id},
  ?{event_type},
  ?{tail_file_id},
  ?{checksum},
  ?{file_position},
  ?{data},
  ?{now}
);