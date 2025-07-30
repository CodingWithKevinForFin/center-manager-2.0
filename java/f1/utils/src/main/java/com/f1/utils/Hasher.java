package com.f1.utils;

public interface Hasher<T> {
	public int hashcode(T o);

	public boolean areEqual(T l, T r);

}
