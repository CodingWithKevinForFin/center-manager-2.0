package com.f1.utils;

import com.f1.base.ToStringable;

public interface TextMatcher extends Matcher<String>, ToStringable {

	public boolean matches(CharSequence input);

	@Override
	public boolean matches(String input);

}
