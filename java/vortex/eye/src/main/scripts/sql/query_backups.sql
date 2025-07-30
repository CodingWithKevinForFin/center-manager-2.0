SELECT 
    a.id,
    a.revision,
    a.deployment_id as deploymentId,
    a.source_machine_uid as sourceMachineUid,
    a.source_path as sourcePath,
    a.backup_destination_id as backupDestinationId,
    a.metadata as '(STRINGMAP)metadata',
    a.options,
    a.description,
    a.ignore_expression as 'ignoreExpression',
    b.status,
    b.now,
    b.message,
    b.file_count as 'fileCount',
    b.ignored_file_count as 'ignoredFileCount',
    b.bytes_count as 'bytesCount',
    b.latest_modified_time as 'latestModifiedTime',
    b.manifest_vvid as 'manifestVvid',
    b.manifest_length as 'manifestLength',
    b.manifest_time as 'manifestTime',
    b.status as 'status'
FROM  Backups a JOIN BackupStatuses b ON a.id=b.backup_id 
WHERE a.active and b.active
