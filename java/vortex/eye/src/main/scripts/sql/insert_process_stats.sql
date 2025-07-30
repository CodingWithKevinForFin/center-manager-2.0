UPDATE ProcessStats SET active=false where active=true AND process_id=?{id};
INSERT INTO ProcessStats
(
  active,
  process_id,
  now,
  memory,
  cpu_percent
) VALUES (
  true,
  ?{id},
  ?{now},
  ?{memory},
  ?{cpu_percent}
);