SELECT 
    id,
    revision,
    name,
    destination_machine_uid as destinationMachineUid,
    metadata as '(STRINGMAP)metadata',
    destination_path as destinationPath
FROM  BackupDestinations where active