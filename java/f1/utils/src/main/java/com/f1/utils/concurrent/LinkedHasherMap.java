package com.f1.utils.concurrent;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import com.f1.base.IterableAndSize;
import com.f1.base.ToStringable;
import com.f1.utils.Hasher;
import com.f1.utils.Iterator2Iterable;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.impl.BasicHasher;

public class LinkedHasherMap<K, V> implements Map<K, V>, ToStringable, Iterable<java.util.Map.Entry<K, V>> {

	private static final int MIN_BUCKET_SIZE = 16;

	public static class Node<K, V> implements ToStringable, Map.Entry<K, V> {
		final private int hash;
		final K key;
		V value;

		public Node(int hash, Node<K, V> next, K key, V value, Node<K, V> linkBefore, Node<K, V> linkAfter) {
			this.hash = hash;
			this.key = key;
			this.value = value;
			this.next = next;
			this.linkBefore = linkBefore;
			this.linkAfter = linkAfter;
		}

		Node<K, V> next, linkAfter, linkBefore;

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
			return o != null && o.getClass() == Node.class && ((Node<K, V>) o).key == key;
		}

		@Override
		public int hashCode() {
			return hash;
		}

		public Node<K, V> getNextNode() {
			return linkAfter;
		}
		public Node<K, V> getPriorNode() {
			return linkBefore;
		}
		public K getKey() {
			return key;
		}
		public V getValue() {
			return value;
		}
		@Override
		public V setValue(V value) {
			V old = this.value;
			this.value = value;
			return old;
		}
	}

	private Node<K, V> buckets[];
	private int size;
	private int mask;
	private Hasher<K> hasher;

	private Node<K, V> head, tail;
	private Set<K> ks;
	private LinkedHasherMap<K, V>.Values<V> values;

	public LinkedHasherMap() {
		this(BasicHasher.INSTANCE);
	}
	public LinkedHasherMap(Hasher<K> hasher) {
		buckets = new Node[MIN_BUCKET_SIZE];
		mask = this.buckets.length - 1;
		this.hasher = hasher;
	}
	public LinkedHasherMap(Hasher<K> hasher, int initialCapacity) {
		this(hasher);
		reset(initialCapacity);
	}
	public LinkedHasherMap(Map<? extends K, ? extends V> values) {
		this(BasicHasher.INSTANCE, values);
	}
	public LinkedHasherMap(Hasher<K> hasher, Map<? extends K, ? extends V> values) {
		this.hasher = hasher;
		reset(values.size());
		for (Map.Entry<? extends K, ? extends V> e : values.entrySet())
			put(e.getKey(), e.getValue());
	}
	private void reset(int initialCapacity) {
		initialCapacity <<= 2;
		int i = MIN_BUCKET_SIZE;
		while (i < initialCapacity)
			i <<= 1;
		buckets = new Node[i];
		mask = i - 1;
	}

	public Node<K, V> getOrCreateEntry(K key) {
		final int h = hash(key);
		final int pos = indexFor(h);
		if (size == 0) {
			Node<K, V> r = buckets[pos] = head = tail = new Node<K, V>(h, null, key, null, null, null);
			incSizeAndEnsureCapacity();
			return r;
		}
		Node<K, V> t = buckets[pos];
		if (t == null) {
			Node<K, V> r = new Node<K, V>(h, null, key, null, tail, null);
			buckets[pos] = r;
			tail.linkAfter = r;
			tail = r;
			incSizeAndEnsureCapacity();
			return r;
		} else {
			for (;;) {
				if (eq(t, key, h))
					return t;
				if (t.next == null) {
					Node<K, V> r = new Node<K, V>(h, null, key, null, tail, null);
					t.next = r;
					tail.linkAfter = r;
					tail = r;
					incSizeAndEnsureCapacity();
					return r;
				}
				t = t.next;
			}
		}
	}
	@Override
	public V put(K object, V value) {
		final int h = hash(object);
		final int pos = indexFor(h);
		if (size == 0) {
			buckets[pos] = head = tail = new Node<K, V>(h, null, object, value, null, null);
		} else {
			Node<K, V> t = buckets[pos];
			if (t == null) {
				Node<K, V> n = new Node<K, V>(h, null, object, value, tail, null);
				buckets[pos] = n;
				tail.linkAfter = n;
				tail = n;
			} else {
				for (;;) {
					if (eq(t, object, h)) {
						return t.setValue(value);
					}
					if (t.next == null) {
						Node<K, V> n = new Node<K, V>(h, null, object, value, tail, null);
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
		return null;
	}
	public V putAtHead(K object, V value) {
		final int h = hash(object);
		final int pos = indexFor(h);
		if (size == 0) {
			buckets[pos] = head = tail = new Node<K, V>(h, null, object, value, null, null);
		} else {
			Node<K, V> t = buckets[pos];
			if (t == null) {
				Node<K, V> n = new Node<K, V>(h, null, object, value, null, head);
				buckets[pos] = n;
				head.linkBefore = n;
				head = n;
			} else {
				for (;;) {
					if (eq(t, object, h)) {
						return t.setValue(value);
					}
					if (t.next == null) {
						Node<K, V> n = new Node<K, V>(h, null, object, value, null, head);
						t.next = n;
						head.linkBefore = n;
						head = n;
						break;
					}
					t = t.next;
				}
			}
		}
		incSizeAndEnsureCapacity();
		return null;
	}
	/**
	 * @param key
	 * @return key if it didn't already exist, otherwise existing entry
	 */
	public V putIfAbsent(K object, V v) {
		final int h = hash(object);
		final int pos = indexFor(h);
		if (size == 0) {
			buckets[pos] = head = tail = new Node<K, V>(h, null, object, v, null, null);
		} else {
			Node<K, V> t = buckets[pos];
			if (t == null) {
				Node<K, V> n = new Node<K, V>(h, null, object, v, tail, null);
				buckets[pos] = n;
				tail.linkAfter = n;
				tail = n;
			} else {
				for (;;) {
					if (eq(t, object, h))
						return t.getValue();
					if (t.next == null) {
						Node<K, V> n = new Node<K, V>(h, null, object, v, tail, null);
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
		return null;
	}
	private void incSizeAndEnsureCapacity() {
		if (++size >= (buckets.length >> 2))
			rehash(buckets.length << 1);
	}

	@Override
	public V get(Object key) {
		Node<K, V> n = getNode(key);
		return n == null ? null : n.getValue();
	}
	public Node<K, V> getNode(Object key) {
		if (size == 0)
			return null;
		final int h = hash(key);
		final int p = indexFor(h);
		for (Node<K, V> bucket = buckets[p]; bucket != null; bucket = bucket.next)
			if (eq(bucket, key, h))
				return bucket;
		return null;
	}
	@Override
	public V remove(Object object) {
		if (size == 0)
			return null;
		final int h = hash(object);
		final int pos = indexFor(h);
		Node<K, V> t = buckets[pos];
		if (t == null)
			return null;
		else if (eq(t, object, h)) {
			buckets[pos] = t.next;
		} else {
			for (;;) {
				Node<K, V> n = t.next;
				if (n == null)
					return null;
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
		return t.getValue();
	}

	private void rehash(int length) {
		Node<K, V>[] target = new Node[length];
		this.buckets = target;
		this.mask = this.buckets.length - 1;
		for (Node<K, V> n = head; n != null; n = n.linkAfter) {
			final int pos = indexFor(n.hash);
			n.next = target[pos];
			target[pos] = n;
		}
	}
	@Override
	public boolean containsKey(Object o) {
		if (size == 0)
			return false;
		final int h = hash((K) o);
		final int p = indexFor(h);
		for (Node<K, V> t = buckets[p]; t != null; t = t.next)
			if (eq(t, o, h))
				return true;
		return false;
	}
	protected boolean eq(Node<K, V> l, Object r, int rHash) {
		return l.hash == rHash && hasher.areEqual(l.key, (K) r);
	}
	private int hash(Object key) {
		return MH.hash(hasher.hashcode((K) key));
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
		if (size == 0)
			return sb.append("{}");
		sb.append('{');
		for (Node<K, V> n = head;;) {
			sb.append(n.key).append('=').append(n.value);
			if ((n = n.linkAfter) == null)
				return sb.append('}');
			sb.append(", ");
		}
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
	public void clear() {
		if (size == 0)
			return;
		buckets = new Node[MIN_BUCKET_SIZE];
		this.mask = this.buckets.length - 1;
		size = 0;
	}

	public EntryIterator entryIterator() {
		return new EntryIterator(this.buckets);
	}
	public Iterable<Entry<K, V>> entryIterable() {
		return new Iterator2Iterable<Entry<K, V>>(entryIterator());
	}
	public KeyIterator keyIterator() {
		return new KeyIterator(this.buckets);
	}

	public class KeyIterator implements Iterator<K> {
		private Node<K, V> pos;

		public KeyIterator(Node<K, V>[] blocks) {
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
		public K next() throws NoSuchElementException {
			K r = pos.key;
			pos = pos.linkAfter;
			return r;
		}
	}

	public ValueIterator valueIterator() {
		return new ValueIterator(this.buckets);
	}

	public class ValueIterator implements Iterator<V> {
		private Node<K, V> pos;

		public ValueIterator(Node<K, V>[] blocks) {
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
			V r = pos.value;
			pos = pos.linkAfter;
			return r;
		}
	}

	public EntryIterator Iterator() {
		return new EntryIterator(this.buckets);
	}
	@Override
	public Iterator<Map.Entry<K, V>> iterator() {
		return new EntryIterator(this.buckets);
	}

	public class EntryIterator implements Iterator<Map.Entry<K, V>> {
		private Node<K, V> pos;

		public EntryIterator(Node<K, V>[] blocks) {
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
		public Node<K, V> next() throws NoSuchElementException {
			Node<K, V> r = pos;
			pos = pos.linkAfter;
			return r;
		}
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || o.getClass() != LinkedHasherMap.class)
			return false;
		LinkedHasherMap other = (LinkedHasherMap) o;
		if (other.size() != this.size())
			return false;
		for (Node<K, V> n = head, m = other.head; n != null; n = n.linkAfter)
			if (n.hash != m.hash || !hasher.areEqual(n.key, m.key) || OH.ne(n.getValue(), m.getValue()))
				return false;
		return true;
	}
	@Override
	public int hashCode() {
		int r = 0;
		for (int i = 0; i < buckets.length; i++)
			for (Node<K, V> node = buckets[i]; node != null; node = node.next) {
				r = r * 31 + node.hash;
				r = r * 31 + OH.hashCode(node.value);
			}
		return r;
	}

	public K getHead() {
		return this.head == null ? null : this.head.key;
	}
	public K getTail() {
		return this.tail == null ? null : this.tail.key;
	}
	public Node<K, V> getHeadNode() {
		return this.head;
	}
	public Node<K, V> getTailNode() {
		return this.tail;
	}
	@Override
	public boolean containsValue(Object value) {
		return false;
	}
	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		for (Map.Entry<? extends K, ? extends V> e : m.entrySet())
			put(e.getKey(), e.getValue());
	}
	@Override
	public Set<K> keySet() {
		if (this.ks == null)
			this.ks = new KeySet();
		return this.ks;
	}

	@Override
	public Values<V> values() {
		Values<V> vs = values;
		return (vs != null ? vs : (values = new Values()));
	}

	public final class Values<T> extends AbstractCollection<V> implements IterableAndSize<V> {
		public Iterator<V> iterator() {
			return valueIterator();
		}

		public int size() {
			return size;
		}

		public boolean contains(Object o) {
			return containsValue(o);
		}

		public void clear() {
			LinkedHasherMap.this.clear();
		}
	}

	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		if (this.es == null)
			this.es = new EntrySet();
		return this.es;
	}

	private EntrySet es;

	public class EntrySet implements Set<Entry<K, V>> {

		@Override
		public int size() {
			return size;
		}

		@Override
		public boolean isEmpty() {
			return size == 0;
		}

		@Override
		public boolean contains(Object o) {
			if (o instanceof Entry) {
				final Entry e = (Entry) o;
				return containsKey(e.getKey()) && OH.eq(e.getValue(), get(e.getKey()));
			}
			return false;
		}

		@Override
		public java.util.Iterator<Entry<K, V>> iterator() {
			return entryIterator();
		}

		@Override
		public Object[] toArray() {
			throw new UnsupportedOperationException();
		}

		@Override
		public <T> T[] toArray(T[] a) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean add(Entry<K, V> e) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean remove(Object o) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean containsAll(Collection<?> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean addAll(Collection<? extends Entry<K, V>> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean retainAll(Collection<?> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean removeAll(Collection<?> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void clear() {
			LinkedHasherMap.this.clear();
		}

	}

	public class KeySet implements Set<K> {

		@Override
		public int size() {
			return size;
		}

		@Override
		public boolean isEmpty() {
			return size == 0;
		}

		@Override
		public boolean contains(Object o) {
			return containsKey(o);
		}

		@Override
		public java.util.Iterator<K> iterator() {
			return keyIterator();
		}

		@Override
		public Object[] toArray() {
			throw new UnsupportedOperationException();
		}

		@Override
		public <T> T[] toArray(T[] a) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean add(K e) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean remove(Object o) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean containsAll(Collection<?> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean addAll(Collection<? extends K> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean retainAll(Collection<?> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean removeAll(Collection<?> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void clear() {
			LinkedHasherMap.this.clear();
		}

	}

}
