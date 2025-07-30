/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.speedlogger;

import java.util.Map;

/**
 * return a new sink, based on some configuration
 */
public interface SpeedLoggerSinkFactory {

	/**
	 * @return the id of this factory
	 */
	public String getId();

	/**
	 * @return an immutable map of default properties and there values
	 */

	public Map<String, String> getConfiguration();

	/**
	 * create a new sink with the specified configuration.
	 * 
	 * @param id
	 *            the id of the newly created sink (see
	 *            {@link SpeedLoggerSink#getId()}
	 * @param configuration
	 *            the configuration.
	 * @return the newly created speed logger
	 */
	public SpeedLoggerSink createSink(String id, Map<String, String> configuration);

}
