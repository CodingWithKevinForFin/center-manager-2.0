package com.f1.utils.casters;

import com.f1.base.Caster;
import com.f1.utils.AbstractCaster;
import com.f1.utils.DetailedException;

public class Caster_Simple<T> extends AbstractCaster<T> {

	public static final Caster<Object> OBJECT = new Caster_Simple<Object>(Object.class);
	private Class<T> type;

	public Caster_Simple(Class<T> type) {
		super(type);
		this.type = type;
	}

	@Override
	protected T castInner(Object o, boolean throwExceptionOnError) {
		if (throwExceptionOnError)
			throw new DetailedException("auto-cast failed").set("value", o).set("cast from class", o.getClass()).set("cast to class", getCastToClass());
		return null;
	}

	@Override
	public String toString() {
		return "Caster:" + this.type.getName();
	}

}
