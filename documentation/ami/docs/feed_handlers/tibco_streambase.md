# TIBCO Streambase

## Properties

### Required

```
# Must be exact
ami.relay.fh.active=ssocket,tibcostreambase
ami.relay.fh.tibcostreambase.class= com.f1.ami.relay.fh.tibcostreambase.AmiTibcoStreambaseFH

# Name of the streams to connect to
ami.relay.fh.tibcostreambase.props.streams=stream1,stream2,...

# Specifies the host address of the TIBCO Streambase instance
ami.relay.fh.tibcostreambase.props.server.uri= sb://hostName:Port
```

### Optional

```
# Specifies the username to be used
ami.relay.fh.tibcostreambase.props.username=username

# Specifies the password to be used
ami.relay.fh.tibcostreambase.props.password=password

# Enable TLS configuration
ami.relay.fh.tibcostreambase.props.useTLS=true or false

# AMI will try to reconnect to Streambase every x milliseconds
ami.relay.fh.tibcostreambase.props.reconnect.interval.ms = 5000

```