package com.f1.ami.web;

import java.util.Map;

import com.f1.utils.Hasher;
import com.f1.utils.OH;

public class CertainKeysHasher<T extends Map> implements Hasher<T> {

	private Object[] keys;

	public CertainKeysHasher(Object... keys) {
		this.keys = keys;
	}
	@Override
	public int hashcode(Map o) {
		int r = 0;
		for (Object key : keys)
			r = OH.hashCode(r, o.get(key));
		return r;
	}

	@Override
	public boolean areEqual(Map l, Map r) {
		if (l == null || r == null)
			return l == r;
		for (Object key : keys)
			if (OH.ne(l.get(key), r.get(key)))
				return false;
		return true;
	}
}
