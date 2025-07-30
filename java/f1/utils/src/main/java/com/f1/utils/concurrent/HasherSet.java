package com.f1.utils.concurrent;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Set;

import com.f1.base.ToStringable;
import com.f1.utils.Hasher;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.impl.BasicHasher;

public class HasherSet<V> implements Set<V>, ToStringable {
	private static final int MIN_BUCKET_SIZE = 16;

	//For non-collisions, just the value is stored, otherwise a Node is used as a linked list, this minimizes objects
	private Object[] buckets;
	private int mask;
	private int size = 0;//does NOT account for null, see hasNull.  use size() method to get true size
	private boolean hasNull = false;

	private Hasher<V> hasher;

	public HasherSet() {
		this(BasicHasher.INSTANCE);
	}
	public HasherSet(Hasher<V> hasher) {
		buckets = new Object[MIN_BUCKET_SIZE];
		mask = this.buckets.length - 1;
		this.hasher = hasher;
	}

	public HasherSet(Hasher<V> hasher, int initialCapacity) {
		this(hasher);
		reset(initialCapacity);
	}
	public HasherSet(Set<? extends V> values) {
		this(BasicHasher.INSTANCE, values);
	}

	public HasherSet(Hasher<V> hasher, Set<? extends V> values) {
		this.hasher = hasher;
		if (values instanceof HasherSet && this.hasher == ((HasherSet) values).hasher) {
			HasherSet o = (HasherSet) values;
			this.mask = o.mask;
			this.buckets = o.buckets.clone();
			this.size = o.size;
			this.hasNull = o.hasNull;
			for (int i = 0; i < buckets.length; i++)
				buckets[i] = copy(buckets[i]);
		} else {
			reset(values.size());
			for (V v : values)
				add(v);
		}
	}
	private void reset(int initialCapacity) {
		initialCapacity <<= 2;
		int i = MIN_BUCKET_SIZE;
		while (i < initialCapacity)
			i <<= 1;
		buckets = new Object[i];
		mask = i - 1;
	}
	private Object copy(Object node) {
		if (node instanceof Node) {
			Node<V> n = (Node<V>) node;
			return new Node<V>(n.key, copy(n.next), n.hash);
		}
		return node;
	}

	public boolean add(V key) {
		if (key == null) {
			if (hasNull)
				return false;
			hasNull = true;
			return true;
		}

		OH.assertNotNull(key);
		final int h = hash(key);
		final int i = indexFor(h);
		Object b = buckets[i];
		while (b instanceof Node) {
			Node<V> bucket = (Node<V>) b;
			if (eq(bucket, key, h))
				return false;
			b = bucket.next;
		}
		if (eq((V) b, key))
			return false;
		else {
			if (buckets[i] == null)
				buckets[i] = key;
			else
				buckets[i] = new Node<V>(key, buckets[i], h);
		}
		incSizeAndEnsureCapacity();
		return true;
	}
	/**
	 * @param key
	 * @return key if it didn't already exist, otherwise existing entry
	 */
	public V addIfAbsent(V key) {
		if (key == null) {
			hasNull = true;
			return null;
		}
		OH.assertNotNull(key);
		final int h = hash(key);
		final int i = indexFor(h);
		Object b = buckets[i];
		while (b instanceof Node) {
			Node<V> bucket = (Node<V>) b;
			if (eq(bucket, key, h))
				return bucket.key;
			b = bucket.next;
		}
		if (eq((V) b, key))
			return (V) b;
		else {
			if (buckets[i] == null)
				buckets[i] = key;
			else
				buckets[i] = new Node<V>(key, buckets[i], h);
		}
		incSizeAndEnsureCapacity();
		return key;
	}
	private void incSizeAndEnsureCapacity() {
		if (++size >= (buckets.length >> 2))
			rehash(buckets.length << 1);
	}
	public V get(V key) {
		if (key == null)
			return null;//will always be null
		final int h = hash(key);
		final int i = indexFor(h);
		Object b = buckets[i];
		while (b instanceof Node) {
			Node<V> bucket = (Node<V>) b;
			if (eq(bucket, key, h))
				return bucket.key;
			b = bucket.next;
		}
		if (eq((V) b, key))
			return (V) b;
		return null;
	}

	public boolean remove(Object key) {
		if (key == null) {
			if (hasNull) {
				hasNull = false;
				return true;
			}
			return false;
		}
		final int h = hash((V) key);
		final int i = indexFor(h);
		Object b = buckets[i];
		Node<V> last = null;
		if (b == null)
			return false;
		while (b instanceof Node) {
			Node<V> bucket = (Node<V>) b;
			if (eq(bucket, (V) key, h)) {
				if (last == null)
					buckets[i] = bucket.next;
				else
					last.next = bucket.next;
				size--;
				return true;
			} else {
				b = (last = bucket).next;
			}
		}
		if (eq((V) b, (V) key)) {
			if (last == null)
				buckets[i] = null;
			else
				last.next = null;
			size--;
			return true;
		}
		return false;
	}

	private void rehash(int newSize) {
		Object[] old = buckets;
		buckets = new Object[newSize];
		mask = newSize - 1;
		for (Object head : old) {
			while (head instanceof Node) {
				Node<V> n = (Node<V>) head;
				head = n.next;
				int h = n.hash;
				int i = indexFor(h);
				if (buckets[i] == null)
					buckets[i] = n.key;
				else {
					n.next = buckets[i];
					buckets[i] = n;
				}
			}
			if (head != null) {
				int h = hash((V) head);
				int i = indexFor(h);
				if (buckets[i] == null)
					buckets[i] = head;
				else {
					buckets[i] = new Node((V) head, buckets[i], h);
				}
			}
			//
		}

	}

	public boolean contains(Object key) {
		if (key == null)
			return hasNull;
		int h = hash((V) key);
		final int i = indexFor(h);
		Object b = buckets[i];
		while (b instanceof Node) {
			Node<V> bucket = (Node<V>) b;
			if (eq(bucket, (V) key, h))
				return true;
			else
				b = bucket.next;
		}
		if (b == null)
			return false;
		return eq((V) b, (V) key);
	}

	protected boolean eq(Node<V> l, V key, int rHash) {
		V k = l.key;
		return l.hash == rHash && eq(key, k);
	}
	protected boolean eq(V a, V b) {
		return a == b || (a != null && ((Hasher) hasher).areEqual(a, b));
	}
	private int hash(V key) {

		int h = key == null ? 0 : hasher.hashcode(key);
		return MH.hash(h);
	}
	private int indexFor(int h) {
		return h & mask;
	}

	@Override
	public int size() {
		return hasNull ? size + 1 : size;
	}

	public boolean addAll(Set<V> c) {
		boolean r = false;
		Iterator<V> i = c.iterator();
		while (i.hasNext())
			if (add(i.next()))
				r = true;
		return r;
	}
	@Override
	public StringBuilder toString(StringBuilder sb) {
		boolean first = true;
		sb.append('[');
		for (V n : this) {
			if (first)
				first = false;
			else
				sb.append(", ");
			sb.append(n);
		}
		return sb.append(']');
	}

	@Override
	public String toString() {
		if (size == 0)
			return hasNull ? "[null]" : "[]";
		return toString(new StringBuilder()).toString();
	}

	@Override
	public boolean isEmpty() {
		return !hasNull && size == 0;
	}

	@Override
	public Object[] toArray() {
		return toArray(new Object[size()]);
	}

	@Override
	public <T> T[] toArray(T[] a) {
		int s = size();
		if (a.length < s)
			a = Arrays.copyOf(a, s);
		int i = 0;
		Object[] buckets2 = this.buckets;
		if (hasNull)
			a[i++] = null;
		for (int location = 0; location < buckets2.length; location++) {
			Object b = buckets2[location];
			if (b == null)
				continue;
			while (b instanceof Node) {
				Node<V> n = (Node) b;
				a[i++] = (T) n.key;
				b = n.next;
			}
			if (b != null)
				a[i++] = (T) b;

		}
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
			r = add(o) || r;
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
			r = remove(o) || r;
		return r;
	}

	@Override
	public void clear() {
		this.hasNull = false;
		if (size == 0)
			return;
		buckets = new Object[MIN_BUCKET_SIZE];
		mask = buckets.length - 1;
		size = 0;
	}

	private static class Node<V> implements ToStringable {

		final private int hash;
		final private V key;
		private Object next;

		private Node(V key, Object next, int hash) {
			this.key = key;
			this.next = next;
			this.hash = hash;
		}

		public V getKey() {
			return key;
		}

		@Override
		public StringBuilder toString(StringBuilder sb) {
			return sb.append(key);
		}

		@Override
		public boolean equals(Object o) {
			return o != null && o.getClass() == this.getClass() && ((Node<V>) o).key == key;
		}

		@Override
		public int hashCode() {
			return hash;
		}
		@Override
		public String toString() {
			if (next == null)
				return key == null ? "null" : key.toString();
			return key + " --> " + next;

		}
	}

	@Override
	public Iterator<V> iterator() {
		return new KeyIterator();
	}

	private class KeyIterator implements Iterator<V> {
		private int location = -1;//last checked location
		private Object node;

		public KeyIterator() {
			if (!hasNull)
				for (location++; location != buckets.length; location++) {
					node = buckets[location];
					if (node != null)
						break;
				}
		}

		@Override
		public boolean hasNext() {
			return location != buckets.length;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		@Override
		public V next() throws NoSuchElementException {
			Object n = node;
			V ret;
			if (n instanceof Node) {
				Node<V> node = (Node<V>) n;
				ret = node.key;
				n = node.next;
			} else {
				ret = (V) n;
				n = null;
			}
			if (n == null) {
				Object[] b = buckets;
				int l = this.location + 1;
				for (; l != b.length; l++) {
					n = b[l];
					if (n != null)
						break;
				}

				location = l;
			}
			node = n;
			return ret;
		}

	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof Set))
			return false;
		Set other = (Set) o;
		if (other.size() != this.size())
			return false;
		if (hasNull && !other.contains(null))
			return false;
		for (int i = 0; i < buckets.length; i++) {
			Object b = buckets[i];
			while (b instanceof Node) {
				Node<V> n = (Node) b;
				if (!other.contains(n.key))
					return false;
				b = n.next;
			}
			if (b == null)
				continue;
			else if (!other.contains((V) b))
				return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int r = hasNull ? 1 : 0;
		for (int i = 0; i < buckets.length; i++) {
			Object b = buckets[i];
			if (b == null)
				continue;
			while (b instanceof Node) {
				Node<V> n = (Node) b;
				r += n.hash;
				b = n.next;
			}
			if (b != null)
				r += hash((V) b);
		}
		return r;
	}

	//Note, this is not a fair random because items further down in collisions are much less likely.
	public V getRandom(Random r) {
		if (size == 0)
			return null;
		int n = r.nextInt(buckets.length);
		Object bucket;
		for (;;) {
			bucket = buckets[n];
			if (bucket != null)
				break;
			else if (++n == buckets.length)
				n = 0;
		}
		while (bucket instanceof Node) {
			Node<V> node = (Node<V>) bucket;
			if (node.next == null || r.nextBoolean())
				return ((Node<V>) bucket).key;
			bucket = ((Node<V>) bucket).next;
		}
		return (V) bucket;
	}

}
