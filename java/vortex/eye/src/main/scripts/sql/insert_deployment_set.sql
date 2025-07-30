UPDATE DeploymentSets set active=false where active=true and id=?{id};
INSERT INTO DeploymentSets
(
  active,
  id,
  revision,
  now,
  name,
  properties,
  metadata
) VALUES (
  ?{active},
  ?{id},
  ?{revision},
  ?{now},
  ?{name},
  ?{properties},
  ?{metadata}
);