package com.f1.ami.center.hdb.idx;

import com.f1.utils.OH;

public class AmiHdbIndexNode<T extends Comparable<?>> implements Comparable<AmiHdbIndexNode<T>> {

	private T value;

	protected void setIndexValue(T value) {
		this.value = value;
	}

	public T getIndexValue() {
		return this.value;
	}

	@Override
	public int compareTo(AmiHdbIndexNode<T> o) {
		return OH.compare(value, o.value);
	}

}
