package com.f1.speedlogger.impl;

import java.io.IOException;
import java.io.Writer;
import java.util.concurrent.atomic.AtomicInteger;

public class FastCharArray {

	private static final int LOCKED = -1;
	private AtomicInteger writeLocks = new AtomicInteger();
	private Node node;
	private int suggestedCapacity;
	public FastCharArray(int suggestedCapacity) {
		this.suggestedCapacity = suggestedCapacity;
		node = new Node(suggestedCapacity, 1);
	}

	public boolean write(char[] data, int start, int length) {
		if (length == 0)
			return true;
		for (;;) {
			int current = writeLocks.get();
			if (current == LOCKED) {
				return false;
			}
			if (writeLocks.compareAndSet(current, current + 1))
				break;
		}
		boolean r = node.write2(data, start, length);
		if (writeLocks.decrementAndGet() < 0)
			throw new IllegalStateException();
		return r;
	}

	public void lock() {
		for (;;) {
			int current = writeLocks.get();
			if (current == 0 && writeLocks.compareAndSet(0, LOCKED))
				break;
			else if (current == LOCKED)
				throw new IllegalStateException();
		}
	}
	public void unlock() {
		if (!writeLocks.compareAndSet(LOCKED, 0))
			throw new IllegalStateException();
	}

	private static class Node {
		private volatile Node next;
		private AtomicInteger start = new AtomicInteger();
		private volatile char data[];
		private int depth;
		private Node back;
		private void clear() {
			this.start.set(0);
			if (this.next != null) {
				this.next.clear();
				this.back = next;
				this.next = null;
			}
		}
		public Node(int size, int depth) {
			this.data = new char[size];
			this.depth = depth;
		}

		private boolean write2(char[] data, int start, int length) {
			for (;;) {
				int s = this.start.get();
				if (s < 0) {
					while (next == null)
						;
					return next.write2(data, start, length);
				} else if (s + length > this.data.length) {
					if (depth > 2) {
						return false;
					}
					if (this.start.compareAndSet(s, -1 - s)) {
						if (back != null && this.back.data.length >= length) {
							this.next = back;
						} else
							this.next = new Node(Math.max(length, this.data.length) << 1, depth + 1);
						this.back = null;
						return this.next.write2(data, start, length);
					}
				} else if (this.start.compareAndSet(s, s + length)) {
					System.arraycopy(data, start, this.data, s, length);
					return true;
				}

			}
		}
		public void writeTo(Writer out) throws IOException {
			int len = start.get();
			if (len > 0)
				out.write(data, 0, len);
			else if (len < 0)
				out.write(data, 0, -1 - len);
		}
	}

	public boolean isEmpty() {
		return node.next == null && node.start.get() == 0;
	}
	public void writeTo(Writer out) throws IOException {
		assertLocked();
		for (Node n = node; n != null; n = n.next)
			n.writeTo(out);
	}
	public void reset() {
		assertLocked();
		this.node.clear();
	}

	private void assertLocked() {
		if (writeLocks.get() != LOCKED)
			throw new IllegalStateException();
	}

	public boolean atCapacity() {
		return node.next != null || node.start.get() > suggestedCapacity;
	}

}
