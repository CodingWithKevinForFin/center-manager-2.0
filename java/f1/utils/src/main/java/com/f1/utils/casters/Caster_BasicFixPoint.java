package com.f1.utils.casters;

import com.f1.base.ValuedEnum;
import com.f1.utils.AbstractCaster;
import com.f1.utils.BasicFixPoint;
import com.f1.utils.DetailedException;
import com.f1.utils.SH;

public class Caster_BasicFixPoint extends AbstractCaster<BasicFixPoint> {
	public static final Caster_BasicFixPoint INSTANCE = new Caster_BasicFixPoint();

	public Caster_BasicFixPoint() {
		super(BasicFixPoint.class);
	}

	@Override
	protected BasicFixPoint castInner(Object o, boolean throwExceptionOnError) {
		Class<?> srcClass = o.getClass();
		if (o instanceof Number) {
			if (o instanceof Double || o instanceof Float)
				return (BasicFixPoint) BasicFixPoint.nuw(4, ((Number) o).doubleValue());
			else
				return (BasicFixPoint) BasicFixPoint.nuw(4, ((Number) o).longValue());
		} else if (srcClass == String.class) {
			if ("null".equals(o))
				return null;
			return (BasicFixPoint) BasicFixPoint.nuw((String) o);
		} else if (CharSequence.class.isAssignableFrom(srcClass)) {
			if (SH.equals("null", (CharSequence) o))
				return null;
			return (BasicFixPoint) BasicFixPoint.nuw(o.toString());
		} else if (srcClass.isEnum()) {
			if (o instanceof ValuedEnum)
				return cast(((ValuedEnum<?>) o).getEnumValue());
			return cast(((Enum<?>) o).toString());
		}
		if (throwExceptionOnError)
			throw new DetailedException("auto-cast failed").set("value", o).set("cast from class", srcClass).set("cast to class", getCastToClass());
		else
			return null;
	}

}
