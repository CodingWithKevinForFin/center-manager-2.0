package com.f1.utils.structs;

import java.util.Iterator;
import com.f1.base.ToStringable;
import com.f1.utils.SH;

public class BasicDoubleLinkedList<E extends DoubleLinkedListEntry> implements DoubleLinkedList<E>, ToStringable {

	private E head, tail;

	@Override
	public void add(E entry) {
		if (tail == null)
			head = tail = entry;
		else
			addAfter(tail, entry);
	}

	@Override
	public void remove(E entry) {
		final E next = (E) entry.getNext(), prior = (E) entry.getPrior();
		if (prior != null)
			prior.setNext(next);
		if (next != null)
			next.setPrior(prior);
		if (head == entry)
			head = next;
		if (tail == entry)
			tail = prior;
		if (head == null)
			head = tail;
		else if (tail == null)
			tail = head;
		entry.reset(null, null);
	}

	@Override
	public void addAfter(E existing, E entry) {
		entry.reset(existing, existing.getNext());
		existing.setNext(entry);
		if (existing == tail)
			tail = entry;
	}

	@Override
	public void addBefore(E existing, E entry) {
		entry.reset(existing.getPrior(), existing);
		existing.setPrior(entry);
		if (existing == head)
			head = entry;
	}

	@Override
	public E getHead() {
		return head;
	}

	@Override
	public E getTail() {
		return tail;
	}

	@Override
	public Iterator<E> iterator() {
		return new ForwardIterator();
	}

	@Override
	public Iterator<E> reverseIterator() {
		return new ReverseIterator();
	}

	@Override
	public void setHead(E start) {
		(head = start).setPrior(null);
	}

	@Override
	public void setTail(E end) {
		(tail = end).setNext(null);
	}

	private class ForwardIterator implements Iterator<E> {
		private E current = head, returned;

		@Override
		public boolean hasNext() {
			return current != null;
		}

		@Override
		public E next() {
			returned = current;
			current = (E) returned.getNext();
			return returned;
		}

		@Override
		public void remove() {
			BasicDoubleLinkedList.this.remove(returned);
		}
	}

	private class ReverseIterator implements Iterator<E> {
		private E current = tail, returned;

		@Override
		public boolean hasNext() {
			return current != null;
		}

		@Override
		public E next() {
			returned = current;
			current = (E) returned.getPrior();
			return returned;
		}

		@Override
		public void remove() {
			BasicDoubleLinkedList.this.remove(returned);
		}
	}

	@Override
	public String toString() {
		return toString(new StringBuilder()).toString();
	}

	@Override
	public StringBuilder toString(StringBuilder sb) {
		sb.append('[');
		SH.join(',', this, sb);
		sb.append(']');
		return sb;
	}

	@Override
	public void clear() {
		head = tail = null;
	}

}
