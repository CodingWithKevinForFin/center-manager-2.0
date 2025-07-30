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
  true,
  ?{id},
  ?{revision},
  ?{now},
  ?{params},
  ?{params_delta}
);