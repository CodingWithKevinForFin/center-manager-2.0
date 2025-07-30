/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.speedlogger.impl;

import java.io.File;
import java.io.IOException;
import com.f1.speedlogger.SpeedLoggerManager;

/**
 * Returns the default {@link SpeedLoggerManager} for the running JVM. If not
 * manually configuring, supply a file named
 * {@link #PROPERTY_SPEEDLOGGER_CONFIG}, and populate with the proper
 * configuration details.
 * <P>
 * note: is thread safe
 */
public class SpeedLoggerInstance {

	/**
	 * default config file used for initializing the speed logger manager. see
	 * {@link BasicSpeedLoggerConfigParser} for formatting details
	 */
	private static final String PROPERTY_SPEEDLOGGER_CONFIG = "speedlogger.configfile";
	private static BasicSpeedLoggerManager instance;

	/**
	 * @return the static speed logger manager instance for this JVM. will
	 *         create a new one if necessary. wont return null
	 */
	public static SpeedLoggerManager getInstance() {
		if (instance == null) {
			synchronized (BasicSpeedLoggerManager.class) {
				if (instance == null) {
					BasicSpeedLoggerManager t = new BasicSpeedLoggerManager();
					String config = System.getProperty(PROPERTY_SPEEDLOGGER_CONFIG);
					BasicSpeedLoggerConfigParser parser = new BasicSpeedLoggerConfigParser(t);
					if (config != null) {
						try {
							parser.process(new File(config));
						} catch (IOException e) {
							System.err.println("!!!!!!Error parsing logger configuration from: " + config);
							e.printStackTrace(System.err);
						}
					}
					instance = t;
				}
			}
		}
		return instance;
	}
}
