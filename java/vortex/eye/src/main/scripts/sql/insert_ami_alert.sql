UPDATE AmiAlert set active=false where active=true and id=?{id};
INSERT INTO AmiAlert
(
  active,
  id,
  revision,
  now,
  ami_application_id,
  level,
  alert_type,
  alert_id,
  expires,
  assigned_to
) VALUES (
  true,
  ?{id},
  ?{revision},
  ?{now},
  ?{ami_application_id},
  ?{level},
  ?{alert_type},
  ?{alert_id},
  ?{expires},
  ?{assigned_to}
);
UPDATE AmiAlertParams set active=false,params=if(revision%?{SS_REV}=0,params,null)   where active=true and ami_alert_id=?{id};
INSERT INTO AmiAlertParams
(
  active,
  ami_alert_id,
  revision,
  now,
  params,
  params_delta
) VALUES (
  ?{active},
  ?{id},
  ?{revision},
  ?{now},
  ?{params},
  ?{params_delta}
);