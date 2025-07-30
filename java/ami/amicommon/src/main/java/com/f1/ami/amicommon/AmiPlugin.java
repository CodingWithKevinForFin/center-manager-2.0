package com.f1.ami.amicommon;

import com.f1.container.ContainerTools;
import com.f1.utils.PropertyController;

/**
 * The base interface that represent a plugin for use with AMI. Meaning some instance that is declared and loaded external to Ami code baseinstance that is declared and loaded
 * external to Ami code base. Generally:
 * <P>
 * 1)The plugin class is defined in a properties file (property name depends on the usage)
 * <P>
 * 2)At startup a single instance is instantiated
 * <P>
 * 3) {@link #init(ContainerTools, PropertyController)} is called directly after construction 4) Depending on the type of plugin various methods will be called at runtime.
 * 
 */

public interface AmiPlugin {

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

}
