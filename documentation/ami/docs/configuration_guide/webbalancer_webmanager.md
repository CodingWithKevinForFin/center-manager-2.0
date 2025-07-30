# WebBalancer / WebManager

These are the list of configurable properties for the AMI WebBalancer and WebManager which are responsible for managing deployments with multiple AMI Web servers. 

For more information and how to set up the WebBalancer, see [here](../architecture/webbalancer_webmanager.md). 

## WebBalancer Properties

To run the AMI WebBalancer, include *webbalancer* in your `local.properties` file. 
This is set in the `ami.components` property: 
```
ami.components=webbalancer
```

### File Locations

```
ami.webbalancer.routes.file
```
: 
    **Default**: `data/webbalancer.routes`

    - File containing a list of routing rules for determining how incoming client ip addresses are routed to AMI web server addresses. 
    - Routing Rules are one entry per line with descending priority. 
    - Each line is in the form `client_ip_mask;target1,target2,target3;BREAK[or CONTINUE]`

```
ami.webbalancer.sessions.file
```
: 
    **Default**: `persist/webbalancer.sessions` 

    - File that stores the active session routing in case the Web Balancer restarts.

### Check Frequencies

```
ami.webbalancer.check.sessions.period
```
: 
    **Default**: `5 seconds` 

    - How often to check for changes to routes file and stale sessions.
        
            
```
ami.webbalancer.server.alive.check.period
```
: 
    **Default**: `5 seconds` 

    - How often to test servers marked as DOWN, to see if they are alive. 
        
            
```
ami.webbalancer.server.test.url.period
```
: 
    **Default**: `30 seconds` 

    - How often to ping servers marked as UP to ensure they are healthy.
        
            
```
ami.webbalancer.server.test.url
```
: 
    **Default**: `/portal/rsc/ami/normal.png` 

    - The URL to request from web server for HTTP OK status.
           
            
```
ami.webbalancer.session.timeout.period
```
: 
    **Default**: `1 minute` 

    - After what period of time is a session with zero connections considered stale. 


### Client HTTP Connectivity

```
ami.webbalancer.http.port
```
: 
    **Default**: `33330` (Optional) 

    - The http port to listen for insecure connections on. Ff not specified, http is not available.
        
            
```
ami.webbalancer.http.port.bindaddr
```
: 
    **Default**: Optional

    - Specifies the network interface that the `ami.webbalancer.http.port` port be bound to.
        
            
```
ami.webbalancer.http.port.whitelist
```
: 
    **Default**: Optional 

    - Controls access to the http port. Provide either a list of permitted hostname patterns, or plugin for blocking/granting access based on foreign network address.
    - Syntax: 
        1. file: `file:<file_containing_a_hostname_patterns_per_line\>` 
        2. text: `text:<newline_delimited_list_of_hostname_patterns>` 
        3. plugin: `plugin:<class_name_implementing_com.f1.ami.amicommon.AmiServerSocketEntitlementsPlugin\>` 
        
            
```
ami.webbalancer.https.port
```
: 
    **Default**: Optional 

    - The https port to listen for secure connections. If not specified, https is not available. 


```
ami.webbalancer.https.port.bindaddr
```
: 
    **Default**: Optional

    - Specifies the network interface that the `ami.webbalancer.https.port` port be bound to.
        
              
```
ami.webbalancer.https.port.whitelist
```
: 
    **Default**: Optional 

    - Controls access to the https port. Provide either a list of permitted hostname patterns, or plugin for blocking/granting access based on foreign network address.
    - Syntax: 
        1. file: `file:<file_containing_a_hostname_patterns_per_line\>` 
        2. text: `text:<newline_delimited_list_of_hostname_patterns>` 
        3. plugin: `plugin:<class_name_implementing_com.f1.ami.amicommon.AmiServerSocketEntitlementsPlugin\>` 
        
   
```
ami.webbalancer.https.keystore.file
```
: 
    **Default**: Optional

    - Path to the keystore file (using Oracle keytool).
        
            
```
ami.webbalancer.https.keystore.password
```
: 
    **Default**: Optional 

    - Password associated to the keystore file. 


### `WebBalancer.routes` file

#### Overview 

This file (specified by `ami.webbalancer.routes.file`) should contain a list of rules, one rule per line.

**Rule Format**

```
CLIENT_MASK;SERVERS_LIST;ON_FAILURE \#comments
```

| Parameter    | Description                                                                                                                     |
|--------------|---------------------------------------------------------------------------------------------------------------------------------|
| CLIENT_MASK  | A pattern matching expression for client ip addresses, (*=wild)                                                                 |
| SERVERS_LIST | a comma-delimited list of [weighting*] protocol://host:port entries. (Only http,https protocols supported, default is http)      |
| ON_FAILURE   | If all servers in SERVERS_LIST are down, either CONTINUE trying other rules or BREAK and return error to user. Default is BREAK |

Each new client connection is passed through the webbalancer rules engine to determine which list of webservers are candidates for forwarding starting at the highest priority (first) rule.

If the client's ip address (as seen by the webbalancer) matches the CLIENT_MASK portion then a web server from the SERVERS_LIST option is chosen for forwarding, see selection strategy for details. The selected forwarding is "sticky for all subsequent connections from the same client ip address for the life of the session (see ami.webbalancer.session.timeout.period for determining when a session can expire).

**Selection Strategy**

The servers from the SERVERS_LIST with an UP status (see `ami.webbalancer.server.test.url` property determining UP/DOWN status of a server) and the least number of active client sessions is chosen, based on the servers' weighting. Ties are broken via round-robin. 

All entries have a default weighting of 1, but can be overridden by prefixing with a weighting and star (*). Servers with higher weightings will be assigned more users. For example,

```
[https://server1:33332,2*https://server2:33332](https://server1:33332,2*https://server2:33332),.5*server3:33332
```

In this scenario, server 2 would get twice the number of users as server1. Server 3 would get half the number of users as server 1.

Note: If a client address can not be matched to a rule the user will receive an error message.

**Example**

```
# Send 3 specific clients to either server1 or server2:
192.168.1.1|192.168.1.2|192.168.1.3;https://server1:33332,https://server2:33332;

# Send all clients in the 192.168.1 address range to server3:
192.168.1.*;https://server3:33332;

# Send all clients in the 192.168.2 address range to server6 or server7. 1/5th of users to server6 and 4/5ths to server7 
192.168.2.*;1*https://server6:33332;4*https://server7:33332;

# Send all others to server4 or server5
*;http://server4:33332,http://server5:33332; 
```

Default:

```
*;http://localhost:33332;BREAK
```

## WebManager Properties

To run the AMI WebManager, include *webmanager* in your `local.properties` file. 
This is set in the `ami.components` property: 

```
ami.components=webmanager
```

### Properties List

```
ami.webmanager.port
```
: 
    **Default**: `3260` (Optional)

    - The server port that the WebManager listens on for connections from AMI web servers.
            
```
ami.webmanager.port.bindaddr
```
: 
    **Default**: Optional

    - The server port to bind to.
            
```
ami.webmanager.port.whitelist
```
: 
    **Default**: Optional 

    - Controls access to this port. Provide either a list of permitted hostname patterns, or plugin for blocking/granting access based on foreign network address.
    - Syntax: 
        1. file: `file:<file_containing_a_hostname_patterns_per_line\>` 
        2. text: `text:<newline_delimited_list_of_hostname_patterns>` 
        3. plugin: `plugin:<class_name_implementing_com.f1.ami.amicommon.AmiServerSocketEntitlementsPlugin\>` 
         
```
ami.webmanager.mapping.pwd
```
: 
    **Default**: Java working directory, typically under the root installation of AMI (`ami/amione`) 

    - The working directory for file requests.


```
ami.webmanager.mapping.roots
```
: 
    **Default**: Optional (OS dependent) 

    - A `key=value` comma-delimited list of roots that web server file requests are mapped to. 
    - The key is a "logical name" and the value is the "actual location". E.g: 
        - `/public=/opt/files,/=/` takes all file requests under `/public` and maps them to `/opt/files`, otherwise all files starting with `/` are mapped to `/`
        - For Windows machines, this defaults to `c:/=c:/`

            
```
ami.webmanager.mapping.strict
```
: 
    **Default**: `true`

    - If `true`, file requests requesting a parent directory (via `..`) will be rejected. Also, requests denoting home directory (`~`) will be denied.
    - **IMPORTANT SECURITY NOTE:** Setting to `false` will allow remote access to *all* host files accesible by user process.



