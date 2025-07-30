package com.f1.utils.impl;

import com.f1.base.Valued;
import com.f1.base.ValuedParam;
import com.f1.base.ValuedSchema;
import com.f1.utils.Hasher;
import com.f1.utils.OH;

public class ValuedHasher implements Hasher<Valued> {

	public static final ValuedHasher INSTANCE = new ValuedHasher();

	private ValuedHasher() {

	}

	@Override
	public int hashcode(Valued v) {
		int r = 0;
		for (ValuedParam<Valued> vp : v.askSchema().askValuedParams())
			r = r * 31 + OH.hashCode(vp.getValue(v));
		return r;
	}

	@Override
	public boolean areEqual(Valued vl, Valued vr) {
		if (vl == vr)
			return true;
		else if (vl == null || vr == null)
			return false;
		ValuedSchema<Valued> schema = vl.askSchema();
		if (schema.askOriginalType() != vr.askSchema().askOriginalType())
			return false;
		for (ValuedParam<Valued> vp : vl.askSchema().askValuedParams()) {
			if (OH.ne(vp.getValue(vl), vp.getValue(vr)))
				return false;
		}
		return true;
	}

}
