package com.f1.ami.plugins.apache.avro;

import org.apache.avro.util.Utf8;

import com.f1.utils.CharReader;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.converter.json2.AbstractJsonConverter;
import com.f1.utils.converter.json2.FromJsonConverterSession;
import com.f1.utils.converter.json2.ToJsonConverterSession;

public class AmiAvroUtf8ToJsonConverter extends AbstractJsonConverter<Utf8> {

	public static final AmiAvroUtf8ToJsonConverter INSTANCE = new AmiAvroUtf8ToJsonConverter();
	private static final int[] QUOTES = new int[] { '\'', '\"' };

	public AmiAvroUtf8ToJsonConverter() {
		super(Utf8.class);
	}

	@Override
	public Object stringToObject(FromJsonConverterSession session) {
		CharReader stream = session.getStream();
		StringBuilder temp = session.getTempStringBuilder();
		char c = (char) stream.expectAny(QUOTES);
		stream.readUntil(c, '\\', temp);
		stream.readChar();
		return SH.toStringDecode(temp.toString());
	}

	@Override
	public void objectToString(Utf8 o, ToJsonConverterSession session) {
		StringBuilder out = session.getStream();
		out.append(SH.CHAR_QUOTE);
		SH.toStringEncode(Caster_String.INSTANCE.cast(o), SH.CHAR_QUOTE, out);
		out.append(SH.CHAR_QUOTE);
	}

}
