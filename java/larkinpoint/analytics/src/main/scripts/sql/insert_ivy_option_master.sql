INSERT INTO IvyOptionMaster
(exchange,
expiry_date,
id,
option_id,
security_id,
strike_price,
callput)
VALUES
(
?{exchange},
?{expiry_date},
?{id},
?{option_id},
?{security_id},
?{strike_price},
?{callput}
);
