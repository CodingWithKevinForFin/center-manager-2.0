/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.concurrent;

import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import com.f1.utils.OH;

public class PushStack<NODE extends ConcurrentPushQueueNode> extends FastNode {
	private static final AtomicReferenceFieldUpdater<PushStack, ConcurrentPushQueueNode> topFieldUpdater;
	private static final AtomicReferenceFieldUpdater<ConcurrentPushQueueNode, ConcurrentPushQueueNode> nextFieldUpdater;
	private volatile NODE head, tail;
	static {
		topFieldUpdater = AtomicReferenceFieldUpdater.newUpdater(PushStack.class, ConcurrentPushQueueNode.class, "top");
		nextFieldUpdater = AtomicReferenceFieldUpdater.newUpdater(ConcurrentPushQueueNode.class, ConcurrentPushQueueNode.class, "next");

	}

	private int size = 0;
	volatile private NODE top;

	public void push(NODE node) {
		size++;
		node.next = top;
		top = node;
	}

	public void reset(NODE top, int size) {
		this.size = size;
		this.top = top;
	}

	/*
	 * public int transferToThreadSafe(PushStack<NODE> dest, int count) { NODE t
	 * = top, tail = top; if (count > size) count = size; size -= count; int r =
	 * count; while (--count > 0 && tail.next != null) tail = (NODE) tail.next;
	 * top = (NODE) tail.next;
	 * 
	 * do { tail.next = dest.top; } while (topFieldUpdater.compareAndSet(dest,
	 * tail.next, t)); return r; }
	 * 
	 * public int transferTo(PushStack<NODE> dest, int count) { NODE t = top,
	 * tail = top; if (count > size) count = size; size -= count; int r = count;
	 * while (--count > 0 && tail.next != null) tail = (NODE) tail.next; top =
	 * (NODE) tail.next; tail.next = dest.top; dest.top = t; return r; }
	 */

	/*
	 * public NODE pop(int count) { NODE r = top, tail = top; if (count > size)
	 * count = size; size -= count; while (--count > 0 && tail.next != null)
	 * tail = (NODE) tail.next; top = (NODE) tail.next; tail.next = null; return
	 * r; }
	 */

	public NODE pop() {
		if (top == null)
			return null;
		size--;
		NODE r = top;
		top = (NODE) r.next;
		r.next = null;
		return r;
	}

	public int getSize() {
		return size;
	}

	static class T extends ConcurrentPushQueueNode {
		int i;

		public T(int i) {
			this.i = i;
		}

		public String toString() {
			return "" + i + "," + next;
		}
	}

	public String toString() {
		return OH.toString(top);
	}

	public static void main(String a[]) {
		PushStack<T> ps = new PushStack<T>();
		PushStack<T> ps2 = new PushStack<T>();
		ps.push(new T(1));
		ps.push(new T(2));
		ps.push(new T(3));
		ps.push(new T(4));
		ps.push(new T(5));
		ps.push(new T(6));
		ps.push(new T(7));
		ps.push(new T(8));
		ps.push(new T(9));
		System.out.println(ps);
		System.out.println(ps2);
		System.out.println(ps);
		System.out.println(ps2);
		ps.push(new T(1));
		ps.push(new T(2));
		System.out.println(ps);
		System.out.println(ps2);

	}

	public NODE peek() {
		return top;
	}

}
