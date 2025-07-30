# AMPS

The AMI AMPS Realtime feed handler connects to AMPS at startup and begins an AMPS subscription using the AMPS Command interface. The configuration properties are used to configure the AMPS commands' command id, topic, option, filter, etc.

## Setting up the Adapter

1. Unzip `ami_adapter_amps.<your_version>.obv.tar.gz` and copy the `.jar` files to `amione/lib/`.

1. Go to the config directory `ami/amione/config` and edit or make a `local.properties`. Add the lines below or if the `ami.datasource.plugins` property already exists, add the AMPS plugin to the comma deliminated list:

	```
	ami.datasource.plugins=$${ami.datasource.plugins},com.f1.ami.relay.fh.amps.AmiAmpsDatasourcePlugin
	```
	
	!!! note
	
		`$${ami.datasource.plugins}` references the existing plugin list. Do not put a space before or after the comma.
	
1. Restart AMI

1. Go to **Dashboard-\>Data Modeler** and select **Attach Datasource**.

	![](../resources/legacy_mediawiki/Mongo_Adapter3.png "Mongo_Adapter3.png")

1. Select AMPS as the Datasource. Give your Datasource a name and configure the URL.

## Properties

The following properties are set in `config/local.properties` configuration file. As with all feed handlers, add one uniquely named entry for each AMPS feed handler to the ami.relay.fh.active property. Be sure to include the default ssocket feed handler.

For example, if you have one AMPS feed handler, let's name it amps1:

```
ami.relay.fh.active=ssocket,amps1
```

Then, for each AMPS feed handler, include the following properties, where amps1 matches the name in the *ami.relay.fh.active* property.

### Available Properties

``` python
# required, must be exact
ami.relay.fh.amps1.class=com.f1.ami.relay.fh.amps.AmiAmpsFH

# default is true, set to false to disable this feed handler
ami.relay.fh.amps1.start=true

# overrides the application id(1) that messages coming from this feed handler will be mapped to. Default is the handler name, which in this case is amps1
ami.relay.fh.amps1.props.amiId=amps1

# AMPS URL. Required
ami.relay.fh.amps1.props.url=tcp://flux.3forge.net:9007/amps/nvfix

# AMPS topic(s) to subscribe to. Required
ami.relay.fh.amps1.props.topics=

# AMPS filters(s). One filter per topic
ami.relay.fh.amps1.props.filters=

# AMPS command(s) to send on startup. Default is sow_and_delta_subscribe
ami.relay.fh.amps1.props.commands=sow_and_delta_subscribe

# AMPS option(s) for command, oof (out of focus) is default
ami.relay.fh.amps1.props.options=oof

# AMPS timeout for commands, default is 60000
ami.relay.fh.amps1.props.timeout=60000

# AMPS batchsizes, default is 10000
ami.relay.fh.amps1.props.batchsize=10000

# client name when logging into AMPS, default is AMPSS2AMIRELAY
ami.relay.fh.amps1.props.clientname=AMPS2AMIRELAY

# What should AMPS sow key be mapped to, default is I, use blank string for no mapping
ami.relay.fh.amps1.props.sow_key_mappings=I
```
{ .annotate }

1.	See the [__CONNECTION table docs](../center/realtime_tables.md#__connection) for explanation on application ids

### Examples

``` title="Minimal Config"
ami.relay.fh.active=ssocket,amps1
ami.relay.fh.amps1.class=com.f1.ami.relay.fh.amps.AmiAmpsFH
ami.relay.fh.amps1.props.url=tcp://localhost:9007/amps/json
ami.relay.fh.amps1.props.topics=TOPIC1
```

``` title="Multiple Topics with Filters Subscription"
ami.relay.fh.active=ssocket,amps1
ami.relay.fh.amps1.class=com.f1.ami.relay.fh.amps.AmiAmpsFH
ami.relay.fh.amps1.props.url=tcp://localhost:9007/amps/json
ami.relay.fh.amps1.props.topics=ORDERS,EXECUTIONS
ami.relay.fh.amps1.props.filters=/status=’open’,/MsgType=’fill’
```

!!! Note
	AMI requires a mapping between columns and values and will assign this automatically. To pass raw values *without* specific mappings, use the option flag `_mode`. 
	
	For example, in a datamodel: ``#!amiscript use ds="amps" _mode="raw" insert into `messages` values ("a=b\;c=d\;f=22ff");``

## Logs

Search for `#!java com.f1.ami.relay.fh.amps.AmiAmpsFH` in the `log/AmiOne.log` file for information regarding configuration, initialization of the adapter, sending of AMPS commands and any errors.

For Example:

```
grep com.f1.ami.relay.fh.amps.AmiAmpsFH log/AmiOne.log
```

Returns:

```
ami.relay.fh.amps1.class=com.f1.ami.relay.fh.amps.AmiAmpsFH
INF 20190704-09:17:32.802 EST5EDT [main] com.f1.ami.relay.AmiRelayServer::initFH Initializing fh - com.f1.ami.relay.fh.amps.AmiAmpsFH@42a3abe9
INF 20190704-09:17:32.964 EST5EDT [amps connector] com.f1.ami.relay.fh.amps.AmiAmpsFH::run executed command=sow_and_delta_subscribe, topic=FIXMSG, filter=null, options=oof, timeout=60000, batchSize=10000, sowkeymap=I
```

If you'd like to log each message received from AMPS, add the following to your `config/local.properties`:

```
speedlogger.stream.com.f1.ami.relay.fh.amps.AmiAmpsFH=BASIC_APPENDER;FILE_SINK;FINE
```

And you will start seeing message in the format:

```
FNE 20190704-09:23:57.909 EST5EDT [AMPS Java Client Background Reader Thread 86] com.f1.ami.relay.fh.amps.AmiAmpsFH::invoke AMPS Message received: Message{ ... }
```

