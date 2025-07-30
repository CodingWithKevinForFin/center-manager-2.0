INSERT INTO VortexVault
(
  active,
  id,
  now,
  checksum,
  softlink_vvid,
  data,
  data_length
) VALUES (
  ?{active},
  ?{id},
  ?{now},
  ?{checksum},
  ?{softlink_vvid},
  ?{data},
  ?{data_length}
)
