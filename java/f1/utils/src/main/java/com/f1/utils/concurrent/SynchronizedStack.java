/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.concurrent;

public class SynchronizedStack<T> implements ConcurrentStack<T> {

	private ConcurrentNode<T> top;
	private int size;

	synchronized public ConcurrentNode<T> pop() {
		ConcurrentNode<T> node = top;
		if (node == null)
			return null;
		top = node.next;
		node.next = null;
		size--;
		return node;
	}

	synchronized public void push(ConcurrentNode<T> node) {
		node.next = top;
		top = node;
		size++;
	}

	synchronized public ConcurrentNode<T> peek() {
		return top;
	}

	@Override
	synchronized public int getSize() {
		return size;
	}

	@Override
	synchronized public void reset(ConcurrentNode<T> head, int size) {
		top = head;
		this.size = size;
	}
}
