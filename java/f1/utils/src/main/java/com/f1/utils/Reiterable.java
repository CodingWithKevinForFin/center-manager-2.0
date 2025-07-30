package com.f1.utils;

import java.util.Iterator;

public interface Reiterable<T> extends Iterable<T> {

	@Override
	Iterator<T> iterator();

	Iterator<T> iterator(Iterator<T> iterator);
}

