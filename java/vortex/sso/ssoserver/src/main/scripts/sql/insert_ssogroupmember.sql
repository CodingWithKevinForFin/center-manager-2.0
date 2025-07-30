UPDATE SsoGroupMembers set active=false where group_id=?{group_id} and member_id=?{member_id} and active;
INSERT INTO SsoGroupMembers (
  active,
  id,
  revision,
  now,
  group_id,
  member_id
) VALUES (
  ?{active},
  ?{id},
  ?{revision},
  ?{now},
  ?{group_id},
  ?{member_id}
);
