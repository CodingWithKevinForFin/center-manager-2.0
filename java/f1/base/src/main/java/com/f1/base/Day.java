package com.f1.base;

import java.util.TimeZone;

/**
 * represents a day and {@link TimeZone}.
 * <P>
 * For example May 3rd 1978 EST which would span the time from May 3rd 1978-00:00:00.000 EST to May 3rd 1978-23:59:59.999.
 * 
 * @author rcooke
 * 
 */
public interface Day extends Comparable<Day>, ToStringable {

	/**
	 * @return the timezone associated with this day
	 */
	public TimeZone getTimeZone();

	/**
	 * 
	 * @return start time of this day in milliseconds since unix epoc <B> Note this is inclusive<B>
	 */
	public long getStartMillis();

	/**
	 * 
	 * @return the number of days between this day and other, neg if other is before today.
	 */
	public int getDurationInDaysTo(Day other);

	/**
	 * 
	 * @return start time of this day in nanoseconds since unix epoc <B> Note this is inclusive<B>
	 */
	public long getStartNanos();

	/**
	 * 
	 * @return end time of this day in milliseconds since unix epoc <B> Note this is exclusive<B>
	 */
	public long getEndMillis();

	/**
	 * 
	 * @return end time of this day in nanoseconds since unix epoc <B> Note this is exclusive<B>
	 */
	public long getEndNanos();

	/**
	 * 
	 * @return the year this date represents (0-9999)
	 */
	public short getYear();

	/**
	 * 
	 * @return the one indexed month this date represents (1-12)
	 */
	public byte getMonth();

	/**
	 * 
	 * @return the one indexed day this date represents (1-31)
	 */
	public byte getDay();

	/**
	 * @return YYYYMMDDZZZ
	 */
	public String toString();

	/**
	 * 
	 * @param day
	 *            the number of days offset from this day.
	 * @return same timezone, but offset from this by supplied days
	 */
	public Day add(int day);

	public boolean isOn(DateNanos date);

	public boolean isAfter(DateNanos date);

	public boolean isBefore(DateNanos date);

	public boolean isOnOrBefore(DateNanos date);

	public boolean isOnOrAfter(DateNanos date);

	DateNanos getStartNanoDate();

	DateNanos getEndNanoDate();

	java.sql.Date toSqlDate();

	String toStringNoTimeZone();

}
