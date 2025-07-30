# Common Properties

## AMI Properties

Applies to Components: Center, Relay, Web, WebBalancer.

```
f1.terminate.file
```
: 
    **Default**: `config//../.amionemain.prc`

    - A small file that is created and monitored by AMI
    - If an external process moves/copies to a file of the same name, but with a `.kill` suffix, the process will exit. 
    - This provides any easy way for an external script or job control process to cleanly shutdown AMI. 

```
f1.threadpool.default.size
```
:   
    **Default**: `8`

    - The number of threads in the core thread pool (this does not include threads for http servicing or image processing).


```
f1.threadpool.aggressive
```
: 
    **Default**: `true` 

    - If true will use an "aggressive" thread pool. 
    - This pool will wake up faster, but uses more CPU while idle.


```
f1.conf.dir
```
: 
    **Default**: `amione/config`

    - Directory to where the configuration files are located.

```
f1.conf.filename
```
: 
    **Default**: `root.properties` 

    - Name of the configuration file loaded at start up.


```
f1.timezone
```
: 
    **Default**: `UTC` 

    - Set timezone for Java (this does not control default user or logger timezones).


```
f1.locale
```
: 
    **Default**: `default` 

    - Default locale.


```
f1.logs.dir
```
: 
    **Default**: `amione/log` 

    - The root directory for where log files will be deposited.


```
f1.plugins.dir
```
: 
    **Default**: `amione/plugins` 

    - Directory of where plugins will be loaded from.

```
f1.resources.dir
```
: 
    **Default**: `amione/resources` 

    - Directory of where additional resources will be placed.


```
ami.components
```
: 
    **Default**: `relay,center,web` 

    - A comma-delimited list of which components to load.
    - This can be any combination of: 
        - relay
        - center
        - web
        - webbalancer
        - webmanager


```
ami.amilog.stats.period
```
: 
    **Default**: `15 SECONDS` 

    - How often AMI should log general health statistics such as memory, user load, etc. 
    - Can be used for real-time/historical monitoring via the performance dashboard. 


```
ami.naming.service.resolvers
```
: 
    **Default**: Optional 

    - Specify a comma-delimited list of naming service plugin classes (implements `com.f1.ami.amicommon.AmiNamingServiceResolver`). 
    - These can be used to map logic service names to physical host/ports.


```
ami.content.security.policy
```
: 
    **Default**: `img-src 'self' https://*.mapbox.com data: w3.org/svg/2000; default-src https://*.mapbox.com 'self' 'unsafe-inline' 'unsafe-eval' blob:` 

    - For more information regarding Content Security Policy, please refer to the [this link](https://developer.mozilla.org/en-US/docs/Web/HTTP/CSP).


```
ami.aes.key.file
```
: 
    **Default**: `persist/amikey.aes` 

    - The aes key file used for encrypting/decrypting secure data (see `strEncrypt`/`strDecrypt` in the Center methods for examples).


```
ami.aes.key.text
```
: 
    **Default**: Optional 

    - The aes key used for encrypting/decrypting secure columns (instead of using `ami.aes.key.file`).


```
ami.aes.key.strength
```
: 
    **Default**: `128` 

    - Key strength, e.g: 128.


## Log Properties

For debugging purposes, when AMI launches, it generates a few log files. These are: 

1. `AmiLog.log` 

    - The main log file. 
    - You should refer to this for any issues with your runtime.

1. `AmiLog.amilog` 

    - A machine-readable logfile.
    - Contains information about memory usage etc.

2. `AmiMessages.log`

    - Tracks messages that are being sent to and from AMI (realtime).

For more information on how to interpret log files, see [this](../troubleshooting/logs.md) page. 

### Log Property Structure

There are a number of properties to configure the log files to your preference. The structure of each property is as follows:

```
speedlogger.<log_type>.<log_subtype>.<property>
```

For example, the property:

```
speedlogger.stream.=BASIC_APPENDER;FILE_SINK;INFO
```

is a `stream` property, which determines how different log messages are streamed into the log file. The flag `INFO` refers to the level of verbosity of the output in the log file, in this case informative. 

Where log files are stored, how many previous logs are kept on disk, and the verbosity of a log message can all be configured via the `local.properties` file. 

### Log Files and Directories 

!!! Note
    If you want to change the location of `stderr`, `stdout`, and `gc.log`, you can do a search and replace on the `start.sh` to another location. Search for `/log`.

Properties with the prefix `speedlogger.sink` determine the file structure of AMI logs. The default configuration for the basic log file looks like this: 

```
speedlogger.sink.FILE_SINK.type=file
speedlogger.sink.FILE_SINK.fileName=\${f1.logs.dir}/\${f1.logfilename}.log
speedlogger.sink.FILE_SINK.maxFiles=10
speedlogger.sink.FILE_SINK.maxFileSizeMb=1000
```

The `FILE_SINK` property refers to the file that logs are written to. The default directory for logs will be in the root directory of your AMI installation in `amione/log`, and the default log file is `AmiLog`. 

To change the `FILE_SINK` configuration to use a different directory or file, you will need to change the following variables in your `local.properties` file, which will be referenced in other logging properties:

```
f1.logs.dir
```
: 
    **Default**: `amione/log` 

      - Where the log files are stored. 
      - To change, set this property to `f1.logs.dir=path/to/your/directory`.


```
f1.logfilename
```
: 
    **Default**: `AmiLog.log` 

      - Where the log files are stored. 
      - To change, set this property to `f1.logfilename=your_logfile_name`.

Wherever `FILE_SINK` is called in a property will call back to these variables.

Each collection of log types will have their own similar configurable properties, the full list can be found [here](#speedlogger-properties-list) in this document. 

For example, for the basic properties; to change the number and size of log files, you will need to alter these properties: 


```
speedlogger.sink.FILE_SINK.maxFiles
``` 
: 
    **Default**: `10`

      - Maximum number of session logs saved before overwriting and deleting. 
      - Sessions are numbered in ascending order (most recent to oldest).
      - To change, set this property to your desired value.


```
speedlogger.sink.FILE_SINK.maxFileSizeMb
``` 
: 
    **Default**: `1000`

      - Maximum size of a log file. 
      - To change, set this property to your desired value. 


### Log Message Appenders 

The message appenders determine the content and style of a log message that is added to the log file. For the basic files, the appender properties are below.

```
speedlogger.appender.BASIC_APPENDER.type
```
: 
    **Default**: `BasicAppender`

      - The implementation of `SpeedLoggerAppender` factory used.
      - We recommend using the existing `BasicAppender` class.


```
speedlogger.appender.BASIC_APPENDER.timezone
```
: 
    **Default**: `EST5EDT`

      - Timezone of a log message 
      - To change, set this value to any Java accepted timezone. 

```
speedlogger.stream.
```
: 
    **Default**: `BASIC_APPENDER;FILE_SINK;INFO`

      - Properties of the basic logger: 

        1. Which appender (`BASIC_APPENDER`)
        1. Which log file (`FILE_SINK`)
        1. Which log level (`INFO`) 

### Log Levels 

To adjust the log level for a certain class of logs, replace the corresponding label (the flag at the end of the property) on a `stream` property with the label you require. 

| Code | Full label |
|------|------------|
| ALL  | All        |
| FNE  | Fine       |
| INF  | Info       |
| WRN  | Warning    |
| SVR  | Severe     |
| OFF  | OFF        |

Where `ALL` gives the highest verbosity and `OFF` gives none. 


### Speedlogger Properties List

Below are the default configurations for each log type. 

#### Basic

```
speedlogger.appender.BASIC_APPENDER.pattern=%P %d{YMD-h:m:s.S z} [%t] %c::%M: %m %D%n
speedlogger.appender.BASIC_APPENDER.timezone=EST5EDT
speedlogger.appender.BASIC_APPENDER.type=BasicAppender
speedlogger.sink.FILE_SINK.type=file
speedlogger.sink.FILE_SINK.fileName=${f1.logs.dir}/${f1.logfilename}.log
speedlogger.sink.FILE_SINK.maxFiles=10
speedlogger.sink.FILE_SINK.maxFileSizeMb=1000
speedlogger.stream.=BASIC_APPENDER;FILE_SINK;INFO
speedlogger.stream.AMI=BASIC_APPENDER;FILE_SINK;OFF
```

#### AmiScript

```
speedlogger.appender.SIMPLE_APPENDER.pattern=%P %d{YMD-h:m:s.S z} [%t] %c: %m %D%n
speedlogger.appender.SIMPLE_APPENDER.timezone=EST5EDT
speedlogger.appender.SIMPLE_APPENDER.type=BasicAppender
speedlogger.stream.AMISCRIPT.LOGINFO=SIMPLE_APPENDER;FILE_SINK;INF
speedlogger.stream.AMISCRIPT.LOGWARN=SIMPLE_APPENDER;FILE_SINK;WRN
```

#### Backend Messages

```
speedlogger.appender.AMIMESSAGES_APPENDER.pattern=%d{YMD-h:m:s.S z} %m%n
speedlogger.appender.AMIMESSAGES_APPENDER.timezone=EST5EDT
speedlogger.appender.AMIMESSAGES_APPENDER.type=BasicAppender
speedlogger.sink.AMIMESSAGES_SINK.type=file
speedlogger.sink.AMIMESSAGES_SINK.fileName=${f1.logs.dir}/AmiMessages.log
speedlogger.sink.AMIMESSAGES_SINK.maxFiles=10
speedlogger.sink.AMIMESSAGES_SINK.maxFileSizeMb=1000
speedlogger.stream.AMI_MESSAGES=AMIMESSAGES_APPENDER;AMIMESSAGES_SINK;INFO
```

#### AMI Web Tracking

```
#######################
# AMI Web Activity Tracker
#######################
speedlogger.appender.AMITRACKER_APPENDER.pattern=%d{YMD-h:m:s.S z} %m%n
speedlogger.appender.AMITRACKER_APPENDER.timezone=EST5EDT
speedlogger.appender.AMITRACKER_APPENDER.type=BasicAppender
#speedlogger.sink.AMITRACKER_SINK.type=file
#speedlogger.sink.AMITRACKER_SINK.fileName=${f1.logs.dir}/AmiWebActivityTracker.log
#speedlogger.sink.AMITRACKER_SINK.maxFiles=10
#speedlogger.sink.AMITRACKER_SINK.maxFileSizeMb=1000
speedlogger.stream.AMI_ACTIVITY_TRACKER=AMITRACKER_APPENDER;FILE_SINK;ALL
```

To save AMI tracking logs to a separate file, remove the comment denotation (#) on lines 7-10.



#### AMI Statistics

```
speedlogger.appender.AMISTATS_APPENDER.pattern=%m%n
speedlogger.appender.AMISTATS_APPENDER.type=BasicAppender
speedlogger.sink.AMISTATS_SINK.type=file
speedlogger.sink.AMISTATS_SINK.fileName=${f1.logs.dir}/${f1.logfilename}.amilog
speedlogger.sink.AMISTATS_SINK.maxFiles=10
speedlogger.sink.AMISTATS_SINK.maxFileSizeMb=1000
speedlogger.stream.AMI_STATS=AMISTATS_APPENDER;AMISTATS_SINK;ALL
```

### Disable per-class

Any time you wish to mute certain messages from showing up in your `AmiOne.log`, you can use the following template:

1. Determine the class of AMI message you wish to mute. 
2. Add the following line to your `local.properties`, replacing `CLASSNAME` with the the class you wish to mute: 

    ```
    speedlogger.stream.CLASSNAME=BASIC_APPENDER;FILE_SINK;OFF
    ``` 
  
For example, given a log output like this: 

```
INF 20231108-13:32:25.009 EST5EDT [F1POOL-04] com.f1.ami.amicommon.centerclient.AmiCenterClientState::onUserSnapshotRequest: localhost:3341 (seqnum=721) User demo requested: [team] of which [team] will be requested from the Ami Center
```

The class name would be `com.f1.ami.amicommon.centerclient.AmiCenterClientState`. 

## REST API Properties

These properties configure the AMI REST API endpoint addresses.

```
ami.rest.plugin.classes
``` 
: 
    **Default**: Optional 

    - List of classes that implement `com.f1.ami.amicommon.rest.AmiRestPlugin`.

```
ami.rest.auth.plugin.class
```
: 
    **Default**: Optional 

    - Class for authentication. 

    - Implements `com.f1.ami.amicommon.rest.AmiRestSessionAuth` 

```
ami.rest.show.errors 
```
: 
    **Default**: `True` 

    - If there is an exception in the endpoint, list it in the REST response. 
    - Convenient for debugging, but less secure.

```
ami.rest.show.endpoints 
```
: 
    **Default**: `True` 

    - Determines whether all endpoints should be shown when browsing to /3forge_rest. 
    - Convenient for debugging, but less secure.

```
ami.rest.uses.web.port
```
: 
    **Default**: `True` 

    - If `true`, uses the same port as the HTTP port (access via appending /3forge_rest to the HTTP address). 
    - If `false`, the properties listed below must be manually set.

### REST API Server Properties 

```
ami.rest.http.port
``` 
: 
    **Default**: Optional

    - The HTTP port the REST server should use. 

```
ami.rest.http.port.bindaddr
```
: 
    **Default**: Optional 
    
    - Specifies the network interface that the REST server HTTP port will be bound to.

```
ami.rest.http.port.whitelist
```
: 
  **Default**: Optional 
  
    -   Provide either a list of permitted hostname patterns, or a plugin for blocking/granting access based on foreign network address. 
    -   Syntax: 
        1. file: `file:<file_containing_a_hostname_patterns_per_line\>` 
        2. text: `text:<newline_delimited_list_of_hostname_patterns>` 
        3. plugin: `plugin:<class_name_implementing_com.f1.ami.amicommon.AmiServerSocketEntitlementsPlugin\>` 

```
ami.rest.https.port
```
: 
  **Default**: Optional 

    - Specifies the network interface that the REST server HTTPS port will be bound to. 

```
ami.rest.https.port.bindaddr
```
: 
    **Default**: Optional 
    
    - Specifies the network interface that the REST server HTTPS port will be bound to.

```
ami.rest.https.port.whitelist
``` 
: 
  **Default**: Optional 
  
    -   Provide either a list of permitted hostname patterns, or a plugin for blocking/granting access based on foreign network address. 
    -   Syntax: 
        1. file: `file:<file_containing_a_hostname_patterns_per_line\>` 
        2. text: `text:<newline_delimited_list_of_hostname_patterns>` 
        3. plugin: `plugin:<class_name_implementing_com.f1.ami.amicommon.AmiServerSocketEntitlementsPlugin\>` 

```
ami.rest.https.keystore.contents.base64
```
: 
  **Default**: Optional 

    -   If using an SSL connection, supply contents of a keystore as base64. 

```
ami.rest.https.keystore.file
```
: 
  **Default**: Optional 

    -   Supply if using an SSL connection. 
    -   Path to the keystore file (using Oracle keytool). 

```
ami.rest.https.keystore.password
```
: 
  **Default**: Optional 

    -   Password associated to keystore file.

## Java VM Properties

The Java VM Properties can be configured in `AMI_One.vmoptions` (for Windows) or `AMI_One_linux.vmoptions` (for Linux), which are parsed in `start.sh`. Some of these properties control how messages are logged and how garbage collection is handled. Here are some concrete examples of Java VM Properties  

- `-Df1.global.procinfosink.file=./.f1proc.txt`: controls where AMI will write proc information. Note, this proc information includes uptime/downtime/process id/and processuid
-   `-Df1.license.file=${HOME}/f1license.txt,f1license.txt,config/f1license.txt`: comma delimited list of where the f1license.txt file(s) should be looked for
-   `-Df1.license.property.file=config/local.properties`: The file that contains a line in the format *f1.license.file=xxxxx* which points to the license file
-   `-Dproperty.f1.conf.dir=config/`: Directory where the root.properties is located.
-   `-Djava.util.logging.manager=com.f1.speedlogger.sun.SunSpeedLoggerLogManager`: Forces java to use the 3Forge logger
-   `-Dlog4j.configuratorClass=com.f1.speedloggerLog4j.Log4jSpeedLoggerManager`: Forces other 3rd party plugins that were written for log4j to use 3Forge logger instead
-   `-Dlog4j.configuration=com/f1/speedloggerLog4j/Log4jSpeedLoggerManager.class`: Forces other 3rd party plugins that were written for log4j to use 3Forge logger instead
-   `-Dproperty.f1.properties.secret.key.files=/path/to/keyfile`: If specified, the ${CIPHER:xxxxxx} syntax within .properties files will be decoded using the file's key. (See tools.sh for creating a key). comma delimit for multiple files
-   `-Dproperty.f1.properties.decrypters=[DECRYPTERID]=com.package.DecrypterClass`: If specified, the ${CIPHER:[DECRYPTERID]:xxxxxx} syntax within .properties files will be decoded using class. (See com.f1.utils.encrypt.Decrypter Interface) Comma delimit for multiple classes

