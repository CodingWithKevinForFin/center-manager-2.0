package com.f1.utils;

import com.f1.utils.formatter.AbstractFormatter;

public class ConstFormatter extends AbstractFormatter {

	final private String constValue;

	public ConstFormatter(String constValue) {
		this.constValue = constValue;
	}
	@Override
	public String format(Object key) {
		return constValue;
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
	public void format(Object value, StringBuilder sb) {
		sb.append(constValue);
	}

	@Override
	public Formatter clone() {
		return this;
	}

}
