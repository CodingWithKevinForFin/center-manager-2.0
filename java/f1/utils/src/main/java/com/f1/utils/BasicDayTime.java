/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils;

import java.util.TimeZone;

import com.f1.base.DayTime;

public class BasicDayTime implements DayTime {

	private long timeNanos;
	final private TimeZone timeZone;

	static private long n2m(long nanos) {
		return nanos / NANOS_PER_MIL;
	}

	static private long m2n(long millis) {
		return millisToNanos(millis);
	}

	public long getTimeNanos() {
		return timeNanos;
	}

	public TimeZone getTimeZone() {
		return timeZone;
	}

	public BasicDayTime(TimeZone timeZone) {
		this.timeZone = timeZone;
	}
	public BasicDayTime(TimeZone timeZone, long timeNanos) {
		this.timeZone = timeZone;
		this.timeNanos = timeNanos;
	}

	public void setTime(long hour, long minute) {
		setTime(hour, minute, 0L, 0L, 0L, 0L);
	}

	public void setTime(long hour, long minute, long second) {
		setTime(hour, minute, second, 0L, 0L, 0L);
	}

	public void setTime(long hour, long minute, long second, long millis) {
		setTime(hour, minute, second, millis, 0L, 0L);
	}

	public void setTime(long hour, long minute, long second, long millis, long micros, long nanos) {
		setTimeNanos(hour * NANOS_PER_HOR + minute * NANOS_PER_MIN + second * NANOS_PER_MIL + millisToNanos(millis) + micros * NANOS_PER_MIC + nanos);
	}

	public void setTimeNanos(long nanos) {
		OH.assertBetween(nanos, 0, NANOS_PER_DAY - 1L);
		this.timeNanos = nanos;
	}

	public long getOffsetMillis(long currentTimeMillis) {
		return timeZone.getOffset(currentTimeMillis);
	}

	public long getOffsetNanos(long currentTimeNanos) {
		return millisToNanos(getOffsetMillis(currentTimeNanos / NANOS_PER_MIL));
	}

	public void setTimeFromCurrentNanos(long currentTimeNanos) {
		final long offsetNanos = getOffsetNanos(currentTimeNanos);
		final long dateNanos = currentTimeNanos + offsetNanos;
		final long midnightNanos = dateNanos - (dateNanos % NANOS_PER_DAY);
		this.timeNanos = millisToNanos(currentTimeNanos - midnightNanos + offsetNanos);
	}

	public long getTodaysOccurenceNanos(long currentTimeNanos) {
		final long offsetNanos = getOffsetNanos(currentTimeNanos);
		final long dateNanos = currentTimeNanos + offsetNanos;
		final long midnightNanos = dateNanos - (dateNanos % NANOS_PER_DAY);
		return midnightNanos + getTimeNanos() - offsetNanos;
	}

	public long getPreviousOccurenceNanos(long currentTimeNanos, boolean inclusive) {
		long todayNanos = getTodaysOccurenceNanos(currentTimeNanos);
		if (todayNanos < currentTimeNanos || (inclusive && todayNanos == currentTimeNanos))
			return todayNanos;
		return todayNanos - NANOS_PER_DAY;
	}

	public long getNextOccurenceNanos(long currentTimeNanos, boolean inclusive) {
		long todayNanos = getTodaysOccurenceNanos(currentTimeNanos);
		if (todayNanos > currentTimeNanos || (inclusive && todayNanos == currentTimeNanos))
			return todayNanos;
		return todayNanos + NANOS_PER_DAY;
	}

	// Milliseconds for Convenience
	public void setTimeFromCurrentMillis(long currentTimeMillis) {
		setTimeFromCurrentNanos(m2n(currentTimeMillis));
	}

	public long getTodaysOccurenceMillis(long currentTimeMillis) {
		return n2m(getTodaysOccurenceNanos(m2n(currentTimeMillis)));
	}

	public long getNextOccurenceMillis(long currentTimeMillis, boolean inclusive) {
		return n2m(getNextOccurenceNanos(m2n(currentTimeMillis), inclusive));
	}

	public long getPreviousOccurenceMillis(long currentTimeMillis, boolean inclusive) {
		return n2m(getPreviousOccurenceNanos(m2n(currentTimeMillis), inclusive));
	}

	public long getTimeMillis() {
		return n2m(timeNanos);
	}

	public void setTimeMillis(long timeMillis) {
		setTimeNanos(m2n(timeMillis));
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (timeNanos ^ (timeNanos >>> 32));
		result = prime * result + ((timeZone == null) ? 0 : timeZone.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BasicDayTime other = (BasicDayTime) obj;
		if (timeNanos != other.timeNanos)
			return false;
		if (timeZone == null) {
			if (other.timeZone != null)
				return false;
		} else if (!timeZone.equals(other.timeZone))
			return false;
		return true;
	}

	public static long millisToNanos(long timeInMillis) {
		if (timeInMillis > MAX_TIME_MILLIS)
			return MAX_TIME_NANOS;
		return timeInMillis * NANOS_PER_MIL;
	}

}
