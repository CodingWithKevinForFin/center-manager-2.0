package com.f1.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * Similar to the unix cron implementation. Please note that the job run's in process meaning at most one instance of the job will be running at a given time. Additionally, jobs
 * will <b>not</b> be queued. For example, if you schedule a job to run every 15 seconds (0,15,30,45 * * * * *) but that job takes 20 to complete then the job will only run twice
 * per minute. Also, you can indicate that the job should run a particular number of times<BR>
 * 
 * <P>
 * <B>Threading</B><BR>
 * CronTab implements {@link Runnable} and it is expected that the user will supply a thread (or calls ). This Thread will be the same thread that runs the job. If the number of
 * times the job should be run(see {@link #getRemaining()}) is specified then the Thread will return after the last job has been run<BR>
 * See {@link #start(boolean)}
 * <P>
 * 
 * new Thread("crontab thread",new CronTab("* * * * * *"),myRunnable).start();<BR>
 * or<BR>
 * new CronTab("* * * * * *",myRunnable).start(false);//Don't exit while this is still running<BR>
 * 
 * @author rcooke
 * 
 */
public class CronTab implements Timer {
	private static final Map<Integer, Integer> CALENDAR_DAYS_TO_DAYS = CH.m(Calendar.SUNDAY, 0, Calendar.MONDAY, 1, Calendar.TUESDAY, 2, Calendar.WEDNESDAY, 3, Calendar.THURSDAY,
			4, Calendar.FRIDAY, 5, Calendar.SATURDAY, 6);
	private static final Map<String, Integer> WEEKDAY_NAMES = CH.m("SUN", 0, "MON", 1, "TUE", 2, "WED", 3, "THU", 4, "FRI", 5, "SAT", 6, "7", 0);
	private static final Map<String, Integer> MONTH_NAMES = CH.m("JAN", 1, "FEB", 2, "MAR", 3, "APR", 4, "MAY", 5, "JUN", 6, "JUL", 7, "AUG", 8, "SEP", 9, "OCT", 10, "NOV", 11,
			"DEC", 12);
	private static final int[] ALL_SECONDS = new int[60];
	private static final int[] ALL_MINUTES = new int[60];
	private static final int[] ALL_HOURS = new int[24];
	private static final int[] ALL_DAYS = new int[31];
	private static final int[] ALL_MONTHS = new int[12];
	private static final int[] ALL_WEEKDAYS = new int[7];
	static {
		for (int i = 0; i < 60; i++) {
			if (i < ALL_SECONDS.length)
				ALL_SECONDS[i] = i;
			if (i < ALL_MINUTES.length)
				ALL_MINUTES[i] = i;
			if (i < ALL_HOURS.length)
				ALL_HOURS[i] = i;
			if (i < ALL_DAYS.length)
				ALL_DAYS[i] = i + 1;
			if (i < ALL_MONTHS.length)
				ALL_MONTHS[i] = i;
			if (i < ALL_WEEKDAYS.length)
				ALL_WEEKDAYS[i] = i;
		}
	}

	private final int seconds[], minutes[], hours[], days[], months[], weekdays[];
	private final Calendar calendar;

	public CronTab(int months[], int days[], int weekdays[], int hours[], int minutes[], int seconds[], TimeZone timeZone) {
		this.months = AH.noEmpty(months, ALL_MONTHS);
		this.days = AH.noEmpty(days, ALL_DAYS);
		this.weekdays = AH.noEmpty(weekdays, ALL_WEEKDAYS);
		this.hours = AH.noEmpty(hours, ALL_HOURS);
		this.minutes = AH.noEmpty(minutes, ALL_MINUTES);
		this.seconds = AH.noEmpty(seconds, ALL_SECONDS);
		this.calendar = Calendar.getInstance();
		this.calendar.setTimeZone(timeZone);
	}

	/**
	 * @param expression
	 * 
	 *            <PRE>
	 * 
	 *          .------------------- second (0 - 59) <BR>
	 *          | .---------------- minute (0 - 59) <BR>
	 *          | | .------------- hour (0 - 23)<BR>
	 *          | | | .---------- day_of_month (1 - 31)<BR>
	 *          | | | | .------- month (1 - 12) OR jan,feb,mar,apr,may,jun,jul,aug,sep,oct,nov,dec <BR>
	 *          | | | | | .---- day_of_week (0 - 6) (Sunday=0 or 7) OR sun,mon,tue,wed,thu,fri,sat <BR>
	 *          | | | | | |<BR>
	 *          * * * * * *<BR>
	 *            </PRE>
	 *            <P>
	 *            OR
	 *            <P>
	 *            HH:MM[:SS] (runs every day)
	 *            <P>
	 *            OR
	 *            <P>
	 *            HH:MM[:SS] days_of_week
	 *            <P>
	 *            OR
	 *            <P>
	 *            HH:MM[:SS] month day_of_month
	 * 
	 * @param runnable
	 *            runnable to call on specified period
	 */

	public CronTab(String expression, TimeZone timeZone) {
		this.calendar = Calendar.getInstance();
		this.calendar.setTimeZone(timeZone);
		String parts[] = SH.splitContinous(' ', expression.trim());
		if (parts.length == 0)
			throw new IllegalArgumentException("invalid expression: " + expression);
		if (SH.indexOf(parts[0], ':', 1) != -1) {
			String parts2[] = SH.split(':', parts[0].trim());
			if (parts2.length == 2) {//HH:MM
				this.hours = parsePart(parts2[0], 0, 23, 0, "hour", null);
				this.minutes = parsePart(parts2[1], 0, 59, 0, "minute", null);
				this.seconds = new int[] { 0 };
			} else if (parts2.length == 3) {//HH:MM:SS
				this.hours = parsePart(parts2[0], 0, 23, 0, "hour", null);
				this.minutes = parsePart(parts2[1], 0, 59, 0, "minute", null);
				seconds = AH.noEmpty(parsePart(parts2[2], 0, 59, 0, "second", null), ALL_SECONDS);// : ALL_SECONDS;
			} else
				throw new IllegalArgumentException("Expression must be hh:mm[:ss] [weekday(s)|month day]" + expression);
			if (parts.length == 1) {//hh:mm[:ss]
				this.days = ALL_DAYS;
				this.weekdays = ALL_WEEKDAYS;
				this.months = ALL_MONTHS;
			} else if (parts.length == 2) {//hh:mm[:ss] weekday
				this.days = ALL_DAYS;
				weekdays = AH.noEmpty(parsePart(parts[1], 0, 6, 0, "weekday", WEEKDAY_NAMES), ALL_WEEKDAYS);// : ALL_WEEKDAYS;
				this.months = ALL_MONTHS;
			} else if (parts.length == 3) {//hh:mm[:ss] month day
				this.days = AH.noEmpty(parsePart(parts[2], 1, 31, 0, "day", null), ALL_DAYS);
				this.weekdays = ALL_WEEKDAYS;
				this.months = AH.noEmpty(parsePart(parts[1], 1, 12, -1, "month", MONTH_NAMES), ALL_MONTHS);
			} else
				throw new IllegalArgumentException("Expression must be hh:mm[:ss] [weekday(s)|month day]" + expression);
		} else {
			if (parts.length < 6)
				throw new IllegalArgumentException("Expression must include 6 fields(second minute hour day month weekday): " + expression);
			seconds = AH.noEmpty(parsePart(parts[0], 0, 59, 0, "second", null), ALL_SECONDS);// : ALL_SECONDS;
			minutes = AH.noEmpty(parsePart(parts[1], 0, 59, 0, "minute", null), ALL_MINUTES);// : ALL_MINUTES;
			hours = AH.noEmpty(parsePart(parts[2], 0, 23, 0, "hour", null), ALL_HOURS);// : ALL_HOURS;
			days = AH.noEmpty(parsePart(parts[3], 1, 31, 0, "day", null), ALL_DAYS);// : ALL_DAYS;
			months = AH.noEmpty(parsePart(parts[4], 1, 12, -1, "month", MONTH_NAMES), ALL_MONTHS);// : ALL_MONTHS;
			weekdays = AH.noEmpty(parsePart(parts[5], 0, 6, 0, "weekday", WEEKDAY_NAMES), ALL_WEEKDAYS);// : ALL_WEEKDAYS;
		}
	}
	private int[] parsePart(String part, int min, int max, int offset, String description, Map<String, Integer> optionalAliases) {
		try {
			if ("*".equals(part))
				return OH.EMPTY_INT_ARRAY;
			List<Integer> l = new ArrayList<Integer>();
			if (part.startsWith("*/")) {
				int denominator = parseIntOrAlias(part.substring(2).trim(), optionalAliases);
				if (denominator <= 0)
					throw new IllegalArgumentException(" denominator must be positive number");
				int i = "month".equals(description) || "day".equals(description) ? 1 : 0;
				for (; i <= max; i += denominator)
					l.add(i);
			} else if (part.indexOf('-') != -1) {
				int left = parseIntOrAlias(SH.beforeFirst(part, '-'), optionalAliases);
				int right = parseIntOrAlias(SH.afterFirst(part, '-'), optionalAliases);
				for (int i = left; i <= right; i++)
					l.add(i);
			} else {
				for (String s : SH.split(',', part))
					l.add(parseIntOrAlias(s, optionalAliases));
			}
			Collections.sort(l);
			int[] r = new int[l.size()];
			for (int i = 0; i < l.size(); i++) {
				Integer val = l.get(i);
				if (val > max)
					throw new IllegalArgumentException("" + val + " > " + max);
				else if (val < min)
					throw new IllegalArgumentException("" + val + " < " + min);
				r[i] = val + offset;
			}
			return r;
		} catch (Exception e) {
			throw new IllegalArgumentException("invalid " + description + ": " + e.getMessage(), e);
		}
	}

	private int parseIntOrAlias(String text, Map<String, Integer> optionalAliases) {
		text = text.trim();
		if (optionalAliases != null) {
			Integer r = optionalAliases.get(text.toUpperCase());
			if (r != null)
				return r;
			try {
				SH.parseIntSafe(text, false, true);
			} catch (Exception e) {
				throw new RuntimeException("Valid options are " + SH.join(',', optionalAliases.keySet()));
			}
		}
		return SH.parseInt(text);
	}

	@Override
	public long calculateNextOccurance(final long now) {
		long n = now;
		MONTHS: while (true) {
			TimeRange monthsRange = getMonthsRange(n, Long.MAX_VALUE);
			if (monthsRange == null)
				throw new IllegalStateException();
			if (monthsRange.end <= now) {
				n = monthsRange.end;
				continue;
			}
			n = monthsRange.start;
			DAYS: while (true) {
				TimeRange daysRange = getDaysRange(n, monthsRange.end);
				if (daysRange == null) {
					n = monthsRange.end;
					break;
				}
				if (daysRange.end <= now) {
					n = daysRange.end;
					continue;
				}
				n = daysRange.start;
				DAYS_OF_WEEK: while (true) {
					TimeRange weekDaysRange = getWeekDaysRange(n, daysRange.end);
					if (weekDaysRange == null) {
						n = daysRange.end;
						break;
					}
					if (weekDaysRange.end <= now) {
						n = weekDaysRange.end;
						continue;
					}
					n = weekDaysRange.start;
					HOURS: while (true) {
						TimeRange hoursRange = getHoursRange(n, weekDaysRange.end);
						if (hoursRange == null) {
							n = weekDaysRange.end;
							break;
						}
						if (hoursRange.end <= now) {
							n = hoursRange.end;
							continue;
						}
						n = hoursRange.start;
						MINUTES: while (true) {
							TimeRange minutesRange = getMinutesRange(n, hoursRange.end);
							if (minutesRange == null) {
								n = hoursRange.end;
								break;
							}
							if (minutesRange.end <= now) {
								n = minutesRange.end;
								continue;
							}
							n = minutesRange.start;
							SECONDS: while (true) {
								TimeRange secondsRange = getSecondsRange(n, minutesRange.end);
								if (secondsRange == null) {
									n = minutesRange.end;
									break;
								}
								if (secondsRange.start < now) {
									n = secondsRange.end;
									continue;
								}
								return secondsRange.start;
							}
						}
					}
				}
			}
		}
	}

	private TimeRange getRange(long now, long max, int values[], int field, int parentField) {
		if (now >= max)
			return null;
		if (values.length == 0)
			return new TimeRange(now, max);
		Calendar c = this.calendar;
		c.setTimeInMillis(now);
		clearFieldsAfter(c, field);
		int s = c.get(field);
		boolean found = false;
		for (int value : values) {
			if (value >= s) {
				c.set(field, value);
				found = true;
				break;
			}
		}
		if (!found) {
			c.set(field, values[0]);
			c.add(parentField, 1);
		}
		long start = c.getTimeInMillis();
		if (start >= max)
			return null;
		c.add(field, 1);
		long end = c.getTimeInMillis();
		return new TimeRange(start, end);
	}

	private TimeRange getSecondsRange(long now, long max) {
		return getRange(now, max, seconds, Calendar.SECOND, Calendar.MINUTE);
	}

	private TimeRange getMinutesRange(long now, long max) {
		return getRange(now, max, minutes, Calendar.MINUTE, Calendar.HOUR_OF_DAY);
	}

	private TimeRange getHoursRange(long now, long max) {
		return getRange(now, max, hours, Calendar.HOUR_OF_DAY, Calendar.DAY_OF_MONTH);
	}

	private TimeRange getDaysRange(long now, long max) {
		return getRange(now, max, days, Calendar.DAY_OF_MONTH, Calendar.MONTH);
	}

	private TimeRange getMonthsRange(long now, long max) {
		return getRange(now, max, months, Calendar.MONTH, Calendar.YEAR);
	}

	private TimeRange getWeekDaysRange(long now, long max) {
		if (now >= max)
			return null;
		if (weekdays.length == 0)
			return new TimeRange(now, max);
		Calendar c = this.calendar;
		c.setTimeInMillis(now);
		clearFieldsAfter(c, Calendar.DAY_OF_MONTH);
		Integer t = (c.get(Calendar.DAY_OF_WEEK));
		Integer t2 = CALENDAR_DAYS_TO_DAYS.get(t);
		if (t2 == null)
			throw new RuntimeException("unknown day: " + t);
		int day = t2;
		int increment = -1;
		for (int value : weekdays) {
			if (value >= day) {
				increment = value - day;
				break;
			}
		}
		if (increment == -1) {
			increment = weekdays[0] + 7 - day;
		}
		c.add(Calendar.DAY_OF_YEAR, increment);
		long start = c.getTimeInMillis();
		if (start >= max)
			return null;
		c.add(Calendar.DAY_OF_YEAR, 1);
		long end = c.getTimeInMillis();
		return new TimeRange(start, end);
	}

	private class TimeRange {
		final public long start;
		final public long end;

		public TimeRange(long start, long end) {
			this.start = start;
			this.end = end;
		}

		public String toString() {
			return formatTime(getTimeZone(), start) + "-" + formatTime(getTimeZone(), end);
		}
	}

	public TimeZone getTimeZone() {
		return this.calendar.getTimeZone();
	}

	static public void clearFieldsOnAndAfter(Calendar c, final int mostSignificantField) {
		switch (mostSignificantField) {
			case Calendar.YEAR:
				c.set(Calendar.YEAR, 0);
			case Calendar.MONTH:
				c.set(Calendar.MONTH, 0);
			case Calendar.DAY_OF_MONTH:
				c.set(Calendar.DAY_OF_MONTH, 1);
			case Calendar.HOUR_OF_DAY:
				c.set(Calendar.HOUR_OF_DAY, 0);
			case Calendar.MINUTE:
				c.set(Calendar.MINUTE, 0);
			case Calendar.SECOND:
				c.set(Calendar.SECOND, 0);
			case Calendar.MILLISECOND:
				c.set(Calendar.MILLISECOND, 0);
				break;
			case Calendar.DAY_OF_WEEK:
				c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
				c.set(Calendar.HOUR_OF_DAY, 0);
				c.set(Calendar.MINUTE, 0);
				c.set(Calendar.SECOND, 0);
				c.set(Calendar.MILLISECOND, 0);
				break;
			default:
				throw new IllegalArgumentException("not supported:" + mostSignificantField);
		}
	}

	static public void clearFieldsAfter(Calendar c, final int mostSignificantField) {
		switch (mostSignificantField) {
			case Calendar.YEAR:
				c.set(Calendar.MONTH, 0);
			case Calendar.MONTH:
				c.set(Calendar.DAY_OF_MONTH, 1);
			case Calendar.DAY_OF_MONTH:
				c.set(Calendar.HOUR_OF_DAY, 0);
			case Calendar.HOUR_OF_DAY:
				c.set(Calendar.MINUTE, 0);
			case Calendar.MINUTE:
				c.set(Calendar.SECOND, 0);
			case Calendar.SECOND:
				c.set(Calendar.MILLISECOND, 0);
			case Calendar.MILLISECOND:
				break;
			default:
				throw new IllegalArgumentException("not supported:" + mostSignificantField);
		}
	}

	static public final Calendar newCalendar(TimeZone timeZone, long time) {
		Calendar r = Calendar.getInstance();
		r.setTimeZone(timeZone);
		r.setTimeInMillis(time);
		return r;
	}

	static public String formatTime(TimeZone timeZone, long time) {
		DateFormat df = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
		return df.format(new Date(time));
	}
	public static void main(String a[]) {
		CronTab ct = new CronTab("0 0 16 * * 1-5", TimeZone.getDefault());

		long now = System.currentTimeMillis();
		for (int i = 0; i < 100; i++) {
			long next;
			System.out.println(new Date(next = ct.calculateNextOccurance(now)) + " ( " + next + " )");
			now = next + 1000;
		}
	}

	public int getWeekInYear(long r) {
		calendar.setTimeInMillis(r);
		return calendar.get(Calendar.WEEK_OF_YEAR);
	}

	public int getWeekInMonth(long r) {
		calendar.setTimeInMillis(r);
		return calendar.get(Calendar.WEEK_OF_MONTH);
	}

	public int getDayInMonth(long r) {
		calendar.setTimeInMillis(r);
		return calendar.get(Calendar.DAY_OF_MONTH);
	}

	public int getDayOfWeekInMonth(long r) {
		calendar.setTimeInMillis(r);
		return calendar.get(Calendar.DAY_OF_WEEK_IN_MONTH);
	}

	public long getNextDay(long r) {
		calendar.setTimeInMillis(r);
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		clearFieldsAfter(calendar, Calendar.DAY_OF_MONTH);
		return calendar.getTimeInMillis();
	}

	//Allows for multiple crontabs separated by &
	public static Timer parse(String s, TimeZone timeZone) {
		if (SH.indexOf(s, '&', 0) == -1)
			return parseSingle(s, timeZone);
		String[] parts = SH.split('&', s);
		Timer[] timers = new Timer[parts.length];
		for (int i = 0; i < parts.length; i++)
			timers[i] = parseSingle(parts[i], timeZone);
		return new MultiTimer(timers);
	}

	private static Timer parseSingle(String s, TimeZone timeZone) {
		s = SH.trim(s);
		if (SH.isEmpty(s))
			throw new IllegalArgumentException("empty timer expression");
		//		if (SH.areBetween(s, '0', '9'))
		//			return new RepeatTimer(SH.parseLong(s), TimeUnit.MILLISECONDS);
		return new CronTab(s, timeZone);
	}
}
