package com.f1.ami.amicommon;

import com.f1.utils.converter.json2.BasicToJsonConverterSession;
import com.f1.utils.converter.json2.JsonConverter;
import com.f1.utils.converter.json2.JsonableToJsonConverter;
import com.f1.utils.converter.json2.ObjectToJsonConverter;
import com.f1.utils.converter.json2.ToJsonConverterSession;

public class AmiObjectToJsonConverter extends ObjectToJsonConverter {
	public static final ObjectToJsonConverter INSTANCE_AMI = new AmiObjectToJsonConverter();
	static {
		INSTANCE_AMI.setCompactMode(MODE_COMPACT);
		INSTANCE_AMI.setSortMaps(false);
		INSTANCE_AMI.setValuedTypeKeyName(null);
	}

	public AmiObjectToJsonConverter() {
		super();
		registerConverter(new JsonableToJsonConverter());
	}

	@Override
	public String objectToString(Object o) {
		lock();
		if (o == null)
			return "null";
		else {
			Class type = o.getClass();
			JsonConverter conv = getConverterNoThrow(type);
			if (conv != null) {
				StringBuilder sb = new StringBuilder();
				ToJsonConverterSession out = new BasicToJsonConverterSession(this, sb);
				objectToString(o, out);
				return sb.toString();
			} else {
				return o.toString();
			}
		}

	}
	@Override
	public void objectToString(Object o, ToJsonConverterSession out) {
		lock();
		if (o == null) {
			out.getStream().append("null");
			return;
		} else {
			Class type = o.getClass();
			JsonConverter converter = getConverterNoThrow(type);
			if (converter != null) {
				converter.objectToString(o, out);
			} else {
				out.getStream().append(o.toString());
			}
		}
	}
}
