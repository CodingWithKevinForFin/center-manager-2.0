package com.f1.utils.casters;

import com.f1.base.ValuedEnum;
import com.f1.base.ValuedEnumCache;
import com.f1.utils.AbstractCaster;
import com.f1.utils.OH;

public class Caster_ValuedEnum extends AbstractCaster<ValuedEnum> {

	final private Class<ValuedEnum> castToClass;

	public Caster_ValuedEnum(Class<ValuedEnum> castToClass) {
		super(castToClass);
		this.castToClass = castToClass;
	}

	@Override
	protected ValuedEnum castInner(Object o, boolean throwExceptionOnError) {
		Class<?> type = ValuedEnumCache.getEnumValueType(this.castToClass);
		return ValuedEnumCache.getEnumValue(this.castToClass, OH.getCaster(type).cast(o));
	}
}
