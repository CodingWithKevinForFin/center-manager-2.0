package com.f1.bootstrap.appmonitor;

import com.f1.utils.SH;
import com.f1.utils.converter.json2.AbstractJsonConverter;
import com.f1.utils.converter.json2.FromJsonConverterSession;
import com.f1.utils.converter.json2.ToJsonConverterSession;

public class ObjToJsonConverter extends AbstractJsonConverter<Object> {

	public ObjToJsonConverter() {
		super(Object.class);
	}

	@Override
	public Object stringToObject(FromJsonConverterSession session) {
		return null;
	}

	@Override
	public void objectToString(Object o, ToJsonConverterSession session) {
		StringBuilder out = session.getStream();
		if (o == null) {
			out.append("null");
		} else {
			out.append(SH.CHAR_QUOTE);
			SH.toStringEncode(o.toString(), SH.CHAR_QUOTE, out);
			out.append(SH.CHAR_QUOTE);
		}
	}

}
