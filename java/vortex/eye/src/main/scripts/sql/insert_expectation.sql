UPDATE Expectations set active=false where active=true and id=?{id};
INSERT INTO Expectations
(
  active,
  id,
  revision,
  now,
  name,
  machine_uid,
  target_type,
  field_masks,
  tolerances,
  target_metadata,
  metadata
) VALUES (
  ?{active},
  ?{id},
  ?{revision},
  ?{now},
  ?{name},
  ?{machine_uid},
  ?{target_type},
  ?{field_masks},
  ?{tolerances},
  ?{target_metadata},
  ?{metadata}
);