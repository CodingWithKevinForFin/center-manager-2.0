# How Do You Configure AMI?

## Overview 

By default, AMI ships with a `defaults.properties` file located in the `amione/config` folder of the root directory of your AMI installation. This is helpful for initial setup of AMI, but for additional plugins and other functionality, you will need to configure your own properties by creating a `local.properties` file.

!!!Note
	We do not recommend changing `defaults.properties` as this file gets overwritten on update. Please configure your AMI instance in a `local.properties` file.

## Setup

In the root directory of your AMI installation, navigate to the `amione/config` directory and create a new, empty `local.properties` file. You can then populate the file line-by-line with your desired properties where each property comes in the format `property.name=value`. The default properties are a good baseline, but you can configure your application further depending on your requirements. 

Some common properties you might want to add or change can be found [here](../configuration_guide/common.md), but you can also change specific properties for the [relay](../configuration_guide/relay.md), [center](../configuration_guide/center.md), [web](../configuration_guide/web.md), and [web management](../configuration_guide/webbalancer_webmanager.md).


!!! Note

	For instructions on how to encrypt properties, see the [encryption section](../encryption/properties_files.md).

## Example

Below is a sample `local.properties` file:

```
#My local.properties located under config directory
#Lines beginning with # are comments (except for #INCLUDE)

#Override the http port to standard port
http.port=80

#Override the location of the access.txt file
users.access.file=/home/myname/access.txt

#Process and include properties from a custom properties file
#INCLUDE home/env_specific.properties

#Define my own custom value
my.custom.value=5

#Reference my custom value
ami.frames.per.second=${my.custom.value}

#Append to an existing variable
ami.datasource.plugins=$${ami.datasource.plugins},com.my.Plugin

#Remove a specific property in any of AMI's prepackaged configuration (root.properties, speedlogger.properties, defaults.properties). 

#UNDEFINE  speedlogger.sink.FILE_SINK.maxFiles=10
```

## Multiple `.properties` Files

While we recommend just using `local.properties` for your AMI environments, you can specify multiple .properties files if you wish. If you have multiple environments and have multiple configuration files, e.g `my_env.properties`, to use this configuration in your current AMI instance, you need to add the following to your `local.properties` file:

```
#INCLUDE my_env.properties
```