package com.f1.utils.concurrent;

public class ObjectIntern<T> {

	final protected HasherSet<T> inner = new HasherSet<T>();

	public T intern(T object) {
		if (object == null)
			return null;
		return inner.addIfAbsent(object);
	}

	public ObjectInternThreadlocal<T> getThreadLocalVersion() {
		return new ObjectInternThreadlocal(this);
	}

	public int size() {
		return inner.size();
	}
}
