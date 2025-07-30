package com.f1.ami.amicommon;

import java.util.Collection;

/**
 * A plugin that is capable of producing instances, depending on the type of plugin. Note, this interface is not directly used, see sub-interfaces for specific usage.
 */
public interface AmiFactoryPlugin extends AmiPlugin {

	/**
	 * @return a list of allowed options for this plugin. Those keys that can be supplied in the USE ... clause.
	 */
	public Collection<AmiFactoryOption> getAllowedOptions();
}
