package com.f1.utils.casters;

import com.f1.base.DateMillis;
import com.f1.base.ValuedEnum;
import com.f1.utils.AbstractCaster;
import com.f1.utils.DetailedException;
import com.f1.utils.SH;

public class Caster_DateMillis extends AbstractCaster<DateMillis> {

	public static final Caster_DateMillis INSTANCE = new Caster_DateMillis();

	public Caster_DateMillis() {
		super(DateMillis.class);
	}

	@Override
	protected DateMillis castInner(Object o, boolean throwExceptionOnError) {
		Class<?> srcClass = o.getClass();
		if (o instanceof Number) {
			return new DateMillis((Number) o);
		} else if (o instanceof java.util.Date) {
			return new DateMillis(((java.util.Date) o).getTime());
		} else if (o instanceof CharSequence) {
			if (SH.evaluateNumber((CharSequence) o) != SH.NUMBER_EVALUATED_TO_STRING)
				return new DateMillis(SH.parseLongSafe((CharSequence) o, true));
		} else {
			if (srcClass.isEnum()) {
				if (o instanceof ValuedEnum)
					return cast(((ValuedEnum<?>) o).getEnumValue());
				return cast(((Enum<?>) o).toString());
			}
		}
		if (throwExceptionOnError)
			throw new DetailedException("auto-cast failed").set("value", o).set("cast from class", srcClass).set("cast to class", getCastToClass());
		else
			return null;
	}

}
