package com.f1.utils;

import java.io.IOException;
import java.io.Writer;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import com.f1.base.DateMillis;
import com.f1.base.DateNanos;

public final class DateFormatNano implements Formatter {
	private static final byte TYPE_MILLI = 0;
	private static final byte TYPE_MICRO = 1;
	private static final byte TYPE_NANO = 2;
	private static final FieldPosition DontCareFieldPosition_INSTANCE = (FieldPosition) RH.getStaticField(RH.getClass("java.text.DontCareFieldPosition"), "INSTANCE");
	private static final TimeZone UTC = TimeZone.getTimeZone("UTC");

	private abstract static class FormatChain {
		public FormatChain next;

		abstract public void setTimeZone(TimeZone tz);
		abstract public void formatMillis(long value, StringBuffer sink);
		abstract public void formatMicros(long value, StringBuffer sink);
		abstract public void formatNanos(long value, StringBuffer sink);
		abstract public FormatChain copy();
		abstract public long parseNanos(String text, ParsePosition pp, boolean honorTimezone);
		abstract public long parseMicros(String text, ParsePosition pp, boolean honorTimezone);
		abstract public long parseMillis(String text, ParsePosition pp, boolean honorTimezone);
	}

	static private class DateFormat extends FormatChain {
		private SimpleDateFormat inner;

		public DateFormat(SimpleDateFormat inner) {
			this.inner = inner;
		}

		@Override
		public void setTimeZone(TimeZone tz) {
			this.inner.setTimeZone(tz);
		}

		private Date tmp = new Date();

		@Override
		public void formatMillis(long value, StringBuffer sink) {
			tmp.setTime(value);
			this.inner.format(tmp, sink, DontCareFieldPosition_INSTANCE);
		}

		@Override
		public void formatMicros(long value, StringBuffer sink) {
			if (value < 0L)
				value -= 999L;
			formatMillis(value / 1000L, sink);
		}

		@Override
		public void formatNanos(long value, StringBuffer sink) {
			if (value < 0L)
				value -= 999999L;
			formatMillis(value / 1000000L, sink);
		}

		@Override
		public FormatChain copy() {
			return new DateFormat((SimpleDateFormat) inner.clone());
		}

		@Override
		public long parseMillis(String text, ParsePosition pp, boolean honorTimezone) {
			if (!honorTimezone) {
				TimeZone tz = this.inner.getTimeZone();
				try {
					this.inner.setTimeZone(UTC);
					return this.inner.parse(text, pp).getTime();
				} finally {
					this.inner.setTimeZone(tz);
				}
			}
			return this.inner.parse(text, pp).getTime();

		}
		@Override
		public long parseMicros(String text, ParsePosition pp, boolean honorTimezone) {
			return 1000L * parseMillis(text, pp, honorTimezone);

		}
		@Override
		public long parseNanos(String text, ParsePosition pp, boolean honorTimezone) {
			return 1000000L * parseMillis(text, pp, honorTimezone);
		}

	}

	static private class MicroFormat extends FormatChain {

		private int length;

		public MicroFormat(int length) {
			this.length = length;
		}
		@Override
		public void formatMillis(long value, StringBuffer sink) {
			for (int i = 0; i < length; i++)
				sink.append('0');
		}

		@Override
		public void formatMicros(long value, StringBuffer sink) {
			if (value < 0)
				value = value % 1000L + 1000L;
			print000(value, sink, length);
		}

		@Override
		public void formatNanos(long value, StringBuffer sink) {
			if (value < 0)
				value = value % 1000000L + 1000000L;
			print000(value / 1000, sink, length);
		}
		@Override
		public void setTimeZone(TimeZone tz) {
		}
		@Override
		public FormatChain copy() {
			return new MicroFormat(length);
		}
		@Override
		public long parseMillis(String text, ParsePosition pp, boolean honorTimezone) {
			parseNumber(text, pp, length);
			return 0;
		}
		@Override
		public long parseMicros(String text, ParsePosition pp, boolean honorTimezone) {
			return parseNumber(text, pp, length);
		}
		@Override
		public long parseNanos(String text, ParsePosition pp, boolean honorTimezone) {
			return 1000L * parseNumber(text, pp, length);
		}
	}

	static private class NanoFormat extends FormatChain {

		private int length;

		public NanoFormat(int length) {
			this.length = length;
		}
		@Override
		public void formatMillis(long value, StringBuffer sink) {
			for (int i = 0; i < length; i++)
				sink.append('0');
		}

		@Override
		public void formatMicros(long value, StringBuffer sink) {
			for (int i = 0; i < length; i++)
				sink.append('0');
		}

		@Override
		public void formatNanos(long value, StringBuffer sink) {
			if (value < 0)
				value = value % 1000L + 1000L;
			print000(value, sink, length);
		}
		@Override
		public void setTimeZone(TimeZone tz) {
		}
		@Override
		public FormatChain copy() {
			return new MicroFormat(length);
		}
		@Override
		public long parseMillis(String text, ParsePosition pp, boolean honorTimezone) {
			parseNumber(text, pp, length);
			return 0;
		}
		@Override
		public long parseMicros(String text, ParsePosition pp, boolean honorTimezone) {
			parseNumber(text, pp, length);
			return 0;
		}
		@Override
		public long parseNanos(String text, ParsePosition pp, boolean honorTimezone) {
			return parseNumber(text, pp, length);
		}
	}

	static private void print000(long value, StringBuffer sink, int length) {
		switch (length) {
			default:
				for (int i = 3; i < length; i++)
					sink.append('0');
			case 3:
				print(sink, (value / 100));
				print(sink, (value / 10));
				print(sink, value);
				break;
			case 2:
				value %= 1000;
				if (value >= 100)
					print(sink, (value / 100));
				print(sink, (value / 10));
				print(sink, value);
				break;
			case 1:
				value %= 1000;
				if (value >= 100)
					print(sink, (value / 100));
				if (value >= 10)
					print(sink, (value / 10));
				print(sink, value);
				break;

		}
	}
	static private long parseNumber(String text, ParsePosition pp, int length) {
		int i = pp.getIndex();
		int r = 0;
		if (text.length() - i < length)
			throw new RuntimeException("Unexpected end of line: " + text);
		for (int n = 0; n < length; n++) {
			char c = text.charAt(i + n);
			if (c >= '0' && c <= '9')
				r = r * 10 + (c - '0');
			else if (n == 0)
				throw new RuntimeException("Unexpected char at " + i + n + ": " + text);
			else
				break;
		}
		pp.setIndex(i + length);
		return r;
	}
	static private void print(StringBuffer sb, long i) {
		sb.append((char) ((i % 10) + '0'));
	}

	static private class StringFormat extends FormatChain {

		final private String str;
		final private int start, len;

		public StringFormat(String str, int start, int len) {
			this.str = str;
			this.start = start;
			this.len = len;
		}

		@Override
		public void setTimeZone(TimeZone tz) {
		}

		@Override
		public void formatMillis(long value, StringBuffer sink) {
			sink.append(str, start, len);
		}

		@Override
		public void formatMicros(long value, StringBuffer sink) {
			sink.append(str, start, len);
		}

		@Override
		public void formatNanos(long value, StringBuffer sink) {
			sink.append(str, start, len);
		}

		@Override
		public FormatChain copy() {
			return new StringFormat(str, start, len);
		}

		@Override
		public long parseMillis(String text, ParsePosition pp, boolean honorTimezone) {
			int i = pp.getIndex();
			if (text.length() - i < len)
				throw new RuntimeException("Unexpected end of line: " + text);
			for (int n = 0; n < len; n++) {
				if (text.charAt(i + n) != this.str.charAt(start + n))
					throw new RuntimeException("Unexpected char at " + i + n + ": " + text);
			}
			pp.setIndex(i + len);
			return 0;
		}

		@Override
		public long parseNanos(String text, ParsePosition pp, boolean honorTimezone) {
			return parseMillis(text, pp, honorTimezone);
		}

		@Override
		public long parseMicros(String text, ParsePosition pp, boolean honorTimezone) {
			return parseMillis(text, pp, honorTimezone);
		}

	}

	static private class CharFormat extends FormatChain {

		private char c;

		public CharFormat(char c) {
			this.c = c;
		}

		@Override
		public void setTimeZone(TimeZone tz) {
		}

		@Override
		public void formatMillis(long value, StringBuffer sink) {
			sink.append(c);
		}

		@Override
		public void formatMicros(long value, StringBuffer sink) {
			sink.append(c);
		}

		@Override
		public void formatNanos(long value, StringBuffer sink) {
			sink.append(c);
		}

		@Override
		public FormatChain copy() {
			return new CharFormat(c);
		}

		@Override
		public long parseMillis(String text, ParsePosition pp, boolean honorTimezone) {
			int i = pp.getIndex();
			if (text.length() < i + 1)
				throw new RuntimeException("Unexpected end of line: " + text);
			if (text.charAt(i) != this.c)
				throw new RuntimeException("Unexpected char at " + i + ": " + text);
			pp.setIndex(i + 1);
			return 0;
		}

		@Override
		public long parseNanos(String text, ParsePosition pp, boolean honorTimezone) {
			return parseMillis(text, pp, honorTimezone);
		}

		@Override
		public long parseMicros(String text, ParsePosition pp, boolean honorTimezone) {
			return parseMillis(text, pp, honorTimezone);
		}

	}

	private String pattern;
	private byte type;
	private FormatChain chain;
	private TimeZone timezone;

	public DateFormatNano(String pattern) {
		reset(pattern);
	}

	public DateFormatNano(DateFormatNano o) {
		this.pattern = o.pattern;
		this.type = o.type;
		this.timezone = o.timezone;
		if (o.chain != null) {
			this.chain = o.chain.copy();
			for (FormatChain t = this.chain; t.next != null; t = t.next)
				t.next = t.next.copy();
		}
	}

	public void setTimeZone(TimeZone timezone) {
		if (this.timezone == timezone)
			return;
		this.timezone = timezone;
		for (FormatChain c = chain; c != null; c = c.next)
			c.setTimeZone(timezone);
	}

	private void reset(String pattern) {
		this.chain = null;
		FormatChain chain = null;
		FormatChain tail = null;
		boolean inQuote = false, isNano = false, isMicro = false;
		int startPos = 0;
		int length = pattern.length();
		int i = 0;
		boolean isSpecial = true;
		boolean hasQuote = true;
		while (i < length) {
			char c = pattern.charAt(i);
			if (c == '\'') {
				inQuote = !inQuote;
				hasQuote = true;
				i++;
			} else if (inQuote) {
				i++;
			} else if (c == 'r') {
				if (startPos < i) {
					FormatChain t = toChain(pattern, startPos, i, isSpecial, hasQuote);
					if (chain == null)
						chain = tail = t;
					else
						tail = tail.next = t;
				}
				isMicro = true;
				int end = i + 1;
				while (end < length && pattern.charAt(end) == 'r')
					end++;
				if (chain == null)
					chain = tail = new MicroFormat(end - i);
				else
					tail = tail.next = new MicroFormat(end - i);
				startPos = i = end;
				isSpecial = hasQuote = false;
			} else if (c == 'R') {
				if (startPos < i) {
					FormatChain t = toChain(pattern, startPos, i, isSpecial, hasQuote);
					if (chain == null)
						chain = tail = t;
					else
						tail = tail.next = t;
				}
				isNano = true;
				int end = i + 1;
				while (end < length && pattern.charAt(end) == 'R')
					end++;
				if (chain == null)
					chain = tail = new NanoFormat(end - i);
				else
					tail = tail.next = new NanoFormat(end - i);
				startPos = i = end;
				isSpecial = hasQuote = false;
			} else if (c == '.' && startPos == i) {//TODO: check for more special break chars than just period
				FormatChain t = new CharFormat('.');
				if (chain == null)
					chain = tail = t;
				else
					tail = tail.next = t;
				i++;
				startPos++;
			} else {
				if (isSpecial && !isSpecial(c))
					isSpecial = false;
				i++;
			}
		}
		if (inQuote)
			throw new IllegalArgumentException("Unterminated quote in pattern (')");
		if (startPos < length) {
			FormatChain t = toChain(pattern, startPos, length, isSpecial, hasQuote);
			if (chain == null)
				chain = tail = t;
			else
				tail = tail.next = t;
		}
		this.chain = chain;
		this.pattern = pattern;
		if (isNano)
			this.type = TYPE_NANO;
		else if (isMicro)
			this.type = TYPE_MICRO;
		else
			this.type = TYPE_MILLI;
	}
	private static FormatChain toChain(String s, int start, int end, boolean isSpecial, boolean hasQuote) {
		if (!isSpecial)
			return new DateFormat(new SimpleDateFormat(s.substring(start, end)));
		final int len = end - start;
		if (hasQuote) {
			if (len == 2 && s.charAt(0) == '\'')
				return new CharFormat('\'');
			StringBuilder sb = new StringBuilder(len);
			for (int i = start; i < end; i++) {
				char c = s.charAt(i);
				if (c != '\'')
					sb.append(c);
				else {
					sb.append(c = s.charAt(++i));
					if (c != '\'')
						for (;;)
							if ((c = s.charAt(++i)) == '\'')
								break;
							else
								sb.append(c);
				}
			}
			return new StringFormat(sb.toString(), 0, sb.length());
		}
		return len == 1 ? new CharFormat(s.charAt(start)) : new StringFormat(s, start, len);
	}
	private static boolean isSpecial(char c) {
		return c != '\'' && OH.isntBetween(c, 'a', 'z') && OH.isntBetween(c, 'A', 'Z');
	}
	public static void main(String b[]) {
	}

	private StringBuffer formatInner(byte type, long value, StringBuffer sink) {
		synchronized (sink) {
			sink.setLength(0);
			switch (type) {
				case TYPE_NANO:
					for (FormatChain c = chain; c != null; c = c.next)
						c.formatNanos(value, sink);
					break;
				case TYPE_MICRO:
					for (FormatChain c = chain; c != null; c = c.next)
						c.formatMicros(value, sink);
					break;
				case TYPE_MILLI:
					for (FormatChain c = chain; c != null; c = c.next)
						c.formatMillis(value, sink);
					break;
			}
		}
		return sink;
	}
	@Override
	public String get(Object key) {
		return format(key);
	}

	private StringBuffer tmp = new StringBuffer();

	@Override
	public void format(Object value, StringBuilder out) {
		out.append(format(value));
	}

	public String format(long value) {
		return formatInner(type, value, tmp).toString();
	}
	public String format(Number value) {
		if (value instanceof DateMillis)
			return format((DateMillis) value);
		else if (value instanceof DateNanos)
			return format((DateNanos) value);
		else
			return formatInner(type, value.longValue(), tmp).toString();
	}
	public String format(java.util.Date value) {
		return formatInner(TYPE_MILLI, value.getTime(), tmp).toString();
	}
	public String format(DateMillis value) {
		return formatInner(TYPE_MILLI, value.getDate(), tmp).toString();
	}
	public String format(DateNanos value) {
		return formatInner(TYPE_NANO, value.getTimeNanos(), tmp).toString();
	}
	@Override
	public String format(Object value) {
		if (value instanceof Number) {
			return format((Number) value);
		} else if (value instanceof java.util.Date) {
			return format((java.util.Date) value);
		} else if (value == null) {
			return null;
		} else
			throw new IllegalArgumentException(OH.getClassName(value));
	}
	@Override
	public void format(Object value, Writer out) throws IOException {
		out.append(format(value));
	}

	@Override
	public boolean canFormat(Object obj) {
		return obj instanceof Number || obj instanceof java.util.Date || obj instanceof DateNanos;
	}

	@Override
	public boolean canParse(String text) {
		return (chain instanceof DateFormat);
	}

	@Override
	public Date parse(String text) {
		return new java.util.Date(parseTo(text, TYPE_MILLI));
	}

	public DateNanos parseToNanos(String text) {
		return new DateNanos(parseTo(text, TYPE_NANO));
	}

	private long parseTo(String text, byte type) {
		ParsePosition pp = new ParsePosition(0);
		long r = 0;
		boolean htz = true;
		switch (type) {
			case TYPE_NANO:
				for (FormatChain c = chain; c != null; c = c.next) {
					r += c.parseNanos(text, pp, htz);
					if (htz && c instanceof DateFormat)
						htz = false;
				}
				break;
			case TYPE_MICRO:
				for (FormatChain c = chain; c != null; c = c.next) {
					r += c.parseMicros(text, pp, htz);
					if (htz && c instanceof DateFormat)
						htz = false;
				}
				break;
			case TYPE_MILLI:
				for (FormatChain c = chain; c != null; c = c.next) {
					r += c.parseMillis(text, pp, htz);
					if (htz && c instanceof DateFormat)
						htz = false;
				}
				break;
		}
		return r;
	}

	@Override
	public String getPattern() {
		return pattern;
	}
	public DateFormatNano clone() {
		return new DateFormatNano(this);
	}

	public Date parseObject(String text, ParsePosition pos) {
		return (chain instanceof DateFormat) ? ((DateFormat) chain).inner.parse(text, pos) : null;
	}

}
