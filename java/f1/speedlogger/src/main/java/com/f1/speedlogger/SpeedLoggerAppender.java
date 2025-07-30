/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.speedlogger;

import java.io.IOException;

import com.f1.speedlogger.impl.AppendableBuffer;

/**
 * Converts a {@link SpeedLogger#log(Object, int, Object)} call into a string for logging. Typically, this is based on some configuration. Each
 */

public interface SpeedLoggerAppender {

	/**
	 * the id associated with this appender.
	 * 
	 * @return
	 */
	public String getId();

	/**
	 * @return a globally unique id for this speed logging object
	 */
	public long getUid();
	/**
	 * format the message,level,extras,... into a sequence and append it to the sink.
	 * 
	 * @param sink
	 *            the stream to be written to
	 * @param message
	 *            the message passed into {@link SpeedLogger#log(Object, int, Object)}
	 * @param level
	 *            the level passed into {@link SpeedLogger#log(Object, int, Object)}
	 * @param extras
	 *            the extras passed into {@link SpeedLogger#log(Object, int, Object)} (typically an exception
	 * @param logger
	 *            the logger that {@link SpeedLogger#log(Object, int, Object)} was called on
	 * @param timeMs
	 *            when (in milliseconds) {@link SpeedLogger#log(Object, int, Object)} was called. only reliable if {@link #getRequiresTimeMs()} returns true
	 * @param stackTrace
	 *            the stack trace when {@link SpeedLogger#log(Object, int, Object)} was called. only supplied if {@link #getRequiresStackTrace()} returns true
	 * @param string
	 * @throws IOException
	 *             if data could not be written to sink
	 */
	public void append(AppendableBuffer sink, Object message, int level, SpeedLogger logger, long timeMs, StackTraceElement stackTrace) throws IOException;

	/**
	 * because getting the stack trace is an expensive call, returning false will allow the speed logger framework to bypass getting the stack trace
	 * 
	 * @return true if {@link #append(Appendable, Object, int, Object, SpeedLogger, long, StackTraceElement)} needs the stackTrace to be supplied
	 */
	public boolean getRequiresStackTrace();

	/**
	 * because getting the time is an expensive call, returning false will allow the speed logger framework to bypass getting the time
	 * 
	 * @return true if {@link #append(Appendable, Object, int, Object, SpeedLogger, long, StackTraceElement)} needs the timeMs to be supplied
	 */
	public boolean getRequiresTimeMs();

	public String describe();

}
