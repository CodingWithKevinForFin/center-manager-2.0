/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.tester.json;

import java.util.regex.Pattern;
import com.f1.utils.CharReader;
import com.f1.utils.converter.json2.FromJsonConverterSession;
import com.f1.utils.converter.json2.ObjectToJsonConverter;

public class TestingToJsonConverter extends ObjectToJsonConverter {

	public TestingToJsonConverter() {
		super();
		registerConverter(new RegexToJsonConverter());
		registerConverter(new ExpressionToJsonConverter());
		setTreatValuedAsMaps(true);
	}

	@Override
	public Object stringToObject(FromJsonConverterSession session) {
		final CharReader stream = session.getStream();
		session.skipWhite();
		while (skipComments(stream) != null)
			session.skipWhite();
		char c = stream.peak();
		if (c == '/')
			return getConverter(Pattern.class).stringToObject(session);
		if (c == ';')
			return getConverter(TestingExpression.class).stringToObject(session);
		return super.stringToObject(session);
	}
}
