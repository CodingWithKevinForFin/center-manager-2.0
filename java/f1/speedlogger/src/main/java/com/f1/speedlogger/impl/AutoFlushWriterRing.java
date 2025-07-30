/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.speedlogger.impl;

import java.io.IOException;
import java.io.Writer;
import java.util.concurrent.locks.ReentrantLock;

import com.f1.utils.FastBufferedWriter;
import com.f1.utils.concurrent.FastRing;

public class AutoFlushWriterRing extends Writer implements Runnable {

	public static final long DEFAULT_AGGRESIVE_TIMEOUT_NANOS = 1 * 1000 * 1000;//5 milliseconds
	final private int maxSize;
	final private long maxTimeNanos;
	private Writer writer;
	volatile ShutdownThread shutdownThread;
	final private Thread thread;
	final private int maxBufferSize;

	private final FastRing<BufferedWriter> ring;
	private final FastRing<BufferedWriter> pool;
	private BufferedWriter[] buffer;

	public AutoFlushWriterRing(Writer writer, int maxSize, long maxTimeMs, int buffersCount) {
		this.ring = new FastRing<BufferedWriter>(buffersCount);
		this.pool = new FastRing<BufferedWriter>(buffersCount);
		this.ring.setAggresiveTimeoutNanos(DEFAULT_AGGRESIVE_TIMEOUT_NANOS);
		this.pool.setAggresiveTimeoutNanos(DEFAULT_AGGRESIVE_TIMEOUT_NANOS);
		this.maxSize = maxSize;
		this.maxBufferSize = this.maxSize << 1;
		this.writer = new FastBufferedWriter(writer, this.maxBufferSize);
		this.maxTimeNanos = maxTimeMs * 1000000L;
		this.buffer = new BufferedWriter[this.ring.getTotalCapacity() * 2];
		thread = new Thread(this, AutoFlushWriterRing.class.getSimpleName() + toString());
		thread.setDaemon(true);
		Runtime.getRuntime().addShutdownHook(shutdownThread = new ShutdownThread(thread.getName() + "-shutdown"));
	}

	public void setAggressiveTimeoutNanos(long timeout) {
		this.ring.setAggresiveTimeoutNanos(timeout);
		this.pool.setAggresiveTimeoutNanos(timeout);
	}

	public AutoFlushWriterRing start() {
		thread.start();
		return this;
	}

	private volatile boolean closed = false;
	private int errorCount = 0;
	private ReentrantLock writerLock = new ReentrantLock();

	@Override
	public void flush() throws IOException {
	}

	@Override
	public void close() {
		if (this.closed)
			return;
		this.closed = true;
		if (shutdownThread != null)
			Runtime.getRuntime().removeShutdownHook(shutdownThread);
	}

	@Override
	public void run() {
		while (!closed || this.ring.size() > 0)
			drainRing();
	}
	private void drainRing() {
		try {
			writerLock.lock();
			boolean needsFlush = false;
			for (;;) {
				int n = this.ring.poll(buffer, 0, buffer.length);
				if (n == 0) {
					BufferedWriter t = this.ring.poll(this.maxTimeNanos);
					if (t != null)
						buffer[n++] = t;
					if (n == 0)
						break;
					n += this.ring.poll(buffer, n, buffer.length);
				}
				for (int i = 0; i < n; i++) {
					BufferedWriter b = buffer[i];
					buffer[i] = null;
					try {
						b.writeTo(writer);
						needsFlush = true;
					} catch (Exception e) {
						errorCount++;
						if (errorCount < 10) {
							e.printStackTrace(System.err);
							System.err.println("SVR: Dropping " + b.getSize() + " byte(s) due to write failure");
							if (errorCount == 9)
								System.err.println("SVR: Too many errors , no longer reporting this message.");
						}
					}
					b.clear(this.maxBufferSize);
					this.pool.offer(b);
				}
			}
			if (needsFlush)
				try {
					writer.flush();
				} catch (Exception e) {
					errorCount++;
					if (errorCount < 10) {
						e.printStackTrace(System.err);
						System.err.println("SVR: Flush failed, due to write failure");
						if (errorCount == 9)
							System.err.println("SVR: Too many errors , no longer reporting this message.");
					}
				}
		} finally {
			writerLock.unlock();
		}
	}
	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		BufferedWriter b = pool.poll();
		if (b == null)
			b = new BufferedWriter(this.maxSize >> 2);
		b.write(cbuf, off, len);
		ring.put(b);
	}

	@Override
	public AutoFlushWriterRing append(CharSequence cbuf, int off, int len) throws IOException {
		BufferedWriter b = pool.poll();
		if (b == null)
			b = new BufferedWriter(this.maxSize >> 2);
		b.append(cbuf, off, len);
		ring.put(b);
		return this;
	}

	private class ShutdownThread extends Thread {
		public ShutdownThread(String name) {
			super(name);
		}

		@Override
		public void run() {
			shutdownThread = null;
			close();
			drainRing();
		}
	}

}
