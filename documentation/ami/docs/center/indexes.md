# Indexes

Indexes are data structures that improve the speed of `#!amiscript SELECT ... WHERE` queries. Without an index, a `#!amiscript SELECT ... WHERE` query needs to scan and consider each row. However, if there is an index on the column referenced in the `#!amiscript WHERE` clause then the query can quickly locate the desired row.

## CREATE INDEX

When creating an index, there are three different data structures to consider:

-   `HASH`: Under the hood this is a hashmap. This is the fastest most versatile indexing approach but is only useful for queries that directly use equality, because the data is stored in an unsorted fashion.
-   `SORT`: Under the hood this is a treemap. This has additional overhead to a HASH index for both inserting and querying but can be used with ordinality comparisons, such as less than, greater than as well as equals.
-   `SERIES`: Under the hood this is a sorted array. This is a special purpose and highly optimized index useful for when data will be inserted in ascending order, as entries can quickly be added to the end of the array, and queries (including ordinal searches) can use a binary lookup.  If data is out of order the array is forced to do a memcopy which can be very expensive.

Constraints can be added to an index to determine the outcome of a key collision. Three different types of constraints are supported:

-   `NONE`: If a constraint is not supplied, this is the default. There is no restriction on having multiple rows with the same key.
-   `UNIQUE`: An attempt to insert (or update) a row such that two rows in the table will have the same key will fail.
-   `PRIMARY`: An attempt to insert a row with the same key as an existing row will cause the existing row to be updated instead of a new row being inserted (specifically, those cells specified and not participating in the index will be updated). This can be thought of as an "UPSERT" in other popular databases. An attempt to update a row such that two rows in the table will have the same key will fail. Each table can have at most one `PRIMARY` index.

Primary indexes can also be automatically generated on a particular column using `AUTOGEN`, where two options are available:

- `RAND`: A random UID is assigned to the column with a unique value for each row. 
- `INC` : An auto-incrementing UID is assigned to the column with a unique value for each row, starting at 0 for the first row, 1 for the second row and etc. Note that this option is only supported for `INT` and `LONG`columns. Please refer to *[Using AUTOGEN In a Primary Index](https://doc.3forge.com/center/indexes/?h=index#autogen)* for detailed examples.

### Single indexes

```amiscript
CREATE INDEX [IF NOT EXISTS] idx_name
	ON tbl_name(col_name [HASH|SORT|SERIES] [,col_name [HASH|SORT|SERIES] ...])
	[USE  CONSTRAINT="[NONE|UNIQUE|PRIMARY]"]
```

-	`idx_name`:	Name of the index, each index's name must be unique for the table
-	`tbl_name`:	Name of the table to add the index to
-	`col_name`:	Name of the column to put the index on

#### Example

The command below will create a primary index on the column `Name`:

```amiscript
CREATE INDEX MyIndex ON MyTable(Name HASH) USE CONSTRAINT="PRIMARY"
```
### Joint Indexes

Joint indexes are indexes that span across multiple columns in a table. The syntax is:

```amiscript
CREATE INDEX myIndex ON MyTable(A <Type>,B <Type>,C <Type>,....);
```

Joint indexes are helpful when multiple columns participate in your query's where clause. It will enable the query optimizer to find out the best path to return the results by reducing the search space.

!!! note

	Order matters. The two statements below will produce different indexes:
	
	```amiscript
	CREATE INDEX myIndex ON MyTable(A HASH,B HASH);
	CREATE INDEX myIndex ON MyTable(B HASH,A HASH);
	```
	
	If you create an index on column A and column B, it first creates the index on column A and for every value in column A, it creates the second index on column B.
	
	This means it's important to put **high cardinality columns first**. If `A` is a binary value (e.g. BUY or SELL) but `B` is a higher cardinality value (e.g. asset symbol) then we should put `B` first. This is because you are more likely to reduce the search space more in the initial lookup.

#### Example 1

Given the table *orders*:

```amiscript
CREATE PUBLIC TABLE ORDERS(orderId int, Symbol string, Region string, Quantity int, Price double);

List regionList = new list("Asia","North America","South America","Europe","Africa");

for(int i=0;i<1000000;i++){ INSERT INTO ORDERS VALUES(rand(10000), "sym"+rand(1000),(string) (regionList.get(rand(5))), 500+rand(1000), rand()*200); }
```

Since Symbol has a cardinality of 500 and Region of 5. It is advisable to first create index on Symbol then Region, ie:  

```amiscript
CREATE INDEX jointIndex ON orders(Symbol HASH, Region HASH);
```

Now let's run some concrete queries to see how this joint index helps increase the query speed.

-   Case 1: full usage of the joint index

	```amiscript
	SELECT * FROM orders WHERE Symbol=="sym500" AND Region=="North America"; //use both part of the jointIndex
	```

-   Case 2: partial usage of the joint index

	```amiscript
	SELECT * FROM orders WHERE Symbol=="sym500"; //use only "Symbol" part of the jointIndex
	```

-   Case 3: no usage of the joint index

	```amiscript
	SELECT * FROM orders WHERE Region=="North America"; //not using index, do a hard forward scan
	
	SELECT * FROM orders WHERE Region=="North America" AND Symbol=="sym500"; //not using index, do a hard forward scan
	```

#### Example 2

This example will add an index to the MyTable's Account and Price column. Queries that use the Account column or the Account and Price column will be faster.  Note that queries using only the Price column will not sped up by this index. This is an ideal index if we know where going to be running queries of the form: select ... from MyIndex where Account==*somevalue* and Price\[ \<, ==, \> \] *somevalue*

```amiscript
CREATE INDEX MyIndex ON MyTable(Account HASH, Price SORT)

CREATE INDEX MyIndex ON MyTable(Name HASH) USE CONSTRAINT="PRIMARY"
```


### AUTOGEN

#### Column Types Supported Using AUTOGEN

The table below outlines AMI table column types that are supported using `AUTOGEN` options, where `Y` denotes supported and `N` denotes unsupported.

| `ColumnType\Option` | `INC` | `RAND` |
|---------------------|-------|--------|
| INT                 | Y     | Y      |
| LONG                | Y     | Y      |
| STRING              | N     | Y      |
| UUID                | N     | Y      |
| FLOAT               | N     | Y      |
| DOUBLE              | N     | Y      |
| Anything else       | N     | N      |

#### AUTOGEN="RAND"

This example demonstrates how to use `AUTOGEN` to create automatically generated random primary indexes on a string column.
Assume the table below exists:

```amiscript
CREATE PUBLIC table MarketOrders(Id String, Symbol String, Quantity Int);
```

The command below will create a primary index on the column `Id String` using `RAND` option:

```amiscript
CREATE INDEX RandomIndex ON MarketOrders(Id HASH) USE CONSTRAINT="PRIMARY" AUTOGEN="RAND"
```

The randomly generated indexes will look something like this:

```amiscript
INSERT INTO MarketOrders(Symbol, Quantity) values("AAPL",20),("IBM",30),("MSFT",50);
SELECT Id FROM MarketOrders;
```

| Id <BR> String |
|----------------|
| FdZXX4BWEN     |
| NcbSCeVusm     |
| IOVQJkLDbX     |

#### AUTOGEN="INC"

This example demonstrates how to use `AUTOGEN` to create automatically generated auto-incrementing primary indexes on a integer column.
Assume the table below exists:

```amiscript
CREATE PUBLIC table MarketOrders2(Id Int, Symbol String, Quantity Int);
```

The command below will create a primary index on the column `Id Integer` using `INC` option:

```amiscript
CREATE INDEX RandomIndex2 ON MarketOrders2(Id HASH) USE CONSTRAINT="PRIMARY" AUTOGEN="INC"
```

The randomly generated indexes will look something like this:

```amiscript
INSERT INTO MarketOrders2(Symbol, Quantity) values("AAPL",20),("IBM",30),("MSFT",50);
SELECT Id FROM MarketOrders2;
```

| Id <BR> Integer |
|-----------------|
| 0               |
| 1               |
| 2               |

## DROP INDEX

Removes the specified index from the given table, as created by CREATE INDEX

```amiscript
DROP INDEX [IF EXISTS] index_name ON tbl_name [,index_name ON tbl_name...]
```

#### Example

This example will drop the index named MyIndex from the MyTable table. 

```amiscript
Drop index MyIndex on MyTable
```
