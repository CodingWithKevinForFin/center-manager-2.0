/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.speedlogger;

import java.util.Collection;

/**
 * A Logger which can potentially be associated with many stream, and hence send
 * a single log event to many streams. this class allows for all those streams
 * to be combined and treated as a single logger. It is expected that all
 * loggers will infact be isntances of this interface. These methods of this
 * interface are kept seperate from the {@link SpeedLogger} to avoid confusion
 * for the common users interacting with the {@link SpeedLogger} interface.
 * <P>
 * 
 * As streams are added several rules are applied to determine which streams
 * will receive log events.<BR>
 * 
 * 1. Only those streams whose id is a prefix to this loggers id will receive
 * events.<BR>
 * 2. if multiple streams with the same sink id both meet the first rule,then
 * only the stream with the closet match(longest id name) will receive events
 * <BR>
 * 
 */
public interface SpeedLogger2Streams extends SpeedLogger {

	/**
	 * add the set of streams to this logger whose id's are compatible with this
	 * speed loggers id.
	 * <P>
	 * 
	 * @param streams
	 */
	public void addStreams(Collection<SpeedLoggerStream> streams);

	/**
	 * @return the streams that could receive events from this logger
	 */
	public Collection<SpeedLoggerStream> getStreams();

}
