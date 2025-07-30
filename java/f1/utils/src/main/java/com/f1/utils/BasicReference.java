/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils;

import com.f1.base.Reference;

public final class BasicReference<T> implements Reference<T>, Immutable {

	public BasicReference(T value) {
		this.value = value;
	}

	private final T value;

	@Override
	public T get() {
		return value;
	}

	@Override
	public String toString() {
		return SH.toString(value);
	}

}

