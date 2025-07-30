insert into IvyStdOptionPrice(
cp,
currency_id,
days,
delta,
file_id,
forward,
gamma,
implied_vol,
premium,
quote_date,
security_id,
strike_price,
theta,
vega)
VALUES
(
?{cp},
?{currency_id},
?{days},
?{delta},
?{file_id},
?{forward},
?{gamma},
?{implied_vol},
?{premium},
?{quote_date},
?{security_id},
?{strike_price},
?{theta},
?{vega}
);


