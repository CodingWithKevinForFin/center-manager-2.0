package com.f1.utils.casters;

import com.f1.base.ValuedEnum;
import com.f1.utils.AbstractCaster;
import com.f1.utils.DetailedException;
import com.f1.utils.SH;

public class Caster_Boolean extends AbstractCaster<Boolean> {

	public static final Caster_Boolean INSTANCE = new Caster_Boolean(false);
	public static final Caster_Boolean PRIMITIVE = new Caster_Boolean(true);

	final private boolean primitive;

	public Caster_Boolean(boolean primitive) {
		super(primitive ? boolean.class : Boolean.class);
		this.primitive = primitive;
	}

	@Override
	protected Boolean castInner(Object o, boolean throwExceptionOnError) {
		if (this.primitive && o instanceof Number) {
			return Boolean.valueOf(((Number) o).intValue() != 0);
		}
		Class<?> srcClass = o.getClass();
		if (this.primitive && srcClass.isEnum()) {
			if (o instanceof ValuedEnum)
				return cast(((ValuedEnum<?>) o).getEnumValue());
			return cast(((Enum<?>) o).ordinal());
		}
		if (o instanceof Number)
			return ((Number) o).longValue() != 0;
		String s = SH.s(o);
		if (s.equals("true"))
			return Boolean.TRUE;
		else if (s.equals("false"))
			return Boolean.FALSE;
		if (throwExceptionOnError)
			throw new DetailedException("auto-cast failed").set("value", o).set("cast from class", srcClass).set("cast to class", getCastToClass());
		else
			return null;
	}

}
