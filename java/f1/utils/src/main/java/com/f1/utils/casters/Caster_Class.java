package com.f1.utils.casters;

import com.f1.utils.AbstractCaster;
import com.f1.utils.DetailedException;
import com.f1.utils.OH;

public class Caster_Class extends AbstractCaster<Class> {

	public static final Caster_Class INSTANCE = new Caster_Class();

	public Caster_Class() {
		super(Class.class);
	}
	@Override
	protected Class<?> castInner(Object o, boolean throwExceptionOnError) {
		try {
			return OH.forName(o.toString());
		} catch (ClassNotFoundException e) {
			if (throwExceptionOnError)
				throw new DetailedException("auto-cast failed").set("value", o).set("cast from class", o.getClass()).set("cast to class", getCastToClass());
			else
				return null;
		}
	}

}
