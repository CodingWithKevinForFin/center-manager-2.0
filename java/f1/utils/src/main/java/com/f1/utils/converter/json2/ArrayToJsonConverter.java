package com.f1.utils.converter.json2;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import com.f1.utils.CharReader;

public class ArrayToJsonConverter extends AbstractJsonConverter<Object> {

	private static final int[] COMMA_OR_CLOSEBRACKET = new int[] { ',', ']' };

	public ArrayToJsonConverter() {
		super(Object.class);
	}

	@Override
	public boolean isCompatible(Class<?> o) {
		return o.isArray();
	}
	@Override
	public Class getType() {
		return Object[].class;
	}

	@Override
	public void objectToString(Object list, ToJsonConverterSession session) {
		StringBuilder out = session.getStream();
		out.append('[');
		session.pushDepth();
		int size = Array.getLength(list);
		for (int i = 0; i < size; i++) {
			if (i > 0)
				out.append(',');
			session.appendNewLine();
			session.appendPrefix();
			session.getConverter().objectToString(Array.get(list, i), session);
		}
		session.popDepth();
		session.appendNewLine();
		session.appendPrefix();
		out.append(']');
	}

	@Override
	public Object stringToObject(FromJsonConverterSession session) {
		CharReader stream = session.getStream();
		List l = new ArrayList<Object>();
		stream.expect('[');
		boolean first = true;
		for (;;) {
			session.skipWhite();
			if (first) {
				first = false;
				if (stream.peak() == ']') {
					stream.readChar();
					break;
				}
			} else if (stream.expectAny(COMMA_OR_CLOSEBRACKET) == ']')
				break;
			l.add(session.getConverter().stringToObject(session));
		}
		return l;
	}

	@Override
	public boolean isLeaf() {
		return false;
	}
}
