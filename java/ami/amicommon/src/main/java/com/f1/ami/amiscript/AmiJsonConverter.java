package com.f1.ami.amiscript;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.utils.converter.json2.FromJsonConverterSession;
import com.f1.utils.converter.json2.JsonConverter;
import com.f1.utils.converter.json2.StringToJsonConverter;
import com.f1.utils.converter.json2.ToJsonConverterSession;

public class AmiJsonConverter implements JsonConverter<Object> {

	private boolean locked;

	@Override
	public void lock() {
		this.locked = true;

	}

	@Override
	public boolean isLocked() {
		return locked;
	}

	@Override
	public boolean isCompatible(Class<?> type) {
		return true;
	}

	@Override
	public Class<Object> getType() {
		return Object.class;
	}

	@Override
	public void objectToString(Object o, ToJsonConverterSession session) {
		String s = AmiUtils.s(o);
		StringToJsonConverter.INSTANCE.objectToString(s, session);
	}

	@Override
	public Object stringToObject(FromJsonConverterSession session) {
		return StringToJsonConverter.INSTANCE.stringToObject(session);
	}

	@Override
	public boolean isLeaf() {
		return true;
	}

}
