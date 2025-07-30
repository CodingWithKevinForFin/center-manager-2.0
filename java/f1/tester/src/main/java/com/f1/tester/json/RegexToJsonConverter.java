package com.f1.tester.json;

import java.util.regex.Pattern;
import com.f1.base.BasicTypesString;
import com.f1.utils.CharReader;
import com.f1.utils.MH;
import com.f1.utils.converter.json2.FromJsonConverterSession;
import com.f1.utils.converter.json2.JsonConverter;
import com.f1.utils.converter.json2.ToJsonConverterSession;

public class RegexToJsonConverter implements JsonConverter<Pattern> {

	@Override
	public String getBasicType() {
		return BasicTypesString.REGEX;
	}

	@Override
	public boolean isCompatible(Class<?> type) {
		return Pattern.class.isAssignableFrom(type);
	}

	@Override
	public Class<Pattern> getType() {
		return Pattern.class;
	}

	@Override
	public void objectToString(Pattern o, ToJsonConverterSession session) {
		StringBuilder stream = session.getStream();
		stream.append('/').append(o.pattern()).append('/');
		int flags = o.flags();
		if (flags != 0) {
			if (MH.areAnyBitsSet(flags, Pattern.CANON_EQ))
				stream.append('q');
			if (MH.areAnyBitsSet(flags, Pattern.CASE_INSENSITIVE))
				stream.append('i');
			if (MH.areAnyBitsSet(flags, Pattern.COMMENTS))
				stream.append('c');
			if (MH.areAnyBitsSet(flags, Pattern.DOTALL))
				stream.append('d');
			if (MH.areAnyBitsSet(flags, Pattern.LITERAL))
				stream.append('l');
			if (MH.areAnyBitsSet(flags, Pattern.MULTILINE))
				stream.append('m');
			if (MH.areAnyBitsSet(flags, Pattern.UNICODE_CASE))
				stream.append('u');
			if (MH.areAnyBitsSet(flags, Pattern.UNIX_LINES))
				stream.append('x');
		}
	}

	@Override
	public Object stringToObject(FromJsonConverterSession session) {
		CharReader stream = session.getStream();
		stream.expect('/');
		StringBuilder sink = new StringBuilder();
		stream.readUntil('/', '\\', sink);
		stream.expect('/');
		session.skipWhite();
		int flags = 0;
		outer : for (;;) {
			int c = stream.peakOrEof();
			switch (c) {
				case 'q' :
					flags |= Pattern.CANON_EQ;
					break;
				case 'i' :
					flags |= Pattern.CASE_INSENSITIVE;
					break;
				case 'c' :
					flags |= Pattern.COMMENTS;
					break;
				case 'd' :
					flags |= Pattern.DOTALL;
					break;
				case 'l' :
					flags |= Pattern.LITERAL;
					break;
				case 'm' :
					flags |= Pattern.MULTILINE;
					break;
				case 'u' :
					flags |= Pattern.UNICODE_CASE;
					break;
				case 'x' :
					flags |= Pattern.UNIX_LINES;
					break;
				default :
					break outer;
			}
			stream.readChar();
		}
		return Pattern.compile(sink.toString(), flags);
	}
}
