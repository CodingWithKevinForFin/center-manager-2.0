package com.f1.utils.impl;

import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import com.f1.base.Clock;
import com.f1.base.DateNanos;
import com.f1.utils.EH;

public class SimulatedClock implements Clock {

	private TimeZone timeZone;
	private Locale locale;
	private long shift;
	private double dilate;
	private long startNanos;
	private long startMillis;

	public SimulatedClock(TimeZone timeZone, Locale locale, long shift, double dilate) {
		startNanos = EH.currentTimeNanos();
		startMillis = EH.currentTimeMillis();
		this.timeZone = timeZone;
		this.locale = locale;
		this.shift = shift;
		this.dilate = dilate;

	}
	@Override
	public long getNow() {
		long dif = (long) (EH.currentTimeMillis() - startMillis);
		dif *= dilate;
		return dif + startMillis + shift / 1000000L;
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
		return ((long) ((EH.currentTimeNanos() - startNanos) * dilate)) + startNanos + shift;
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
