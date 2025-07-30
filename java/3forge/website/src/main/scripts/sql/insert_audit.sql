INSERT INTO Audit(
  id,
  created_on,
  user_id,
  username,
  session_id ,
  remote_addr,
  audit,
  description
) VALUES (
  ?{id},
  ?{createdOn},
  ?{userId},
  ?{username},
  ?{sessionId},
  ?{remoteAddr},
  ?{audit},
  ?{description}
);