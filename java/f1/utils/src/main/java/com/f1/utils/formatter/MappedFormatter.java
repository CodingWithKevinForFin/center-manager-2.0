/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.formatter;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import com.f1.utils.CH;
import com.f1.utils.Formatter;

public class MappedFormatter implements Formatter {

	private Map<Object, String> values;

	public MappedFormatter(Object... map) {
		values = CH.m(map);
	}

	public MappedFormatter(Map<Object, String> values) {
		this.values = values;
	}

	public MappedFormatter setDefault(String defaultValue) {
		this.defaultValue = defaultValue;
		return this;
	}

	public MappedFormatter put(Object key, String value) {
		CH.putOrThrow(values, key, value);
		return this;
	}

	public String defaultValue;

	@Override
	public void format(Object value, StringBuilder sb) {
		sb.append(format(value));
	}

	@Override
	public String format(Object value) {
		return CH.getOr(values, value, defaultValue);
	}

	@Override
	public void format(Object value, Writer out) throws IOException {
		out.write(format(value));
	}

	@Override
	public MappedFormatter clone() {
		return new MappedFormatter(values);
	}

	@Override
	public String get(Object key) {
		return format(key);
	}

	@Override
	public boolean canFormat(Object obj_) {
		return true;
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
	public String getPattern() {
		return null;
	}

}
