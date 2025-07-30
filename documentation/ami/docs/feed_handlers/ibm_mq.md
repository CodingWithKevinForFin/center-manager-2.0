# IBM Message Queue 

For users of AMI that have an IBM MQ instance, stream messages directly into AMI.

## Overview 

This feedhandler enables users to stream data from an IBM MQ instance into AMI. In order to use it, you will need to include the IBM MQ feedhandler class in your `local.properties`:

```
com.f1.ami.relay.fh.ibmmq.AmiIBMMQFH
```

Please contact <support@3forge.com> to have it assigned to your account. 

In order to use the feedhandler, you will need to configure several properties which are listed below.

!!! Note 

    Configuration properties for feedhandlers take effect upon restart of AMI. Add the properties then restart AMI in order to enable streaming of IBM MQ data.

## Properties

### Required

```
# Must be exact
ami.relay.fh.active=ssocket,ibmmq
ami.relay.fh.ibmmq.class=com.f1.ami.relay.fh.ibmmq.AmiIBMMQFH

# Specifies the host address of the IBM MQ instance
ami.relay.fh.ibmmq.props.host=<HOSTNAME>

# Specifies the host port of the IBM MQ instance
ami.relay.fh.ibmmq.props.port=<PORT>

# Specifics the format of data from IBM MQ
ami.relay.fh.ibmmq.props.format=<JSON/XML/FIX>

# Specifics the Queue Manager to connect to
ami.relay.fh.ibmmq.props.queuemanager=<QUEUEMANAGER_NAME>

# Specifics the Queue within the Queue Manager to connect to
ami.relay.fh.ibmmq.props.queuename=<QUEUE_NAME>
```

### Optional

```
# Specifies the username to be used
ami.relay.fh.ibmmq.props.username=username

# Specifies the password to be used
ami.relay.fh.ibmmq.props.password=password

# Enable debug logging - defaults to false
ami.relay.fh.ibmmq.props.debug=<true/false> 

# Specifies whether messages are consumed after browsing or not - defaults to false
ami.relay.fh.ibmmq.props.readanddelete==<true/false>

```