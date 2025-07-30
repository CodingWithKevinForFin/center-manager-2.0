package com.f1.utils.structs;

import java.util.Iterator;

/**
 * All operations run in constant time
 * 
 * @param <E>
 */
public interface DoubleLinkedList<E extends DoubleLinkedListEntry> extends Iterable<E> {
	void add(E entry);

	void remove(E entry);

	void addAfter(E existing, E entry);

	void addBefore(E existing, E entry);

	E getHead();

	E getTail();

	void setHead(E start);

	void setTail(E start);

	void clear();

	@Override
	Iterator<E> iterator();

	Iterator<E> reverseIterator();
}
