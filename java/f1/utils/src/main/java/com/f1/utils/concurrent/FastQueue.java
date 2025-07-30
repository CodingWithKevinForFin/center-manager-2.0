/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.concurrent;

import java.util.List;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

public class FastQueue<T> {
	private volatile ConcurrentNode<T> tail;
	private final ConcurrentNode<T> TMARKER = new ConcurrentNode<T>();
	private volatile ConcurrentNode<T> head;
	private static final AtomicReferenceFieldUpdater<FastQueue, ConcurrentNode> tailFieldUpdater;
	private static final AtomicReferenceFieldUpdater<FastQueue, ConcurrentNode> headFieldUpdater;
	static {
		tailFieldUpdater = AtomicReferenceFieldUpdater.newUpdater(FastQueue.class, ConcurrentNode.class, "tail");
		headFieldUpdater = AtomicReferenceFieldUpdater.newUpdater(FastQueue.class, ConcurrentNode.class, "head");
	}

	public FastQueue() {
		tail = head = new ConcurrentNode();
		head.value = (T) new Object();
	}

	public void put(final T value) {

		ConcurrentNode<T> n = null;
		n = new ConcurrentNode<T>();
		ConcurrentNode<T> t;
		t = tail;
		while (t == TMARKER || !tailFieldUpdater.compareAndSet(this, t, TMARKER))
			t = tail;
		n.value = value;
		t.next = n;
		tail = n;
	}

	final public boolean isEmpty() {
		return head.next == null;
	}

	final public T get() {
		ConcurrentNode<T> n = head.next;
		if (n == null || n == TMARKER)
			return null;
		ConcurrentNode<T> oldHead = head;
		head = n;
		T r = head.value;
		head.value = null;
		oldHead.next = null;
		return r;
	}

	final public T getThreadSafe() {
		ConcurrentNode<T> oldHead, newHead;
		T r;
		do {
			if ((newHead = (oldHead = head).next) == null)
				return null;
		} while (newHead == TMARKER || !headFieldUpdater.compareAndSet(this, oldHead, newHead));
		r = newHead.value;
		newHead.value = null;
		oldHead.next = null;
		return r;
	}

	public void getEvents(List<T> sink) {
		ConcurrentNode<T> n = head;
		if (n == null)
			return;
		n = n.next;
		while (n != null && n != TMARKER) {
			T v = n.value;
			n = n.next;
			if (v != null)
				sink.add(v);
		}

	}

}
