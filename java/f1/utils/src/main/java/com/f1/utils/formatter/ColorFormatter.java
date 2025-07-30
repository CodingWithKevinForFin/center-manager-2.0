package com.f1.utils.formatter;

import java.awt.Color;

import com.f1.utils.ColorHelper;
import com.f1.utils.Formatter;

public class ColorFormatter extends AbstractFormatter {

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
		Color color = (Color) value;
		ColorHelper.toString(color, sb);
	}

	@Override
	public Formatter clone() {
		return this;
	}

}
