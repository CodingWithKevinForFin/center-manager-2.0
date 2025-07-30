/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.speedlogger;

import java.io.IOException;
import java.util.Map;

/**
 * represents a final destination for log events. Some examples might be sinks for writting log events to files, of perhaps sending then as emails. A sink is responsible for
 * maintaining a writer which will receive formatted log events as char arrays.
 */
public interface SpeedLoggerSink {

	/**
	 * @return the id (name) of this sink
	 */
	public String getId();

	/**
	 * @return a globally unique id for this speed logging object
	 */
	public long getUid();

	/**
	 * 
	 * @return the writer associated with this sink.
	 * @throws IOException
	 */
	public void write(char[] data, int dataStart, int dataLength, int level, SpeedLogger loggerId, Object originalMessage) throws IOException;

	/**
	 * ensure the data written to the writer has been flushed.
	 */
	public void flush();

	public Map<String, String> getConfiguration();

	/**
	 * 
	 * @return the total number of bytes written by this sink
	 */
	public long getBytesWritten();

	/**
	 * @return the number of lines total for this sink
	 */
	long getLogCalls();

	public void addSpeedLoggerEventListener(SpeedLoggerEventListener listener);
	public SpeedLoggerEventListener[] getListeners();

	public String describe();

}
