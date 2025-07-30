package com.f1.utils.concurrent;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import com.f1.utils.MH;

public final class FastRing<T> {

	public static final int NO_POSITION = -1;

	private static final long DEFAULT_AGGRESSIVE_TIMEOUT_NANOS = 1000;// 1 microsecond

	private final ReentrantLock writeLock = new ReentrantLock(true);
	private final Condition writeCond = writeLock.newCondition();
	private final ReentrantLock readLock = new ReentrantLock(true);
	private final Condition readCond = readLock.newCondition();
	private volatile int blockingWriters = 0;
	private volatile int blockingReaders = 0;
	private long aggresiveTimeoutNanos = DEFAULT_AGGRESSIVE_TIMEOUT_NANOS;
	private final AtomicLong readPos = new AtomicLong(0);
	private final AtomicLong writePos = new AtomicLong(0);
	private volatile Object[] values;
	private volatile long[] modCounts;
	private final long sizeMask;
	private final int capacity;

	private int shiftMask;
	public FastRing(int size) {
		this.capacity = (int) MH.getPowerOfTwoUpper(size);
		this.shiftMask = MH.indexOfOnlyBitSet(this.capacity);
		this.values = new Object[this.capacity];
		this.modCounts = new long[this.capacity];
		this.sizeMask = this.capacity - 1;
	}

	public static void main(String a[]) {
		System.out.println(MH.indexOfOnlyBitSet(1));
		System.out.println(MH.indexOfOnlyBitSet(2));
		System.out.println(MH.indexOfOnlyBitSet(4));
		System.out.println(MH.indexOfOnlyBitSet(8));
	}

	/**
	 * Inserts the specified element at the tail of this queue if it is possible to do so immediately without exceeding the queue's capacity, returning true upon success and false
	 * if this queue is full.
	 */
	public boolean offer(T data) {
		if (write2(data))
			notifyBlockingReaders();
		return true;
	}

	/**
	 * Inserts the specified element at the tail of this queue, waiting up to the specified wait time for space to become available if the queue is full.
	 */
	public boolean offer(T data, long timeout, TimeUnit tu) {
		return offer(data, tu.toNanos(timeout));
	}

	/**
	 * Inserts the specified element at the tail of this queue, waiting up to the specified wait time for space to become available if the queue is full.
	 */
	public boolean offer(final T data, final long timeoutNanos) {
		if (!aggressiveWrite(data, timeoutNanos)) {
			if (timeoutNanos <= 0)
				return false;
			try {
				long endNanos = System.nanoTime() + timeoutNanos - aggresiveTimeoutNanos;
				if (!this.writeLock.tryLock(timeoutNanos, TimeUnit.NANOSECONDS))
					return false;
				try {
					blockingWriters++;
					for (;;) {
						if (write2(data))
							break;
						final long remainingNanos = endNanos - System.nanoTime();
						if (remainingNanos <= 0)
							return false;
						this.writeCond.awaitNanos(remainingNanos);
					}
				} finally {
					if (blockingWriters > 0)
						blockingWriters--;
					this.writeLock.unlock();
				}

			} catch (InterruptedException e1) {
				return false;
			}
		}
		notifyBlockingReaders();
		return true;
	}

	/**
	 * Inserts the specified element at the tail of this queue, waiting for space to become available if the queue is full.
	 */
	public boolean put(final T data) {
		if (!aggressiveWrite(data, this.aggresiveTimeoutNanos)) {
			try {
				for (;;) {
					try {
						this.writeLock.lock();
						blockingWriters++;
						if (write2(data))
							break;
						this.writeCond.await();
					} finally {
						if (blockingWriters > 0)
							blockingWriters--;
						this.writeLock.unlock();
					}
					if (aggressiveWrite(data, this.aggresiveTimeoutNanos))
						break;
				}

			} catch (InterruptedException e1) {
				return false;
			}
		}
		notifyBlockingReaders();
		return true;
	}

	/**
	 * Retrieves and removes the head of this queue into the start index of sink, following element into start+1 index of sink, etc, or returns 0 if this queue is empty, otherwise
	 * elements drained to sink
	 */
	public int poll(T sink[], int start, int end) {
		int r = start;
		while (r < end) {
			T t = read2();
			if (t == null)
				break;
			sink[r++] = t;
		}
		if (r > start)
			notifyBlockingWriters();
		return r - start;
	}

	/**
	 * Retrieves and removes the head of this queue, or returns null if this queue is empty
	 */
	public T poll() {
		T r = read2();
		if (r != null)
			notifyBlockingWriters();
		return r;
	}
	/**
	 * /** Retrieves and removes the head of this queue, waiting up to the specified wait time if necessary for an element to become available
	 */
	public T poll(long timeout, TimeUnit tu) {
		return poll(tu.toNanos(timeout));
	}
	/**
	 * Retrieves and removes the head of this queue, waiting up to the specified wait time if necessary for an element to become available
	 */
	public T poll(final long timeoutNanos) {
		T r = aggressiveRead(timeoutNanos);
		if (r == null) {
			if (timeoutNanos <= 0)
				return null;
			try {
				final long endNanos = System.nanoTime() + timeoutNanos;
				if (!this.readLock.tryLock(timeoutNanos, TimeUnit.NANOSECONDS))
					return null;
				try {
					blockingReaders++;
					for (;;) {
						r = read2();
						if (r != null)
							break;
						final long remainingNanos = endNanos - System.nanoTime();
						if (remainingNanos <= 0)
							return null;
						this.readCond.awaitNanos(remainingNanos);
					}
				} finally {
					if (blockingReaders > 0)
						blockingReaders--;
					this.readLock.unlock();
				}

			} catch (InterruptedException e1) {
				return null;
			}
		}
		notifyBlockingWriters();
		return r;
	}

	/**
	 * Retrieves and removes the head of this queue, waiting if necessary until an element becomes available.
	 */
	public T take() {
		T r = aggressiveRead(this.aggresiveTimeoutNanos);
		if (r == null) {
			try {
				for (;;) {
					try {
						this.readLock.lock();
						blockingReaders++;
						r = read2();
						if (r != null)
							break;
						this.readCond.await();
					} finally {
						if (blockingReaders > 0)
							blockingReaders--;
						this.readLock.unlock();
					}
					r = aggressiveRead(this.aggresiveTimeoutNanos);
					if (r != null)
						break;
				}
			} catch (InterruptedException e1) {
				return null;
			}
		}
		notifyBlockingWriters();
		return r;
	}

	public long getCountRemoved() {
		return this.readPos.get();
	}
	public long getCountAdded() {
		return this.writePos.get();
	}

	public long getAggresiveTimeoutNanos() {
		return aggresiveTimeoutNanos;
	}

	public void setAggresiveTimeoutNanos(long aggresiveTimeoutNanos) {
		this.aggresiveTimeoutNanos = aggresiveTimeoutNanos;
	}

	private T read2() {

		long i;
		for (;;) {
			long limit = writePos.get();
			if ((i = readPos.get()) >= limit)
				return null;
			if (readPos.compareAndSet(i, i + 1))
				break;
		}
		int pos = (int) (i & sizeMask);
		for (;;) {
			Object r = values[pos];
			if (r != null) {
				values[pos] = null;
				this.modCounts[pos]++;
				return (T) r;
			}
		}
	}

	private boolean write2(T value) {
		long i;
		for (;;) {
			i = writePos.get();
			int pos = (int) (i & sizeMask);
			if (this.modCounts[pos] != (i >> shiftMask))
				return false;
			long limit = readPos.get() + capacity;
			if (i >= limit)
				return false;

			if (writePos.compareAndSet(i, i + 1)) {
				values[pos] = value;
				return true;
			}
		}
	}
	private boolean aggressiveWrite(T data, long nanos) {
		if (write2(data))
			return true;
		nanos = Math.min(nanos, this.aggresiveTimeoutNanos);
		if (nanos > 0 && blockingWriters == 0)
			for (final long endTime = nanos + System.nanoTime(); System.nanoTime() < endTime;)
				for (int i = 0; i < 1000; i++)
					if (write2(data))
						return true;
		return false;
	}
	private T aggressiveRead(long nanos) {
		T r = read2();
		if (r != null)
			return r;
		nanos = Math.min(nanos, this.aggresiveTimeoutNanos);
		if (nanos > 0 && blockingReaders == 0)
			for (final long endTime = nanos + System.nanoTime(); System.nanoTime() < endTime;) {
				for (int i = 0; i < 1000; i++) {
					r = read2();
					if (r != null)
						return r;
				}
			}
		return null;
	}

	private void notifyBlockingWriters() {
		if (blockingWriters > 0) {
			this.writeLock.lock();
			blockingWriters = 0;
			this.writeCond.signalAll();
			this.writeLock.unlock();
		}
	}
	private void notifyBlockingReaders() {
		if (blockingReaders > 0) {
			this.readLock.lock();
			blockingReaders = 0;
			this.readCond.signalAll();
			this.readLock.unlock();
		}
	}

	public int size() {
		return (int) -(this.getCountRemoved() - this.getCountAdded());
	}

	public int getTotalCapacity() {
		return this.capacity;
	}
}
