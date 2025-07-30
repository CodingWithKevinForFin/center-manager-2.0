package com.f1.utils.converter.json2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.f1.utils.CharReader;

public class ListToJsonConverter extends AbstractJsonConverter<Collection> {

	private static final int[] COMMA_OR_CLOSEBRACKET = new int[] { ',', ']' };

	public ListToJsonConverter() {
		super(Collection.class);
	}

	@Override
	public void objectToString(Collection list, ToJsonConverterSession session) {
		ObjectToJsonConverter converter = session.getConverter();
		StringBuilder out = session.getStream();
		out.append('[');
		if (converter.getCompactMode() == ObjectToJsonConverter.MODE_SEMI && !converter.hasComplex(list)) {
			boolean first = true;
			StringBuilder stream = session.getStream();
			int startPos = stream.length();
			boolean tooLong = false;
			for (Object o : list) {
				if (first == true)
					first = false;
				else
					out.append(',');
				converter.objectToString(o, session);
				if (list.size() > 1 && stream.length() - startPos > converter.getSemiCompactMaxLineLength()) {
					tooLong = true;
					break;
				}
			}
			if (tooLong)
				stream.setLength(startPos);
			else {
				out.append(']');
				return;
			}
		}
		session.pushDepth();
		boolean first = true;
		for (Object o : list) {
			if (first == true)
				first = false;
			else
				out.append(',');
			session.appendNewLine();
			session.appendPrefix();
			converter.objectToString(o, session);
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
