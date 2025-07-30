/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.concurrent;

public class LocalObjectPool<T extends ConcurrentNode> {
	private ConcurrentStack<T> localPool = new SimpleStack<T>();
	private ConcurrentStack<T> overflow = new SimpleStack<T>();
	final private GlobalObjectPool<T> globalPool;
	final private int localSize, globalSize;

	public LocalObjectPool(GlobalObjectPool<T> globalPool) {
		this.globalPool = globalPool;
		this.localSize = globalPool.getLocalSize();
		this.globalSize = globalPool.getGlobalSize();
	}

	public void old(T o) {
		if (in)
			throw new RuntimeException();
		try {
			in = true;
			o.clear();
			if (localPool.getSize() > localSize) {
				overflow.push(o);
				if (overflow.getSize() > localSize) {
					if (globalPool.getSize() < globalSize) {
						globalPool.push(overflow);
					}
					overflow.reset(null, 0);
				}
			} else {
				localPool.push(o);
			}
		} finally {
			in = false;
		}
	}

	boolean in;

	public T nw() {
		if (in)
			throw new RuntimeException();
		try {
			in = true;
			T r = (T) localPool.pop();
			if (r != null) {
				return r;
			}
			r = (T) localPool.pop();
			if (r != null) {
				return r;
			}
			if (overflow.getSize() > 0) {
				ConcurrentStack<T> t = overflow;
				overflow = localPool;
				localPool = t;
				r = (T) localPool.pop();
				if (r == null)
					throw new NullPointerException();
				return r;
			} else
				globalPool.pop(localPool);
			r = (T) localPool.pop();
			if (r == null) {
				for (int i = 0; i < localSize / 10; i++)
					localPool.push(globalPool.nw());
				r = globalPool.nw();
			}
			return r;
		} finally {
			in = false;
		}
	}
}
