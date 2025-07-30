package com.f1.ami.center.triggers;

import com.f1.ami.center.table.AmiImdb;

/**
 * A timed runnable is an object that is registered with the {@link AmiImdb} using {@link AmiImdb#registerTimer(AmiTimedRunnable, long, Object)}. Timers can either be scheduled for
 * a particular time or some time offset from "now" (see {@link AmiImdb#getNow()}.
 * 
 * <P>
 * To avoid unecessary object creation, it's preferable to use the same AmiTimedRunnable (instead of creating new ones for each timed event) and then using the correlationId as a
 * way of passing the payload to the runnable (as opposed to baking the payload into the runnable and creating a new runnable each time).
 * <P>
 * <B>Note on timerIds:</B><BR>
 * Each call to {@link AmiImdb#registerTimer(AmiTimedRunnable, long, Object)} returns a unique timed event id. This same id will also be passed into the timers
 * {@link #onTimer(long, Object)} and can be useful to correlate timer registration with invocation.
 * <P>
 * 
 */
public interface AmiTimedRunnable {

	/**
	 * Called once for each call to {@link AmiImdb#registerTimer(AmiTimedRunnable, long, Object)}.
	 * 
	 * @param timerId
	 *            The id that was returned when the event was registered via {@link AmiImdb#registerTimer(AmiTimedRunnable, long, Object)}
	 * @param correlationId
	 *            The correlation id that was passed in when the event was registered via {@link AmiImdb#registerTimer(AmiTimedRunnable, long, Object)}
	 */
	public void onTimer(long timerId, Object correlationId);
}
