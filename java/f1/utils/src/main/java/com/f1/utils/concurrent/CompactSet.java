package com.f1.utils.concurrent;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import com.f1.base.ToStringable;
import com.f1.utils.EmptyIterator;
import com.f1.utils.SingletonIterator;

/**
 * This class is designed to minimize memory overhead, especially for smaller (ex: single or no entries).<BR>
 * Implementation details: Three different modes:<BR>
 * (a) TYPE_SINGLE for when size<=1: values is just a pointer to the value<BR>
 * (b) TYPE_ARRAY for when size<=SIMPLE_ARAAY_SIZE: values is a simple array of elements. All lookups require a scan over the array. Elements are always compacted towards the front
 * of the array<BR>
 * (c) TYPE_BUCKETS for when size>SIMPLE_ARRAY_SIZE: values is an array of hash buckets<BR>
 * Empty buckets are null, buckets with one element references the value directly, otherwise for collisions an array of values is used.
 * <P>
 * Note: nulls use the place-holder NULL internally
 */
public class CompactSet<V> implements Set<V>, ToStringable {

	private static final byte TYPE_SINGLE = 0;//when size<=1: values is just a pointer to the value
	private static final byte TYPE_ARRAY = 1;//when size<=SIMPLE_ARAAY_SIZE: values is a simple array of elements
	private static final byte TYPE_BUCKETS = 2;//when size>SIMPLE_ARRAY_SIZE: values is an array of hash buckets

	private static final int SIMPLE_ARRAY_SIZE = 6;
	private static final int LOAD_FACTOR = 2;//When to example bucket count
	private static final int BUCKET_SIZE = 3;//Each Bucket is an array, this is the default size of bucket
	private static Object NULL = new Object();

	private Object values;
	private int size;
	private byte type = TYPE_SINGLE;

	static private Object handleNull(Object v) {
		return v == NULL ? null : v;
	}
	@Override
	public boolean add(V e) {
		if (e instanceof Object[])
			throw new IllegalArgumentException("Can not use Object arrays in CompactHashset");
		if (e == null)
			e = (V) NULL;
		switch (type) {
			case TYPE_SINGLE:
				if (size == 0) {
					values = e;
					size++;
				} else if (eq(values, e))
					return false;
				else {
					Object[] array = new Object[SIMPLE_ARRAY_SIZE];
					this.type = TYPE_ARRAY;
					array[0] = values;
					array[1] = e;
					this.values = array;
					size++;
				}
				return true;
			case TYPE_ARRAY:
				Object[] array = (Object[]) this.values;
				for (int i = 0; i < size; i++)
					if (eq(array[i], e))
						return false;
				if (size < array.length) {
					array[size++] = e;
				} else {
					this.type = TYPE_BUCKETS;
					Object[] buckets = new Object[32];
					this.values = buckets;
					for (Object i : array)
						addToBucket(buckets, i);
					addToBucket(buckets, e);
					size++;
				}
				return true;
			case TYPE_BUCKETS:
				Object[] buckets = (Object[]) this.values;
				if (!addToBucket(buckets, e))
					return false;
				if (++size >= (buckets.length / LOAD_FACTOR))
					rehash(buckets.length << 1);
				return true;
			default:
				throw new IllegalStateException();
		}
	}
	private void rehash(int size) {
		Object[] oldBuckets = (Object[]) this.values;
		Object[] target = new Object[size];
		this.values = target;
		for (Object head : oldBuckets) {
			if (head != null) {
				if (head instanceof Object[]) {
					for (Object o : (Object[]) head)
						if (o == null)
							break;
						else
							addToBucket(target, o);
				} else
					addToBucket(target, head);
			}
		}
	}
	private boolean addToBucket(Object[] buckets, Object e) {
		int pos = hash(e) & (buckets.length - 1);
		Object bucket = buckets[pos];
		if (bucket == null) {
			buckets[pos] = e;
			return true;
		} else if (bucket instanceof Object[]) {
			Object[] entries = (Object[]) bucket;
			int len = entries.length;
			for (int i = 0; i < len; i++) {
				Object en = entries[i];
				if (en == null) {
					entries[i] = e;
					return true;
				} else if (eq(en, e)) {
					return false;
				}
			}
			entries = Arrays.copyOf(entries, len + len);
			entries[len] = e;
			buckets[pos] = entries;
			return true;
		} else {
			if (eq(bucket, e))
				return false;
			Object[] b = new Object[BUCKET_SIZE];
			buckets[pos] = b;
			b[0] = bucket;
			b[1] = e;
			return true;
		}
	}
	@Override
	public boolean contains(Object o) {
		if (o == null)
			o = NULL;
		switch (type) {
			case TYPE_SINGLE:
				return eq(values, o);
			case TYPE_ARRAY:
				Object[] array = (Object[]) this.values;
				for (int i = 0; i < size; i++)
					if (eq(array[i], o))
						return true;
				return false;
			case TYPE_BUCKETS:
				Object[] buckets = (Object[]) this.values;
				int pos = hash(o) & (buckets.length - 1);
				Object bucket = buckets[pos];
				if (bucket == null)
					return false;
				if (eq(bucket, o))
					return true;
				if (bucket instanceof Object[]) {
					Object[] entries = (Object[]) bucket;
					for (Object e : entries)
						if (e == null)
							return false;
						else if (eq(e, o))
							return true;
				}
				return false;
			default:
				throw new IllegalStateException();
		}
	}
	@Override
	public Iterator<V> iterator() {
		if (size == 0)
			return EmptyIterator.INSTANCE;
		switch (type) {
			case TYPE_SINGLE:
				return new SingletonIterator(this.values);
			case TYPE_ARRAY:
				return new ArrayIterator((Object[]) this.values);
			case TYPE_BUCKETS:
				return new BucketsIterator((Object[]) this.values);
			default:
				throw new IllegalStateException();

		}
	}

	@Override
	public boolean remove(Object o) {
		if (size == 0)
			return false;
		if (o == null)
			o = NULL;
		switch (type) {
			case TYPE_SINGLE:
				if (values != null && eq(values, o)) {
					values = null;
					size--;
					return true;
				}
				return false;
			case TYPE_ARRAY:
				Object[] array = (Object[]) this.values;
				for (int i = 0; i < size; i++)
					if (eq(array[i], o)) {
						size--;
						array[i] = array[size];
						array[size] = null;
						if (size == 1) {
							this.values = array[0];
							type = TYPE_SINGLE;
						}
						return true;
					}
				return false;
			case TYPE_BUCKETS:
				Object[] buckets = (Object[]) this.values;
				int pos = hash(o) & (buckets.length - 1);
				Object bucket = buckets[pos];
				if (bucket == null)
					return false;
				if (eq(bucket, o)) {
					buckets[pos] = null;
					size--;
					compactBuckets();
					return true;
				} else if (bucket instanceof Object[]) {
					Object[] entries = (Object[]) bucket;
					final int length = entries.length;
					for (int i = 0; i < length; i++) {
						Object e = entries[i];
						if (e == null)
							return false;
						else if (eq(e, o)) {
							for (int n = i;; n++) {
								if (n + 1 == length || entries[n + 1] == null) {
									if (n == 0) {
										buckets[pos] = entries[1];
									} else {
										entries[i] = entries[n];
										entries[n] = null;
									}
									size--;
									compactBuckets();
									return true;
								}
							}
						}
					}
				}
				return false;
			default:
				throw new IllegalStateException();
		}
	}
	private void compactBuckets() {
		if (size == 0) {
			this.type = TYPE_SINGLE;
			this.values = null;
		}

	}
	@Override
	public int size() {
		return size;
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
			return "[]";
		return toString(new StringBuilder()).toString();
	}

	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	@Override
	public Object[] toArray() {
		Object[] r = new Object[size];
		int i = 0;
		for (V l : this)
			r[i++] = l;
		return r;
	}

	@Override
	public <T> T[] toArray(T[] a) {
		if (a.length < size)
			a = Arrays.copyOf(a, size);
		int i = 0;
		for (V l : this)
			a[i++] = (T) l;
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
		size = 0;
		this.type = TYPE_SINGLE;
		values = null;
	}

	public static class BucketsIterator<V> implements Iterator<V> {

		private Object[] buckets;
		private int bucketLoc = 0;
		private int entryLoc = 0;

		public BucketsIterator(Object[] values) {
			this.buckets = values;
			moveToNextFilledBucket();
		}

		private void moveToNextFilledBucket() {
			for (;;)
				if (bucketLoc == buckets.length) {
					bucketLoc = -1;
					break;
				} else if (buckets[bucketLoc] == null)
					bucketLoc++;
				else
					break;
		}

		@Override
		public boolean hasNext() {
			return bucketLoc != -1;
		}

		@Override
		public V next() {
			Object t = buckets[bucketLoc];
			if (t instanceof Object[]) {
				Object[] entries = (Object[]) t;
				Object r = entries[entryLoc++];
				if (entryLoc == entries.length || entries[entryLoc] == null) {
					bucketLoc++;
					entryLoc = 0;
					moveToNextFilledBucket();
				}
				return (V) handleNull(r);
			} else {
				bucketLoc++;
				moveToNextFilledBucket();
				return (V) handleNull(t);

			}
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

	}

	public static class ArrayIterator<V> implements Iterator<V> {

		final private Object[] values;
		private int pos;
		public ArrayIterator(Object[] values) {
			this.values = values;
			this.pos = 0;
		}

		@Override
		public boolean hasNext() {
			return pos < values.length && values[pos] != null;
		}

		@Override
		public V next() {
			return (V) handleNull(values[pos++]);
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

	}

	protected int hash(Object o) {
		return o == NULL ? 0 : o.hashCode();
	}
	protected boolean eq(Object a, Object b) {
		if (a == NULL || b == NULL)
			return a == b;
		return a.equals(b);
	}
}
