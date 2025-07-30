package com.f1.utils.converter.json2;

import com.f1.utils.SH;

public class JsonableToJsonConverter extends AbstractJsonConverter<Jsonable> {

	public JsonableToJsonConverter() {
		super(Jsonable.class);
	}

	@Override
	public Object stringToObject(FromJsonConverterSession session) {
		throw new IllegalStateException("not supported");
	}

	@Override
	public void objectToString(Jsonable o, ToJsonConverterSession out) {
		StringBuilder stream = out.getStream();
		stream.append(SH.CHAR_QUOTE);
		SH.toStringEncode(o.objectToJson(), SH.CHAR_QUOTE, stream);
		stream.append(SH.CHAR_QUOTE);
	}

}
