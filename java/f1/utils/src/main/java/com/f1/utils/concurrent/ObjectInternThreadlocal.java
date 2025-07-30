package com.f1.utils.concurrent;


public class ObjectInternThreadlocal<T> extends ObjectIntern<T> {

	final private ObjectIntern<T> global;

	public ObjectInternThreadlocal(ObjectIntern<T> global) {
		this.global = global;
	}

	@Override
	public T intern(T object) {
		if (object == null)
			return null;
		T r = inner.get(object);
		if (r != null)
			return r;
		synchronized (global) {
			r = global.intern(object);
		}
		inner.add(r);
		return r;
	}

}
