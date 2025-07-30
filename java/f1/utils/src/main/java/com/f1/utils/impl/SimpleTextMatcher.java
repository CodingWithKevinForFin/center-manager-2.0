package com.f1.utils.impl;

import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.TextMatcher;

public class SimpleTextMatcher implements TextMatcher {

	public static final TextMatcher EMPTY = new SimpleTextMatcher("", false);

	private final String text;

	private final boolean ignoreCase;

	public SimpleTextMatcher(String text, boolean ignoreCase) {

		this.text = text;

		this.ignoreCase = ignoreCase;

	}

	@Override

	public boolean matches(String input) {

		return ignoreCase ? text.equalsIgnoreCase(input) : text.equals(input);

	}

	@Override

	public boolean matches(CharSequence input) {

		return ignoreCase ? SH.equalsIgnoreCase(input, text) : SH.equals(text, input);

	}

	@Override

	public StringBuilder toString(StringBuilder sink) {

		if (!ignoreCase)

			sink.append("~");

		return sink.append(text);

	}

	@Override

	public String toString() {

		return ignoreCase ? text : ("~" + text);

	}

	public String getText() {
		return text;
	}
	@Override
	public boolean equals(Object obj) {
		if (obj == null || obj.getClass() != SimpleTextMatcher.class)
			return false;
		SimpleTextMatcher other = (SimpleTextMatcher) obj;
		return OH.eq(ignoreCase, other.ignoreCase) && OH.eq(text, other.text);
	}

	public boolean ignoreCase() {
		return this.ignoreCase;
	}

}
