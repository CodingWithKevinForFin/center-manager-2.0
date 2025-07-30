package com.f1.utils.structs;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.f1.utils.OH;

public class TupleComparator implements Comparator<Tuple>, Serializable {

	private int[] indexes;

	public TupleComparator(int... indexes) {
		this.indexes = indexes;
	}
	@Override
	public int compare(Tuple o1, Tuple o2) {
		for (int i : indexes) {
			int result = OH.compare((Comparable) o1.getAt(i), (Comparable) o2.getAt(i));
			if (result != 0)
				return result;
		}
		return 0;
	}

	static public void sort(List<? extends Tuple> tuples, int... indexes) {
		Collections.sort(tuples, new TupleComparator(indexes));
	}

}
