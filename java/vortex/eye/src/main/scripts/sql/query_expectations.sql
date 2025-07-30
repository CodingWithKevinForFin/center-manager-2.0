SELECT 
  id,
  revision,
  name,
  now,
  machine_uid as 'machineUid',
  target_type as 'targetType',
  field_masks as '(BYTEMAP)fieldMasks',
  tolerances as '(BYTEMAP)tolerances',
  metadata as '(STRINGMAP)metadata',
  target_metadata as '(BYTEMAP)targetMetadata'
FROM  Expectations where active
