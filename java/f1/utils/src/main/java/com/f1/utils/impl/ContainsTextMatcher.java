package com.f1.utils.impl;

import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.TextMatcher;

public class ContainsTextMatcher implements TextMatcher {

	final private String expression;
	final private boolean ignoreCase;

	public ContainsTextMatcher(String expression, boolean ignoreCase) {
		if (expression == null)
			throw new NullPointerException();
		this.ignoreCase = ignoreCase;
		this.expression = expression;
	}

	@Override
	public boolean matches(CharSequence input) {
		if (ignoreCase)
			return SH.indexOfIgnoreCase(input, expression, 0) != -1;
		return SH.indexOf(input, expression, 0) != -1;
	}
	@Override
	public boolean matches(String input) {
		if (ignoreCase)
			return SH.indexOfIgnoreCase(input, expression, 0) != -1;
		return input.indexOf(expression) != -1;
	}

	@Override
	public String toString() {
		return expression;
	}

	@Override
	public StringBuilder toString(StringBuilder sink) {
		return sink.append(expression);
	}
	@Override
	public boolean equals(Object obj) {
		if (obj == null || obj.getClass() != ContainsTextMatcher.class)
			return false;
		ContainsTextMatcher other = (ContainsTextMatcher) obj;
		return OH.eq(ignoreCase, other.ignoreCase) && OH.eq(expression, other.expression);
	}

}
