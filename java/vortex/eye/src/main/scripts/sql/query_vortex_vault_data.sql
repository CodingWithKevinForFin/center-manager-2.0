SELECT 
  id,
  checksum,
  softlink_vvid as 'softlinkVvid',
  data,
  data_length as 'dataLength'
from VortexVault where id=?{id} and active
