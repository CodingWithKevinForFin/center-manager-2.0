# Center Replication

## Overview

Data replication across multiple centers is supported by AMI for horizontal scaling. This can either be realtime replication -- data subscription from one center to another -- or for transferring some historical data across centers. 

Center replication is done via the AMIDB Shell Tool and requires the following:

-   At least 2 AMI centers running, either locally or accessible via browser (see [this](../architecture/advanced_setup.md/#configuring-multiple-instances-on-a-single-machine) guide to configure two instances of AMI on the same machine).
-   The table schema of interest predefined on the source and target center(s).

## Realtime Replication

Realtime replication allows for subscription to data from a different (source) center which is then replicated on the target center. The source table **must** be stored as a Public table with `Broadcast` enabled (set to `true`) in order to be successfully replicated into the target center. 

See the [following](../center/realtime_tables.md#create-public-table) documentation to create and configure Public tables in a center. Configuring the `RefreshPeriodMs` of the source table will allow you to adjust for throughput, performance, and how often updates are pushed:  

-   Lower `RefreshPeriodMs` means updates are pushed more frequently, which typically means lower throughput.
-   Higher `RefreshPeriodMs` generally results in higher throughput and better performance, but fewer updates.  

Adjust your values as per your requirements. 

### Replication Procedures

Replication is done in the AMIDB Shell Tool using the following procedures which are defined below.   

`#!amiscript __ADD_CENTER(CenterName String, Url String, Certfile String, Password String)`  
:  
    Add a center with the given properties:  

    -   `CenterName` (Required): Specified name for center (source)
    -   `Url` (Required): Url of center (host: ami.center.port)
    -   `Certfile`: Path to the certfile (optional)
    -   `Password`: Password if a certfile is given (optional)

`#!amiscript __REMOVE_CENTER(CenterName String)`  
:  
    Remove center by name

`#!amiscript __ADD_REPLICATION(Definition String, Name String, Mapping String, Options String)`  
:  
    Add a replication (target table) of a source table with the following properties:  

    -   `Definition`: `Target_TableName`=`Source_CenterName.Source_TableName` or `Source_CenterName.TableName` (if both source and target have the same table name)
    -   `Name`: Name for the replication
    -   `Mapping` (Optional): Mappings to be applied for the tables, (key value delimited by comma) e.g: `"target_col_name=source_col_name"` or `"act=account,symbol=symbol,value=(px\*qty)"`. Pass in null to skip mapping.
    -   `Options` (Optional): options to clear the replication on
        -   `"Clear=onConnect"` or `"Clear=onDisconnect"` or `"Clear=Off"`
        -   If configured, the replicated table will clear when the center connects or disconnect

`#!amiscript __REMOVE_REPLICATION(Name String)`  
:  
    Remove a named replication table

`#!amiscript SHOW CENTERS`
:  
    Shows properties of all replication sources. Properties shown include: 

    -   `CenterName` (String)
    -   `URL` (String)
    -   `CertFile` (String)
    -   `Password` (String)
    -   `Status` (String): shows whether the replication source is connected or not
    -   `ReplicatedTables` (int): Number of replicated tables
    -   `ReplicatedRows` (int): Number of replicated rows

`#!amiscript SHOW REPLICATIONS`  
:  
    Shows properties of all replications. Properties shown include:  

    -   `ReplicationName` (String)
    -   `TargetTable` (String)
    -   `SourceCenter` (String)
    -   `SourceTable` (String)
    -   `Mapping` (String): shows the mapping for the replication
    -   `Options` (String): shows options to clear replications on
    -   `ReplicatedRows` (int): Number of replicated rows
    -   `Status` (String): shows whether a successful connection is established to the target table


!!!Note  
    Removing the replication will clear all the copied entries on the target side's table, regardless of the options.

### Realtime Replication Example 

The following is a simple guide for setting up a realtime replication from one center to another, both hosted on the same local machine. Enter the following command in the target center's AMIDB shell tool: 

```amiscript
call __ADD_CENTER("source", "localhost:3270");
```

This creates a new callable center in the target center named `source` which is hosted on port `3270`. This assumes the target AMI instance is hosted on a different port, for example `4270`. It is important that you configure your AMI instances correctly to avoid port conflicts, especially if using multiple instances from the same machine. 

To create a replicated table, do the following: 

```amiscript
call __ADD_REPLICATION("dest_table=source.mytable","myReplication"," account=account", "Clear=onConnect");
```

This copies the table `mytable` from the source center into the target table `dest_table`.

### AMI Center Persist Files

There are two persist files responsible for maintaining the replications, `__REPLICATION.dat` and `__CENTER.dat`. **We do not recommend modifying the contents of these files manually, these files may have strict formatting and could cause serious issues if altered incorrectly.**

#### Persist File Location

By default, these files are located in the `amione/persist` directory. This may not be the case if the persist directory has been changed by the property `ami.db.persist.dir` or the system tables directory has been changed by the property `ami.db.persist.dir.system.tables`.

To change **only** the `__REPLICATION.dat` and `__CENTER.dat` locations, add the following property to your `local.properties` file:  

```
ami.db.persist.dir.system.table.__REPLICATION=path
ami.db.persist.dir.system.table.__CENTER=path
```

`__REPLICATION.dat` Format:

```
ReplicationName|TargetTable|SourceCenter|SourceTable|Mapping|Options| "ReplicationName"|"SourceTableName"|"SourceName"|"TargetTableName"|"TableMapping"|"ClearSetting"|
```

`__CENTER.dat` Format:

```
CenterName|Url|CertFile|Password| "CenterName"|"CenterURL"|"CertFileIfProvided"|"PasswordForCertFile"|
```

## Historical Archive

Center replication is a real-time process, however you may wish to move data from a realtime database to some historical database or archive using the principles outlined [here](../center/historical_tables.md/#archiving-realtime-tables-into-hdb). 

Unlike realtime replication, a historical table does **not** subscribe to a realtime feed from the source center. Instead, the data can be manually transferred across, or some procedure executed to copy the data at some given time frame.

Historical data archival does **not** use AMI's inbuilt system procedures, but requires the user to define their own procedure to copy the data across with some condition (e.g, timeframe). 

For more information on how historical databases work, see the in-depth documentation [here](../center/historical_tables.md).

### Requirements 

To transfer historical data from one center to another requires the following:  

-   A source table in some center.
-   A target center containing the historical table.

### Historical Archive Example:  

In this example, the source center contains a Public table with some the following schema:  

``` amiscript
CREATE PUBLIC TABLE DataTable (AccountID String, Price double, Quantity Double, Date Long, D long);
```

Similarly, in the historical center, we have a table with the same name but a different schema:

``` amiscript
CREATE PUBLIC TABLE DataTable (AccountID String, Price double, Quantity Double, Date Long, SourceD Long, HistDate Long);
```

!!! Note 
    `D` is an auto-generated incrementing unique id for the row which unique across all tables (see [Reserved Columns on Public Tables](../center/realtime_tables.md#reserved-columns-on-public-tables)).

Next, we will use the below procedure to transfer the historical data. The arguments for the procedure are as follows:

-   `tableName` - this is the table you will be transferring data from and to (ie. DataTable)
-   `histCenter` - this is the name of the datasource where the historical data will sent to
-   `whereClause` - argument that can be used to get the data you want to transfer (ie. you may want to send across data *where Date == 20220101*)
-   `batchSize` - argument to specify the number of data rows to send across in each go

Note that this procedure uses column D to decide which data to send.

```amiscript
CREATE PROCEDURE MoveRowsToHist OFTYPE AMISCRIPT USE 

  arguments="string tableName, string histCenter, string whereClause, int batchSize" 

  script="long histDate = formatDate(timestamp(), \"yyyyMMdd\", \"UTC\");
          long srcTblDmax = select max(D) from ${tableName} where ${whereClause};
          srcTblDmax = srcTblDmax != null ? srcTblDmax : 0L;
          int destTblDmax = use ds=${histCenter} execute select max(SourceD) from ${tableName} where ${whereClause} AND HistDate == ${histDate};
          destTblDmax = destTblDmax != null ? destTblDmax : 0;
         
          while (srcTblDmax > destTblDmax) {
              use ds=${histCenter} insert into ${tableName} from select * except(D), D as SourceD, ${histDate} as HistDate from ${tableName} 
                  where ${whereClause} AND D > destTblDmax limit batchSize order by SourceD;
              destTblDmax = use ds=${histCenter} execute select max(SourceD) from ${tableName} where ${whereClause} AND HistDate == ${histDate};
         }"
```

```amiscript
CREATE PROCEDURE MoveRowsToHist OFTYPE AMISCRIPT USE arguments="string tableName, string histCenter, string whereClause, int batchSize" script="long histDate = formatDate(timestamp(), \"yyyyMMdd\", \"UTC\"); long srcTblDmax = select max(D) from ${tableName} where ${whereClause}; srcTblDmax = srcTblDmax != null ? srcTblDmax : 0L; int destTblDmax = use ds=${histCenter} execute select max(SourceD) from ${tableName} where ${whereClause} AND HistDate == ${histDate}; destTblDmax = destTblDmax != null ? destTblDmax : 0; while (srcTblDmax > destTblDmax) {use ds=${histCenter} insert into ${tableName} from select * except(D), D as SourceD, ${histDate} as HistDate from ${tableName} where ${whereClause} AND D > destTblDmax limit batchSize order by SourceD; destTblDmax = use ds=${histCenter} execute select max(SourceD) from ${tableName} where ${whereClause} AND HistDate == ${histDate};}"
```

