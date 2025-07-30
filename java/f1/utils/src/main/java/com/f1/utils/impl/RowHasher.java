package com.f1.utils.impl;

import com.f1.base.Row;
import com.f1.utils.Hasher;
import com.f1.utils.OH;

public class RowHasher implements Hasher<Row> {

	public static RowHasher INSTANCE = new RowHasher();

	@Override
	public int hashcode(Row o) {
		int hash = 0;
		for (int i = 0, s = o.size(); i < s; i++)
			hash = OH.hashCode(hash, o.getAt(i));
		return hash;
	}
	@Override
	public boolean areEqual(Row l, Row r) {
		final int s = l.size();
		if (s != r.size())
			return false;
		for (int i = 0; i < s; i++)
			if (OH.ne(l.getAt(i), r.getAt(i)))
				return false;
		return true;
	}

}
