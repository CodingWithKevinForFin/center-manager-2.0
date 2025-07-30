package com.f1.utils.casters;

import java.math.BigDecimal;
import java.math.BigInteger;

import com.f1.base.Complex;
import com.f1.base.ValuedEnum;
import com.f1.utils.AbstractCaster;
import com.f1.utils.DetailedException;
import com.f1.utils.SH;

public class Caster_BigDecimal extends AbstractCaster<BigDecimal> {

	public static final Caster_BigDecimal INSTANCE = new Caster_BigDecimal();

	public Caster_BigDecimal() {
		super(BigDecimal.class);
	}

	@Override
	public BigDecimal castInner(Object o, boolean throwExceptionOnError) {
		Class<?> srcClass = o.getClass();
		if (o instanceof Number) {
			if (o instanceof Long || o instanceof Integer || o instanceof Short || o instanceof Byte)
				return BigDecimal.valueOf(((Number) o).longValue());
			if (o instanceof Double || o instanceof Float || o instanceof Complex)
				return BigDecimal.valueOf(((Number) o).doubleValue());
			if (o instanceof BigInteger)
				return new BigDecimal((BigInteger) o);
			return new BigDecimal(o.toString());
		} else if (srcClass == String.class) {
			if ("null".equals(o))
				return null;
			return new BigDecimal((String) o);
		} else if (CharSequence.class.isAssignableFrom(srcClass)) {
			if (SH.equals("null", (CharSequence) o))
				return null;
			return new BigDecimal(o.toString());
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
