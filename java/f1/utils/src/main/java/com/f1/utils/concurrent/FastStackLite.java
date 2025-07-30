/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.concurrent;

import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

public class FastStackLite<T> extends ConcurrentNode implements ConcurrentStack<T> {
	private volatile ConcurrentNode<T> top;
	private final ConcurrentNode<T> MARKER = new ConcurrentNode<T>();
	private static final AtomicReferenceFieldUpdater<FastStackLite, ConcurrentNode> topFieldUpdater = AtomicReferenceFieldUpdater.newUpdater(
			FastStackLite.class, ConcurrentNode.class, "top");

	public ConcurrentNode<T> pop() {
		ConcurrentNode<T> node;
		while ((node = top) != null && (node == MARKER || !topFieldUpdater.compareAndSet(this, node, MARKER)));
		if (node == null)
			return null;
		top = node.next;
		node.next = null;
		return node;
	}

	public void push(ConcurrentNode<T> node) {
		while ((node.next = top) == MARKER || !topFieldUpdater.compareAndSet(this, node.next, node));
	}

	public ConcurrentNode<T> peek() {
		return top;
	}

	@Override
	public int getSize() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void reset(ConcurrentNode<T> head, int i) {
		this.top = head;
	}
}
