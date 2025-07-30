package com.f1.utils.casters;

import com.f1.base.UUID;
import com.f1.utils.AbstractCaster;
import com.f1.utils.DetailedException;

public class Caster_UUID extends AbstractCaster<UUID> {

	public static final Caster_UUID INSTANCE = new Caster_UUID();

	public Caster_UUID() {
		super(UUID.class);
	}

	@Override
	protected UUID castInner(Object o, boolean throwExceptionOnError) {
		Class<?> srcClass = o.getClass();
		if (o instanceof String)
			return new UUID((String) o);
		if (throwExceptionOnError)
			throw new DetailedException("auto-cast failed").set("value", o).set("cast from class", srcClass).set("cast to class", getCastToClass());
		else
			return null;
	}

}
