/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.base;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Represents a date (similar to {@link java.util.Date}) as the number of nanoseconds since the unix epoch in UTC. Please note, there are convenience constructors and getters for
 * converting to and from the traditional millisecond based {@link java.util.Date} and the {@link Timestamp}
 * <P>
 * Note: To who reads this near 2262AD I apologize in advance for the world of hurt this may be causing you. I suggest you facilitate the negative flag to extend another 292 years
 * and pass the buck down the road. Please append your apologies in advance for who ever needs to re-address this in 2555AD.
 * 
 * @author rcooke
 * 
 */
public final class DateNanos extends Number implements ToStringable, Legible, Comparable<DateNanos> {

	public static final DateNanos ZERO = new DateNanos(0);
	private long timeNanos;

	public DateNanos(long timeNanos) {
		this.timeNanos = timeNanos;
	}

	public DateNanos(DateNanos date) {
		this(date.getTimeNanos());
	}
	public DateNanos(Number value) {
		if (value instanceof DateMillis)
			this.timeNanos = ((DateMillis) value).longValue() * DayTime.NANOS_PER_MIL;
		else
			this.timeNanos = value.longValue();
	}

	/**
	 * 
	 * @param timeMillis
	 *            millis since unix epoch
	 * @param nanosOffset
	 *            - nanos as a fraction of millis. Ex, 0-999,999
	 */
	public DateNanos(long timeMillis, long nanosOffset) {
		this.timeNanos = (timeMillis * DayTime.NANOS_PER_MIL) + nanosOffset;
	}
	public DateNanos(Date date) {
		this(date.getTime() * DayTime.NANOS_PER_MIL);
	}

	public DateNanos(Timestamp date) {
		this(date.getTime(), date.getNanos() % 1000000L);
	}
	public Date toDate() {
		return new Date(getTimeMillis());
	}

	public String getPanelType() {
		return "timeNanos";
	}

	@Override
	public String toString() {
		return Long.toString(this.timeNanos);
	}

	@Override
	public StringBuilder toString(StringBuilder sb) {
		return sb.append(this.timeNanos);
	}
	/**
	 * set the time in nanos since the unix epoch in UTC.
	 * 
	 * @param timeMillis
	 *            time in nanos since the unix epoch
	 */
	public void setTimeNanos(long timeNanos) {
		this.timeNanos = timeNanos;
	}

	/**
	 * @return the number of nanoseconds since the unix epoch in UTC
	 */
	public long getTimeNanos() {
		return timeNanos;
	}

	/**
	 * set the time in milliseconds since the unix epoch in UTC. Please note, precision with bee to the millis and nanos will be rounded to zero.
	 * 
	 * @param timeMillis
	 *            time in milliseconds since the unix epoch
	 */
	public void setTimeMillis(long timeMillis) {
		this.timeNanos = timeMillis * DayTime.NANOS_PER_MIL;
	}

	/**
	 * @return the number of milliseconds since the unix epoch in UTC
	 */
	public long getTimeMillis() {
		return timeNanos / DayTime.NANOS_PER_MIL;
	}

	/**
	 * @return the nanosecond portion of the time represented by this nano date (between 0-999)
	 */
	public int getNanos() {
		return (int) (timeNanos % DayTime.NANOS_PER_MIC);
	}

	/**
	 * @return the microsecond portion of the time represented by this nano date (between 0-999)
	 */
	public int getMicros() {
		return (int) ((timeNanos % DayTime.NANOS_PER_MIL) / DayTime.NANOS_PER_MIC);
	}

	/**
	 * @return the millis portion of the time represented by this nano date (between 0-999)
	 */
	public int getMillis() {
		return (int) ((timeNanos % DayTime.NANOS_PER_SEC) / DayTime.NANOS_PER_MIL);
	}

	public Timestamp toTimestamp() {
		Timestamp r = new Timestamp(getTimeMillis());
		r.setNanos((int) (getTimeNanos() % DayTime.NANOS_PER_MIL));
		return r;
	}
	@Override
	public int compareTo(DateNanos o) {
		if (o == null)
			return 1;
		long d = o.timeNanos;
		return d == timeNanos ? 0 : (d < timeNanos ? 1 : -1);
	}
	@Override
	public int intValue() {
		return (int) timeNanos;
	}

	@Override
	public long longValue() {
		return timeNanos;
	}
	@Override
	public float floatValue() {
		return timeNanos;
	}
	@Override
	public double doubleValue() {
		return timeNanos;
	}

	@Override
	public boolean equals(Object other) {
		return other == this || (other != null && other.getClass() == DateNanos.class && ((DateNanos) other).timeNanos == timeNanos);
	}

	@Override
	public int hashCode() {
		return (int) timeNanos;
	}

	private static final TimeZone UTC = TimeZone.getTimeZone("UTC");

	@Override
	public String toLegibleString() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd'T'HH:mm:ss.SSS");
		sdf.setTimeZone(UTC);
		StringBuilder sb = new StringBuilder();
		sb.append(sdf.format(new Date(getTimeMillis())));
		long nanos = timeNanos;
		sb.append(',');
		append(sb, nanos / 100000);
		append(sb, nanos / 10000);
		append(sb, nanos / 1000);
		sb.append(',');
		append(sb, nanos / 100);
		append(sb, nanos / 10);
		append(sb, nanos);
		sb.append("UTC");
		return sb.toString();
	}

	private void append(StringBuilder sb, long i) {
		sb.append((char) ((i % 10) + '0'));
	}
	public static void main(String a[]) {
		System.out.println(new DateNanos(System.currentTimeMillis() * 1000000L + 123456L).toLegibleString());
	}
}
