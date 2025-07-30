package com.f1.utils.impl;

import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import com.f1.base.Clock;
import com.f1.base.DateNanos;
import com.f1.utils.EH;

public class DefaultClock implements Clock {

	@Override
	public long getNow() {
		return EH.currentTimeMillis();
	}

	@Override
	public Locale getLocale() {
		return Locale.getDefault();
	}

	@Override
	public TimeZone getTimeZone() {
		return TimeZone.getDefault();
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
