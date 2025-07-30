SELECT 
  close as 'close',
  bid as 'bid',
  ask as 'ask',
  open as 'open',         
  quote_date as 'quote_date',                  
  volume as 'volume',
  total_return as 'total_return',
  security_id as 'security_id'
FROM IvySecurityPrice  WHERE security_id = ?{security_id}