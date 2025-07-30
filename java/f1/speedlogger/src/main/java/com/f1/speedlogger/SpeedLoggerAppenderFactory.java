/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.speedlogger;

import java.util.Map;

/**
 * used to generate appenders based on a set of options.
 */
public interface SpeedLoggerAppenderFactory {

	/**
	 * @return the unique id (name) of this factory.
	 */
	public String getId();

	/**
	 * @return an immutable map of default configuration options and values.
	 */
	public Map<String, String> getConfiguration();

	/**
	 * creates and returns a new {@link SpeedLoggerAppender} based on the
	 * supplied options.
	 * 
	 * @param id
	 *            the id of the appender. see
	 *            {@link SpeedLoggerAppender#getId()}
	 * @param options
	 *            the options for the appender.
	 * @return the newly created speed logger
	 */
	public SpeedLoggerAppender createAppender(String id, Map<String, String> options);
}
