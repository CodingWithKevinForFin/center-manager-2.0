package com.f1.utils.casters;

import java.sql.Timestamp;

import com.f1.base.DateMillis;
import com.f1.base.DateNanos;
import com.f1.utils.AbstractCaster;
import com.f1.utils.DetailedException;

public class Caster_Timestamp extends AbstractCaster<Timestamp> {

	public static final Caster_Timestamp INSTANCE = new Caster_Timestamp();

	public Caster_Timestamp() {
		super(Timestamp.class);
	}
	@Override
	protected Timestamp castInner(Object o, boolean throwExceptionOnError) {
		Class<?> srcClass = o.getClass();
		if (o instanceof DateNanos) {
			DateNanos dn = (DateNanos) o;
			Timestamp r = new Timestamp(dn.getTimeMillis());
			r.setNanos((int) (dn.getNanos() % 1000000000L));
			return r;
		} else if (o instanceof DateMillis)
			return new Timestamp(((DateMillis) o).getDate());
		else if (o instanceof Number)
			return new Timestamp(((Number) o).longValue());
		else if (throwExceptionOnError)
			throw new DetailedException("auto-cast failed").set("value", o).set("cast from class", srcClass).set("cast to class", getCastToClass());
		else
			return null;
	}

}
