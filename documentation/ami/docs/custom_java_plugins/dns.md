# Custom DNS

### Overview

In Enterprise environments, some services cannot be directly identified by a physical destination (ex: host name) and are instead logically identified. In this situation, the organization implements a directory naming service that can map, in realtime, the logical identifier to a physical destination. For AMI to access resources in this scenario, a plugin must be written that interfaces with the directory naming service. Then, when a resource is requested inside AMI, AMI will first ask the Plugin to "resolve" the logical name to a physical one, passing the resolved physical one to the underlying connectors. It's the plugin's responsibility to connect to the naming service and provide an answer in a timely fashion.

### Using Multiple Resolvers

Note, that many resolvers can be supplied. The order in which they are defined in the property is the order in which they are visited. Once a resolver plugin says it "canResolve" the identifier, the remaining resolvers are not called.

### Default case

If no resolvers plugins are provided, or none of the resolvers `#!java canResolve(...)` a given identifier, then the identifier is considered a physical identifier and passed straight to the connector.

### Java interface

```java
com.f1.ami.amicommon.AmiNamingServiceResolver
```

### Properties

```
ami.naming.service.resolvers=comma_delimited_list_of_fully_qualified_java_class_names
```

