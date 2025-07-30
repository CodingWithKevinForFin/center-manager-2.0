package com.f1.utils.casters;

import com.f1.base.Password;
import com.f1.utils.AbstractCaster;

public class Caster_Password extends AbstractCaster<Password> {

	public static final Caster_Password INSTANCE = new Caster_Password();

	public Caster_Password() {
		super(Password.class);
	}

	@Override
	protected Password castInner(Object o, boolean throwExceptionOnError) {
		Class<?> srcClass = o.getClass();
		if (o instanceof CharSequence) {
			return new Password((CharSequence) o);
		} else
			return null;
	}

}
