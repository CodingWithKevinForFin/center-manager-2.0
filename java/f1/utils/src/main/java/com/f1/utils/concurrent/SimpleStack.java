/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.concurrent;

import java.util.concurrent.atomic.AtomicInteger;

public class SimpleStack<T extends ConcurrentNode> extends ConcurrentNode implements ConcurrentStack<T> {

	volatile private ConcurrentNode<T> top;
	volatile private AtomicInteger size = new AtomicInteger();

	public ConcurrentNode<T> pop() {
		ConcurrentNode<T> node = top;
		if (node == null)
			return null;
		top = node.next;
		node.next = null;
		size.decrementAndGet();
		return node;
	}

	public void push(ConcurrentNode<T> node) {
		node.next = top;
		top = node;
		size.incrementAndGet();
	}
	public ConcurrentNode<T> peek() {
		return top;
	}

	@Override
	public int getSize() {
		return size.get();
	}

	@Override
	public void reset(ConcurrentNode<T> head, int size) {
		top = head;
		this.size.set(size);
	}
}
