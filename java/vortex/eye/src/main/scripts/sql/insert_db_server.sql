UPDATE DbServer set active=false where active=true and id=?{id};
INSERT INTO DbServer
(
  active,
  machine_uid,
  id,
  revision,
  now,
  psw,
  db_type,
  description,
  url,
  hints,
  server_port,
  metadata
) VALUES (
  ?{active},
  ?{machine_uid},
  ?{id},
  ?{revision},
  ?{now},
  ?{psw},
  ?{db_type},
  ?{description},
  ?{url},
  ?{hints},
  ?{server_port},
  ?{metadata}
);
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