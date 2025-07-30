SELECT 
  close as 'close',
  high as 'high',
  low as 'low',
  open as 'open',         
quote_date as 'quote_date',              
  symbol as 'symbol',      
  volume as 'volume'
FROM DailyUnderlyingData WHERE  symbol = ?{symbol}