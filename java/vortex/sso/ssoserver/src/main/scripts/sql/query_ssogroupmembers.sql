SELECT
  id,
  group_id as "groupId",
  member_id as "memberId",
  revision as "revision"
FROM SsoGroupMembers WHERE active;
