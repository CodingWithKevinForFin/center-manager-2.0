package com.f1.ami.center.hdb.idx;

import java.util.Map;

public class AmiHdbIndexEntry<T extends Comparable<?>> extends AmiHdbIndexNode<T> implements Map.Entry<T, int[]> {

	int[] rows;

	public AmiHdbIndexEntry(T value, int rows[]) {
		super.setIndexValue(value);
		this.rows = rows;
	}
	public AmiHdbIndexEntry() {
	}

	@Override
	public T getKey() {
		return this.getIndexValue();
	}
	@Override
	public int[] getValue() {
		return rows;
	}
	@Override
	public int[] setValue(int[] value) {
		int[] old = this.rows;
		this.rows = value;
		return old;
	}

}
