SELECT
  count(*)
from TailFileEvent where checksum=?{checksum} and file_position=?{file_position} and data=?{data}
