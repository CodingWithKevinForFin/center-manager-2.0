UPDATE DbServerStatus set active=false where active=true and dbserver_id=?{id};
INSERT INTO DbServerStatus
(
  active,
  dbserver_id,
  now,
  status,
  inspected_time,
  message
) VALUES (
  ?{active},
  ?{id},
  ?{now},
  ?{status},
  ?{inspected_time},
  ?{message}
);