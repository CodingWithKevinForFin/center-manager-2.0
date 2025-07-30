/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.speedlogger;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.f1.speedlogger.impl.SpeedLoggerInstance;

/**
 * houses all of the different components for providing a full speedlogger environment. Typically, there should only be one manager per JVM.<BR>
 * The default instance can be obtained by using {@link SpeedLoggerInstance#getInstance()}
 */

public interface SpeedLoggerManager {

	/**
	 * return the speed logger with the associated id, or create one if one doesnt exist.
	 * 
	 * @param id
	 *            the id of the speed logger that is returned. see {@link SpeedLogger#getId()}
	 * @return the associated speed logger. never null
	 */
	SpeedLogger getLogger(String id);
	/**
	 * @return a globally unique id for this speed logging object
	 */
	public long getUid();

	/**
	 * register a sink factory with this manager, which can be obtained by calling {@link #getSinkFactory(String)}. Note the id of the factory must be unique across all sink
	 * factories registered with this manager. see {@link SpeedLoggerSink#getId()}
	 * 
	 * @param sinkFactory
	 *            the factory to register.
	 * @throws Exception
	 *             if another sink factory has already been registered with this is
	 */
	void addSinkFactory(SpeedLoggerSinkFactory sinkFactory);

	/**
	 * returns the sink factory associated with the id, or null if none associated
	 * 
	 * @param id
	 *            the id of the sink factory to return. see {@link SpeedLoggerSink#getId()}
	 * @return the associated factory, or null.
	 */
	SpeedLoggerSinkFactory getSinkFactory(String id);

	/**
	 * @return an immutable set of all ids of sink factories assocatiated with this manager
	 */
	Set<String> getSinkFactoryIds();

	/**
	 * create a new sink and register it with this manager.
	 * 
	 * @param type
	 *            the id of the sink factory which will create the sink. See {@link SpeedLoggerSinkFactory#getId()}
	 * @param id
	 *            the id of the sink to create. see {@link SpeedLoggerSink#getId()}
	 * @param configuration
	 *            the configuration. see {@link SpeedLoggerSinkFactory#createSink(String, Map)}
	 * @return the newly created sink factory. never null
	 */
	SpeedLoggerSink newSink(String type, String id, Map<String, String> configuration);

	/**
	 * the registered sink associated with provided id
	 * 
	 * @param id
	 *            the id of the sink to return. see {@link SpeedLoggerSink#getId()}
	 * @return the sink. never null
	 * @throws Exception
	 *             if no sink with said id is registered
	 */
	SpeedLoggerSink getSink(String id);

	/**
	 * @return an immutable list of all ids for sinks associated with this manager.
	 */
	Set<String> getSinkIds();

	/**
	 * create a stream which is to backed by a particular appender / sink and have an associated level and id. Please note, multiple stream can share the same exact id.
	 * 
	 * @param appender
	 *            the id of the appender to associate with the stream. see {@link SpeedLoggerAppender#getId()}
	 * @param sink
	 *            the id of the sink to associate with the stream. see {@link SpeedLoggerSink#getId()}
	 * @param level
	 *            the level of the stream. see {@link SpeedLoggerStream#getMinimumLevel()}
	 * @param id
	 *            the id of the stream. see {@link SpeedLoggerStream#getId()}
	 * @return the newly created stream. will never be null.
	 */
	SpeedLoggerStream addStream(String appender, String sink, int level, String id);

	void addStream(SpeedLoggerStream stream);

	/**
	 * 
	 * @param streamId
	 *            the id of the stream to return
	 * @return a list of all streams matching supplied stream id. see {@link SpeedLoggerStream#getId()}. never null.
	 */
	Collection<SpeedLoggerStream> getStream(String streamId);

	/**
	 * register an appender factory with this manager, which can be obtained by calling {@link #getAppenderFactory(String)}. Note the id of the factory must be unique across all
	 * appender factories registered with this manager. see {@link SpeedLoggerAppender#getId()}
	 * 
	 * @param appenderFactory
	 *            the factory to register.
	 * @throws Exception
	 *             if another appender factory has already been registered with this is
	 */
	void addAppenderFactory(SpeedLoggerAppenderFactory appenderFactory);

	/**
	 * @param id
	 *            the id of the appender factory to return.
	 * @return the appender factory associated with supplied id, or none exist.
	 */
	SpeedLoggerAppenderFactory getAppenderFactory(String id);

	/**
	 * @return an immutable set of all the ids for appender factories registered with this manager. see {@link SpeedLoggerAppender#getId()}
	 */
	Set<String> getAppenderFactoryIds();

	// Appender
	SpeedLoggerAppender newAppender(String type, String id, Map<String, String> configuration);

	/**
	 * returns the appender registered with this speed logger whose id matches the supplied id. see {@link SpeedLoggerAppender#getId()}
	 * 
	 * @param id
	 *            the id of the appender to return
	 * @return the appender with the supplied id.
	 * @throws Exception
	 *             if there is no appender associated with the supplied id
	 */
	SpeedLoggerAppender getAppender(String id);

	/**
	 * @return an immutable set of all ids for appenders associated with this manager. see {@link SpeedLoggerAppender#getId()}
	 */
	Set<String> getAppenderIds();

	/**
	 * start the logger up. After this, the manager is considered to be in a 'started' state.
	 * 
	 * @throws Exception
	 *             if start has already been called
	 */
	void start();

	/**
	 * @return true iff {@link #start()} has been called, meaning that this manager has been started.
	 */
	boolean isStarted();

	/**
	 * should be called by loggers if this manager is not in a started state. This hook will allow for otherwise silently dropped log messages to be handled (ideally logged to
	 * stdout or stderr)
	 * 
	 * @param logger
	 *            the logger which has {@link SpeedLogger#log(Object, int, Object)}
	 * @param level
	 *            the level passed into {@link SpeedLogger#log(Object, int, Object)}
	 * @param msg
	 *            the msg passed into {@link SpeedLogger#log(Object, int, Object)}
	 */
	void prelog(SpeedLogger logger, int level, Object msg);

	Set<String> getLoggerIds();

	void removeStream(String streamId, String sinkId);

	void setDefaultStackTraceFormatter(StackTraceFormatter instance);

	StackTraceFormatter getDefaultStackTraceFormatter();

	public void addSpeedLoggerManagerListener(SpeedLoggerManagerListener listener);

	public SpeedLoggerManagerListener[] getManagerListeners();

	public void addSpeedLoggerEventListener(SpeedLoggerEventListener listener);
	public SpeedLoggerEventListener[] getListeners();
	Set<String> getStreamIds();

}
