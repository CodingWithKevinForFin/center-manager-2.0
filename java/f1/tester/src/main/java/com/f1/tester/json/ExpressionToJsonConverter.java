/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.tester.json;

import com.f1.utils.CharReader;
import com.f1.utils.converter.json2.FromJsonConverterSession;
import com.f1.utils.converter.json2.JsonConverter;
import com.f1.utils.converter.json2.ToJsonConverterSession;
import com.f1.utils.impl.StringCharReader;
import com.f1.utils.string.JavaExpressionParser;
import com.f1.utils.string.Node;

public class ExpressionToJsonConverter implements JsonConverter<TestingExpression> {

	@Override
	public String getBasicType() {
		return "Expression";
	}

	@Override
	public boolean isCompatible(Class<?> type) {
		return TestingExpression.class.isAssignableFrom(type);
	}

	@Override
	public Class<TestingExpression> getType() {
		return TestingExpression.class;
	}

	@Override
	public void objectToString(TestingExpression o, ToJsonConverterSession session) {
		session.getStream().append(o.toString());
	}

	@Override
	public Object stringToObject(FromJsonConverterSession session) {
		CharReader stream = session.getStream();
		stream.expect(';');
		int start = stream.getCountRead();
		Node expression = new JavaExpressionParser().parseNode(session.getStream());
		int end = stream.getCountRead();
		StringCharReader s = (StringCharReader) stream;
		return new TestingExpression(expression, new String(s.getInner(start, end)));
	}

}
