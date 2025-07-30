SELECT 
    b.deployment_id as id,
    b.build_result_id as 'currentBuildResultId',
    b.status,
    b.now,
    b.running_pid as 'runningPid',
    b.running_process_uid as 'runningProcessUid',
    b.deployed_instance_id as 'deployedInstanceId',
    b.message
FROM  DeploymentStatuses b WHERE b.deployment_id in (?{ids}) LIMIT ?{lim}
