package com.f1.utils.casters;

import java.math.BigDecimal;
import java.math.BigInteger;

import com.f1.base.Complex;
import com.f1.base.ValuedEnum;
import com.f1.utils.AbstractCaster;
import com.f1.utils.DetailedException;
import com.f1.utils.SH;

public class Caster_BigInteger extends AbstractCaster<BigInteger> {

	public static final Caster_BigInteger INSTANCE = new Caster_BigInteger();

	public Caster_BigInteger() {
		super(BigInteger.class);
	}
	@Override
	public BigInteger castInner(Object o, boolean throwExceptionOnError) {
		Class<?> srcClass = o.getClass();
		if (o instanceof Number) {
			if (o instanceof Long || o instanceof Integer || o instanceof Double || o instanceof Float || o instanceof Short || o instanceof Byte || o instanceof Complex)
				return BigInteger.valueOf(((Number) o).longValue());
			if (o instanceof BigDecimal)
				return ((BigDecimal) o).toBigInteger();
			return new BigInteger(SH.beforeFirst(o.toString(), "."));
		} else if (srcClass == String.class) {
			if ("null".equals(o))
				return null;
			return new BigInteger(SH.beforeFirst((String) o, '.'));
		} else if (CharSequence.class.isAssignableFrom(srcClass)) {
			if (SH.equals("null", (CharSequence) o))
				return null;
			return new BigInteger(SH.beforeFirst(o.toString(), '.'));
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
