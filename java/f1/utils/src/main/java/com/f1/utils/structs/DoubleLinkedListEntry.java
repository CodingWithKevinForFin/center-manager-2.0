package com.f1.utils.structs;

public interface DoubleLinkedListEntry<E extends DoubleLinkedListEntry<E>> {

	E getNext();

	void setNext(E next);

	E getPrior();

	void setPrior(E prior);

	void reset(E prior, E next);

}
