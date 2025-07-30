/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.container;

import com.f1.container.exceptions.ContainerException;

/**
 * Represents an object that goes through a start -> stop lifecycle. The two
 * states are:<BR>
 * <i>Started</i> The object has had {@link #start()} called more recently then
 * {@link #stop()}. {@link #isStarted()} will return true.<BR>
 * <i>Stopped</i> The object has had {@link #stop()} called more recently then
 * {@link #start()} or has not had {@link #start()} ever called since
 * construction. {@link #isStarted()} will return false.
 * <P>
 * Objects go through the following states: <BR>
 * 1) After construction an object is initially in a 'Stopped' state, such that
 * {@link #isStarted()} would return false. <BR>
 * 2) {@link #start()} is called <BR>
 * 3) The object is in a 'Started' state<BR>
 * 4) {@link #stop()} is called <BR>
 * 5) The object is back in a 'Stopped' state. <BR>
 * 6) {@link #start()} may be called again, returning to step 3<BR>
 * <P>
 * In practice, this ties in with the builder pattern. it is expected that
 * during the stopped state that the object may be configured and mutated.
 * During the started state it expected that various threads are accessing the
 * object and mutations would be kept to a minimum.
 * 
 */
public interface StartStoppable {
	long NO_TIME = -1;

	/**
	 * this object will pass from a 'stopped' to a 'started' state. Further,
	 * implementation specific logic may take place during this call
	 * 
	 * @throws ContainerException
	 *             if already in a started state.
	 */
	void start();

	/**
	 * this object will pass from a 'started' to a 'stopped' state. Further,
	 * implementation specific logic may take place during this call
	 * 
	 * @throws ContainerException
	 *             if already in a started state.
	 */
	void stop();

	/**
	 * 
	 * @return true iff in a started state
	 * @see {@link #start()}, {@link #stop()}
	 */
	boolean isStarted();

	/**
	 * @throws ContainerException
	 *             if in a stopped state
	 */
	void assertStarted() throws ContainerException;

	/**
	 * @throws ContainerException
	 *             if in a started state
	 */
	void assertNotStarted() throws ContainerException;

	/**
	 * @return the last time {@link #start()} was succesfully called.
	 *         {@link #NO_TIME} if not in started state
	 */
	long getStartedMs();
}
