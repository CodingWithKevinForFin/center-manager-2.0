package com.f1.utils.impl;

import com.f1.utils.OH;
import com.f1.utils.TextMatcher;

public class ConstTextMatcher implements TextMatcher {
	public static final ConstTextMatcher TRUE = new ConstTextMatcher(true);
	public static final ConstTextMatcher FALSE = new ConstTextMatcher(false);
	final private boolean returnValue;
	final private String text;

	private ConstTextMatcher(boolean returnValue) {
		this.returnValue = returnValue;
		this.text = returnValue ? "*" : "<never-match>";
	}

	@Override
	public boolean matches(CharSequence input) {
		return returnValue;
	}

	@Override
	public boolean matches(String input) {
		return returnValue;
	}

	@Override
	public String toString() {
		return text;
	}

	@Override
	public StringBuilder toString(StringBuilder sink) {
		return sink.append(text);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || obj.getClass() != ConstTextMatcher.class)
			return false;
		ConstTextMatcher other = (ConstTextMatcher) obj;
		return OH.eq(returnValue, other.returnValue) && OH.eq(text, other.text);
	}

}