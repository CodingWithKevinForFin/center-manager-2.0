/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.speedlogger.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import com.f1.utils.SH;

/**
 * Efficient method for formatting date/time... Principle is that the date does not change very often, so we calculate the start / end of the current day & as long as we are within
 * that time we don't need to recalcuate that portion. <BR>
 * Please note, this implementation is thread safe.
 * 
 * pass the format into the constructor. The following format options are supported: Y - four digit year<BR>
 * <B>M</B> - 2 digit month (01 - 12)<BR>
 * <B>D</B> - 2 digit day of month (01 -31)<BR>
 * <B>h</B> - 2 digit hour of day (00-23)<BR>
 * <B>s</B> - 2 digit second (00-59)<BR>
 * <B>S</B> - 3 digit millisecnds (000 - 999)<BR>
 * <B>z</B> - time zone<BR>
 * <B>\</B> - escape (following char will be added as a literal
 * <P>
 * 
 * note: any chars not in the list above will be treated as a literal
 */
public class TimeFormatter {
	private static final int DAY_IN_MS = 86400000;
	final private static char CLAUSE_YEAR = 'Y', CLAUSE_MONTH = 'M', CLAUSE_DAY = 'D', CLAUSE_HOUR = 'h', CLAUSE_MINUTE = 'm', CLAUSE_SECOND = 's', CLAUSE_MILLIS = 'S',
			CLAUSE_ZONE = 'z', CLAUSE_ESCAPE = '\\';
	final private static int MILLIS_PER_SECOND = 1000;
	final private static int MILLIS_PER_MINUTE = 60000;
	final private static int MILLIS_PER_HOUR = 3600000;
	final private static Set<Character> symbols = new HashSet<Character>();
	static final String DIGITS2[] = new String[100];
	static final String DIGITS3[] = new String[1000];
	static final String DIGITS4[] = new String[10000];
	public static final String DEFAULT = "YMD-h:m:s.S z";
	static {
		symbols.add(CLAUSE_YEAR);
		symbols.add(CLAUSE_MONTH);
		symbols.add(CLAUSE_DAY);
		symbols.add(CLAUSE_HOUR);
		symbols.add(CLAUSE_MINUTE);
		symbols.add(CLAUSE_SECOND);
		symbols.add(CLAUSE_MILLIS);
		symbols.add(CLAUSE_ZONE);
		String zeros = "000";
		int i = 0;
		for (i = 0; i < 10000; i++) {
			String s = SH.toString(i);
			int l = s.length();
			DIGITS4[i] = zeros.substring(0, 4 - l) + s;
			if (l < 4)
				DIGITS3[i] = zeros.substring(0, 3 - l) + s;
			if (l < 3)
				DIGITS2[i] = zeros.substring(0, 2 - l) + s;
		}
	}

	static final private class Today {
		final public long start, end;
		final public String year, month, day;

		public Today(long start, long end, String year, String month, String day) {
			this.start = start;
			this.end = end;
			this.year = year;
			this.month = month;
			this.day = day;
		}
	}

	private Today today;

	public Today getContext(long time) {
		Today c = today;
		if (c != null && c.start <= time && time < c.end)
			return c;
		Calendar cal = Calendar.getInstance(timeZone);
		cal.setTimeInMillis(time);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MILLISECOND, 0);
		cal.add(Calendar.DAY_OF_YEAR, 1);
		long end = cal.getTimeInMillis();
		cal.add(Calendar.DAY_OF_YEAR, -1);
		long start = cal.getTimeInMillis();
		if (end - start != DAY_IN_MS) {//handle DST
			long cutoff = getStartOfDstMs(cal, 15);
			if (time < cutoff)
				end = cutoff;
			else
				start += end - start > DAY_IN_MS ? timeZone.getDSTSavings() : -timeZone.getDSTSavings();
		}
		return this.today = new Today(start, end, DIGITS4[cal.get(Calendar.YEAR)], DIGITS2[cal.get(Calendar.MONTH) + 1], DIGITS2[cal.get(Calendar.DAY_OF_MONTH)]);
	}

	private static long getStartOfDstMs(Calendar cal, int stepCheckMinutes) {
		long t = cal.getTimeInMillis();
		for (int offset = stepCheckMinutes;; offset += stepCheckMinutes) {
			cal.add(Calendar.MINUTE, stepCheckMinutes);
			if (cal.get(Calendar.HOUR) != offset / 60 || cal.get(Calendar.MINUTE) != offset % 60) {
				long r = cal.getTimeInMillis();
				cal.setTimeInMillis(t);
				return r;
			}
		}
	}

	final private TimeZone timeZone;
	final private char[] format;

	public TimeFormatter(String format, TimeZone timeZone) {
		this.timeZone = timeZone;
		List<Character> chars = new ArrayList<Character>();
		boolean inEscape = false;
		for (char c : format.toCharArray()) {
			if (inEscape) {
				inEscape = false;
			} else if (c == CLAUSE_ESCAPE) {
				inEscape = true;
			} else if (!symbols.contains(c)) {
				chars.add(CLAUSE_ESCAPE);
			}
			chars.add(c);
		}
		this.format = new char[chars.size()];
		for (int i = 0; i < chars.size(); i++)
			this.format[i] = chars.get(i);
		if (inEscape)
			throw new RuntimeException("Tralling escape found in '" + format + "'");
	}

	public Appendable append(Appendable appendable, long time) throws IOException {
		Today c = getContext(time);
		int now = (int) (time - c.start);
		for (int i = 0; i < format.length; i++) {
			switch (format[i]) {
				case CLAUSE_YEAR:
					appendable.append(c.year);
					break;
				case CLAUSE_MONTH:
					appendable.append(c.month);
					break;
				case CLAUSE_DAY:
					appendable.append(c.day);
					break;
				case CLAUSE_HOUR:
					appendable.append(DIGITS2[now / MILLIS_PER_HOUR]);
					break;
				case CLAUSE_MINUTE:
					appendable.append(DIGITS2[(now / MILLIS_PER_MINUTE) % 60]);
					break;
				case CLAUSE_SECOND:
					appendable.append(DIGITS2[(now / MILLIS_PER_SECOND) % 60]);
					break;
				case CLAUSE_MILLIS:
					appendable.append(DIGITS3[now % 1000]);
					break;
				case CLAUSE_ZONE:
					appendable.append(timeZone.getID());
					break;
				case CLAUSE_ESCAPE:
					appendable.append(format[++i]);
					break;
			}
		}
		return appendable;
	}

	@Override
	public String toString() {
		return new String(format);
	}

	public String toString(long now) {
		try {
			return append(new StringBuilder(), now).toString();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
