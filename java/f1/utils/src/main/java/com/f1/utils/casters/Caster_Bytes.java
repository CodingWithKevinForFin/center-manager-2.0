package com.f1.utils.casters;

import com.f1.base.Bytes;
import com.f1.utils.AbstractCaster;
import com.f1.utils.DetailedException;

public class Caster_Bytes extends AbstractCaster<Bytes> {

	public static final Caster_Bytes INSTANCE = new Caster_Bytes();

	public Caster_Bytes() {
		super(Bytes.class);
	}

	@Override
	protected Bytes castInner(Object o, boolean throwExceptionOnError) {
		if (o instanceof byte[]) {
			byte[] t = (byte[]) o;
			return new Bytes(t.clone());
		} else if (o instanceof String) {
			String t = (String) o;
			return new Bytes(t.getBytes());
		} else if (o instanceof CharSequence) {
			CharSequence t = (CharSequence) o;
			return new Bytes(t.toString().getBytes());
		} else if (throwExceptionOnError)
			throw new DetailedException("auto-cast failed").set("value", o).set("cast from class", o.getClass()).set("cast to class", getCastToClass());
		else
			return null;
	}

}
