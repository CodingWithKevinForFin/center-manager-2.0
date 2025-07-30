package com.f1.utils;

import com.f1.base.Pointer;

public class VolatilePointer<T> implements Pointer<T> {

	public VolatilePointer(T value) {
		this.value = value;
	}

	public VolatilePointer() {
	}

	volatile private T value;

	@Override
	public T get() {
		return value;
	}

	@Override
	public T put(T value) {
		final T r = this.value;
		this.value = value;
		return r;
	}

	@Override
	public String toString() {
		return SH.toString(value);
	}

}

