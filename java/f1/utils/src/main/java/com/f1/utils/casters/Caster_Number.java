package com.f1.utils.casters;

import com.f1.base.Caster;
import com.f1.base.ValuedEnum;
import com.f1.utils.AbstractCaster;
import com.f1.utils.DetailedException;
import com.f1.utils.OH;
import com.f1.utils.SH;

abstract public class Caster_Number<T extends Number> extends AbstractCaster<T> {

	private static final Integer ONE = 1, ZERO = 0;
	public static final Caster<Number> INSTANCE = new Caster_Number<Number>(Number.class) {

		@Override
		protected Number getPrimitiveValue(Number n) {
			return n;
		}

		@Override
		protected Number getParsedFromString(CharSequence cs, boolean throwExceptionOnError) {
			return OH.valueOf(SH.parseDoubleSafe(cs, throwExceptionOnError));
		}
	};

	public Caster_Number(Class<T> clazz) {
		super(clazz);
	}

	abstract protected T getPrimitiveValue(Number n);

	abstract protected T getParsedFromString(CharSequence cs, boolean throwExceptionOnError);

	@Override
	protected T castInner(Object o, boolean throwExceptionOnError) {
		Class<?> srcClass = o.getClass();
		if (o instanceof Number) {
			return getPrimitiveValue((Number) o);
		} else if (srcClass == String.class) {
			if ("null".equals(o))
				return null;
			else
				return getParsedFromString((String) o, throwExceptionOnError);
		} else if (CharSequence.class.isAssignableFrom(srcClass)) {
			if ("null".equals((CharSequence) o))
				return null;
			return getParsedFromString((CharSequence) o, throwExceptionOnError);
		} else if (srcClass.isEnum()) {
			if (o instanceof ValuedEnum)
				return cast(((ValuedEnum<?>) o).getEnumValue());
			return cast(((Enum<?>) o).toString());
		} else if (o instanceof Character)
			return getPrimitiveValue((int) (Character) o);
		else if (o instanceof Boolean)
			return getPrimitiveValue(((Boolean) o).booleanValue() ? ONE : ZERO);
		if (throwExceptionOnError)
			throw new DetailedException("auto-cast failed").set("value", o).set("cast from class", srcClass).set("cast to class", getCastToClass());
		else
			return null;
	}

}
