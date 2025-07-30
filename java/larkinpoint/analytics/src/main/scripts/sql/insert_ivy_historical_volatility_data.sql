insert into IvyHistoricalVolatility(
currency_id,
quote_date,
days,
file_id,
security_id,
volatility)
VALUES
(
?{currency_id},
?{quote_date},
?{days},
?{file_id},
?{security_id},
?{volatility}
);


