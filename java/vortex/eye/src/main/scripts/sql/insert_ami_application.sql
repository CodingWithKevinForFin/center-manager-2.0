UPDATE AmiApplication set active=false where active=true and id=?{id};
INSERT INTO AmiApplication
(
  active,
  id,
  revision,
  now,
  connections_count,
  host_ip,
  app_id
) VALUES (
  ?{active},
  ?{id},
  ?{revision},
  ?{now},
  ?{connections_count},
  ?{host_ip},
  ?{app_id}
);
UPDATE AmiApplicationParams set active=false,params=if(revision%?{SS_REV}=0,params,null)   where active=true and ami_application_id=?{id};
INSERT INTO AmiApplicationParams
(
  active,
  ami_application_id,
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