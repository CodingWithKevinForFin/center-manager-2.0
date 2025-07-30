# WebBalancer / WebManager

## WebManager User Guide

The AMI WebManager is a centralized server for deployments with multiple AMI Web Servers. Its purpose is to store and send requested layouts and preferences. The WebManager is used in conjunction with AMI Web Servers or the AMI WebBalancer. See the [WebBalancer User Guide](#webbalancer-user-guide) below for more details.

### Setup for AmiWeb Manager

1. Download AMIOne

1. Navigate to the installation directory and open amione/config/local.properties

	1. If does not exist `local.properties`, manually create it in `amione/config`

1. For a minimal configuration, add the following properties:

	```
	# Starts up AMI WebManager
	ami.components=webmanager
	
	# If true, file requests to parent directories are rejected
	ami.webmanager.mapping.strict=true
	
	# Assign port number
	ami.webmanager.port=WEBMANAGER_PORT
	```

1. Additional configuration properties are:

	```
	# Working directory for the WebManager
	# Default is amione directory
	ami.webmanager.mapping.pwd=WORKING_DIR
	
	# Maps alias/logical name to directory
	# Key=value comma-delimited list
	ami.webmanager.mapping.roots=/ALIAS=PATH,\[...\]
	```

For more details on the configuration properties, refer to the [WebManager Configuration Properties](../configuration_guide/webbalancer_webmanager.md).

### Required Configuration for AMI Web Servers

Establishing a connection between an AMI Web Server and AMI WebManager will require additional configuration properties. To do so,

1. Navigate to the AMI Web Server installation directory and open `amione/config/local.properties`

1. Add the following properties:

	```
	# Default host is localhost
	ami.webmanager.host=WEBMANAGER_HOST
	ami.webmanager.port=WEBMANAGER_PORT
	```

## WebBalancer User Guide

The AMI WebBalancer is a server that routes users to various AMI Web Servers. The purpose of using the AMI WebBalancer is to distribute the web load across multiple AMI Web Servers. The AMI WebBalancer is usually setup along with an AMI WebManager, see the [WebManager User Guide](#webbalancer-user-guide) above for more information.

### Setup

1. Download and Install AMIOne

1. Navigate to the installation directory and open amione/config/local.properties

	- If not found local.properties, manually create it in amione/config

1. Add the following properties:

	```
	# Starts up AMI WebBalancer
	ami.components=webbalancer
	
	# Routing file for AMI WebBalancer, default path is data/webbalancer.routes
	ami.webbalancer.routes.file=FILE_NAME
	```

The routing file contains a list of rules for mapping IP addresses to AMI Web Servers.

Above is the minimal configuration for the AMI WebBalancer. For more details on configuration properties, refer to the **WebBalancer Configuration Properties**.

For more details on routing, refer to the **WebBalancer.routes** documentation.

#### Reference Links

[WebBalancer Configuration Properties](../configuration_guide/webbalancer_webmanager.md)

[WebBalancer.routes](../configuration_guide/webbalancer_webmanager.md)