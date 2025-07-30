/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils;

import java.util.AbstractCollection;
import java.util.Iterator;

public class EmptyCollection extends AbstractCollection {
	public static final EmptyCollection INSTANCE = new EmptyCollection();

	@Override
	public Iterator iterator() {
		return EmptyIterator.INSTANCE;
	}

	@Override
	public int size() {
		return 0;
	}

}
