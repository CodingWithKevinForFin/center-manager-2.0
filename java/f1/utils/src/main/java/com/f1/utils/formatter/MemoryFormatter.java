package com.f1.utils.formatter;

import com.f1.utils.Formatter;
import com.f1.utils.SH;

public class MemoryFormatter extends AbstractFormatter {

	@Override
	public boolean canParse(String text) {
		return false;
	}

	@Override
	public Object parse(String text) {
		return null;
	}

	@Override
	public void format(Object value, StringBuilder sb) {
		sb.append(SH.formatMemory(((Number) value).longValue()));
	}

	@Override
	public Formatter clone() {
		return this;
	}

}
