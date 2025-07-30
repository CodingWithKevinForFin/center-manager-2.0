# AMI REST Server 

AMI provides its own REST server API for retrieving statistics about the AMI session. 

## Overview

The AMI REST server is used for providing information about the AMI session and the VM it's running in. To use, ensure that it is [configured](../configuration_guide/common.md/#rest-api-properties) appropriately and with the correct authentication setup.

By default, the AMI REST endpoints are attributed to the web session's own ports and can be easily accessed by appending the appropriate queries to the web session URL, for example: "localhost:33332/3forge_rest/query". 

For a list of the endpoints, see below.

## Default Endpoints

### /3forge_rest/query

Executes a command against the center and returns the result sets. To use, authentication is required which can be configured via the REST [properties](../configuration_guide/common.md/#rest-api-properties).

#### Params: 

`cmd` (required): 

- Command to execute on center. See the [tools](../center/tools.md) page on center queries.
- E.g: `#!amiscript SHOW TABLES`

`display`: 

- Response display type. 
- Valid options are: `text`, `json`, `jsonRows`, `jsonMaps`, `pipe` 

`timeout`: 

- Time in milliseconds before the command should be considered timed out. 

`limit`: 

- Limit to apply to a query. 

`ds` 

- Which datasource to use.  

`show_plan` 

- Whether to show query plan. 
- Valid options are: `on` or `off` 

`string_template` 

- Whether to interpret string commands encased in `${...}` or to interpret them as literals.
- Valid options are: `on` or `off`

### /3forge_rest/stats      

Shows statistics about the VM. Publicly available information that doesn't require authentication to access.

#### Params: 

`display`: 

- What format to display the response.
- Valid formats are: `text` or `json`.

### /3forge_rest/version    

Shows AMI version information. Requires both authentication and `ISADMIN=true` permissions to access.
	
#### Params: 

`display`: 

- What format to display the response.
- Valid formats are: `text` or `json`.

### /3forge_rest/whatsmyip  

Shows user IP address as web server sees it.

#### Params: 

`display`: 

- What format to display the response.
- Valid formats are: `text` or `json`.

### /3forge_rest/whoami 

Returns username and properties, authentication required to access.

#### Params: 

`display`: 

- What format to display the response.
- Valid formats are: `text` or `json`.