UPDATE BackupStatuses set active=false where active=true and backup_id=?{id};
INSERT INTO BackupStatuses
(
  active,
  now,
  backup_id,
  status,
  message,
  invoked_by
) VALUES (
  ?{active},
  ?{now},
  ?{id},
  ?{status},
  ?{message},
  ?{invoked_by}
);