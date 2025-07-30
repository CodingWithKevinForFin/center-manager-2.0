UPDATE Backups set active=false where active=true and id=?{id};
INSERT INTO Backups
(
  active,
  id,
  revision,
  now,
  deployment_id,
  description,
  source_path,
  source_machine_uid,
  backup_destination_id,
  ignore_expression,
  options,
  metadata
) VALUES (
  ?{active},
  ?{id},
  ?{revision},
  ?{now},
  ?{deployment_id},
  ?{description},
  ?{source_path},
  ?{source_machine_uid},
  ?{backup_destination_id},
  ?{ignore_expression},
  ?{options},
  ?{metadata}
);
UPDATE BackupStatuses set active=false where active=true and backup_id=?{id};
INSERT INTO BackupStatuses
(
  active,
  now,
  backup_id,
  status,
  message,
  file_count,
  ignored_file_count,
  bytes_count,
  latest_modified_time,
  manifest_vvid,
  manifest_length,
  manifest_time,
  invoked_by
) VALUES (
  ?{active},
  ?{now},
  ?{id},
  ?{status},
  ?{message},
  ?{file_count},
  ?{ignored_file_count},
  ?{bytes_count},
  ?{latest_modified_time},
  ?{manifest_vvid},
  ?{manifest_length},
  ?{manifest_time},
  ?{invoked_by}
);