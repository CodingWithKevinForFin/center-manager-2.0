# Custom Entitlements 

On initial launch, AMI runs using a set of default login properties, for example the default user "demo." This is generally only recommended for demonstration purposes. For actual production cases, we recommend a combination of [encryption](./encryption.md) and custom entitlements plugins to assign the correct permissions to users. 

## Overview

Custom entitlements are given by implementing an instance of the AMI factory `AmiAuthenticatorPlugin`. The plugin should do the following: 

- Verify when a user attempts to log into AMI that they have used a valid user name and password. 
- On successful login, assign the correct hierarchy of permissions (for dev and admin).
- Assign a user the correct level of data and layout access. This is further fine-tuned by implementing a [data filter](./data_filter.md).

There are two different entry points into AMI, each of which can have their own instance of an authentication adapter:

1.   Frontend Web Interface 
    - Implemented when using a browser to access AMI. 
    - A user must supply a username and password via the html login page (see property name for front end web access). 

1.   Backend Command line interface 
    - Used when accessing AMI's in-memory database using the command line interface.
    - A user must execute the `login` command, which in turn calls an instance of this plugin (see property name for backend command line access).

## AMI Predefined Attributes

| Attribute                           | Value |Description                                                                                                                                       |
|-------------------------------------|-------|--------------------------------------------------------------------------------------------------------------------------------------------------|
| `ISADMIN`| `true` or `false` | If true, the user will be logged into the website with admin rights                                                                                             |
| `ISDEV` | `true` or `false` | If true, the user will be logged into the website with developer rights                                                                                          |
| `DEFAULT_LAYOUT` | `supplied_layout.ami` or `ABSOLUTE:path/to/supplied_layout.ami` | If set, this will be the default layout loaded from the cloud directory on login. You may also specify other directories with the appropriate prefix flag of either `ABSOLUTE`, `LOCAL`, `CLOUD`, or `SHARED`                                                                                           |
| `LAYOUTS` | `layout1.ami,layout2.ami...` or `ABSOLUTE:path/to/...ami`| A comma-delimited list of regular expressions for layouts that are available in cloud. You may use other directories with the appropriate prefix flag either `ABSOLUTE`, `LOCAL`, `CLOUD`, or `SHARED`                                                                                                                                                                                         |
| `amiscript.variable.<some_varname>` | key-value pair, e.g `region=New York` | Supply a session variable of type `some_varname` and value to add to the user session. For example, `amiscript.variable.region=New York`                                                                                                                                                                                            |
| `AMIDB_PERMISSIONS` | `READ,WRITE,ALTER,EXECUTE` | A comma-delimited combination of AMIDB permissions which controls permissions for the user when logging in via jdbc or db command line      |

### Deprecated

| Attribute                           | Description                                                                                                                                       |
|-------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------|
| `amivar_>some_varname>            ` | A variable named user.*some_varname* of type string is added to the user's session. Use `amiscript.variable.<some_varname>` instead           |
| `ami_layout_shared                ` | If set, this will be the default layout loaded from the shared directory on login. Use `DEFAULT_LAYOUT` instead                 |

## Java interface

```java
com.f1.ami.web.auth.AmiAuthenticator
```

## Properties

```
ami.auth.plugin.class=fully_qualified_class_name # for Web access
ami.db.auth.plugin.class=fully_qualified_class_name # for Center access
```

## Example

### Java Code:

``` java
package authenticator_example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.f1.ami.web.auth.AmiAuthAttribute;
import com.f1.ami.web.auth.AmiAuthResponse;
import com.f1.ami.web.auth.AmiAuthenticatorPlugin;
import com.f1.ami.web.auth.BasicAmiAttribute;
import com.f1.ami.web.auth.BasicAmiAuthResponse;
import com.f1.ami.web.auth.BasicAmiAuthUser;
import com.f1.container.ContainerTools;
import com.f1.utils.PropertyController;

public class TestAuth implements AmiAuthenticatorPlugin {

        @Override
        public void init(ContainerTools tools, PropertyController props) {
                // TODO Auto-generated method stub
        }
        
        @Override
        public String getPluginId() {
                return "TestAuthenticator";
        }

        @Override
        public AmiAuthResponse authenticate(String namespace, String location, String user, String password) {
                final Map<String, Object> attributes = new HashMap<>();
                attributes.put("ISDEV","false"); // Set to true for developer privileges 
                attributes.put("ISADMIN", "false"); // Set to true for admin privileges 
                attributes.put("DEFAULT_LAYOUT", "default.ami"); // Layout loaded on startup from cloud
                attributes.put("LAYOUTS", "layout1.ami,layout2.ami"); // Supply as many layouts from cloud as needed
                attributes.put("amiscript.variable.region", "New York"); // Creates a custom variable in the user session assigning "New York" to the user's "region"

                Map<String, Object> allowedWindows = new HashMap<String, Object>();
                allowedWindows.put("namespace1", new HashSet(Arrays.asList("Window1PNL", "Window2PNL")));
                attributes.put("amiscript.variable.allowedWindows", allowedWindows); // This adds a custom AMI Session Variable called `allowedWindows` which will then be used in some custom script to control which windows are visible
                attributes.put("amiscript.variable.env", "UAT"); // This adds a custom AMI Session Variable called `env` to "UAT"

                // Use AmiAuthResponse.STATUS_GENERAL_ERROR if authentication failed.
                return new BasicAmiAuthResponse(AmiAuthResponse.STATUS_OKAY, null, new BasicAmiAuthUser(user, attributes));
        }
}

```


### In Web 

Ensure you have first added to the properties: 

```
ami.auth.plugin.class=authenticator_example.TestAuth
```

Controlling which windows are visible using the onStartup Callback:

-   The following is required: Dashboard Settings: User Preferences Namespace

``` amiscript
// First, let's find out which dashboard the user has loaded.
String layoutNamespace = session.getUserPreferencesNamespace();

// Get set of Allowed Windows for the current dashboard, per entitlements.  Note, the allowedWindows map was defined and populated in the entitlements plugin above
Set allowedWindowsSet =  allowedWindows.get(layoutNamespace);

// Loop through all windows in the dashboard, marking any windows that are not in the entitlments as HIDDEN so the user does not have access to them
Map windowsMap = session.getWindowsMap();
for(String id: windowsMap.getKeys()){
  if(allowedWindowsSet == null || !allowedWindowsSet.contains(id)){
    Window w = windowsMap.get(id);
    w.setType("HIDDEN");  
    w.minimize();
  }
}
```

## Blank `AmiAuthenticatorPlugin` Java File

```java 
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.f1.ami.web.auth.AmiAuthAttribute;
import com.f1.ami.web.auth.AmiAuthResponse;
import com.f1.ami.web.auth.AmiAuthenticatorPlugin;
import com.f1.ami.web.auth.BasicAmiAttribute;
import com.f1.ami.web.auth.BasicAmiAuthResponse;
import com.f1.ami.web.auth.BasicAmiAuthUser;
import com.f1.container.ContainerTools;
import com.f1.utils.PropertyController;

/**
 * 
 * A single instance will be loaded at startup of the web server and used for authenticating users for login verification.
 * 
 */
public interface AmiAuthenticatorPlugin extends AmiPlugin {

	public static final String NAMESPACE_AMIADMIN_CLI = "AMIADMIN_CLI";
	public static final String NAMESPACE_AMIDB_JDBC = "AMIDB_JDBC";
	public static final String NAMESPACE_AMIDB_CLI = "AMIDB_CLI";
	public static final String NAMESPACE_AMIWEB_GUI = "AMIWEB_GUI"; 

        /**
	 * will be called right after object construction with its properties.
	 * 
	 * @param tools
	 *            has various tools that can be used for accessing system-wide items such as properties, clock, thread pool, object pool, etc
	 * @param props
	 *            properties specific to this plugin
	 */
	public void init(ContainerTools tools, PropertyController props);

	/**
	 * A unique identifier, within the scope of the class type of the plugin. Note, this must not change for the life of the instance of the plugin.
	 */
	public String getPluginId();

	/**
	 * 
	 * @param namespace
	 *            this is the value specified using the f1.appname property and is useful when there are multiple web servers that should have distinct login permissions.
	 * @param location
	 *            the ip address of the remote user (as gathered by using the http connection's remote ip)
	 * @param user
	 *            name passed into the user field on the login page
	 * @param password
	 *            password passed into the user field on the login page
	 * @return The result of doing the authentication, should never be null
	 */
	public AmiAuthResponse authenticate(String namespace, String location, String user, String password);

}
```