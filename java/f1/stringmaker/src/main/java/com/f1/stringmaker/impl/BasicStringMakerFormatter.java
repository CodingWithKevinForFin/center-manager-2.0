package com.f1.stringmaker.impl;

import com.f1.stringmaker.StringMakerFormatter;
import com.f1.stringmaker.StringMakerSession;

public class BasicStringMakerFormatter implements StringMakerFormatter {

	public static final StringMakerFormatter INSTANCE = new BasicStringMakerFormatter();

	private BasicStringMakerFormatter() {
	}

	@Override
	public void append(Object value, String format, String args, StringMakerSession session) {
		if (value != null)
			session.getSink().append(value);
	}

}
