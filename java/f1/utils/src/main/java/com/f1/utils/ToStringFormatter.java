package com.f1.utils;

import com.f1.utils.formatter.AbstractFormatter;

public final class ToStringFormatter extends AbstractFormatter {

	final private String prefix;
	final private String suffix;
	final private String nullReplacement;

	public ToStringFormatter() {
		this.nullReplacement = this.prefix = this.suffix = null;
	}

	public ToStringFormatter(String prefix, String suffix, String nullReplacement) {
		this.prefix = prefix;
		this.suffix = suffix;
		this.nullReplacement = nullReplacement;
	}

	@Override
	public String format(Object key) {
		if (key == null) {
			return nullReplacement;
		} else if (suffix == null && prefix == null) {
			return SH.s(key);
		} else {
			final StringBuilder sb = new StringBuilder();
			if (prefix != null)
				sb.append(prefix);
			SH.s(key, sb);
			if (suffix != null)
				sb.append(suffix);
			return sb.toString();
		}
	}

	public void format(Object key, StringBuilder sb) {
		if (key == null) {
			sb.append(nullReplacement);
		} else if (suffix == null && prefix == null) {
			SH.s(key, sb);
		} else {
			if (prefix != null)
				sb.append(prefix);
			SH.s(key, sb);
			if (suffix != null)
				sb.append(suffix);
		}
	}

	@Override
	public boolean canParse(String text) {
		return false;
	}

	@Override
	public Object parse(String text) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Formatter clone() {
		return this;
	}

}
