# Solace

## Message Format

This feedhandler accepts either JSON or protobuf messages as an input source. For protobuf messages, use the `ami.relay.fh.solace.props.protobufClass` property to configure the parser. For JSON messages, it expects it to be in a single map where each key represents a column on a table and its value a single column value.

Sample valid JSON message:

```
{
  "colA": "colAValue",
  "colB": 123,
  ...
}
```

Note that by default, all column types are inferred to be Strings unless explicitly defined In the `ami.relay.fh.solace.props.tableMapping` property using a `colName=colType,...` syntax.

Null values in the JSON message are skipped.

Sample valid protobuf message in a sample.proto file:

```protobuf
package protobuf;
option java_package = "com.sample.protobuf";
message Sample {
required string name = 1;
required int32 id = 2; 
}
```

Users can download the protobuf releases from GitHub and run the protoc.exe on the proto file to generate the protobuf parser class.

Valid types are as follows:

| Underlying Column Type | Valid Property Name (Case Insensitive) |
|------------------------|----------------------------------------|
| String                 | `str`, `string`                            |
| Integer                | `int`, `integer`                           |
| Short                  | `short`                                  |
| Long                   | `long`                                   |
| Float                  | `float`                                  |
| Double                 | `double`                                 |
| Character              | `char`, `character`                        |
| Boolean                | `bool`, `boolean`                          |

## Properties

```
# Required - use this to configure one or more FHs
ami.relay.fh.active=solace

# Required - used to start the FH
ami.relay.fh.solace.start=true

# Required - must match exactly
ami.relay.fh.solace.class=com.f1.ami.relay.fh.solace.AmiSolaceFH

# Required - name of AMI table for data to be streamed into
ami.relay.fh.solace.props.tableName=solaceTable

# Optional - Specify column name and its underlying types, see Message Format above for more info
ami.relay.fh.solace.props.tableMapping=colA=String,colB=int

# Optional - Specify a solace property file to be used
ami.relay.fh.solace.props.propertyFilepath=/location/to/property

# Semi-optional - Specifies a topic to subscribe to, either this or the queue must be specified, or optionally both can be specified (Uses Direct Message subscription)
ami.relay.fh.solace.props.topic=try-me

# Semi-optional - Specifies a queue to subscribe to, either this or the topic must be specified, or optionally both can be specified (Uses Persistent Message subscription)
ami.relay.fh.solace.props.queue=queueName

# Optional - specify queue type to be used, valid inputs (non-case sensitive) are: DurableExclusive, DurableNonExclusive, and NonDurableExclusive. Defaults to NonDurableExclusive
ami.relay.fh.solace.props.queueType=DurableExclusive

# Optional - specifies how the received message should be parsed, valid inputs (non-case sensitive)are: json, protobuf, and plaintext. Defaults to json
ami.relay.fh.solace.props.parseMode=json

# Optional - required if protobof is used as the parsing mode, specifies the protobuf parser class to be used
ami.relay.fh.solace.props.protobufParserClass=parser class

# Optional - required if protobof is used as the parsing mode, do not need to use fully qualified name since it searches within protobufParserClass
ami.relay.fh.solace.props.protobufClass=parser

# Optional - specifies the host address of the solace instance
ami.relay.fh.solace.props.host=localhost:55555

# Optional - specifies the vpn name of the solace queue/topic
ami.relay.fh.solace.props.vpnName=default
```

**Authentication (at least one mode has to be in use, defaults to basic)**

```
# Client Certificate Authentication (Optional) - Required if TLS is in use
ami.relay.fh.solace.props.keystoreUrl=url
ami.relay.fh.solace.props.keystorePassword=password

# Kerberos Authentication (Optional)
ami.relay.fh.solace.props.kerberosInstanceName=instanceName
ami.relay.fh.solace.props.kerberosJaasContextName=contextName
ami.relay.fh.solace.props.kerberosUsernameOnBroker=username

# OAuth 2.0 Authentication
ami.relay.fh.solace.props.oauthAccessToken=token
# Optional - defaults to authenticating with no issuer identifier if not specified
ami.relay.fh.solace.props.oauthIssuerIdentifier=identifier

# Basic Authentication
ami.relay.fh.solace.props.username=admin
ami.relay.fh.solace.props.password=admin
```

**Connection**

```
# Optional - Use TLS for connection, valid inputs are true/false, defaults to false
ami.relay.fh.solace.props.useTLS=false
ami.relay.fh.solace.props.tlsTruststorePassword=password

# Optional - Ignore expiration of cert, defaults to false
ami.relay.fh.solace.props.tlsIgnoreExpiration=false
```