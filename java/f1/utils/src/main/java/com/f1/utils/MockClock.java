package com.f1.utils;

import java.util.Locale;
import java.util.TimeZone;

import com.f1.utils.impl.BasicClock;

public class MockClock extends BasicClock {

	public MockClock(long now, TimeZone timeZone, Locale locale) {
		super(timeZone, locale);
		this.now = now;
	}

	private long now;

	public void setNow(long now) {
		this.now = now;
	}

	public void incNow(long add) {
		now += add;
	}

	@Override
	public long getNow() {
		return now;
	}

}
