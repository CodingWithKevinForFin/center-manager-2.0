package com.f1.utils.casters;

import com.f1.utils.AbstractCaster;

public class Caster_Object extends AbstractCaster<Object> {

	public static final Caster_Object INSTANCE = new Caster_Object();

	public Caster_Object() {
		super(Object.class);
	}

	@Override
	protected Object castInner(Object o, boolean throwExceptionOnError) {
		return o;
	}

}
