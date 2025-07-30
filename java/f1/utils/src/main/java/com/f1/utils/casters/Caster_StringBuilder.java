package com.f1.utils.casters;

import com.f1.utils.AbstractCaster;

public class Caster_StringBuilder extends AbstractCaster<StringBuilder> {

	public static final Caster_StringBuilder INSTANCE = new Caster_StringBuilder();

	public Caster_StringBuilder() {
		super(StringBuilder.class);
	}

	@Override
	protected StringBuilder castInner(Object o, boolean throwExceptionOnError) {
		return new StringBuilder(o.toString());
	}

}
