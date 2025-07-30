package com.f1.utils.converter.json2;

public class NullToJsonConverter implements JsonConverter<Object> {

	public static final JsonConverter INSTANCE = new NullToJsonConverter();

	@Override
	public boolean isCompatible(Class<?> type_) {
		return true;
	}

	@Override
	public Class<Object> getType() {
		return Object.class;
	}

	@Override
	public void objectToString(Object o_, ToJsonConverterSession session_) {
		return;
	}

	@Override
	public Object stringToObject(FromJsonConverterSession session_) {
		return null;
	}

	@Override
	public void lock() {

	}

	@Override
	public boolean isLocked() {
		return true;
	}

	@Override
	public boolean isLeaf() {
		return true;
	}

}
