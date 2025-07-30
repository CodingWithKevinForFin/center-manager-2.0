package com.f1.utils;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import com.f1.base.Day;
import com.f1.base.DateNanos;

public class BasicDay implements Day {

	final private TimeZone timezone;
	final private long startNanos;
	final private short year;
	final private byte month;
	final private byte day;

	public BasicDay(TimeZone timezone, short year, byte month, byte day) {
		this.timezone = timezone;
		this.year = year;
		this.month = month;
		this.day = day;
		GregorianCalendar c = new GregorianCalendar(timezone);
		c.clear();
		c.set(year, month - 1, day);
		startNanos = BasicDayTime.millisToNanos(c.getTimeInMillis());
	}
	public BasicDay(TimeZone timezone, DateNanos startNanos) {
		this(timezone, startNanos.getTimeNanos());
	}
	public BasicDay(TimeZone timezone, Date date) {
		this(timezone, BasicDayTime.NANOS_PER_MIL * date.getTime());
	}
	public BasicDay(TimeZone timezone, long startNanos) {
		this.timezone = timezone;
		GregorianCalendar c = new GregorianCalendar(timezone);
		c.clear();
		c.setTimeInMillis(startNanos / BasicDayTime.NANOS_PER_MIL);
		this.year = (short) c.get(GregorianCalendar.YEAR);
		this.month = (byte) (c.get(GregorianCalendar.MONTH) + 1);
		this.day = (byte) c.get(GregorianCalendar.DAY_OF_MONTH);
		this.startNanos = BasicDayTime.millisToNanos(c.getTimeInMillis());
	}

	private BasicDay(TimeZone timezone, long startNanos, short year, byte month, byte day) {
		this.timezone = timezone;
		this.startNanos = startNanos;
		this.year = year;
		this.month = month;
		this.day = day;
	}

	@Override
	public TimeZone getTimeZone() {
		return timezone;
	}

	@Override
	public long getStartMillis() {
		return startNanos / BasicDayTime.NANOS_PER_MIL;
	}

	@Override
	public long getStartNanos() {
		return startNanos;
	}

	@Override
	public long getEndMillis() {
		return (startNanos + BasicDayTime.NANOS_PER_DAY) / BasicDayTime.NANOS_PER_MIL;
	}

	@Override
	public long getEndNanos() {
		return startNanos + BasicDayTime.NANOS_PER_DAY;
	}

	@Override
	public short getYear() {
		return year;
	}

	@Override
	public byte getMonth() {
		return month;
	}

	@Override
	public byte getDay() {
		return day;
	}

	@Override
	public BasicDay add(int day) {
		return new BasicDay(timezone, startNanos + BasicDayTime.NANOS_PER_DAY * day);
	}

	@Override
	public String toString() {
		return SH.toString(this);
	}

	@Override
	public String toStringNoTimeZone() {
		return toStringNoTimezone(new StringBuilder()).toString();
	}

	@Override
	public StringBuilder toString(StringBuilder sb) {
		return toStringNoTimezone(sb).append(timezone.getID());
	}

	public StringBuilder toStringNoTimezone(StringBuilder sb) {
		SH.rightAlign('0', SH.toString(year), 4, false, sb);
		SH.rightAlign('0', SH.toString(month), 2, false, sb);
		SH.rightAlign('0', SH.toString(day), 2, false, sb);
		return sb;
	}
	@Override
	public boolean isOn(DateNanos date) {
		return !isAfter(date) && !isBefore(date);
	}
	@Override
	public boolean isAfter(DateNanos date) {
		return date.getTimeNanos() >= getEndNanos();
	}
	@Override
	public boolean isBefore(DateNanos date) {
		return date.getTimeNanos() < getStartNanos();
	}
	@Override
	public boolean isOnOrBefore(DateNanos date) {
		return date.getTimeNanos() < getEndNanos() - 1;
	}
	@Override
	public boolean isOnOrAfter(DateNanos date) {
		return date.getTimeNanos() >= startNanos;
	}

	@Override
	public DateNanos getStartNanoDate() {
		return new DateNanos(getStartNanos());
	}

	@Override
	public DateNanos getEndNanoDate() {
		return new DateNanos(getEndNanos());
	}
	@Override
	public int compareTo(Day o) {
		return OH.compare(getStartMillis(), o.getStartMillis());
	}
	public static BasicDay createUnchecked(TimeZone tz, long start, short year, byte month, byte day) {
		return new BasicDay(tz, start, year, month, day);
	}

	@Override
	public int hashCode() {
		return 31 + (int) (startNanos ^ (startNanos >>> 32));
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BasicDay other = (BasicDay) obj;
		if (startNanos != other.startNanos)
			return false;
		return true;
	}
	@Override
	public java.sql.Date toSqlDate() {
		return new java.sql.Date(getStartMillis());
	}
	@Override
	public int getDurationInDaysTo(Day other) {
		if (OH.ne(other.getTimeZone(), getTimeZone()))
			throw new ToDoException("can not handle different timeszones");
		return (int) ((other.getStartNanos() - getStartNanos()) / BasicDayTime.NANOS_PER_DAY);
	}

}
