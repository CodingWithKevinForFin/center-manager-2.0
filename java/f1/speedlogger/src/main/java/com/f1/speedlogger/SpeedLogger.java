/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.speedlogger;

/**
 * represents a logger with a specific name(id). Typically, there is one static instance associated with each class that wishes to log. Generally, the id of the logger is the name
 * of the class its associated with. Each instanceof a SpeedLogger will have a unique name. To determine if messages are actually Logged, setting are configued up front,
 * associating names with log levels. Then, when {@link #log(Object, int, Object)} is called, the log level is inspected to determine if the event should actually get logged. <BR>
 * An example usage:
 * <P>
 * 
 * public static class MyClass{ <BR>
 * private static final SpeedLogger<BR>
 * logger=SpeedLogerInstance.getInstance().getLogger(MyClass.getName());<R> <R> public void someMethod(){ <BR>
 * logger.log("hello",Level.DBG,null); <BR>
 * }<BR>
 * }<BR>
 */
public interface SpeedLogger {

	/**
	 * @return the id (name) of this logger
	 */
	public String getId();

	/**
	 * @return a globally unique id for this speed logging object
	 */
	public long getUid();

	/**
	 * logs a message as an atomic transaction.
	 * 
	 * @param msg
	 *            - the message to log.If not null and this logger is actively logging, then (the {@link Object#toString()} method will be called on msg.
	 * @param level
	 *            - the level to log at
	 * @param obj
	 *            additional information about this log statement. Typically, this is an exception
	 */
	public void log(int level, Object obj);

	/**
	 * can be used to short circuit expensive logging. For example:
	 * <P>
	 * 
	 * if(logger.getMinimumLevel >= Level.INFO)){ <BR>
	 * logger.log(someExpensiveExpression(), level.INFO,null); <BR>
	 * }
	 * <P>
	 * 
	 * @return the minimum level that will actually cause an event to be logged.
	 */
	public int getMinimumLevel();

	public void addSpeedLoggerEventListener(SpeedLoggerEventListener listener);
	public SpeedLoggerEventListener[] getListeners();
}
