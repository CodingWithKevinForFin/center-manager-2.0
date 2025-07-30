package com.f1.utils.structs;

import com.f1.utils.SH;

public class BasicDoubleLinkedListEntry<T> extends AbstractDoubleLinkedListEntry {

	private T value;

	public BasicDoubleLinkedListEntry() {

	}

	public BasicDoubleLinkedListEntry(T value) {
		this.value = value;
	}

	public void setValue(T value) {
		this.value = value;
	}

	public T getValue() {
		return value;
	}

	@Override
	public String toString() {
		return SH.toString(value);
	}

}
