UPDATE DeploymentStatuses set active=false where active=true and deployment_id=?{deployment_id};
INSERT INTO DeploymentStatuses
(
  active,
  now,
  deployment_id,
  status,
  running_pid,
  running_process_uid,
  message,
  build_result_id,
  deployed_instance_id,
  build_invoked_by
) VALUES (
  ?{active},
  ?{now},
  ?{deployment_id},
  ?{status},
  ?{running_pid},
  ?{running_process_uid},
  ?{message},
  ?{build_result_id},
  ?{deployed_instance_id},
  ?{build_invoked_by}
);