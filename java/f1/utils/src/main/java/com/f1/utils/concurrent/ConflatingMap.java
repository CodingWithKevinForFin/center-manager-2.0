/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.concurrent;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

import com.f1.utils.CopyOnWriteHashMap;
import com.f1.utils.EmptyIterator;
import com.f1.utils.OH;

final public class ConflatingMap<K, V> implements Iterable<V> {
	private static final ValueNode END = new ValueNode();
	private static final AtomicReferenceFieldUpdater<ConflatingMap, ValueNode> topFieldUpdater;
	static {
		topFieldUpdater = AtomicReferenceFieldUpdater.newUpdater(ConflatingMap.class, ValueNode.class, "top");
	}

	final private CopyOnWriteHashMap<K, ValueNode<V>> inner = new CopyOnWriteHashMap<K, ValueNode<V>>();
	volatile private ValueNode<V> top = END;
	final private boolean ignoreDups;

	final private ConflatingMapListener listener;
	private AtomicInteger size = new AtomicInteger(0);

	public ConflatingMap(boolean ignoreDups, ConflatingMapListener listener) {
		this.ignoreDups = ignoreDups;
		this.listener = listener;
	}

	public static class ValueNode<V> {
		public volatile FastSemaphore lock = new FastSemaphore();
		public volatile V value;
		public volatile ValueNode<V> next = null;
	}

	public void put(K k, V v) {
		ValueNode<V> vn = inner.get(k);
		if (vn == null) {
			ValueNode<V> exists = inner.putIfAbsent(k, vn = new ValueNode<V>());
			if (exists != null) {
				vn = exists;
				if (ignoreDups && OH.eq(vn.value, v))
					return;
			}
		} else if (ignoreDups && OH.eq(vn.value, v))
			return;
		V old = null;
		boolean putOnList = false;
		vn.lock.aquire();
		if (vn.next != null)
			old = vn.value;
		else {
			vn.next = top;
			putOnList = true;
		}
		vn.value = v;
		vn.lock.release();
		if (putOnList) {
			while (!topFieldUpdater.compareAndSet(this, vn.next, vn))
				vn.next = top;
			size.incrementAndGet();
		}
		if (old != null && listener != null)
			listener.onConflatedOut(old);
	}

	public V get(K k) {
		ValueNode<V> r = inner.get(k);
		return r == null ? null : r.value;
	}

	public boolean isEmpty() {
		return top == END;
	}

	@Override
	public Iterator<V> iterator() {
		if (isEmpty())
			return EmptyIterator.INSTANCE;
		ValueNode<V> t;
		while (!topFieldUpdater.compareAndSet(this, t = top, END))
			;
		return new CowlhmIterator<V>(t);
	}

	private class CowlhmIterator<V> implements Iterator<V> {

		private ValueNode<V> next;

		public CowlhmIterator(ValueNode<V> next) {
			this.next = next;
		}

		@Override
		public boolean hasNext() {
			return next != END;
		}

		@Override
		public V next() {
			ValueNode<V> t = next;
			next = t.next;
			t.lock.aquire();
			V r = t.value;
			t.value = null;
			t.next = null;
			t.lock.release();
			size.decrementAndGet();
			return r;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

	}

	public static interface ConflatingMapListener<V> {
		public void onConflatedOut(V old);
	}

	public int size() {
		return size.get();
	}

	public void getValues(List<V> sink) {
		for (ValueNode<V> i : this.inner.values()) {
			V v = i.value;
			if (v != null)
				sink.add(v);
		}
	}

}
