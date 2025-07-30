package com.f1.ami.web;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class AmiWebTimeFormats {
	public final String timeFormat, timeFormatSeconds, timeFormatMillis, timeFormatMicros, timeFormatNanos, example;

	public AmiWebTimeFormats(String hhmm, String suffix, String example) {
		this.timeFormat = hhmm + suffix;
		this.timeFormatSeconds = hhmm + ":ss" + suffix;
		this.timeFormatMillis = hhmm + ":ss.SSS" + suffix;
		this.timeFormatMicros = hhmm + ":ss.SSS,rrr" + suffix;
		this.timeFormatNanos = hhmm + ":ss.SSS,rrr,RRR" + suffix;
		this.example = example;
	}

	public static final Map<String, AmiWebTimeFormats> BY_HOUR_MINUTE = new LinkedHashMap<String, AmiWebTimeFormats>();
	public static final AmiWebTimeFormats DEFAULT;

	private static AmiWebTimeFormats addTimeFormat(String prefix, String suffix, String example) {
		AmiWebTimeFormats r = new AmiWebTimeFormats(prefix, suffix, example);
		BY_HOUR_MINUTE.put(prefix + suffix, r);
		return r;
	}

	static {
		DEFAULT = addTimeFormat("H:mm", "", "1:30");
		addTimeFormat("h:mm", " a", "1:30 PM");
		addTimeFormat("HH:mm", "", "13:30");
		addTimeFormat("hh:mm", " a", "01:30 PM");
	}

	public static AmiWebTimeFormats getByTimeFormat(String hhmm) {
		return BY_HOUR_MINUTE.get(hhmm);
	}
	public static AmiWebTimeFormats getByTimeFormat(String hhmm, AmiWebTimeFormats dflt) {
		AmiWebTimeFormats r = BY_HOUR_MINUTE.get(hhmm);
		return r == null ? dflt : r;
	}

	public static Collection<AmiWebTimeFormats> options() {
		return BY_HOUR_MINUTE.values();
	}

}
