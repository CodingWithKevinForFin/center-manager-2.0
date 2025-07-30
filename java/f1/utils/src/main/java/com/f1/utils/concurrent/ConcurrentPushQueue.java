/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.concurrent;

import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

final public class ConcurrentPushQueue<NODE extends ConcurrentPushQueueNode> {
	private static final AtomicReferenceFieldUpdater<ConcurrentPushQueue, ConcurrentPushQueueNode> tailFieldUpdater;
	private static final AtomicReferenceFieldUpdater<ConcurrentPushQueue, ConcurrentPushQueueNode> headFieldUpdater;
	private static final AtomicReferenceFieldUpdater<ConcurrentPushQueueNode, ConcurrentPushQueueNode> nextFieldUpdater;
	private volatile ConcurrentPushQueueNode head, tail;
	static {
		tailFieldUpdater = AtomicReferenceFieldUpdater.newUpdater(ConcurrentPushQueue.class, ConcurrentPushQueueNode.class, "tail");
		nextFieldUpdater = AtomicReferenceFieldUpdater.newUpdater(ConcurrentPushQueueNode.class, ConcurrentPushQueueNode.class, "next");
		headFieldUpdater = AtomicReferenceFieldUpdater.newUpdater(ConcurrentPushQueue.class, ConcurrentPushQueueNode.class, "head");

	}

	public int getSize(int maxCount) {
		int cnt = 0;
		for (ConcurrentPushQueueNode n = head; n != null; n = n.next)
			if (++cnt >= maxCount)
				return maxCount;

		return cnt;
	}

	public ConcurrentPushQueue() {
		head = tail = null;
	}

	public void put(NODE newNode) {
		if (headFieldUpdater.compareAndSet(this, null, newNode)) {
			tailFieldUpdater.compareAndSet(this, null, newNode);
			return;
		}
		ConcurrentPushQueueNode oldTail;
		while (!tailFieldUpdater.compareAndSet(this, oldTail = this.tail, newNode))
			;
		if (oldTail != null)
			nextFieldUpdater.compareAndSet(oldTail, null, newNode);
	}

	public boolean isEmpty() {
		return head == null;
	}

	public NODE get() {
		if (head == null)
			return null;
		ConcurrentPushQueueNode n = (ConcurrentPushQueueNode) head;
		head = n.next;
		return (NODE) n;
	}

	public NODE peek() {
		return (NODE) head;
	}

	static ConcurrentPushQueue q = new ConcurrentPushQueue();

	public static void main(String a[]) {
		while (true) {
			System.out.println("starting");
			for (int i = 0; i < 10; i++) {
				new Thread() {
					public void run() {
						for (int i = 0; i < 100000; i++)
							q.put(new ConcurrentPushQueueNode());

					}

				}.start();
			}
			int count = 0;
			while (true) {
				ConcurrentPushQueueNode n = q.get();
				if (n != null)
					count++;
				if ((count % 10000) == 0)
					System.out.println(count);
				if (count == 1000000)
					break;
			}
		}
	}

}
