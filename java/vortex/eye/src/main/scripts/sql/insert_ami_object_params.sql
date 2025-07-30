UPDATE AmiObjectParams set active=false,params=if(revision%?{SS_REV}=0,params,null)   where active=true and ami_object_id=?{id};
INSERT INTO AmiObjectParams
(
  active,
  ami_object_id,
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