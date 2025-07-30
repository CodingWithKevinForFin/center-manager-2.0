/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.speedlogger;

/**
 * represents the combination of a {@link SpeedLoggerSink} and a {@link SpeedLoggerAppender}. Essentially, loggers send there log events to a stream and the stream formats the data
 * using the appending and then sends the formatted text to the sink. in addition, stream has an associated log level such that log events which are below the log level are ignored
 * 
 * Note: A sink may participate in many streams and an appender may also participate in many streams.
 */
public interface SpeedLoggerStream {

	/**
	 * Converts the message into a string (using the internal appender) and sends that string to the sink
	 * 
	 * @param msg
	 *            - the message to convert
	 * @param level
	 *            - the level of the message
	 * @param extras
	 *            - extra data, usually an associated exception. may be null
	 * @param logger
	 *            - the logger that {@link SpeedLogger#log(Object, int, Object)} was called on
	 * @param timeMs
	 *            - when {@link SpeedLogger#log(Object, int, Object)} was called
	 * @param stackTrace
	 *            - the stack trace when {@link SpeedLogger#log(Object, int, Object)} was called
	 */
	public void log(Object msg, int level, SpeedLogger logger, long timeMs, StackTraceElement stackTrace);

	/**
	 * @return true if the internal appender requires a stack trace. See {@link SpeedLoggerAppender#getRequiresStackTrace()}
	 */

	public boolean getRequiresStackTrace();

	/**
	 * @return true if the internal appender requires a time stamp. See {@link SpeedLoggerAppender#getRequiresTimeMs()}
	 */
	public boolean getRequiresTimeMs();

	/**
	 * @return the minimum level of events to be logged
	 */
	public int getMinimumLevel();

	/**
	 * @return the id of the associated sink. see {@link SpeedLoggerSink#getId()}
	 */
	public String getSinkId();

	/**
	 * @return the id of the associated sink. see {@link SpeedLoggerAppender#getId()}
	 */
	public String getAppenderId();

	/**
	 * the id of this stream
	 * 
	 * @return
	 */
	public String getId();

	public String describe();

}
