## Center Properties

To run the ami center, include `center` in the list of components found in the the ami.components property, e.g: `ami.components=center`. By default, all three components are running. 

### General 

`ami.center.port` 
: 
    **Default**: `3270` 
    
    Sets the port of the primary instance of AMI center.

`ami.center.port.bindaddr` 
: 
    **Default**: Optional 

    Specifies the network interface that the `ami.center.port` server port will be bound to.



-   `ami.center.port.bindaddr`: Optional. Specifies the network interface that the *ami.center.port* server port will be bound to
-   `ami.port.whitelist`: Provide either a list of permitted hostname patterns or plugin for blocking/granting access based on foreign network address. Syntax is either file:<file_containing_a_hostname_patterns_per_line\> or text:<newline_delimited_list_of_hostname_patterns> or plugin:<class_name_implementing_com.f1.ami.amicommon.AmiServerSocketEntitlementsPlugin\>
-   `ami.center.ssl.port`: Optionally, sets the secure port that ami center is listening on.
-   `ami.center.ssl.keystore.file`: The path to the key store file, generated using java's keytool.
-   `ami.center.ssl.keystore.password`: The password associated with the key store file
-   `idfountain.path`: The path that is used persist files for managing auto-incrementing ids (amiid)
-   `idfountain.batchsize`: (advanced)  The number of IDs AMI should generate per visit to the physical store.  Larger numbers mean less frequent visits to the file system but result in larger potential id-gaps on restart. Default is 1000000
-   `ami.db.auth.plugin.class`: Class name of custom authenticator which must implement com.f1.ami.web.auth.AmiAuthenticator interface and be deposited in the plugin directory. The plugin will be responsible for user authentication and configuration. See [Custom Java Plugins](../custom_java_plugins/authentication.md) for details.
-   `ami.unknown.realtime.table.behavior`: Defines the behavior when real-time data is streamed into AMI but the type (T="...") values is undefined in the realtime database schema. The default is *LEGACY*.  Permissible values are:  *IGNORE* - drop records and don’t log anything, *LOG_ERROR* - drop records and log a warning, *LEGACY* - insert as a legacy record, *CREATE_TABLE* - Automatically create a new PUBLIC table with the correct columns corresponding to the record's fields and values.
-   `ami.center.publish.changes.period.millis` - How often AMI Center publishes batched data changes out to consumers such as AmiWeb. Default is 500 milliseconds
-   `ami.log.query.max.chars` - By default all queries, along with the username and duration are logged to the server log file. This property controls the max length of a query to get logged before truncating.  If set to 0 then the contents of the query are not logged. If -1 then nothing is logged
-   `ami.center.log.stats.period.millis` - How often stats are appended to the AmiOne.amilog file in milliseconds (default is 15 seconds)
-   `ami.resources.monitor.period.millis` - How often the resources directory is scanned for new/updated files. Default is 5,000 milliseconds
-   `ami.resources.dir` - Where resources, such as images and audio files, are stored. These resources are accessed from the front end via Dashboard \> Resource manager

### Realtime Database

-   `ami.datasource.timeout.millis`: Default amount of time, in milliseconds, AMI will wait for a datasource to respond to a query before timing out. Default is 60000 (one minute)
-   `ami.datasource.concurrent.queries.per.user`: The max number of queries to a specific datasource from a specific user that can be invoked simultaneously.  Default is 5
-   `ami.db.schema.config.files`: A comma delimited list of sql files to execute on startup
-   `ami.db.schema.managed.file`: The file that contains the schema to execute on startup, as defined via the command line.  For example, if a user creates a new public table, this file will be updated to include the modified schema so that next time AMI is restarted the table still exists.
-   `ami.db.table.default.refresh.period.millis`: The max delay for refreshing changes to the front end.  (See RefreshPeriodMs column in the __TABLE table)
-   `ami.db.max.stack.size`: The max stack size for nesting triggers / procedure calls. Default is 4
-   `ami.db.persist.dir`: For tables with persistence, the directory that the data is stored in.  (See PersistEngine in the USE options of the CREATE PUBLIC TABLE clause)
-   `ami.db.persist.dir.system.tables`: The directory where the contents of system tables (ex: __TABLE, __COLUMN, ...)  are stored by default.
-   `ami.db.persist.dir.system.table.[__*system_table_name*]`: The Directory where the contents of a specific system table is stored. Ex: *ami.db.persist.dir.system.table.__DATASOURCE=/var/mydatasources*
-   `ami.db.timer.logging.enabled`: Default is true, which says that each time a timer is fired a log line will be written to the log file.  In instances where timers are firing frequently, it is optimal to set this to false
-   `ami.db.console.port`: The port for connecting via telnet command line interface. Default is 3290
-   `ami.db.console.port.bindaddr`: Optional. Specifies the network interface that the *ami.db.console.port.bindaddr* server port will be bound to
-   `ami.db.console.port.whitelist`: provide either a list, file or plugin for blocking/granting access based on foreign network address. Syntax is either file:<file_containing_a_hostname_patterns_per_line\> or text:<comma_delimited_list_of_hostname_patterns\> or plugin:<class_name_implementing_com.f1.ami.amicommon.AmiServerSocketEntitlementsPlugin\>
-   `ami.db.console.history.dir`: The directory that stores command line history
-   `ami.db.console.history.max.lines`: Max number of lines to store, default is 10000
-   `ami.db.jdbc.port`: The port for connecting to AMI via the AMI JDBC driver. Default is 3280, -1 to disable
-   `ami.db.jdbc.port.bindaddr`: Optional. Specifies the network interface that the *ami.db.jdbc.port* server port will be bound to
-   `ami.db.jdbc.port.whitelist`: provide either a list of permitted hostname patterns or plugin for blocking/granting access based on foreign network address. Syntax is either file:<file_containing_a_hostname_patterns_per_line\> or text:<comma_delimited_list_of_hostname_patterns\> or plugin:<class_name_implementing_com.f1.ami.amicommon.AmiServerSocketEntitlementsPlugin\>
-   `ami.db.jdbc.ssl.port`: The port for securely connecting to AMI via the AMI JDBC driver.
-   `ami.db.jdbc.ssl.port.bindaddr`: Optional. Specifies the network interface that the *ami.db.jdbc.ssl.port* server port will be bound to
-   `ami.db.jdbc.ssl.keystore.file`: Keystore file (.jks) used for secure AMI JDBC port
-   `ami.db.jdbc.ssl.keystore.password`: Password for keystore file (AMI JDBC SSL)
-   `ami.jdbc.auth.plugin.class`: By default pointing to ${ami.db.auth.plugin.class}, which sets target database's JDBC endpoints
-   `ami.db.anonymous.datasources.enabled`: should users be able to access undefined-datasources dynamically (via the USE DS_ADAPTER directive)
-   `ami.db.disable.functions`: Comma delimited list of functions to disable, by default strDecrypt is disabled
-   `ami.db.default.permissions`: When a user logs into the database and does not have an AMIDB_PERMISSIONS option associated with there account, what are the default permissions. Should be a comma delimited combination of READ, WRITE, ALTER, and EXECUTE. Blank means no permissions, default is all permissions( READ,WRITE,ALTER,EXECUTE)
-   `amiscript.db.variable.*varname*`: Declare a readonly variable available in the AMI Center database.  The value should be wrapped in quotes for a string, or properly formatted to indicate the type. Note, you can see declared variables via SHOW VARS. Examples:
    -   amiscript.db.variable.hello="world"  //declare a string named hello
    -   amiscript.db.variable.num=123L  //declare a long named num.
-   `ami.db.onstartup.ondisk.defrag `: If true, then on startup the ONDISK columns for persisted tables will be defragmented meaning empty blocks, do to deletes/updates will be removed. If false, then defrag is skipped but empty blocks will attempt to be reused. true is default

### Plugins

-   `ami.object.nobroadcast`: Used to stop a type of message or data from broadcasting to AMI Web. This is used for increasing the performance of AMI Web when a type of data does not need to be pushed in real time to the front end and can be used as a "hard filter".  Should be a comma (,) delimited list of types (T=) of messages to suppress. See [AMI Realtime Messages](../reference/ami_realtime_messages.md) manual for details on specifying types on messages
-   `ami.object.indexes`: Force indexes on the internal in memory database. The syntax is a comma delimited list of type.field pairs. Ex: *account.fname,order.order_id*
-   `ami.persist.dir`: Directory where the center should store persistent data. This is any data that needs to persist after AMI center is shut down and restarted
-   `ami.datasource.plugins`: A comma delimited list of java classes that implement the *com.f1.ami.amicommon.AmiDatasourcePlugin* interface. See [Custom Java Plugins](../custom_java_plugins/datasource_adapters.md) for details
-   `ami.db.procedure.plugins`: A comma delimited list of java classes that implement the *com.f1.ami.center.procs.* *AmiStoredProcFactory* interface. See [Custom Java Plugins](../custom_java_plugins/center_objects.md) for details
-   `ami.db.trigger.plugins`: A comma delimited list of java classes that implement the *com.f1.ami.center.triggers.* *AmiTriggerFactory* interface. See [Custom Java Plugins](../custom_java_plugins/center_objects.md) for details
-   `ami.db.persister.plugins`: A comma delimited list of java classes that implement the *com.f1.ami.center.table.persist.* *AmiTablePersisterFactory* interface. See [Custom Java Plugins](../custom_java_plugins/center_objects.md) for details
-   `ami.db.timer.plugins`: A comma delimited list of java classes that implement the *com.f1.ami.amicommon.AmiFactoryPlugin.* *AmiTimerFactory* interface. See [Custom Java Plugins](../custom_java_plugins/center_objects.md) for details
-   `ami.db.dialect.plugins`: Comma delimted list of classes implementing the com.f1.ami.center.dialectsAmiDbDialectPlugin interface.  Dialect plugins are used to adapt specific dialects (such as Tableau's mysql queries) into AMIDB's dialect
-	`ami.db.console.prompt`: takes in a string to customize AMI DB console prompt. E.g. -PROD -> AMIDB-PROD>

### Email

To send emails from you account via AMI, you will need to add the some of these properties to local.properties file:

-   `email.client.host`: The SMTP server to connect to. To connect to the Gmail SMTP server use host as smtp.gmail.com
-   `email.client.port`: The SMTP server port to connect to. To connect to the Gmail SMTP server use port as 465.
-   `email.client.username`: Username for the email account.
-   `email.client.password`: Password for the email account.
-   `email.client.authentication.enabled`: If true, attempts to authenticate the user. Defaults to true.
-   `email.client.ssl.enabled`: If true, use SSL to connect and use the SSL port by default. Defaults to false for the SMTP protocol.
-   `email.client.start.tls.enabled`: If true, enables the use of the STARTTLS command (if supported by the server) to switch the connection to a TLS-protected connection before issuing any login commands. If the server does not support STARTTLS, the connection continues without the use of TLS. Defaults to true.
-   `email.client.retries.count`: Number of times it tries to reconnect to the SMTP server. If 0, it will not retry if connection fails. And, for example, if set to 2 it will retry 2 times (so 3 trys in total).
-   `email.client.debug.enabled`: If true, AMI can debug email related information

