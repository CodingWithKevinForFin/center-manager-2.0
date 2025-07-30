package com.f1.utils;

public class AutolockingPointer<T> extends LockablePointer<T> {

	@Override
	public T get() {
		lock();
		return super.get();
	}
}
