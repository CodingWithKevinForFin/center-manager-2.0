package com.f1.utils.concurrent;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

import com.f1.base.ToStringable;
import com.f1.utils.Hasher;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.impl.BasicHasher;

public class LinkedHasherSet<V> implements Set<V>, ToStringable {
	private static final int MIN_BUCKET_SIZE = 16;

	public static final LinkedHasherSet EMPTY_INSTANCE = new LinkedHasherSet() {
		public boolean add(Object object) {
			throw new UnsupportedOperationException("EMPTY SET");
		};
		public Object addIfAbsent(Object object) {
			throw new UnsupportedOperationException("EMPTY SET");
		};

	};

	private Node<V> buckets[];
	private int size;
	private int mask;
	private Hasher<V> hasher;

	private Node<V> head, tail;

	public LinkedHasherSet() {
		this(BasicHasher.INSTANCE);
	}
	public LinkedHasherSet(Hasher<V> hasher) {
		buckets = new Node[MIN_BUCKET_SIZE];
		mask = this.buckets.length - 1;
		this.hasher = hasher;
	}
	public LinkedHasherSet(Hasher<V> hasher, int initialCapacity) {
		this(hasher);
		reset(initialCapacity);
	}
	public LinkedHasherSet(Set<? extends V> values) {
		this(BasicHasher.INSTANCE, values);
	}
	public LinkedHasherSet(Hasher<V> hasher, Set<? extends V> values) {
		this.hasher = hasher;
		reset(values.size());
		for (V v : values)
			add(v);
	}
	private void reset(int initialCapacity) {
		initialCapacity <<= 2;
		int i = MIN_BUCKET_SIZE;
		while (i < initialCapacity)
			i <<= 1;
		buckets = new Node[i];
		mask = i - 1;
	}

	@Override
	public boolean add(V object) {
		final int h = hash(object);
		final int pos = indexFor(h);
		if (size == 0) {
			buckets[pos] = head = tail = new Node<V>(h, null, object, null);
		} else {
			Node<V> t = buckets[pos];
			if (t == null) {
				Node<V> n = new Node<V>(h, null, object, tail);
				buckets[pos] = n;
				tail.linkAfter = n;
				tail = n;
			} else {
				for (;;) {
					if (eq(t, object, h))
						return false;
					if (t.next == null) {
						Node<V> n = new Node<V>(h, null, object, tail);
						t.next = n;
						tail.linkAfter = n;
						tail = n;
						break;
					}
					t = t.next;
				}
			}
		}
		incSizeAndEnsureCapacity();
		return true;
	}
	/**
	 * @param key
	 * @return key if it didn't already exist, otherwise existing entry
	 */
	public V addIfAbsent(V object) {
		final int h = hash(object);
		final int pos = indexFor(h);
		if (size == 0) {
			buckets[pos] = head = tail = new Node<V>(h, null, object, null);
		} else {
			Node<V> t = buckets[pos];
			if (t == null) {
				Node<V> n = new Node<V>(h, null, object, tail);
				buckets[pos] = n;
				tail.linkAfter = n;
				tail = n;
			} else {
				for (;;) {
					if (eq(t, object, h))
						return t.key;
					if (t.next == null) {
						Node<V> n = new Node<V>(h, null, object, tail);
						t.next = n;
						tail.linkAfter = n;
						tail = n;
						break;
					}
					t = t.next;
				}
			}
		}
		incSizeAndEnsureCapacity();
		return object;
	}
	private void incSizeAndEnsureCapacity() {
		if (++size >= (buckets.length >> 2))
			rehash(buckets.length << 1);
	}
	public Node<V> getNode(V key) {
		if (size == 0)
			return null;
		final int h = hash(key);
		final int p = indexFor(h);
		for (Node<V> bucket = buckets[p]; bucket != null; bucket = bucket.next)
			if (eq(bucket, key, h))
				return bucket;
		return null;
	}
	@Override
	public boolean remove(Object object) {
		if (size == 0)
			return false;
		final int h = hash(object);
		final int pos = indexFor(h);
		Node<V> t = buckets[pos];
		if (t == null)
			return false;
		else if (eq(t, object, h)) {
			buckets[pos] = t.next;
		} else {
			for (;;) {
				Node<V> n = t.next;
				if (n == null)
					return false;
				if (eq(n, object, h)) {
					t.next = n.next;
					t = n;
					break;
				}
				t = n;
			}
		}
		if (t.linkBefore == null)
			head = t.linkAfter;
		else
			t.linkBefore.linkAfter = t.linkAfter;
		if (t.linkAfter == null)
			tail = t.linkBefore;
		else
			t.linkAfter.linkBefore = t.linkBefore;
		size--;
		return true;
	}

	private void rehash(int length) {
		Node<V>[] target = new Node[length];
		this.buckets = target;
		this.mask = this.buckets.length - 1;
		for (Node<V> n = head; n != null; n = n.linkAfter) {
			final int pos = indexFor(n.hash);
			n.next = target[pos];
			target[pos] = n;
		}
	}
	@Override
	public boolean contains(Object o) {
		if (size == 0)
			return false;
		final int h = hash((V) o);
		final int p = indexFor(h);
		for (Node<V> t = buckets[p]; t != null; t = t.next)
			if (eq(t, o, h))
				return true;
		return false;
	}
	protected boolean eq(Node<V> l, Object r, int rHash) {
		return l.hash == rHash && hasher.areEqual(l.key, (V) r);
	}
	private int hash(Object key) {
		return MH.hash(hasher.hashcode((V) key));
	}
	private int indexFor(int h) {
		return h & mask;
	}
	@Override
	public int size() {
		return size;
	}
	@Override
	public StringBuilder toString(StringBuilder sb) {
		boolean first = true;
		if (size == 0)
			return sb.append("[]");
		sb.append('[');
		for (Node<V> n = head; n != null; n = n.linkAfter) {
			if (first)
				first = false;
			else
				sb.append(", ");
			sb.append(n.key);
		}
		return sb.append(']');
	}

	@Override
	public String toString() {
		if (size == 0)
			return "[]";
		return toString(new StringBuilder()).toString();
	}

	@Override
	public boolean isEmpty() {
		return size == 0;
	}
	@Override
	public Object[] toArray() {
		if (size == 0)
			return OH.EMPTY_OBJECT_ARRAY;
		Object[] r = new Object[size];
		int pos = 0;
		for (Node<V> n = head; n != null; n = n.linkAfter)
			r[pos++] = n.key;
		return r;
	}

	@Override
	public <T> T[] toArray(T[] a) {
		if (a.length < size)
			a = Arrays.copyOf(a, size);
		int pos = 0;
		for (Node<V> n = head; n != null; n = n.linkAfter)
			a[pos++] = (T) n.key;
		return a;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		for (Object o : c)
			if (!contains(o))
				return false;
		return true;
	}
	public boolean containsAny(Iterable<?> c) {
		for (Object o : c)
			if (contains(o))
				return true;
		return false;
	}
	@Override
	public boolean addAll(Collection<? extends V> c) {
		boolean r = false;
		for (V o : c)
			if (add(o))
				r = true;
		return r;
	}
	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}
	@Override
	public boolean removeAll(Collection<?> c) {
		boolean r = false;
		for (Object o : c)
			if (remove(o))
				r = true;
		return r;
	}

	@Override
	public void clear() {
		if (size == 0)
			return;
		buckets = new Node[MIN_BUCKET_SIZE];
		this.mask = this.buckets.length - 1;
		this.head = tail = null;
		size = 0;
	}

	public static class Node<V> implements ToStringable {
		final private int hash;
		final V key;

		public Node(int hash, Node<V> next, V key, Node<V> linkBefore) {
			this.hash = hash;
			this.key = key;
			this.next = next;
			this.linkBefore = linkBefore;
		}

		Node<V> next, linkAfter, linkBefore;

		@Override
		public String toString() {
			return key == null ? null : key.toString();
		}
		@Override
		public StringBuilder toString(StringBuilder sb) {
			return sb.append(key);
		}

		@Override
		public boolean equals(Object o) {
			return o != null && o.getClass() == Node.class && ((Node<V>) o).key == key;
		}

		@Override
		public int hashCode() {
			return hash;
		}

		public Node<V> getNextNode() {
			return linkAfter;
		}
		public Node<V> getPriorNode() {
			return linkBefore;
		}
		public V getValue() {
			return key;
		}
	}

	@Override
	public Iterator<V> iterator() {
		return new KeyIterator(this.buckets);
	}

	public class KeyIterator implements Iterator<V> {

		private Node<V> pos;

		public KeyIterator(Node<V>[] blocks) {
			pos = head;
		}

		@Override
		public boolean hasNext() {
			return pos != null;
		}
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		@Override
		public V next() throws NoSuchElementException {
			V r = pos.key;
			pos = pos.linkAfter;
			return r;
		}

	}

	@Override
	public boolean equals(Object o) {
		if (o == null || o.getClass() != LinkedHasherSet.class)
			return false;
		LinkedHasherSet other = (LinkedHasherSet) o;
		if (other.size() != this.size())
			return false;
		for (Node<V> n = head, m = other.head; n != null; n = n.linkAfter, m = m.linkAfter)
			if (n.hash != m.hash || !hasher.areEqual(n.key, m.key))
				return false;
		return true;
	}
	@Override
	public int hashCode() {
		int r = 0;
		for (int i = 0; i < buckets.length; i++)
			for (Node<V> node = buckets[i]; node != null; node = node.next)
				r = r * 31 + node.hash;
		return r;
	}

	public V getHead() {
		return this.head == null ? null : this.head.key;
	}
	public V getTail() {
		return this.tail == null ? null : this.tail.key;
	}
	public Node<V> getHeadNode() {
		return this.head;
	}
	public Node<V> getTailNode() {
		return this.tail;
	}

}
