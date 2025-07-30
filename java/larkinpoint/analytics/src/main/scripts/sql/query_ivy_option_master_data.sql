SELECT 
exchange as 'exchange' ,
expiry_date as 'expiry_date',
id as 'id',
option_id as 'option_id',
security_id as 'security_id',
strike_price as 'strike_price',
callput as 'callput'
FROM IvyOptionMaster where security_id = ?{security_id}