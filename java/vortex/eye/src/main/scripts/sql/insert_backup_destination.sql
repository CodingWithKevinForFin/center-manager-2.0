UPDATE BackupDestinations set active=false where active=true and id=?{id};
INSERT INTO BackupDestinations
(
  active,
  id,
  revision,
  now,
  name,
  destination_machine_uid,
  destination_path,
  metadata
) VALUES (
  ?{active},
  ?{id},
  ?{revision},
  ?{now},
  ?{name},
  ?{destinationMachineUid},
  ?{destinationPath},
  ?{metadata}
);