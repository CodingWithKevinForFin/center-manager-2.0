package com.f1.utils.casters;

import com.f1.base.DateNanos;
import com.f1.base.DayTime;
import com.f1.base.ValuedEnum;
import com.f1.utils.AbstractCaster;
import com.f1.utils.DetailedException;
import com.f1.utils.SH;

public class Caster_DateNanos extends AbstractCaster<DateNanos> {

	public static final Caster_DateNanos INSTANCE = new Caster_DateNanos();

	public Caster_DateNanos() {
		super(DateNanos.class);
	}

	@Override
	protected DateNanos castInner(Object o, boolean throwExceptionOnError) {
		Class<?> srcClass = o.getClass();
		if (o instanceof Number) {
			return new DateNanos((Number) o);
		} else if (o instanceof java.util.Date) {
			if (o instanceof java.sql.Timestamp)
				return new DateNanos((java.sql.Timestamp) o);
			return new DateNanos(((java.util.Date) o).getTime() * DayTime.NANOS_PER_MIL);
		} else if (o instanceof CharSequence) {
			if (SH.evaluateNumber((CharSequence) o) != SH.NUMBER_EVALUATED_TO_STRING)
				return new DateNanos(SH.parseLongSafe((CharSequence) o, true));
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
