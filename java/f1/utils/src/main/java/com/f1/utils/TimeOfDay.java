package com.f1.utils;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.logging.Logger;

import com.f1.utils.structs.LongKeyMap;

//Thread safe
public class TimeOfDay {
	final static private Logger log = LH.get();

	volatile private LongKeyMap<Boolean> timeInDay = LongKeyMap.EMPTY;
	volatile private long lastCache = -1L;//low24=day, high40=offset Given Day in seconds
	final private TimeZone timezone;
	final private int millis;
	final private GregorianCalendar cal;
	final private int seconds;
	final private int minutes;
	final private int hours;
	final private String timeAndTz;
	final private int dstOffset;
	final private int regOffset;
	final private Date tmpDate = new Date();
	final private int millisSinceMidnight;

	public TimeOfDay(String timeAndTz) {
		this.timeAndTz = timeAndTz;
		String time = SH.trim(SH.beforeFirst(timeAndTz, " "));
		String tz = SH.trim(SH.afterFirst(timeAndTz, " "));
		int millis = (int) SH.parseTime(time);
		OH.assertBetween(millis, 0, 86399999, "milliseconds for supplied time of day");
		this.millis = millis % 1000;
		this.seconds = (millis / 1000) % 60;
		this.minutes = (millis / 60000) % 60;
		this.hours = (millis / 3600000) % 24;
		this.millisSinceMidnight = millis;
		this.timezone = EH.getTimeZone(tz);
		this.cal = new GregorianCalendar();
		this.cal.setTimeZone(timezone);
		this.dstOffset = this.timezone.getRawOffset() + this.timezone.getDSTSavings();
		this.regOffset = this.timezone.getRawOffset();
	}
	// 0 = same, 1 supplied Time is after, -1 supplied Time is before
	public int compare(long datetime) {
		return OH.compare(datetime, getTimeForToday(datetime));
	}
	public boolean isGe(long datetime) {
		return datetime >= getTimeForToday(datetime);
	}
	public boolean isLe(long datetime) {
		return datetime <= getTimeForToday(datetime);
	}
	public boolean isLt(long datetime) {
		return datetime < getTimeForToday(datetime);
	}
	public boolean isGt(long datetime) {
		return datetime > getTimeForToday(datetime);
	}
	public boolean isEq(long datetime) {
		return datetime == getTimeForToday(datetime);
	}

	public long getTimeForToday(long datetime) {
		long day = (datetime / 86400000);
		long cache = this.lastCache;
		boolean isDst;
		if ((cache & 0xffffff) == day) {
			isDst = (cache >> 24) == 1;
		} else {
			Boolean t = timeInDay.get(day);
			if (t == null) {
				synchronized (this) {
					tmpDate.setTime(day * 86400000);
					isDst = timezone.inDaylightTime(tmpDate);
					if (timeInDay.size() < 100) {
						LongKeyMap<Boolean> t2 = new LongKeyMap<Boolean>(timeInDay);
						t2.put(day, isDst);
						this.timeInDay = t2;
					}
				}
			} else
				isDst = t.booleanValue();
			this.lastCache = isDst ? (day | (1 << 24)) : day;
		}
		long offset = isDst ? this.dstOffset : this.regOffset;
		long t = ((datetime + offset) / 86400000) * 86400000 + millisSinceMidnight - offset;
		return t;
	}

	public TimeZone getTimeZone() {
		return this.timezone;
	}

	public int getMillis() {
		return millis;
	}
	public int getSeconds() {
		return seconds;
	}
	public int getMinutes() {
		return minutes;
	}
	public int getHours() {
		return hours;
	}
}
