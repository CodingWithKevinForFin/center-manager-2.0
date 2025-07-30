/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.structs;

import java.util.Collection;
import java.util.Set;

import com.f1.base.Clearable;
import com.f1.base.IntIterable;
import com.f1.base.IntIterator;
import com.f1.base.ToStringable;
import com.f1.utils.MH;
import com.f1.utils.OH;

public class IntSet implements Set<Integer>, ToStringable, Clearable, IntIterable {

	private Node[] buckets;
	private int mask;
	private int size = 0;

	public IntSet() {
		buckets = new Node[4];
		mask = 3;
	}

	public IntSet(int bucketsCount) {
		int i = 1;
		while (i < bucketsCount)
			i <<= 1;
		buckets = new Node[i];
		mask = i - 1;
	}

	public boolean add(int key) {
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

	public boolean remove(int key) {
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
		Node bucket = buckets[hash(key)];
		while (bucket != null)
			if (bucket.key == key)
				return bucket;
			else
				bucket = bucket.next;
		return null;
	}

	public boolean contains(int key) {
		Node bucket = buckets[hash(key)];
		while (bucket != null)
			if (bucket.key == key)
				return true;
			else
				bucket = bucket.next;
		return false;
	}

	private int hash(int h) {
		h = MH.hash(h);
		return h & mask;
	}

	public static class Node implements ToStringable, Comparable<Node> {

		final private int key;
		private Node next;

		public Node(int key, Node next) {
			this.key = key;
			this.next = next;
		}

		public int getKey() {
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
			return key;
		}
	}

	public IntIterator iterator() {
		return new KeyIterator();
	}

	public class KeyIterator implements IntIterator {
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
		public Integer next() {
			return nextInt();
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		@Override
		public int nextInt() {
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

	public boolean addAllInts(Iterable<Integer> c) {
		boolean r = false;
		if (c instanceof IntIterable) {
			for (IntIterator i = ((IntIterable) c).iterator(); i.hasNext();)
				r = add(i.nextInt()) || r;
		} else {
			for (Integer i : c)
				r = add(i) || r;
		}
		return r;
	}

	@Override
	public StringBuilder toString(StringBuilder sb) {
		boolean first = true;
		sb.append('{');
		for (int n : this) {
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
		return o instanceof Integer && contains(((Integer) o).intValue());
	}

	@Override
	public Object[] toArray() {
		Object[] r = new Object[size];
		int n = 0;
		for (IntIterator i = this.iterator(); i.hasNext();)
			r[n++] = OH.valueOf(i.nextInt());
		return r;
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return null;
	}

	@Override
	public boolean add(Integer e) {
		return add(e.intValue());
	}

	@Override
	public boolean remove(Object o) {
		if (o instanceof Integer)
			return remove(((Integer) o).intValue());
		return false;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		for (Object o : c)
			if (!contains(o))
				return false;
		return true;
	}

	@Override
	public boolean addAll(Collection<? extends Integer> c) {
		boolean r = false;
		for (Integer o : c)
			r = add(o.intValue()) || r;
		return r;
	}
	public boolean addAll(IntIterator c) {
		boolean r = false;
		while (c.hasNext())
			r = add(c.nextInt()) || r;
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
		if (size == 0 && mask == 3)
			return;
		buckets = new Node[4];
		mask = 3;
		size = 0;
	}

	public int[] toIntArray() {
		if (size == 0)
			return OH.EMPTY_INT_ARRAY;
		int[] r = new int[size];
		int i = 0;
		for (int l : this)
			r[i++] = l;
		return r;
	}

}
