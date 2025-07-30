insert into IvyVolSurface(
id,
security_id,
quote_date,
days,
delta,
cp,
implied_vol,
implied_strike,
implied_premium,
dispersion,
currency_id,
file_id)
VALUES
(
?{id},
?{security_id},
?{quote_date},
?{days},
?{delta},
?{cp},
?{implied_vol},
?{implied_strike},
?{implied_premium},
?{dispersion},
?{currency_id},
?{file_id}
);


