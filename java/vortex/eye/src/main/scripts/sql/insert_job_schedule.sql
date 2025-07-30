UPDATE JobSchedules SET active=false WHERE active=true AND id=?{id};
INSERT INTO JobSchedules
(
  active,
  id,
  user_name,
  machine_instance_id,
  revision,
  second,
  minute,
  hour,
  day_of_month,
  month,
  day_of_week,
  timezone,
  now,
  command
) values (
  ?{active},
  ?{id},
  ?{user_name},
  ?{machine_instance_id},
  ?{revision},
  ?{second},
  ?{minute},
  ?{hour},
  ?{day_of_month},
  ?{month},
  ?{day_of_week},
  ?{timezone},
  ?{now},
  ?{command}
)

