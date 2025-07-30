# SAML 

The SAML adapter ships by default with AMI. To use it, include the following line in your `local.properties`:

```
saml.plugin.class=com.f1.ami.plugins.amisaml.AmiWebSamlPluginImpl
```
There are additional properties you will need to configure to use the adapter. Please see the property list below. 

## Properties 

### General

```
saml.plugin.class
```
: 
    **Default**: `com.f1.ami.plugins.amisaml.AmiWebSamlPluginImpl`

    - Fully qualified class name of the plugin implementing the `com.f1.ami.web.AmiWebSamlPlugin` interface. 
    - Use `com.f1.ami.plugins.amisaml.AmiWebSamlPluginImpl` unless implementing a custom version.


```
saml.identity.provider.url
```
: 
    **Default**: User-provided

    - The URL of the identity provider.


```
saml.service.provider.url
``` 
: 
    **Default**: User-provided

    - The URL of the service provider.


```
saml.entityid
```
: 
    **Default**: User-provided

    - The issuer ID as provided by the SAML request.


```
saml.relay.state
```
: 
    **Default**: None (Optional)

    - Adds the `RelayState` parameter to the request for optional user input.


```
saml.username.field
```
: 
    **Default**: `uid` (Optional)

    - Name of the attribute field of the response to extract username from.
    - For example: `Name="username"`.

```
saml.ami.isadmin.field
```
: 
    **Default**: `true` (Optional)

    - Name of the attribute field of the response to extract if a user has admin permissions or not.
    - If not supplied, defaults to `true`. 


```
saml.ami.isadmin.values
``` 
:   
    **Default**: `true` (Optional)

    - Value of attribute field of the response to extract if a user has admin permissions or not. 
    - If not supplied, defaults to `true`.

```
saml.ami.isdev.field
```
: 
    **Default**: `true` (Optional)

    - Name of the attribute field of the response to extract if a user has dev permissions or not.
    - If not supplied, defaults to `true`. 

```
saml.ami.isdev.values
```
:   
    **Default**: `true` (Optional)

    - Value of attribute field of the response to extract if a user has dev permissions or not. 
    - If not supplied, defaults to `true`.

```
saml.ami.group.field
``` 
:  
    **Default**: User-provided (Optional) 

    - Name of the attribute field of the response to extract group names from.
    - For example: `user_group`.


```
saml.ami.groups
```
: 
    **Default**: User-provided (Optional)

    - Values of the different group names associated to the AMI groups field.
    - For example: `group_1,group_2`.

```
saml.debug
``` 
: 
    **Default**: `true` (Optional)

    - Set to true to show verbose logging on SAML related information.


### Security

```
saml.identity.provider.cert.file
```
: 
    **Default**: User-provided (Optional) 

    - The file containing the security certificate for the SAML authenticator. 

```
saml.identity.provider.clock.skew.ms
```
: 
    **Default**: `100` (Optional) 

    - The amount of time (in milliseconds) that the identity provider timestamp and service provider timestamp can drift.

```
saml.identity.provider.lifetime.ms
```
: 
    **Default** `60000000` (Optional) 

    - Expiry time of IdP request (in milliseconds).


```
saml.identity.provider.nocert.rsa.key.strength
```
: 
    **Default**: `2048` (Optional)

    - The RSA key strength of the SAML certificate file. 
    - We recommend a minimum of 2048. 

```
saml.nameID.format
```
: 
    **Default**: `transient` (Optional)

    - Format the NameID from the service provider is expected in. 
    - AMI supports the following: 
        - email
        - unspecified
        - persistent
        - transient (default)

