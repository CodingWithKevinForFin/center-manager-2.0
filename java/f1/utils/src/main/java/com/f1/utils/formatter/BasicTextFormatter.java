/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.formatter;

import java.io.IOException;
import java.io.Writer;

import com.f1.utils.SH;

public class BasicTextFormatter extends AbstractFormatter {

	@Override
	public void format(Object value, StringBuilder sb) {
		SH.s(value, sb);
	}

	@Override
	public BasicTextFormatter clone() {
		return new BasicTextFormatter();
	}

	@Override
	public String format(Object value) {
		return SH.s(value);
	}

	@Override
	public void format(Object value, Writer out) throws IOException {
		out.write(SH.s(value));
	}

	@Override
	public boolean canParse(String text) {
		return false;
	}

	@Override
	public Object parse(String text) {
		throw new UnsupportedOperationException();
	}
}
