package com.f1.utils.ids;

import java.util.Collection;

public interface IdGenerator<T> {
	public T createNextId();
	public void createNextIds(int count, Collection<? super T> sink);
}
