SELECT 
  last as 'last',
  bid as 'bid',
  ask as 'ask',
  callput as 'cp',         
  quote_date as 'quote_date',              
  volume as 'volume',
  expiry_date as 'expiry',
  strike_price as 'strike_price',
  symbol as 'option_symbol',
  open_interest as 'open_interest',
  file_id as 'file_id',
  implied_vol as 'implied_vol',
  vega as 'vega',
  gamma as 'gamma',
  delta as 'delta',
  theta as 'theta',
  option_id as 'option_id'
FROM IvyOptionPrices   WHERE  security_id =?{security_id} and quote_date >= ?{qdate1} and quote_date <= ?{qdate2} 
	