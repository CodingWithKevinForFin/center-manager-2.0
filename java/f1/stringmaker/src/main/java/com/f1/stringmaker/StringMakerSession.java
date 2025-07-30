package com.f1.stringmaker;

import java.util.Iterator;

public interface StringMakerSession {

	StringBuilder getSink();

	Object dereference(Object source, String key);

	Object getValue(String key);

	Object getValue(String[] keyPath, String origKey);

	void append(Object obj);

	Iterator<?> toIterator(Object object);

	StringMaker getStringMaker(String name);

	void pushValue(String key, Object obj);

	Object popValue(String key);

	boolean toBoolean(Object obj);

	int toLength(Object value);

	boolean containsKey(String key);

}
