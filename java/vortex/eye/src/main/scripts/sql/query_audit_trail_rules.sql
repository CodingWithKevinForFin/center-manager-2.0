SELECT 
  id,
  revision,
  name,
  now,
  rule_type as 'ruleType',
  rules as '(RULES)rules'
FROM AuditTrailRules where active
