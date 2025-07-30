/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.base;

import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * 
 * Represents the time of day. To accurately represent the time of day we use offset in nanos from midnight and a timezone
 * 
 */
public interface DayTime {

	public static final long NANOS_PER_MIC = TimeUnit.MICROSECONDS.toNanos(1);
	public static final long NANOS_PER_MIL = TimeUnit.MILLISECONDS.toNanos(1);
	public static final long NANOS_PER_SEC = TimeUnit.SECONDS.toNanos(1);
	public static final long NANOS_PER_HOR = TimeUnit.HOURS.toNanos(1);
	public static final long NANOS_PER_MIN = TimeUnit.MINUTES.toNanos(1);
	public static final long NANOS_PER_DAY = TimeUnit.DAYS.toNanos(1);
	public static final long MAX_TIME_NANOS = Long.MAX_VALUE;
	public static final long MAX_TIME_MILLIS = MAX_TIME_NANOS / NANOS_PER_MIL;
	/**
	 * 
	 * @return The time in nanos since start of day for the assoicated timezone
	 */
	public long getTimeNanos();

	/**
	 * 
	 * @return The timezone this object represents
	 */
	public TimeZone getTimeZone();

	/**
	 * 
	 * @return The offset for today (in the appropriate timezone ) for a given unix epoch time
	 */
	public long getOffsetMillis(long currentTimeMillis);
	/**
	 * 
	 * @return The offset for nanos (in the appropriate timezone ) for a given unix epoch time
	 */
	public long getOffsetNanos(long currentTimeNanos);

	/**
	 * 
	 * @return The unix epoch time of today for a given unix epoch time
	 */
	public long getTodaysOccurenceNanos(long currentTimeNanos);
	public long getPreviousOccurenceNanos(long currentTimeNanos, boolean inclusive);
	public long getNextOccurenceNanos(long currentTimeNanos, boolean inclusive);
	public long getTodaysOccurenceMillis(long currentTimeMillis);
	public long getNextOccurenceMillis(long currentTimeMillis, boolean inclusive);
	public long getPreviousOccurenceMillis(long currentTimeMillis, boolean inclusive);
	public long getTimeMillis();

}
