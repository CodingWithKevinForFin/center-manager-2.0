package com.f1.utils.impl;

import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import com.f1.base.Clock;
import com.f1.base.DateNanos;
import com.f1.utils.EH;

public class BasicClock implements Clock {

	private TimeZone timeZone;
	private Locale locale;

	public BasicClock(TimeZone timeZone, Locale locale) {
		this.timeZone = timeZone;
		this.locale = locale;
	}

	public BasicClock() {
		this(TimeZone.getDefault(), Locale.getDefault());
	}

	@Override
	public long getNow() {
		return EH.currentTimeMillis();
	}

	@Override
	public Locale getLocale() {
		return locale;
	}

	@Override
	public TimeZone getTimeZone() {
		return timeZone;
	}

	@Override
	public long getNowNano() {
		return EH.currentTimeNanos();
	}

	@Override
	public DateNanos getNowNanoDate() {
		return new DateNanos(getNowNano());
	}

	@Override
	public Date getNowDate() {
		return new Date(getNow());
	}

}
