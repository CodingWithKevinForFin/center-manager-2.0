# TIBCO EMS 

For users who use the TIBCO EMS messaging system, 3forge offers an inbuilt feedhandler for easy integration into AMI.

## Message Format

There are three accepted formats for the TIBCO EMS feedhandler:

1. JSON
2. Plaintext
3. Custom (user-defined)

Messages should be structured such that entries are key-value pairs representing a column (key) and the value of that column (value).

By default, all column types are inferred to be Strings unless explicitly defined in the `ami.relay.fh.tibcoems.props.tableMapping` property using a `colName=colType,...` syntax.

Valid types are as follows:

| Underlying Column Type | Valid Property Name (Case Insensitive) |
|------------------------|----------------------------------------|
| `String`               | `str, string`                          |
| `Integer`              | `int, integer`                         |
| `Short`                | `short`                                |
| `Long`                 | `long`                                 |
| `Float`                | `float`                                |
| `Double`               | `double`                               |
| `Character`            | `char, character`                      |
| `Boolean`              | `bool, boolean`                        |


### JSON 

Sample valid JSON message:

```
{
  "colA": "colAValue",
  "colB": 123,
  ...
}
```

Null values in a JSON message are skipped.

### Plaintext

Sample valid plaintext message: 

```
colA=colAValue|colB=123
```

You will also need to supply the column and key-value pair delimiter as the following optional properties: 

```
# The delimiter or symbol separating columns, in this example "|"
ami.relay.fh.tibcoems.props.plaintext.column.delimiter=|

# The delimiter or symbol representing the mapping in a key-value pair, in this example "="
ami.relay.fh.tibcoems.props.plaintext.keyval.delimiter==
```

### Custom

For user-defined custom message formats, we suggest reaching out to us at <support@3forge.com> so that we can assist you in implementing a solution. 

Examples include a custom FIX parser, the setup of which can be found [here](#custom-fix).

## Properties

### Required

```
# Use this to configure one or more feedhandlers
ami.relay.fh.active=tibcoems

# Used to start the FH
ami.relay.fh.tibcoems.start=true

# Must match exactly
ami.relay.fh.tibcoems.class=com.f1.ami.relay.fh.tibcoems.AmiTibcoEMSFH

# Name of AMI table for data to be streamed into
ami.relay.fh.tibcoems.props.tableName=tableName

# Client id to be used
ami.relay.fh.tibcoems.props.clientID=id

# Specifies the host address of the TIBCO EMS instance
ami.relay.fh.tibcoems.props.serverUrl=localhost:1234

# Specifies the username to be used
ami.relay.fh.tibcoems.props.username=username

# Specifies the password to be used
ami.relay.fh.tibcoems.props.password=password

# Specifies a topic or queue to subscribe to, one or the other must be specified
ami.relay.fh.tibcoems.props.topic=topicName
ami.relay.fh.tibcoems.props.queue=queueName

# Define which format messages are coming in. Available options are: "json", "plaintext", and "custom"
ami.relay.fh.tibcoems.props.parseMode=json
```

### Optional

```
# Specify column name and its underlying types
ami.relay.fh.tibcoems.props.tableMapping=colA=String,colB=int

# Specifies if the JNDI protocol should be used for the connection, defaults to false
ami.relay.fh.tibcoems.props.useJNDI=true/false

# If using the JNDI protocol, should TLS be used, defaults to false
ami.relay.fh.tibcoems.props.useTLS=true/false

# Specifies if/how messages should be acknowledged. Valid inputs are: auto,client,dups_ok,explicit_client,explicit_client_dups_ok,no. Defaults to auto
ami.relay.fh.tibcoems.props.ackMode=auto
```

## Custom (Fix) 

### Overview

3forge offers a FIX parser to be used in conjunction with a TIBCO EMS feed. You will need to contact us at <support@3forge.com> to have it assigned to your account. 

You will need to configure your `local.properties` with additional properties as well as an XML file to define the FIX mappings. 

Add the following to your properties:

```
# Set this to custom 
ami.relay.fh.tibcoems.props.parseMode=custom

# The fully qualified class name of the 3forge-provided FIX parser
ami.relay.fh.tibcoems.props.custom.parser.class=com.f1.ami.relay.fh.tibcoems.AmiTibcoEMSFIXParser

# The path to the XML file containing the FIX mappings
ami.relay.fh.tibcoems.props.fix.dictionary=resources/FIX50.xml
```

This is the minimum configuration for the FIX parser, however there are additional properties to configure depending on your FIX dictionary specifics.

### Optional Properties 

#### Symbols 

```
# Define the delimiter symbol the parser uses. Default is "\u0001"
ami.relay.fh.tibcoems.props.fix.column.delimiter=\u0001

# Define the equality symbol the parser uses. Default is "="
ami.relay.fh.tibcoems.props.fix.keyval.delimiter==
```

#### Field Mappings

For FIX dictionaries formatted with field attributes, configure the mappings as follows:

```
# Path to field expression in the FIX XML file
ami.relay.fh.tibcoems.props.fix.dictionary.field.xpath.expression=fix/fields/field

# Column mapping key-value pair from FIX heading to corresponding column name
ami.relay.fh.tibcoems.props.fix.dictionary.field.keyval.mapping=number=name

# Whether the FIX XML file is structured with attributes
ami.relay.fh.tibcoems.props.fix.dictionary.field.attributes=true
``` 

#### Enum Mappings

Mapping Enum values to their corresponding outputs is done similarly: 

```
# Path to field value expression in the FIX XML file
ami.relay.fh.tibcoems.props.fix.dictionary.enum.xpath.expression=fix/fields/field/value

# Key-value pair mapping the field value enum to the corresponding quantity
ami.relay.fh.tibcoems.props.fix.dictionary.enum.keyval.mapping=enum=description

# Whether the FIX XML file is structured with attributes
ami.relay.fh.tibcoems.props.fix.dictionary.enum.attributes=true
``` 

Following this property setup, the enum values will be transformed into their corresponding to description. To receive the raw value (the enum) itself, set the xpath expression property to empty:

```
ami.relay.fh.tibcoems.props.fix.dictionary.enum.xpath.expression=
```