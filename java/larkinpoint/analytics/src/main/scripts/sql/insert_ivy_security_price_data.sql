insert into IvySecurityPrice(
id,
ask,
bid,
close,
adj_factor,
exchange,
cum_total_return,
currency,
file_id,
quote_date,
open,
security_id,
total_return,
volume)
VALUES
(
?{id},
?{ask},
?{bid},
?{close},
?{adj_factor},
?{exchange},
?{cum_total_return},
?{currency},
?{file_id},
?{quote_date},
?{open},
?{security_id},
?{total_return},
?{volume}
);


