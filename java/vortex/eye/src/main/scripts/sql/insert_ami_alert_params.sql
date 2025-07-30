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
  true,
  ?{id},
  ?{revision},
  ?{now},
  ?{params},
  ?{params_delta}
);