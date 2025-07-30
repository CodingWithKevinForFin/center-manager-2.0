package com.f1.utils.converter.json2;

import com.f1.base.ValuedEnum;

public class ValuedEnumToJsonConverter extends AbstractJsonConverter<ValuedEnum> {

	public static final ValuedEnumToJsonConverter INSTANCE = new ValuedEnumToJsonConverter();

	public ValuedEnumToJsonConverter() {
		super(ValuedEnum.class);
	}

	@Override
	public void objectToString(ValuedEnum o, ToJsonConverterSession session) {
		session.getConverter().objectToString(o.getEnumValue(), session);
	}

	@Override
	public Object stringToObject(FromJsonConverterSession session) {
		throw new IllegalStateException("not supported");
	}
}
