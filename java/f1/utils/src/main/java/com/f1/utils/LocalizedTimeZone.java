package com.f1.utils;

import java.util.Locale;
import java.util.TimeZone;

public class LocalizedTimeZone implements Comparable<LocalizedTimeZone> {

	final private TimeZone timeZone;
	final private Locale displayLocale;
	private boolean isLocalTimeZone;

	public LocalizedTimeZone(TimeZone timeZone, Locale displayLocale, TimeZone displayTimeZone) {
		this.timeZone = timeZone;
		this.displayLocale = displayLocale;
		this.isLocalTimeZone = OH.eq(displayTimeZone.getID(), timeZone.getID());
	}

	public TimeZone getTimeZone() {
		return timeZone;
	}

	public String getId() {
		return timeZone.getID();
	}

	public String toString() {
		return getId();
	}

	public String toDisplayString() {
		return timeZone.getDisplayName(displayLocale);
	}

	@Override
	public int compareTo(LocalizedTimeZone o) {
		return o == null ? 1 : OH.compare(getRawOffset(), getRawOffset());
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof LocalizedTimeZone && compareTo((LocalizedTimeZone) obj) == 0;
	}

	private int getRawOffset() {
		return timeZone.getRawOffset();
	}

	public boolean isLocalTimeZone() {
		return isLocalTimeZone;
	}

}
