SELECT active,checksum,data from FileData where id=?{id} and not (mask & 128)
