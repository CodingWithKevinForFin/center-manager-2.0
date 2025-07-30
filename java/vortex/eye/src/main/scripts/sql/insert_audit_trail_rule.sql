UPDATE AuditTrailRules set active=false where active=true and id=?{id};
INSERT INTO AuditTrailRules
(
  active,
  id,
  revision,
  now,
  rule_type,
  rules
) VALUES (
  ?{active},
  ?{id},
  ?{revision},
  ?{now},
  ?{rule_type},
  ?{rules}
);