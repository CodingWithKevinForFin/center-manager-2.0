# AMIScript: SQL

For this demonstration we need to include company.db and university.db sqlite databases as datasources into AMI. For better comprehension, the examples should be executed in the order presented

## CREATE

```amiscript
CREATE TABLE BusinessTeam (name string, job string, salary int);

CREATE PUBLIC TABLE ManagementTeam (name string);

CREATE TABLE IF NOT EXISTS OpenJobs (job string, id int);

CREATE TEMPORARY TABLE BonusJobs (job string, id int, bonus double);// same as CREATE TABLE ...

CREATE TABLE Graduates (name string, degree string);
```

## CREATE AS

```amiscript
CREATE TABLE Team AS USE ds = "company" EXECUTE SELECT * from Employees;

CREATE TABLE SoftwareTeam AS SELECT name, job, salary FROM Team WHERE job IN ("Programming", "Testing", "DevOps");

CREATE TABLE AnalyzeBudget AS ANALYZE name, job, salary, avg(win.salary) AS avgSalary, median(win.salary) AS medianSalary, salary - median(win.salary) AS diff FROM SoftwareTeam WINDOW win ON true;

CREATE TABLE PrepareBudget AS PREPARE name, job, salary, stack(salary), norm(salary) FROM SoftwareTeam ORDER BY salary ASC PARTITION BY job;

CREATE TABLE DescTable AS DESCRIBE TABLE OpenJobs;

CREATE TABLE ShowTable AS SHOW TEMPORARY TABLES;

CREATE TABLE PrefixTeam AS CALL PrefixFind("Jo", "name", "Team");
```

## ANALYZE and PREPARE

```amiscript
{
  CREATE TABLE data(sym string, price double, hours int);
  INSERT INTO data VALUES
  ("AAPL",180.40,0),
  ("AAPL",175.78,1),
  ("AAPL",173.20,2),
  ("AAPL",169.98,3),
  ("AAPL",176.62,4),
  ("GOOG",150.01,2),
  ("GOOG",148.65,3),
  ("GOOG",149.97,4),
  ("GOOG",152.56,5),
  ("GOOG",157.91,6);
}

// ANALYZE statement to evaluate the minimum, maximum, and average price of a stock in a given period of time.

{
CREATE TABLE AnalyzePrice AS ANALYZE 
sym, price, avg(win.price) AS avgPrice, min(win.price) AS minPrice, max(win.price) AS maxPrice, hours as hours
FROM data
WINDOW win ON hours > 1 && hours < 3
};
```

``` amiscript
{
  CREATE TABLE data(sym string, amount int, price double);
  INSERT INTO data VALUES
  ("AAPL",10,140.23),
  ("GOOG", 100,189.59),
  ("ORCL",20,120.93),
  ("MSFT",50,99.12),
  ("GOOG",10,201.23),
  ("AAPL", 100,120.16),
  ("GOOG",15,198.89),
  ("MSFT",5,108.27),
  ("AAPL", 20,118.12),
  ("GOOG",-120,204.36),
  ("MSFT",-50,110.66);
}

// PREPARE statement to take a running total of stocks being bought and sold and how much has been spent

{
  CREATE TABLE TotalSpend AS PREPARE 
  sym, amount, price, stack(amount) as runningTot, stack(price*amount) as totSpent 
  FROM data 
  ORDER BY price ASC 
  PARTITION BY sym;
}
```
## INSERT INTO ... VALUES

```amiscript
INSERT INTO OpenJobs VALUES ("Programming", 1),("Sales", 2), ("Management", 3), ("Finance", 4), ("DevOps", 5), ("Fax", -1) LIMIT 5;

Double discountFactor1 = 1.03, discountFactor2 = 1.05, discountFactor3 = 1.09, discountFactor6 = 1.08;

Double bonusAmount1 = 25, bonusAmount2 = 20, bonusAmount3 = 30, bonusAmount6 = 15;

INSERT INTO BonusJobs VALUES ("Programming", 1, power(bonusAmount1, discountFactor1)), ("Sales", 2, power(bonusAmount2, discountFactor2)), ("Management", 3, power(bonusAmount3, discountFactor3)), ("Machine Learning,AI", 6, power(bonusAmount6, discountFactor6));

INSERT INTO Graduates VALUES ("Jeff", "Programming"), ("Peter", "Machine Learning"), ("Jennifer", "Finance"), ("Michelle", "Sales");
```

## INSERT INTO ... FROM

```amiscript
INSERT INTO Team FROM SELECT name, degree, 80 FROM Graduates;

INSERT INTO ManagementTeam FROM SELECT name from Team WHERE job == "Management";

INSERT INTO BusinessTeam FROM SELECT name, job, salary FROM Team WHERE job IN ("Management", "Sales", "Operations");

INSERT INTO Team USE ds = "university" EXECUTE SELECT name, subject, 2 * salary FROM Professors;

INSERT INTO AnalyzeBudget FROM ANALYZE name, job, salary, avg(win.salary) AS avgSalary, median(win.salary) AS medianSalary, salary - median(win.salary) AS diff FROM BusinessTeam WINDOW win ON true;

INSERT INTO PrepareBudget FROM PREPARE name, job, salary, stack(salary), norm(salary) FROM BusinessTeam ORDER BY salary ASC PARTITION BY job;

INSERT INTO DescTable FROM DESCRIBE TABLE Team;

INSERT INTO ShowTable FROM SHOW PUBLIC TABLES;

INSERT INTO PrefixTeam FROM CALL PrefixFind("Ma", "name", "Team");
```

## INSERT INTO ... BYNAME FROM

```amiscript
INSERT INTO MiamiHeat BYNAME FROM SELECT * FROM IndianaPacers;

INSERT INTO MiamiHeat BYNAME FROM SELECT *, a AS b FROM IndianaPacers; // manually map column a to column b during the insertion
```

## ALTER

```amiscript
ALTER TABLE Team MODIFY salary AS salary BigInteger;

ALTER TABLE Graduates RENAME degree TO profession;

ALTER TABLE Graduates ADD income int BEFORE profession;

ALTER TABLE Graduates DROP income;

ALTER TABLE Graduates ADD income int;
```

## RENAME

```amiscript
RENAME TABLE ManagementTeam TO Leaders;
```

## DESCRIBE

```amiscript
DESCRIBE TABLE Team;
```

## SELECT

```amiscript
SELECT * FROM Team;

SELECT * FROM Team LIMIT 3;

SELECT * FROM Team LIMIT 3, 7;

SELECT * FROM Team WHERE job == "Programming" OR job == "Testing";

SELECT * FROM Team WHERE job IN ("Finance", "Machine Learning");

SELECT * FROM Team WHERE salary < 100;

SELECT * FROM Team WHERE job == "Programming" AND salary == 100;

SELECT * FROM Team WHERE (job, salary) IN ("Programming", 100);

SELECT * FROM Team ORDER BY salary ASC;

SELECT sum(salary) AS Budget FROM Team;

SELECT job, count(*) AS numberOfEmployees, sum(salary) AS jobBudget, avg(salary) AS jobAvgSalary FROM Team GROUP BY job ORDER BY numberOfEmployees;

SELECT job, count(*) AS numberOfEmployees, sum(salary) AS jobBudget, avg(salary) AS jobAvgSalary FROM Team GROUP BY job HAVING numberOfEmployees >=2;

SELECT * EXCEPT (salary) from Team;

SELECT job FROM Team GROUP BY job;

SELECT * FROM OpenJobs, BonusJobs WHERE OpenJobs.id == BonusJobs.id;

SELECT OpenJobs.job AS job, OpenJobs.id AS id, bonus FROM OpenJobs, BonusJobs WHERE OpenJobs.id == BonusJobs.id;

SELECT * FROM Team WHERE job IN (SELECT job FROM OpenJobs);

SELECT * FROM OpenJobs JOIN BonusJobs on OpenJobs.id == BonusJobs.id;

SELECT * FROM OpenJobs OUTER JOIN BonusJobs on OpenJobs.id == BonusJobs.id;

SELECT * FROM OpenJobs OUTER ONLY JOIN BonusJobs on OpenJobs.id == BonusJobs.id;

SELECT * FROM OpenJobs LEFT JOIN BonusJobs on OpenJobs.id == BonusJobs.id;

SELECT * FROM OpenJobs LEFT ONLY JOIN BonusJobs on OpenJobs.id == BonusJobs.id;

SELECT * FROM OpenJobs RIGHT JOIN BonusJobs on OpenJobs.id == BonusJobs.id;

SELECT * FROM OpenJobs RIGHT ONLY JOIN BonusJobs on OpenJobs.id == BonusJobs.id;

SELECT * FROM BonusJobs UNPACK job ON ",";
```

## UPDATE

```amiscript
UPDATE Graduates SET income = 50;

UPDATE Team SET salary = salary + 5 WHERE job == "Sales";

UPDATE Team JOIN BonusJobs ON BonusJobs.job =~ Team.job SET salary = salary + bonus;
```

## USE ... EXECUTE

```amiscript
USE ds = "company" EXECUTE SELECT * from Employees;

CREATE TABLE Students AS USE ds = "university" EXECUTE SELECT * from Students;
```

## USE ... SHOW

```amiscript
USE ds = "university" SHOW TABLES;
```

## USE ... INSERT

```amiscript
USE ds = "company" INSERT INTO Employees VALUES ("Kevin", "DevOps", 110);

USE ds = "company" INSERT INTO Employees SELECT name, profession, 80 FROM Graduates;

USE ds = "company" INSERT INTO Employees USE ds = "university" EXECUTE SELECT name, subject, 2 * salary FROM Professors;
```

## SYNC

```amiscript
SYNC INTO Graduates FROM SELECT * FROM Team WHERE salary < 90;

SYNC INTO Graduates USE ds = "university" EXECUTE SELECT name, subject, 80 FROM Students;

SYNC INTO Graduates ON(name) FROM SELECT * FROM Team;

SYNC INTO AnalyzeBudget FROM ANALYZE name, job, salary, avg(win.salary) AS avgSalary, median(win.salary) AS medianSalary, salary - median(win.salary) AS diff FROM Team WINDOW win ON true;

SYNC INTO PrepareBudget PREPARE name, job, salary, stack(salary), norm(salary) FROM Team ORDER BY salary ASC PARTITION BY job;

```

## DELETE FROM

```amiscript
DELETE FROM Graduates;

DELETE FROM OpenJobs WHERE id == 1;

DELETE FROM Team WHERE job == "Sales" LIMIT 2;

DELETE FROM Team WHERE job == "Programming" LIMIT 2,1;

DELETE FROM OpenJobs JOIN BonusJobs ON OpenJobs.id == BonusJobs.id;

DELETE FROM Team JOIN OpenJobs ON Team.job == OpenJobs.job;

DELETE FROM SoftwareTeam LEFT ONLY JOIN BonusJobs ON SoftwareTeam.job == BonusJobs.job;
```

## TRUNCATE

```amiscript
TRUNCATE TABLE SoftwareTeam;
```

## DROP

```amiscript
DROP TABLE Graduates, BonusJobs;
DROP TABLE IF EXISTS Team;
```

## Historical Tables

### CREATE
```amiscript
//create a PARTITION Column on TradeDate
CREATE PUBLIC TABLE HistoricalOrders(TradeDate LONG PARTITION, TradeId int, Symbol String, Price double, Quantity int) use PersistEngine="HISTORICAL";
```

Likewise, you could declare a '''BITMAP, FLAT, VARSIZE''' column in a similar manner. The general
format is '''<DATA_TYPE> + <STORAGE_TYPE>''':

```amiscript
//create a BITMAP2 STRING Column on Symbol
CREATE PUBLIC TABLE HistoricalOrders(TradeDate LONG PARTITION, TradeId int, Symbol String BITMAP2, Price double, Quantity int) use PersistEngine="HISTORICAL";
```

```amiscript
//create a VARSIZE3 STRING Column on Symbol
CREATE PUBLIC TABLE HistoricalOrders(TradeDate LONG PARTITION, TradeId int, Symbol String VARSIZE3, Price double, Quantity int) use PersistEngine="HISTORICAL";
```

```amiscript
//create a FLAT_NONULL Integer Column on Quantity
CREATE PUBLIC TABLE HistoricalOrders(TradeDate LONG PARTITION, TradeId int, Symbol String, Price double, Quantity int FLAT_NONULL) use PersistEngine="HISTORICAL";
```

### CALL __OPTIMIZE_HISTORICAL_TABLE
AMIHDB has a unique optimization procedure that helps optimize the storage of data in the historical table and writes it to disk. To call this procedure, the syntax is as follows:
```
CALL __OPTIMIZE_HISTORICAL_TABLE("HistoricalOrders");
```

### DIAGNOSE
In order for the end user to have a detailed view of a particular historical table, we can use the
following syntax to diagnose the table:
```
DIAGNOSE TABLE HistoricalOrders;
```

### CREATE INDEX
```
//create a sort index on Quantity column
CREATE INDEX sortQuantityIndex on HistoricalOrders(Quantity);
```
