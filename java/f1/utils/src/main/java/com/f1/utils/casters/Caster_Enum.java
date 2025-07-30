package com.f1.utils.casters;

import com.f1.utils.AbstractCaster;
import com.f1.utils.OH;

public class Caster_Enum extends AbstractCaster<Enum> {

	final private Class<Enum> castToClass;

	public Caster_Enum(Class<Enum> castToClass) {
		super(castToClass);
		this.castToClass = castToClass;
	}

	@Override
	protected Enum castInner(Object o, boolean throwExceptionOnError) {
		String s = o.toString();
		if (s.length() > 0 && OH.isBetween(s.charAt(0), '0', '9')) {
			return sun.misc.SharedSecrets.getJavaLangAccess().getEnumConstantsShared(this.castToClass)[Integer.parseInt(s)];
		}
		return OH.valueOfEnum(this.castToClass, o.toString(), throwExceptionOnError);
	}
}
