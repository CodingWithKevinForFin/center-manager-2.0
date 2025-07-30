/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.formatter;

import java.text.ParsePosition;
import java.util.Date;
import java.util.TimeZone;

import com.f1.base.DateNanos;
import com.f1.base.Day;
import com.f1.utils.DateFormatNano;
import com.f1.utils.OH;

public class BasicDateFormatter extends AbstractFormatter {

	final private DateFormatNano dateFormat;
	final private String notNull;
	final private String pattern;

	public BasicDateFormatter(String pattern) {
		this.pattern = pattern;
		this.dateFormat = new DateFormatNano(pattern);
		this.notNull = "";
	}
	public BasicDateFormatter(String pattern, TimeZone tz) {
		this.pattern = pattern;
		this.dateFormat = new DateFormatNano(pattern);
		this.dateFormat.setTimeZone(tz);
		this.notNull = "";
	}
	public BasicDateFormatter(String pattern, TimeZone tz, String notNull) {
		this.pattern = pattern;
		this.dateFormat = new DateFormatNano(pattern);
		this.dateFormat.setTimeZone(tz);
		this.notNull = notNull;
	}

	public BasicDateFormatter(DateFormatNano dateFormat, String notNull) {
		this.pattern = dateFormat.getPattern();
		this.dateFormat = dateFormat;
		this.notNull = notNull;
	}

	private static final Long ZERO = 0L;

	@Override
	public void format(Object value, StringBuilder sb) {
		if (ZERO.equals(value))
			sb.append(notNull);
		else {
			if (value instanceof Day)
				value = new Date(((Day) value).getStartMillis());
			if (value instanceof DateNanos) {
				sb.append(dateFormat.format((DateNanos) value));
				return;
			} else if (value instanceof Long) {
				sb.append(dateFormat.format(((Long) value).longValue()));
				return;
			}
			try {
				sb.append(dateFormat.format(value));
			} catch (Exception e) {
				throw new RuntimeException("Could not format: '" + value + "' (Type: " + OH.getClassName(value) + ")", e);
			}
		}
	}

	@Override
	public BasicDateFormatter clone() {
		return new BasicDateFormatter((DateFormatNano) dateFormat.clone(), notNull);
	}

	public DateFormatNano getInner() {
		return dateFormat;
	}

	@Override
	public boolean canParse(String text) {
		ParsePosition pos = new ParsePosition(0);
		return pos.getIndex() == text.length() && pos.getErrorIndex() == -1;
	}

	@Override
	public Date parse(String text) {
		ParsePosition pos = new ParsePosition(0);
		Date r = (Date) dateFormat.parseObject(text, pos);
		if (pos.getIndex() != text.length())
			throw new RuntimeException("trailing text after char " + pos.getIndex() + ": " + text);
		if (pos.getErrorIndex() != -1)
			throw new RuntimeException("could not parse, error at char " + pos.getErrorIndex() + ": " + text);
		return r;
	}

	@Override
	public String getPattern() {
		return this.pattern;
	}

}
