UPDATE ScheduledTaskStatuses set active=false where active=true and scheduled_task_id=?{id};
INSERT INTO ScheduledTaskStatuses
(
  active,
  now,
  scheduled_task_id,
  status,
  message,
  invoked_by,
  next_runtime,
  last_runtime,
  run_count
) VALUES (
  ?{active},
  ?{now},
  ?{id},
  ?{status},
  ?{message},
  ?{invoked_by},
  ?{nextRuntime},
  ?{lastRuntime},
  ?{runCount}
);