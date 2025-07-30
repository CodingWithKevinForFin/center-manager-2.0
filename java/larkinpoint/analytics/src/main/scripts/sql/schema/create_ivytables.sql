use options;

CREATE TABLE `IvyCurrency` (
  `currency_id` int(11) NOT NULL,
  `symbol` varchar(10) DEFAULT NULL,
  `name` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`currency_id`)
) 

CREATE TABLE `IvyOptionMaster` (
  `id` bigint(20) NOT NULL,
  `option_id` int(11) NOT NULL,
  `expiry_date` date NOT NULL,
  `strike_price` double(11,5) NOT NULL,
  `securitiy_id` int(11) NOT NULL,
  `exchange` int(11) NOT NULL,
  PRIMARY KEY (`id`,`expiry_date`,`option_id`,`strike_price`,`securitiy_id`,`exchange`)
) 


CREATE TABLE `IvyExchange` (
  `exchange_code` int(11) NOT NULL,
  `symbol` varchar(3) DEFAULT NULL,
  `country` varchar(30) DEFAULT NULL,
  `name` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`exchange_code`)
) 




CREATE TABLE `IvyHistoricalVolatility` (
  `security_id` int(11) NOT NULL,
  `date` date NOT NULL,
  `days` int(11) NOT NULL,
  `currency_id` int(11) NOT NULL,
  `volatility` double(11,5) DEFAULT NULL,
  PRIMARY KEY (`security_id`,`date`,`days`,`currency_id`)
) 



CREATE TABLE `IvyIndexDividend` (
  `security_id` int(11) NOT NULL,
  `date` date NOT NULL,
  `expiration` date NOT NULL,
  `yield` double(11,5) DEFAULT NULL,
  PRIMARY KEY (`security_id`,`date`,`expiration`)
) 



CREATE TABLE `IvyOptionPrices` (
  `security_id` int(11) NOT NULL,
  `quote_date` date NOT NULL,
  `option_id` int(11) NOT NULL,
  `exchange` int(11) NOT NULL,
  `currency` int(11) DEFAULT NULL,
  `expiry_date` date DEFAULT NULL,
  `strike_price` double(11,5) DEFAULT NULL,
  `callput` smallint(1) DEFAULT NULL,
  `symbol` varchar(21) DEFAULT NULL,
  `bid` double(11,5) DEFAULT NULL,
  `ask` double(11,5) DEFAULT NULL,
  `last` double(11,5) DEFAULT NULL,
  `implied_vol` double(11,5) DEFAULT NULL,
  `delta` double(11,5) DEFAULT NULL,
  `gamma` double(11,5) DEFAULT NULL,
  `vega` double(11,5) DEFAULT NULL,
  `theta` double(11,5) DEFAULT NULL,
  `adj_factor` double(11,5) DEFAULT NULL,
  `volume` bigint(10) DEFAULT NULL,
  `open_interest` int(11) DEFAULT NULL,
  `special_settlement` int(11) DEFAULT NULL,
  `exercise_style` char(1) DEFAULT NULL,
  `symbol_flag` int(11) DEFAULT NULL,
  PRIMARY KEY (`security_id`,`date`,`option_id`,`exchange`)
) 



CREATE TABLE `IvySecurityName` (
  `sec_id` int(11) NOT NULL,
  `cusip` varchar(12) DEFAULT NULL,
  `effective_date` date DEFAULT NULL,
  `ticker` varchar(12) DEFAULT NULL,
  `issuer` varchar(255) DEFAULT NULL,
  `isin` varchar(12) DEFAULT NULL,
  `valor` int(11) DEFAULT NULL,
  `sedol` varchar(4) DEFAULT NULL,
  `active` tinyint(1) DEFAULT NULL,
  `revision` smallint(5) unsigned DEFAULT NULL,
  `file_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`sec_id`)
) 



CREATE TABLE `IvySecurityPrice` (
  `security_id` int(11) NOT NULL,
  `quote_date` date NOT NULL,
  `exchange` int(11) NOT NULL,
  `currency` double(11,5) DEFAULT NULL,
  `bid` double(11,5) DEFAULT NULL,
  `ask` double(11,5) DEFAULT NULL,
  `open` double(11,5) DEFAULT NULL,
  `close` double(11,5) DEFAULT NULL,
  `total_return` double(11,5) DEFAULT NULL,
  `adj_factor` double(11,5) DEFAULT NULL,
  `cum_total_return` double(11,5) DEFAULT NULL,
  `volume` bigint(10) DEFAULT NULL,
  `file_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`security_id`,`date`,`exchange`)
) 




CREATE TABLE `IvyStdOptionPrice` (
  `security_id` int(11) NOT NULL,
  `quote_date` date NOT NULL,
  `days` double(11,5) NOT NULL,
  `forward` double(11,5) DEFAULT NULL,
  `strike_price` double(11,5) DEFAULT NULL,
  `cp` smallint(1) NOT NULL,
  `premium` double(11,5) DEFAULT NULL,
  `implied_vol` double(11,5) DEFAULT NULL,
  `delta` double(11,5) DEFAULT NULL,
  `gamma` double(11,5) DEFAULT NULL,
  `vega` double(11,5) DEFAULT NULL,
  `theta` double(11,5) DEFAULT NULL,
  `currency_id` int(11) NOT NULL,
  PRIMARY KEY (`security_id`,`date`,`days`,`cp`,`currency_id`)
) 




CREATE TABLE `IvyVolSurface` (
  `security_id` int(11) NOT NULL,
  `quote_date` date NOT NULL,
  `days` int(11) NOT NULL,
  `delta` int(11) NOT NULL,
  `cp` char(1) NOT NULL,
  `implied_vol` double(11,5) DEFAULT NULL,
  `implied_strike` double(11,5) DEFAULT NULL,
  `implied_premium` double(11,5) DEFAULT NULL,
  `dispersion` double(11,5) DEFAULT NULL,
  `currency_id` int(11) NOT NULL,
  PRIMARY KEY (`security_id`,`date`,`days`,`delta`,`cp`,`currency_id`)
) 




CREATE TABLE `IvyZeroCurves` (
  `currency_id` int(11) NOT NULL,
  `quote_date` date NOT NULL,
  `days` int(11) NOT NULL,
  `rate` double(11,5) DEFAULT NULL,
  PRIMARY KEY (`currency_id`,`date`,`days`)
) 


