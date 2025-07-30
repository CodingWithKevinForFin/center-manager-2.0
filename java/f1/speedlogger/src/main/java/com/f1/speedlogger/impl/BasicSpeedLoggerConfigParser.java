/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.speedlogger.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.f1.speedlogger.SpeedLoggerAppenderFactory;
import com.f1.speedlogger.SpeedLoggerLevels;
import com.f1.speedlogger.SpeedLoggerManager;
import com.f1.speedlogger.SpeedLoggerSinkFactory;
import com.f1.utils.OH;
import com.f1.utils.RH;

/**
 * Consumes a {@link Properties} object and then applies the configuration contained to the {@link SpeedLoggerManager} passed in on the constructor. The following configuration
 * syntax is used to define sinks, appenders and streams:
 * <P>
 * 
 * to create a sink with an id of 'MYSINK' add the following options to your properties object. For this example we will be creating a sink of type file:<BR>
 * <BR>
 * <B> speedlogger.sink.<i>MYSINK</i>.type=<i>file</i><BR>
 * speedlogger.sink.<i>MYSINK</i>.fileName=<i>someFile.log</i><BR>
 * </B>
 * <P>
 * To create an appender with an id of 'MYAPPENDER' add the following optiosn to yours properties object. For this example lest use the basic appender:<BR>
 * <BR>
 * <BR>
 * <B> speedlogger.appender.<i>MYAPPENDER</i>.type=<i>BasicAppdener</i> speedlogger.appender.<i>MYAPPENDER</i>.timezone=EST5EDT</i><BR>
 * </B>
 * <P>
 * Now, lets create a stream with id com.mypackage that uses the declared appender and sink and has a level or WRN
 * <P>
 * <B>speedlogger.stream.<i>com.mypackage</i>=<i>MYAPPENDER;MYSINK;WRN</i></B> <BR>
 * <P>
 * if we want to create another stream with the same name, make it unique by adding a ^1 (or ^2 ...)
 * <P>
 * <B>speedlogger.stream.<i>com.mypackage^2</i>=<i>MYSECONDAPPENDER;MYSECONDSINK ;INF</i></B><BR>
 * <P>
 * 
 * please note that all options added after a sink or appender name will be treated as options for creating the apeender and sink. see
 * {@link SpeedLoggerAppenderFactory#createAppender(String, Map)} and {@link SpeedLoggerSinkFactory#createSink(String, Map)}
 * 
 */
public class BasicSpeedLoggerConfigParser {

	/** prefix for all speed logger applicable config options */
	final static public String PREFIX = "speedlogger.";

	/** prefix for all sink factories options */
	final static public String SINK_FACTORY_PREFIX = PREFIX + "factory.sink.";

	/** prefix for all sink factories options */
	final static public String APPENDER_FACTORY_PREFIX = PREFIX + "factory.appender.";

	/** prefix for all appender options */
	final static public String APPENDER_PREFIX = PREFIX + "appender.";

	/** prefix for all sink options */
	final static public String SINK_PREFIX = PREFIX + "sink.";

	/** prefix for all stream options */
	final static public String STREAM_PREFIX = PREFIX + "stream.";

	/** prefix for custom manager options */
	final static public String MANAGER_PREFIX = PREFIX + "manager.";

	/** key word for specifying the type (or id) of a factory */
	private static final String TYPE = "type";
	private static final String CLASS = "class";

	private SpeedLoggerManager manager;

	public BasicSpeedLoggerConfigParser(SpeedLoggerManager manager) {
		this.manager = manager;
	}

	public void process(Properties p) {
		Map<String, Map<String, String>> sinkFactoryConfigurations = new HashMap<String, Map<String, String>>();
		Map<String, Map<String, String>> appenderFactoryConfigurations = new HashMap<String, Map<String, String>>();
		Map<String, Map<String, String>> sinkConfigurations = new HashMap<String, Map<String, String>>();
		Map<String, Map<String, String>> appenderConfigurations = new HashMap<String, Map<String, String>>();
		Map<String, String> streamConfigurations = new HashMap<String, String>();

		for (Map.Entry<Object, Object> e : p.entrySet()) {
			String k = e.getKey().toString();
			String v = e.getValue().toString();
			if (!k.startsWith(PREFIX))
				continue;
			else if (applyOption(APPENDER_PREFIX, k, v, appenderConfigurations))
				continue;
			else if (applyOption(SINK_PREFIX, k, v, sinkConfigurations))
				continue;
			else if (applyOption(SINK_FACTORY_PREFIX, k, v, sinkFactoryConfigurations))
				continue;
			else if (applyOption(APPENDER_FACTORY_PREFIX, k, v, appenderFactoryConfigurations))
				continue;
			else if (k.startsWith(STREAM_PREFIX))
				streamConfigurations.put(k, v);
			else if (k.startsWith(MANAGER_PREFIX))
				continue;
			else
				throw new RuntimeException("Invalid option: " + e.getKey() + "=" + e.getValue());
		}
		for (Map.Entry<String, Map<String, String>> e : sinkFactoryConfigurations.entrySet()) {
			try {
				String clazz = e.getValue().remove(CLASS);
				if (clazz == null)
					throw new RuntimeException("required: " + SINK_FACTORY_PREFIX + e.getKey() + "." + CLASS);
				SpeedLoggerSinkFactory factory = RH.newInstance(clazz, SpeedLoggerSinkFactory.class);
				if (OH.ne(e.getKey(), factory.getId()))
					throw new RuntimeException("id mismatch(param vs factory's id): " + e.getKey() + " != " + factory.getId());
				manager.addSinkFactory(factory);
			} catch (Exception ex) {
				throw new RuntimeException("could not process property: " + SINK_FACTORY_PREFIX + e.getKey() + "." + CLASS, ex);
			}
		}
		for (Map.Entry<String, Map<String, String>> e : appenderFactoryConfigurations.entrySet()) {
			try {
				String clazz = e.getValue().remove(CLASS);
				if (clazz == null)
					throw new RuntimeException("required: " + APPENDER_FACTORY_PREFIX + e.getKey() + "." + CLASS);
				SpeedLoggerAppenderFactory factory = RH.newInstance(clazz, SpeedLoggerAppenderFactory.class);
				if (OH.ne(e.getKey(), factory.getId()))
					throw new RuntimeException("id mismatch(param vs factory's id): " + e.getKey() + " != " + factory.getId());
				manager.addAppenderFactory(factory);
			} catch (Exception ex) {
				throw new RuntimeException("could not process property: " + APPENDER_FACTORY_PREFIX + e.getKey() + "." + CLASS, ex);
			}
		}

		for (Map.Entry<String, Map<String, String>> e : sinkConfigurations.entrySet()) {
			try {
				String type = e.getValue().remove(TYPE);
				if (type == null)
					throw new RuntimeException("required: " + SINK_PREFIX + e.getKey() + "." + TYPE);
				manager.newSink(type, e.getKey(), e.getValue());
			} catch (Exception ex) {
				throw new RuntimeException("could not process property: " + e.getKey(), ex);
			}
		}
		for (Map.Entry<String, Map<String, String>> e : appenderConfigurations.entrySet()) {
			try {
				String type = e.getValue().remove(TYPE);
				if (type == null)
					throw new RuntimeException("required: " + APPENDER_PREFIX + e.getKey() + "." + TYPE);
				manager.newAppender(type, e.getKey(), e.getValue());
			} catch (Exception ex) {
				throw new RuntimeException("could not process property: " + e.getKey(), ex);
			}
		}
		for (Map.Entry<String, String> e : streamConfigurations.entrySet()) {
			try {
				String parts[] = e.getValue().toString().split(";");
				if (parts.length != 3)
					throw new RuntimeException("Expecting format: APPENDER;SINK;LEVEL");
				String appender = parts[0].trim();
				String sink = parts[1].trim();
				int level = SpeedLoggerUtils.parseLevel(parts[2].trim());
				String id = e.getKey().toString().substring(STREAM_PREFIX.length());
				if (id.indexOf('^') != -1)
					id = id.substring(0, id.indexOf('^'));
				manager.addStream(appender, sink, level, id);
			} catch (Exception ex) {
				manager.prelog(null, SpeedLoggerLevels.SEVERE, new Object[] { "could not process property: ", e.getKey(), ex });
			}
		}
		if (!manager.isStarted())
			manager.start();

	}
	public boolean process(File file) throws IOException {
		if (!file.exists())
			return false;
		FileInputStream in = new FileInputStream(file);
		try {
			Properties properties = new Properties();
			properties.load(in);
			process(properties);
			return true;
		} finally {
			try {
				in.close();
			} catch (Exception e) {
			}
		}
	}

	public void process(String text) {
		try {
			Properties properties = new Properties();
			properties.load(new StringReader(text));
			process(properties);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static boolean applyOption(String prefix, String key, String value, Map<String, Map<String, String>> map) {
		if (!key.startsWith(prefix))
			return false;
		String suffix = key.substring(prefix.length());
		int i = suffix.indexOf('.');
		if (i == -1)
			throw new RuntimeException("missing attribute after name for: " + key);
		String name = suffix.substring(0, i);
		String option = suffix.substring(i + 1);
		Map<String, String> m = map.get(name);
		if (m == null)
			map.put(name, m = new HashMap<String, String>());
		m.put(option, value);
		return true;
	}

}
