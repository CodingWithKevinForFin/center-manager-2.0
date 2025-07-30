/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.structs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import com.f1.base.Clearable;
import com.f1.base.IntIterable;
import com.f1.base.IntIterator;
import com.f1.base.IterableAndSize;
import com.f1.base.ToStringable;
import com.f1.utils.DetailedException;
import com.f1.utils.EH;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.SH;

public class IntKeyMap<V> implements IterableAndSize<IntKeyMap.Node<V>>, ToStringable, Clearable {

	private final class Keys implements IntIterable {
		@Override
		public IntIterator iterator() {
			return keyIterator();
		}

		@Override
		public String toString() {
			return SH.join(',', this);
		}

		@Override
		public int size() {
			return IntKeyMap.this.size();
		}
	}

	public static final IntKeyMap EMPTY = new IntKeyMap<Object>() {
		@Override
		public Object put(int key, Object value) {
			throw new UnsupportedOperationException("empty map!");
		}
	};
	private Node<V>[] buckets;
	private int threshold;
	private int mask;
	private int size = 0;
	private Keys keys = null;

	public IntKeyMap() {
		buckets = new Node[8];
		this.threshold = buckets.length >> 1;
		mask = 7;
	}
	public IntKeyMap(int bucketsCount) {
		int i = (int) MH.clip(MH.getPowerOfTwoUpper(bucketsCount) << 2, 4, 1 << 30);
		buckets = new Node[i];
		this.threshold = buckets.length >> 1;
		mask = i - 1;
	}
	public IntKeyMap(IntKeyMap<V> other) {
		buckets = new Node[other.buckets.length];
		mask = other.mask;
		this.threshold = other.threshold;
		for (int i = 0; i < buckets.length; i++) {
			Node<V> obucket = other.buckets[i];
			if (obucket != null) {
				Node<V> b = this.buckets[i] = newNode(obucket.key, obucket.value, obucket.next);
				while (b.next != null)
					b = (b.next = newNode(b.next.key, b.next.value, b.next.next));
			}
		}
		this.size = other.size;
	}

	public void ensureCapacity(int size) {
		int i = (int) MH.clip(MH.getPowerOfTwoUpper(size) << 2, 4, 1 << 30);
		if (i > buckets.length)
			rehash(i);
	}

	public V put(int key, V value) {
		int h = hash(key);
		Node<V> bucket = buckets[h];
		while (bucket != null)
			if (bucket.key == key) {
				V old = bucket.value;
				bucket.value = value;
				return old;
			} else
				bucket = bucket.next;
		buckets[h] = newNode(key, value, buckets[h]);
		if (++size >= threshold)
			rehash(buckets.length << 2);
		return null;
	}

	public V remove(int key) {
		int h = hash(key);
		Node<V> bucket = buckets[h], last = null;
		while (bucket != null)
			if (bucket.key == key) {
				V r = bucket.value;
				if (last == null)
					buckets[h] = bucket.next;
				else
					last.next = bucket.next;
				size--;
				sendToPool(bucket);
				return r;
			} else
				bucket = (last = bucket).next;
		return null;
	}

	private void rehash(int newSize) {
		this.threshold = newSize >> 1;
		if (this.threshold <= 0) {
			this.threshold = Integer.MAX_VALUE;
			newSize = 1 << 30;
		}
		Node<V>[] old = buckets;
		buckets = new Node[newSize];
		mask = newSize - 1;
		if (size > 0)
			for (Node<V> head : old) {
				while (head != null) {
					Node<V> next = head.next;
					int newHash = hash(head.key);
					head.next = buckets[newHash];
					buckets[newHash] = head;
					head = next;
				}

			}
	}
	public Node<V> getNode(int key) {
		for (Node<V> bucket = buckets[hash(key)]; bucket != null; bucket = bucket.next)
			if (bucket.key == key)
				return bucket;
		return null;
	}
	public boolean containsKey(int key) {
		for (Node<V> bucket = buckets[hash(key)]; bucket != null; bucket = bucket.next)
			if (bucket.key == key)
				return true;
		return false;
	}

	public V get(Integer key) {
		return key == null ? null : get(key.intValue());
	}
	public V get(int key) {
		Node<V> bucket = buckets[hash(key)];
		while (bucket != null)
			if (bucket.key == key)
				return bucket.value;
			else
				bucket = bucket.next;
		return null;
	}
	public V getOrThrow(int key) {
		Node<V> bucket = buckets[hash(key)];
		while (bucket != null)
			if (bucket.key == key)
				return bucket.value;
			else
				bucket = bucket.next;
		throw new DetailedException("key not found: " + key).set("key", key).set("permissible keys", this.keys());
	}

	public Node<V> getNodeOrCreate(int key) {
		int h = hash(key);
		Node<V> bucket = buckets[h];
		while (bucket != null)
			if (bucket.key == key)
				return bucket;
			else
				bucket = bucket.next;

		Node<V> r = buckets[h] = newNode(key, null, buckets[h]);
		if (++size >= threshold)
			rehash(buckets.length << 2);
		return r;
	}

	private int hash(int h) {
		h = MH.hash(h);
		return h & mask;
	}

	public int getBucketsUsed() {
		int cnt = 0;
		for (int i = 0; i < buckets.length; i++)
			if (buckets[i] != null)
				cnt++;
		return cnt;
	}

	public static class Node<V> implements ToStringable, Comparable<Node<V>>, Map.Entry<Integer, V> {

		private int key;
		private Node<V> next;
		private V value;

		public Node(int key, V value, Node<V> next) {
			this.key = key;
			this.value = value;
			this.next = next;
		}

		@Override
		public V getValue() {
			return value;
		}

		public int getIntKey() {
			return key;
		}

		@Override
		public Integer getKey() {
			return key;
		}

		@Override
		public V setValue(V value) {
			V r = this.value;
			this.value = value;
			return r;
		}

		@Override
		public String toString() {
			return key + "=" + value;
		}
		@Override
		public StringBuilder toString(StringBuilder sb) {
			sb.append(key).append('=');
			return SH.s(value, sb);
		}

		@Override
		public int compareTo(Node<V> o) {
			return OH.compare(this.key, o.key);
		}

		@Override
		public boolean equals(Object o) {
			return o != null && o.getClass() == this.getClass() && compareTo((Node<V>) o) == 0;
		}

		@Override
		public int hashCode() {
			return OH.hashCode(key);
		}
	}

	public Iterator<V> valuesIterator() {
		return new MapEntryValuesIterator<V>(iterator());
	}

	@Override
	public Iterator<Node<V>> iterator() {
		return new NodeIterator();
	}

	public IntIterator keyIterator() {
		return new KeyIterator();
	}

	public class NodeIterator implements Iterator<Node<V>> {
		private int location = 0;
		private Node<V> node;

		public NodeIterator() {
			while (node == null && location < buckets.length)
				node = buckets[location++];

		}

		public void reset() {
			node = null;
			location = 0;
			while (node == null && location < buckets.length)
				node = buckets[location++];
		}

		@Override
		public boolean hasNext() {
			return node != null;
		}

		@Override
		public Node<V> next() throws NoSuchElementException {
			Node<V> r = node;
			node = node.next;
			while (node == null && location < buckets.length)
				node = buckets[location++];
			return r;

		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

	}

	public class KeyIterator implements IntIterator {
		private int location = 0;
		private Node<V> node;

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
			Node<V> r = node;
			node = node.next;
			while (node == null && location < buckets.length)
				node = buckets[location++];
			return r.key;
		}

	}

	public int size() {
		return size;
	}

	@Override
	public StringBuilder toString(StringBuilder sb) {
		boolean first = true;
		sb.append('{');
		for (Node<V> n : this) {
			if (first)
				first = false;
			else
				sb.append(", ");
			n.toString(sb);
		}
		return sb.append('}');
	}

	@Override
	public String toString() {
		return size == 0 ? "{}" : toString(new StringBuilder()).toString();
	}

	@Override
	public void clear() {
		if (size > 0) {
			int pos = 0;
			while (pos < buckets.length && !isPoolFull() && size > 0) {
				Node<V> b = buckets[pos];
				while (b != null) {
					Node<V> n = b.next;
					size--;
					if (!sendToPool(b))
						break;
					b = n;
				}
				buckets[pos++] = null;
			}
			if (buckets.length > MAX_CLEAR_BUCKET_SIZE) {
				buckets = new Node[MAX_CLEAR_BUCKET_SIZE];
				mask = MAX_CLEAR_BUCKET_SIZE - 1;
				this.threshold = buckets.length >> 1;
			} else if (size > 0)
				while (pos < buckets.length)
					buckets[pos++] = null;
			size = 0;
		}
	}

	public Map<Integer, V> fill(Map<Integer, V> sink) {
		for (Node<V> n : this)
			sink.put(n.key, n.value);
		return sink;
	}
	public IterableAndSize<V> values() {
		return new MapEntryValuesIterator.Iterable<V>(this);
	}

	public void addAll(IntKeyMap<V> types) {
		if (types.size() > 0)
			for (Node<V> v : types)
				put(v.key, v.value);
	}

	public IntIterable keys() {
		if (keys == null)
			keys = new Keys();
		return keys;
	}

	public int getBucketsCount() {
		return this.buckets.length;
	}

	public int[] getKeys() {
		int[] r = new int[size];
		int pos = 0;
		for (Node n : this.buckets)
			for (; n != null; n = n.next)
				r[pos++] = n.key;
		OH.assertEq(pos, size);
		return r;
	}

	private static final int MAX_POOL_SIZE = 1000;
	private static final int MAX_CLEAR_BUCKET_SIZE = 128;
	private Node pool;
	private int poolSize;

	private Node<V> newNode(int key, Object value, Node next) {
		if (poolSize == 0)
			return new Node(key, value, next);
		Node r = pool;
		pool = r.next;
		r.key = key;
		r.value = value;
		r.next = next;
		poolSize--;
		return r;
	}
	private boolean sendToPool(Node n) {
		n.value = null;
		if (isPoolFull())
			return false;
		n.next = pool;
		pool = n;
		poolSize++;
		return true;
	}
	private boolean isPoolFull() {
		return poolSize >= MAX_POOL_SIZE;
	}
	public int[] getBucketSizes() {
		int[] values = new int[this.buckets.length];
		for (int pos = 0, cnt = 0; pos < this.buckets.length; values[pos++] = cnt, cnt = 0)
			for (Node<V> i = this.buckets[pos]; i != null; i = i.next)
				cnt++;
		return values;
	}

	static public StringBuilder diagnose(IntKeyMap<?> map, StringBuilder sink) {
		int[] cnt = map.getBucketSizes();
		return sink.append("size=").append(map.size()).append(", stdev=").append((float) MH.stdev(cnt)).append(" cnt=").append(cnt.length).append(" max=").append(MH.maxi(cnt));
	}
	static public String diagnose(IntKeyMap<?> map) {
		return diagnose(map, new StringBuilder()).toString();
	}
	public boolean isEmpty() {
		return size() == 0;
	}

	public void putOrThrow(int key, V value) {
		int h = hash(key);
		Node<V> bucket = buckets[h];
		while (bucket != null)
			if (bucket.key == key) {
				throw new DetailedException("key already exists: " + key).set("key", key).set("existing", bucket.value).set("supplied", value);
			} else
				bucket = bucket.next;
		buckets[h] = newNode(key, value, buckets[h]);
		if (++size >= threshold)
			rehash(buckets.length << 2);
	}

	public V removeOrThrow(int key) {
		int h = hash(key);
		Node<V> bucket = buckets[h], last = null;
		while (bucket != null)
			if (bucket.key == key) {
				V r = bucket.value;
				if (last == null)
					buckets[h] = bucket.next;
				else
					last.next = bucket.next;
				size--;
				return r;
			} else
				bucket = (last = bucket).next;
		throw new DetailedException("key not found for removal: " + key).set("key", key).set("permissible keys", this.keys());
	}

	public V updateOrThrow(int key, V value) {
		Node<V> bucket = buckets[hash(key)];
		while (bucket != null)
			if (bucket.key == key) {
				V old = bucket.value;
				bucket.value = value;
				return old;
			} else
				bucket = bucket.next;
		throw new DetailedException("key not found for update: " + key).set("key", key).set("permissible keys", this.keys());
	}

	@Override
	public IntKeyMap<V> clone() {
		return new IntKeyMap<V>(this);
	}
	public long getMemorySize() {
		long r = 12 + (2 * EH.ADDRESS_SIZE);
		r += (getBucketsCount() * EH.ADDRESS_SIZE);
		r += (size() * (EH.ESTIMATED_GC_OVERHEAD + EH.ADDRESS_SIZE * 4L));
		return r;
	}
	public List<V> toList() {
		int s = size;
		List<V> r = new ArrayList<V>(s);
		for (Node<V> i : this.buckets) {
			while (i != null) {
				r.add(i.getValue());
				i = i.next;
			}
		}
		return r;
	}
}
