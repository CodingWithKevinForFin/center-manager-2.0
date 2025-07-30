package com.f1.utils.impl;

import java.lang.ref.WeakReference;

public class StrongReference<T> extends WeakReference<T> {

	private T value;

	public StrongReference() {
		super(null);
	}
	public StrongReference(T referent) {
		super(null);
		value = referent;
	}

	public T get() {
		return value;
	}

	public void set(T value) {
		this.value = value;
	}

}
