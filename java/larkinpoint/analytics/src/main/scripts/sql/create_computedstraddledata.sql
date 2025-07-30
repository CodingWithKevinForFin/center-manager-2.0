CREATE TABLE `ComputedStraddleData` (
  `symbol` varchar(20) DEFAULT NULL,
  `expiry_date` date DEFAULT NULL,
  `strike_price` double(11,5) DEFAULT NULL,
  `quote_date` date DEFAULT NULL,
  `under_close` double(11,5) DEFAULT NULL,
  `last` double(11,5) DEFAULT NULL,
  `bid` double(11,5) DEFAULT NULL,
  `ask` double(11,5) DEFAULT NULL,
  `norm_strike` double(11,5) DEFAULT NULL,
  `id` int(11) NOT NULL AUTO_INCREMENT,
  UNIQUE KEY `id` (`id`)
);