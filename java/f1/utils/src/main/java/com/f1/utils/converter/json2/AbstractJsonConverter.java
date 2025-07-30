package com.f1.utils.converter.json2;

import com.f1.base.Lockable;
import com.f1.base.LockedException;

public abstract class AbstractJsonConverter<T> implements JsonConverter<T>, Lockable {

	private final Class<T> type;
	private boolean locked;

	public AbstractJsonConverter(Class<T> type) {
		this.type = type;
	}

	@Override
	abstract public void objectToString(T o, ToJsonConverterSession out);

	@Override
	public Class<T> getType() {
		return type;
	}

	@Override
	public boolean isCompatible(Class<?> o) {
		return getType().isAssignableFrom(o);
	}

	@Override
	public void lock() {
		this.locked = true;
	}

	@Override
	public boolean isLocked() {
		return locked;
	}

	protected void assertNotLocked() {
		LockedException.assertNotLocked(this);
	}

	@Override
	public boolean isLeaf() {
		return true;
	}

}
