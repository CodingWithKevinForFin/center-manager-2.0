/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.formatter;

import java.io.IOException;
import java.io.Writer;

import com.f1.utils.Formatter;

public abstract class AbstractFormatter implements Formatter, Cloneable {

	@Override
	abstract public void format(Object value, StringBuilder sb);

	@Override
	public String format(Object value) {
		StringBuilder sb = new StringBuilder();
		format(value, sb);
		return sb.toString();
	}

	@Override
	public void format(Object value, Writer out) throws IOException {
		StringBuilder sb = new StringBuilder();
		format(value, sb);
		out.write(sb.toString());
	}

	@Override
	public String get(Object key) {
		return format(key);
	}

	@Override
	abstract public Formatter clone();

	@Override
	public boolean canFormat(Object o) {
		return true;
	}

	@Override
	public String getPattern() {
		return null;
	}

}
