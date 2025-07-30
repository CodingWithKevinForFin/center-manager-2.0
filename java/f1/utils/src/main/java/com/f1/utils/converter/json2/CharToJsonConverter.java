package com.f1.utils.converter.json2;

import com.f1.utils.CharReader;

public class CharToJsonConverter extends AbstractJsonConverter<Character> {

	public static final CharToJsonConverter INSTANCE = new CharToJsonConverter();

	public CharToJsonConverter() {
		super(Character.class);
	}

	@Override
	public void objectToString(Character c, ToJsonConverterSession session) {
		StringBuilder out = session.getStream();
		out.append('"');
		switch (c) {
			case '"':
				out.append("\\\"");
				break;
			case '\\':
				out.append("\\\\");
				break;
			default:
				out.append(c);
		}
		out.append('"');
	}

	@Override
	public Object stringToObject(FromJsonConverterSession session) {
		CharReader stream = session.getStream();
		StringBuilder temp = session.getTempStringBuilder();
		stream.expect('"');
		stream.readUntil('"', '\'', temp);
		stream.readChar();
		return temp.toString();
	}
}
