SELECT 
  id,
  now,
  checksum,
  softlink_vvid as 'softlinkVvid',
  data_length as 'dataLength'
from VortexVault where active
