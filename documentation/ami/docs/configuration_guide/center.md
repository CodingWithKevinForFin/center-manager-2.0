# Center 

## Center Properties

To run the ami center, include `center` in the list of components found in the the `ami.components` property, e.g: `ami.components=center`. By default, all three components are running. 

### General 

```
ami.center.port
```
: 
    **Default**: `3270` 
    
    -   Sets the port of the primary instance of AMI center.

```
ami.center.port.bindaddr
``` 
: 
    **Default**: Optional 

    -   Specifies the network interface that the `ami.center.port` server port will be bound to.


```
ami.center.port.whitelist
```
: 
    **Default**: Optional 

    -   Provide either a list of permitted hostname patterns, or a plugin for blocking/granting access based on foreign network address. 
    -   Syntax: 
        1. file: `file:<file_containing_a_hostname_patterns_per_line\>` 
        2. text: `text:<newline_delimited_list_of_hostname_patterns>` 
        3. plugin: `plugin:<class_name_implementing_com.f1.ami.amicommon.AmiServerSocketEntitlementsPlugin\>` 

```
ami.center.ssl.port
``` 
: 
    **Default**: Optional 

    -   Set the secure port that AMI center listens on.


```
ami.center.ssl.keystore.file
``` 
: 
    **Default**: Optional 

    -   Path to the keystore file (using Oracle keytool).

    
```
ami.center.ssl.keystore.password
``` 
: 
    **Default**: Optional 

    -   Password associated to the keystore file. 

    
```
idfountain.path
``` 
: 
    **Default**: `data/idfountain` 

    -   The path used by AMI's persist engine for managing auto-incrementing IDs (amiid).

    
```
idfountan.batchsize
``` 
: 
    **Default**: `1000000` 

    -   The number of IDs AMI should generate per visit to the physical store. 
    -   Larger numbers mean less frequent visits to the file system, but result in larger potential id-gaps on restart. 


### Authentication 


```
ami.db.auth.plugin.class
``` 
: 
    **Default**: `com.f1.ami.web.auth.AmiAuthenticatorFileBacked` (authentication managed via `access.txt`)

    -   Class name of custom authentication plugin if using. 
    -   Custom entitlement authenticators **must** implement the `com.f1.ami.web.auth.AmiAuthenticator` interface. See how [here](../custom_java_plugins/authentication.md).

    
```
ami.jdbc.auth.plugin.class
``` 
: 
    **Default**: `com.f1.ami.web.auth.AmiAuthenticatorFileBacked` (points to `${ami.db.auth.plugin.class}`)

    -   Class name of custom authentication plugin if using. 
    -   Custom entitlement authenticators **must** implement the `com.f1.ami.web.auth.AmiAuthenticator` interface. See how [here](../custom_java_plugins/authentication.md). 
    -   Sets target database's JDBC endpoints. 

    [//]:#(I'm not entirely sure this is correct - could do with going over this in depth since entitlement vs authentication isn't clear to me. We should also do an example for the JDBC thing if possible)


### Datasources

```
ami.datasource.plugins
``` 
: 
    **Default**: List of 3forge-provided datasource adapters (see expandable block)
    ??? info "Datasource adapter list"
        `com.f1.ami.plugins.mysql.AmiMysqlDatasourcePlugin,\
                com.f1.ami.center.ds.AmiKxDatasourcePlugin,\
                com.f1.ami.center.ds.AmiFlatFileDatasourcePlugin,\
                com.f1.ami.center.ds.AmiShellDatasourcePlugin,\
                com.f1.ami.center.ds.AmiAmiDbDatasourcePlugin,\
                com.f1.ami.center.ds.AmiGenericJdbcDatasourcePlugin,\
                com.f1.ami.plugins.postgresql.AmiPostgresqlDatasourcePlugin,\
                com.f1.ami.plugins.oracle.AmiOracleDatasourcePlugin,\
                com.f1.ami.plugins.excel.AmiExcelDatasourcePlugin,\
                com.f1.ami.plugins.db2.AmiDb2DatasourcePlugin,\
                com.f1.ami.plugins.ssh.AmiSshDatasourcePlugin,\
                com.f1.ami.plugins.ssh.AmiSftpDatasourcePlugin,\
                com.f1.ami.center.ds.AmiQuandlDatasourcePlugin,\
                com.f1.ami.center.ds.AmiFredDatasourcePlugin,\
                com.f1.ami.center.ds.AmiOneTickDatasourcePlugin,\
                com.f1.ami.plugins.restapi.AmiRestAPIDatasourcePlugin,\
                com.f1.ami.plugins.sqlite.AmiSqLiteDatasourcePlugin`

    -   Default datasource adapters are provided by AMI and implement the `AmiDatasourcePlugin` interface. 
    -   For custom adapters that we do not provide, see [this](../custom_java_plugins/datasource_adapters.md) documentation to write your own. 


```
ami.datasource.concurrent.queries.per.user
``` 
: 
    **Default**: `5` 

    -   Maximum number of queries to a specific datasource that can be invoked simultaneously.

`ami.datasource.timeout.millis` 
: 
    **Default**: `60000` 

    -   Time in milliseconds AMI will wait for a datsource to respond to a query before timing out. 
    

### Database General

```
ami.db.preschema.config.files
``` 
: 
    **Default**: Optional 

    -   Comma delimited list of `.amisql` files to execute on startup, before the managed schema.  
    -   E.g, custom methods that require compilation *before* the managed schema is executed. 
    -   Recommended file path: `data/custom_methods.amisql`

```
ami.db.schema.config.files
``` 
: 
    **Default**: `config/schema.amisql` (Optional)

    -   Comma delimited list of `.amisql` files to execute on startup, read after the managed schema. 

```
ami.db.schema.managed.file
``` 
: 
    **Default**: `data/managed_schema.amisql` 

    -   File that contains the user-defined schema to execute on startup (public tables). 
    -   This file is updated when the user creates new public tables within AMI via the shell tool.
    -   This is **not** the same as [persisting](../center/realtime_tables.md/#create-public-table) the data. 

```
ami.db.table.default.refresh.period.millis
``` 
: 
    **Default**: `100` 

    -   Max delay for refreshing changes to the front end. 
    -   Corresponds to the `RefreshPeriodMs` setting when creating tables using [persist](../center/realtime_tables.md/#create-public-table) engine.


```
ami.db.max.stack.size
``` 
: 
    **Default**: `4` 

    -   Max stack size for nesting triggers/procedure calls. 

```
ami.db.persist.dir
``` 
: 
    **Default**: `persist`

    -   Directory that [persisted](../center/realtime_tables.md/#create-public-table) data is stored in. 


```
ami.db.persist.dir.system.tables
``` 
: 
    **Default**: `persist`

    -   Directory that the persisted system files are stored in. 

```
ami.db.persist.dir.system.table.<__SYSTEM.TABLE.NAME>
``` 
: 
    **Default**: Optional

    -   Manually set the directory of where to store the persisted data of a given system table file.
    -   E.g, `ami.db.persist.dir.system.table.__DATASOURCE=path/to/datasources`


```
ami.db.persist.encrypter.system.tables
``` 
: 
    **Default**: `default` (Optional) 

    -   Set to `default` to encrypt the system tables. 
    -   Include the decrypter package in your `start.sh` script. 
    -   See the [encryption](../encryption/tables.md) documentation for more information. 


```
ami.db.persist.encrypter.system.table.<__SYSTEM.TABLE.NAME>
``` 
: 
    **Default**: Optional 

    -   Manually set which system table file to encrypt and with what encrypter. 
    -   Include the decrypter package in your `start.sh` script. 
    -   See the [encryption](../encryption/tables.md) documentation for more information. 

```
ami.db.write.lock.wait.millis
``` 
: 
    **Default**: `1000` (Optional)

    -   How long to wait for write-lock before timing out. 


```
ami.db.anonymous.datasources.enabled
``` 
: 
    **Default**: false (Optional)

    -   Allow users to access undefined datasources. 


### Historical Database Options

```
ami.hdb.root.dir
``` 
: 
    **Default**: `hdb` (Optional)

    -   Created when a historical data tables is first created. 
    -   You can override the directory and provide your own. 
    -   For more information on historical tables and partitions, see [here](../center/historical_tables.md).
    
```
ami.hdb.blocksize
``` 
: 
    **Default**: `8192` (Optional) 
    
    -   Size in bytes of data block for reading/writing to `hdb` directory. 


[//]:#(ami.db.service.plugins)


```
ami.hdb.filehandles.max
``` 
: 
    **Default**: `32` (Optional)

    -   Maximum number of open file handles for the historical table.  
    -   For more information on historical tables and partitions, see [here](../center/historical_tables.md). 

### Database Console (For Telnet)

```
ami.db.console.port
``` 
: 
    **Default**: `3290` 

    -   Port for connecting to AMIDB via telnet command line interface.
    
```
ami.db.console.port.bindaddr
``` 
: 
    **Default**: Optional 

    -   Specifies network interface that the AMIDB console server port will be bound to.
    
```
ami.db.console.port.whitelist
``` 
: 
    **Default**: Optional
    
    -   Provide either a list of permitted hostname patterns, or a plugin for blocking/granting access based on foreign network address. 
    -   Syntax: 
        1. file: `file:<file_containing_a_hostname_patterns_per_line\>` 
        2. text: `text:<newline_delimited_list_of_hostname_patterns>` 
        3. plugin: `plugin:<class_name_implementing_com.f1.ami.amicommon.AmiServerSocketEntitlementsPlugin\>` 
    
```
ami.db.console.prompt
``` 
: 
    **Default**: Optional

    -   String to customize AMIDB console prompt.
    -   E.g, *PROD -> AMID-PROD*.
    
```
ami.db.console.history.dir
``` 
: 
    **Default**: `./history` 

    -   Directory storing the command line history.

    
```
ami.db.console.history.max.lines
``` 
: 
    **Default**: `10000` 

    -   Maximum number of lines to store. 

    
```
ami.db.jdbc.port
``` 
: 
    **Default**: `3280` 

    -   -1 to disable.
    -   Port for connecting to AMI via JDBC.

        
```
ami.db.jdbc.port.bindaddr
``` 
: 
    **Default**:Optional

    -   Specifies network interface that the JDBC server port will be bound to.
    
```
ami.db.jdbc.ssl.port
``` 
: 
    **Default**: Disabled

    -   Port for connecting to AMI via JDBC using a SSL connection. 


```
ami.db.jdbc.ssl.keystore.file
``` 
: 
    **Default**: Optional 

    -   Path to the JDBC keystore file (using Oracle keytool).

```
ami.db.jdbc.ssl.keystore.password
``` 
:  
    **Default**: Optional 

    -   Password associated to the JDBC keystore file. 

```
ami.db.jdbc.ssl.port.bindaddr
```
: 
    **Default**: 

    -   Specifies network interface that the JDBC SSL server port will be bound to.
    
```
ami.db.jdbc.protocol.version
```   
: 
    **Default**: 

```
ami.db.jdbc.port.whitelist
```    
: 
    **Default**: Optional
    
    -   Provide either a list of permitted hostname patterns, or a plugin for blocking/granting access based on foreign network address. 
    -   Syntax: 
        1. file: `file:<file_containing_a_hostname_patterns_per_line\>` 
        2. text: `text:<newline_delimited_list_of_hostname_patterns>` 
        3. plugin: `plugin:<class_name_implementing_com.f1.ami.amicommon.AmiServerSocketEntitlementsPlugin\>` 



### Database Plugins 

The following are a list of accepted non-default plugins that AMIDB can take. Each plugin must implement the respective custom Java plugin format, information for which can be found [here](../custom_java_plugins/index.md).

```
ami.db.timer.plugins
``` 
: 
    **Implements**: [`AmiTimerFactory`](../custom_java_plugins/center_objects.md/#custom-timer)

    -   Comma delimited list of timers.

```
ami.db.procedure.plugins
``` 
: 
    **Implements**: [`AmiStoredProcFactory`](../custom_java_plugins/center_objects.md/#custom-stored-procedure)

    -   Comma delimited list of custom procedures. 
    
```
ami.db.trigger.plugins
``` 
: 
    **Implements**: [`AmiTriggerFactory`](../custom_java_plugins/center_objects.md/#custom-triggers)

    -   Comma delimited list of custom triggers.
       
[//]:#(`ami.db.dbo.plugins` -- not sure what this does yet)

        
```
ami.db.persister.plugins
``` 
: 
    **Implements**: [`AmiTablePersisterFactory`](../custom_java_plugins/center_objects.md/#custom-persistence-factory)

    -   Comma delimited list of user-defined persistence engines. 
    

### Resources

```
ami.resources.dir
``` 
: 
    **Default**: Optional 

    -   Path to directory where resources such as images and audio files are stored. 
    -   Accessed via the front end in *Dashboard -> Resource Manager*.


```
ami.resources.monitor.period.millis
``` 
: 
    **Default**: `5000` 

    -   How often the resources directory is scanned for new/updated files.


### Custom Classes  

```
ami.center.amiscript.custom.classes
``` 
: 
    **Default**: Optional

    -   A comma delimited list of the fully qualified Java class name (package, Java class) of your custom AMI class(es). 
    -   For more information on custom classes, see [here](../custom_java_plugins/amiscript_classes.md).

    
```
ami.center.relay.batch.messages.max
``` 
: 
    **Default**: `1000` (Optional) 

    -   Maximum number of messages processed in a single batch. 
    

### Legacy 

```
ami.center.publish.changes.period.millis
``` 
: 
    **Default**: `500` (Optional)

    -   Frequency of publishing changes.


```
ami.center.log.stats.period.millis
```
: 
    **Default**: `15000` (Optional)

    -   Frequency of statistics logging. 
    
```
amiscript.db.variable.<VARNAME>
```
: 
    **Default**: Optional

    -   Declare a readonly variable available in the AMI Center database
    -   The value must be properly formatted to indicate the type. 
    -   You can see variables via SHOW VARS, e.g: 

        1.   `amiscript.db.variable.hello="world"  //declare a string named hello`
        2.   `amiscript.db.variable.num=123L  //declare a long named num.`

```
ami.db.disable.functions
``` 
: 
    **Default**: `strDecrypt` (Optional)
    
    -   Comma delimited list of functions to disable. 
    
```
ami.db.default.permissions
```
: 
    **Default**: `READ,WRITE,ALTER,EXECUTE` 

    -   Comma delimited list of permissions for user with no AMIDB permissions configured for their account.

```
ami.db.onstartup.ondisk.defrag
```
: 
    **Default**: `true` (Optional)

    -   Runs defragmentation on persisted table data if true, which will clear empty blocks.

    
