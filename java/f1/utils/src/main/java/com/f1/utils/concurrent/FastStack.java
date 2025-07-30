/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.concurrent;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

public class FastStack<T> extends ConcurrentNode implements ConcurrentStack<T> {
	private volatile ConcurrentNode<T> MARKER = new ConcurrentNode();
	private volatile ConcurrentNode<T> top;
	private AtomicInteger size = new AtomicInteger();
	private static final AtomicReferenceFieldUpdater<FastStack, ConcurrentNode> topFieldUpdater = AtomicReferenceFieldUpdater.newUpdater(FastStack.class,
			ConcurrentNode.class, "top");

	public ConcurrentNode<T> pop() {
		ConcurrentNode<T> node;
		while ((node = top) != null && (node == MARKER || !topFieldUpdater.compareAndSet(this, node, MARKER)));
		if (node == null)
			return null;
		size.decrementAndGet();
		top = node.next;
		node.next = null;
		return node;
	}

	public void reset(ConcurrentNode<T> top, int size) {
		this.top = top;
		this.size.set(size);
	}

	public void push(ConcurrentNode<T> node) {
		while ((node.next = top) == MARKER || !topFieldUpdater.compareAndSet(this, node.next, node));

		size.incrementAndGet();
	}

	public int getSize() {
		return size.get();
	}

	public ConcurrentNode<T> peek() {
		return top;
	}
}
