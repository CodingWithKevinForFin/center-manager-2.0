SELECT 
    id,
    revision,
    now,
    dbserver_id as 'dbServerId', 
    status,
    message
FROM  DbSeverStatus where active



