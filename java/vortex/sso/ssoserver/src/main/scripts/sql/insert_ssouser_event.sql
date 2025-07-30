INSERT INTO SsoUserEvent
(
  ssouser_id,
  now,
  supplied_user_name,
  supplied_email,
  event_type,
  name_space
) VALUES (
  ?{ssouser_id},
  ?{now},
  ?{supplied_user_name},
  ?{supplied_email},
  ?{event_type},
  ?{name_space}
);