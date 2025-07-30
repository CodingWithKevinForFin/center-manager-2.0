/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.structs;

import java.util.Iterator;
import java.util.NoSuchElementException;

import com.f1.base.Clearable;

public class ByteKeyMap<V> implements Iterable<ByteKeyMap.Node<V>>, Clearable {

	private Node<V>[] buckets;
	private int mask;
	private int size = 0;

	public ByteKeyMap() {
		buckets = new Node[4];
		mask = 3;
	}

	public void put(byte key, V value) {
		int h = hash(key);
		Node<V> bucket = buckets[h];
		while (bucket != null)
			if (bucket.key == key) {
				bucket.value = value;
				return;
			} else
				bucket = bucket.next;
		buckets[h] = new Node<V>(key, value, buckets[h]);
		if (++size >= buckets.length)
			rehash(size * 2);
	}

	public V remove(byte key) {
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
		return null;
	}

	private void rehash(int newSize) {
		Node<V>[] old = buckets;
		buckets = new Node[newSize];
		mask = newSize - 1;
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

	public Node<V> getNode(byte key) {
		Node<V> bucket = buckets[hash(key)];
		while (bucket != null)
			if (bucket.key == key)
				return bucket;
			else
				bucket = bucket.next;
		return null;
	}

	public V get(byte key) {
		Node<V> bucket = buckets[hash(key)];
		while (bucket != null)
			if (bucket.key == key)
				return bucket.value;
			else
				bucket = bucket.next;
		return null;
	}

	public Node<V> getNodeOrCreate(byte key) {
		int h = hash(key);
		Node<V> bucket = buckets[h];
		while (bucket != null)
			if (bucket.key == key)
				return bucket;
			else
				bucket = bucket.next;

		Node<V> r = buckets[h] = new Node<V>(key, null, buckets[h]);
		if (++size >= buckets.length)
			rehash(size * 2);
		return r;
	}

	private int hash(byte key) {
		return key & mask;
	}

	public static class Node<V> {

		final private byte key;
		private Node<V> next;
		private V value;

		public Node(byte key, V value, Node next) {
			this.key = key;
			this.value = value;
			this.next = next;
		}

		public V getValue() {
			return value;
		}

		public byte getByteKey() {
			return key;
		}

		public void setValue(V value) {
			this.value = value;
		}

	}

	@Override
	public Iterator<Node<V>> iterator() {
		return new NodeIterator();
	}

	public class NodeIterator implements Iterator<Node<V>> {
		private int location = 0;
		private Node<V> node;

		public NodeIterator() {
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

	public int size() {
		return size;
	}

	public static void main(String a[]) {
		ByteKeyMap<String> m = new ByteKeyMap<String>();
		for (int i = 0; i < 255; i++) {
			System.out.println(i);
			m.put((byte) i, "" + i);
		}
		System.out.println("--------1:");
		for (int i = 0; i < 255; i++)
			System.out.println(i + ":" + m.get((byte) i));
		System.out.println("--------2:");
		for (int i = 0; i < 255; i += 2)
			System.out.println(i + ":" + m.remove((byte) i));
		System.out.println("--------3:");

		for (Node<String> s : m)
			System.out.println(s.getValue());
	}

	@Override
	public void clear() {
		buckets = new Node[4];
		mask = 3;
		size = 0;
	}
}
