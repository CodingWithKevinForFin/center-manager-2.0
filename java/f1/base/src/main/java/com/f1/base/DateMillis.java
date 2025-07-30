package com.f1.base;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * 
 * represents a date in milliseconds since epoch. As a {@link Number}, it is milliseconds
 * 
 */
public final class DateMillis extends Number implements ToStringable, Legible, Comparable<DateMillis> {

	public static final DateMillis ZERO = new DateMillis(0);
	private long date;

	public DateMillis(long date) {
		this.date = date;
	}
	public DateMillis(Number value) {
		if (value instanceof DateNanos)
			this.date = ((DateNanos) value).getTimeMillis();
		else
			this.date = value.longValue();
	}
	@Override
	public StringBuilder toString(StringBuilder sink) {
		return sink.append(date);
	}
	public String toString() {
		return Long.toString(date);
	}

	/**
	 * @return time in millis
	 */
	public long getDate() {
		return date;
	}

	@Override
	public int intValue() {
		return (int) date;
	}

	@Override
	public long longValue() {
		return date;
	}
	@Override
	public float floatValue() {
		return date;
	}
	@Override
	public double doubleValue() {
		return date;
	}

	@Override
	public boolean equals(Object other) {
		return other == this || (other != null && other.getClass() == DateMillis.class && ((DateMillis) other).date == date);
	}

	@Override
	public int hashCode() {
		return (int) date;
	}
	@Override
	public int compareTo(DateMillis o) {
		if (o == null)
			return 1;
		long d = o.date;
		return d == date ? 0 : (d < date ? 1 : -1);
	}
	private static final TimeZone UTC = TimeZone.getTimeZone("UTC");
	@Override
	public String toLegibleString() {
		return format(date);
	}

	public static String format(long now) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd'T'HH:mm:ss.SSS'UTC'");
		sdf.setTimeZone(UTC);
		return sdf.format(new Date(now));
	}
	public static String formatMinimal(long now) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		sdf.setTimeZone(UTC);
		return sdf.format(new Date(now));
	}

	public static void main(String a[]) {
		System.out.println(new DateMillis(System.currentTimeMillis()).toLegibleString());
	}
}
