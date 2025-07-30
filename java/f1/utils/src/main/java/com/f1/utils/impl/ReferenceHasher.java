package com.f1.utils.impl;

import java.lang.ref.Reference;

import com.f1.utils.Hasher;
import com.f1.utils.OH;

public class ReferenceHasher<T> implements Hasher<Reference<T>> {

	@Override
	public int hashcode(Reference<T> o) {
		return OH.hashCode(o.get());
	}

	@Override
	public boolean areEqual(Reference<T> l, Reference<T> r) {
		return OH.eq(l.get(), r.get());
	}

}
