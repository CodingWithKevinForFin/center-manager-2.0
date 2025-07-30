package com.f1.utils.casters;

import com.f1.utils.AbstractCaster;
import com.f1.utils.SH;

public class Caster_String extends AbstractCaster<String> {

	public static final Caster_String INSTANCE = new Caster_String();

	public Caster_String() {
		super(String.class);
	}
	@Override
	protected String castInner(Object o, boolean throwExceptionOnError) {
		return SH.s(o);
	}

}
