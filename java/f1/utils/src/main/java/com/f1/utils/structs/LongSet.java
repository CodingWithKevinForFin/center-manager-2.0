/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.structs;

import java.util.Collection;
import java.util.Set;

import com.f1.base.Clearable;
import com.f1.base.LongIterable;
import com.f1.base.LongIterator;
import com.f1.base.ToStringable;
import com.f1.utils.LongArrayList;
import com.f1.utils.MH;
import com.f1.utils.OH;

public class LongSet implements Set<Long>, ToStringable, Clearable, LongIterable {

	public static final LongSet EMPTY = new LongSet() {
		public boolean add(long key) {
			throw new UnsupportedOperationException("empty set!");
		}
	};
	private Node[] buckets;
	private int mask;
	private int size = 0;

	public LongSet() {
		buckets = new Node[4];
		mask = 3;
	}

	public LongSet(int bucketsCount) {
		bucketsCount = 8;
		int i = 1;
		while (i < bucketsCount)
			i <<= 1;
		buckets = new Node[i];
		mask = i - 1;
	}

	/**
	 * 
	 * @param key
	 * @return true if the key was added, false if it already existed
	 */
	public boolean add(long key) {
		int h = hash(key);
		Node bucket = buckets[h];
		while (bucket != null)
			if (bucket.key == key) {
				return false;
			} else
				bucket = bucket.next;
		buckets[h] = new Node(key, buckets[h]);
		if (++size >= buckets.length)
			rehash(size * 2);
		return true;
	}

	public boolean remove(long key) {
		if (size == 0)
			return false;
		int h = hash(key);
		Node bucket = buckets[h], last = null;
		while (bucket != null)
			if (bucket.key == key) {
				if (last == null)
					buckets[h] = bucket.next;
				else
					last.next = bucket.next;
				size--;
				return true;
			} else
				bucket = (last = bucket).next;
		return false;
	}

	private void rehash(int newSize) {
		Node[] old = buckets;
		buckets = new Node[newSize];
		mask = newSize - 1;
		if (size > 0)
			for (Node head : old) {
				while (head != null) {
					Node next = head.next;
					int newHash = hash(head.key);
					head.next = buckets[newHash];
					buckets[newHash] = head;
					head = next;
				}

			}
	}

	public Node getNode(int key) {
		if (size == 0)
			return null;
		Node bucket = buckets[hash(key)];
		while (bucket != null)
			if (bucket.key == key)
				return bucket;
			else
				bucket = bucket.next;
		return null;
	}

	public boolean contains(long key) {
		if (size == 0)
			return false;
		Node bucket = buckets[hash(key)];
		while (bucket != null)
			if (bucket.key == key)
				return true;
			else
				bucket = bucket.next;
		return false;
	}

	private int hash(long key) {
		key = MH.hash(key);
		return (int) (key & mask);
	}

	public static class Node implements ToStringable, Comparable<Node> {

		final private long key;
		private Node next;

		public Node(long key, Node next) {
			this.key = key;
			this.next = next;
		}

		public long getKey() {
			return key;
		}

		@Override
		public StringBuilder toString(StringBuilder sb) {
			return sb.append(key);
		}

		@Override
		public int compareTo(Node o) {
			return OH.compare(this.key, o.key);
		}

		@Override
		public boolean equals(Object o) {
			return o != null && o.getClass() == this.getClass() && ((Node) o).key == key;
		}

		@Override
		public int hashCode() {
			return OH.hashCode(key);
		}
	}

	@Override
	public LongIterator iterator() {
		return new KeyIterator();
	}

	public class KeyIterator implements LongIterator {
		private int location = 0;
		private Node node;

		public KeyIterator() {
			while (node == null && location < buckets.length)
				node = buckets[location++];
		}

		@Override
		public boolean hasNext() {
			return node != null;
		}

		@Override
		public Long next() {
			return nextLong();
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		@Override
		public long nextLong() {
			Node r = node;
			node = node.next;
			while (node == null && location < buckets.length)
				node = buckets[location++];
			return r.key;
		}

	}

	@Override
	public int size() {
		return size;
	}

	public boolean addAll(LongSet c) {
		boolean r = false;
		LongIterator i = c.iterator();
		while (i.hasNext())
			r = add(i.nextLong()) || r;
		return r;
	}

	@Override
	public StringBuilder toString(StringBuilder sb) {
		boolean first = true;
		sb.append('{');
		for (long n : this) {
			if (first)
				first = false;
			else
				sb.append(", ");
			sb.append(n);
		}
		return sb.append('}');
	}

	@Override
	public String toString() {
		return toString(new StringBuilder()).toString();
	}

	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	@Override
	public boolean contains(Object o) {
		return o instanceof Long && contains(((Long) o).longValue());
	}

	@Override
	public Object[] toArray() {
		Long[] r = new Long[size];
		int i = 0;
		for (long l : this)
			r[i++] = l;
		return r;
	}
	public long[] toLongArray() {
		if (size == 0)
			return OH.EMPTY_LONG_ARRAY;
		long[] r = new long[size];
		int i = 0;
		for (long l : this)
			r[i++] = l;
		return r;
	}

	@Override
	public <T> T[] toArray(T[] a) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean add(Long e) {
		return add(e.longValue());
	}

	@Override
	public boolean remove(Object o) {
		if (o instanceof Long)
			return remove(((Long) o).longValue());
		return false;
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
		return true;
	}
	public boolean containsAny(LongIterable c) {
		for (LongIterator i = c.iterator(); i.hasNext();)
			if (contains(i.nextLong()))
				return true;
		return false;
	}

	@Override
	public boolean addAll(Collection<? extends Long> c) {
		boolean r = false;
		for (Long o : c)
			r = add(o.longValue()) || r;
		return r;
	}

	public boolean addAll(LongIterable c) {
		boolean r = false;
		for (LongIterator li = c.iterator(); li.hasNext();)
			r = add(li.nextLong()) || r;
		return r;
	}
	public boolean removeAll(LongIterable c) {
		boolean r = false;
		for (LongIterator li = c.iterator(); li.hasNext();)
			r = remove(li.nextLong()) || r;
		return r;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return false;
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
		if (size > 0) {
			buckets = new Node[4];
			mask = 3;
			size = 0;
		}
	}

	//returns longs only in me and not in other
	public void getNotIn(LongSet other, LongArrayList sink) {
		if (size > 0) {
			if (other.isEmpty())
				toList(sink);
			else
				for (int i = 0; i < this.buckets.length; i++)
					for (Node bucket = this.buckets[i]; bucket != null; bucket = bucket.next)
						if (!other.contains(bucket.key))
							sink.add(bucket.key);
		}
	}

	private void toList(LongArrayList sink) {
		sink.ensureCapacity(sink.size() + size);
		for (int i = 0; i < this.buckets.length; i++)
			for (Node bucket = this.buckets[i]; bucket != null; bucket = bucket.next)
				sink.add(bucket.key);
	}

}
