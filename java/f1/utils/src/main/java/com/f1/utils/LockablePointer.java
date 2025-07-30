package com.f1.utils;

import com.f1.base.Lockable;
import com.f1.base.LockedException;

public class LockablePointer<T> extends BasicPointer<T> implements Lockable {

	private boolean locked;

	@Override
	public void lock() {
		locked = true;
	}

	@Override
	public boolean isLocked() {
		return locked;
	}

	@Override
	public T put(T value) {
		LockedException.assertNotLocked(this);
		return super.put(value);
	}

}
