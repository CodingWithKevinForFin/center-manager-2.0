UPDATE AmiObject set active=false where active=true and id=?{id};
INSERT INTO AmiObject
(
  active,
  id,
  revision,
  now,
  ami_application_id,
  object_type,
  object_id,
  expires
) VALUES (
  ?{active},
  ?{id},
  ?{revision},
  ?{now},
  ?{ami_application_id},
  ?{object_type},
  ?{object_id},
  ?{expires}
);
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
  ?{active},
  ?{id},
  ?{revision},
  ?{now},
  ?{params},
  ?{params_delta}
);