SELECT 
active as 'active',
cusip as 'cusip',
isin as 'isin',
issuer as 'issuer',
revision as 'revision',
sec_id as 'security_id',
sedol as 'sedol',
ticker as 'ticker',
valor as 'valor' ,
expiry_dayofweek as 'expiry_dayofweek',
expiry_weekofmonth as 'expiry_weekofmonth'
FROM options.IvySecurityName