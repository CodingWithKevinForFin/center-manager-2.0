/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.speedlogger.impl;

import java.io.IOException;
import java.io.Writer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import com.f1.utils.FastBufferedWriter;
import com.f1.utils.IOH;

public class AutoFlushWriter2 extends Writer implements Runnable {

	final private int maxSize;
	final private long maxTimeNanos;
	private Writer writer;
	volatile private FastCharArray buf, buf2, buf3;
	volatile ShutdownThread shutdownThread;
	final private Thread thread;
	final private ReentrantLock bufferLock = new ReentrantLock(false);
	final private ReentrantLock buffer2Lock = new ReentrantLock(false);
	final private ReentrantLock notifierLock = new ReentrantLock(false);
	final private Condition notifierCondition = notifierLock.newCondition();
	final private int maxBufferSize;

	public AutoFlushWriter2(Writer writer, int maxSize, long maxTimeMs) {
		this.maxSize = maxSize;
		this.maxBufferSize = this.maxSize << 1;
		this.writer = new FastBufferedWriter(writer, this.maxSize);
		this.maxTimeNanos = maxTimeMs * 1000000L;
		this.buf = new FastCharArray(maxSize);
		this.buf2 = new FastCharArray(maxSize);
		this.buf3 = new FastCharArray(maxSize);
		thread = new Thread(this, AutoFlushWriter2.class.getSimpleName() + toString());
		thread.setDaemon(true);
		Runtime.getRuntime().addShutdownHook(shutdownThread = new ShutdownThread(thread.getName() + "-shutdown"));
	}

	public AutoFlushWriter2 start() {
		thread.start();
		return this;
	}

	private int errorCount = 0;
	volatile private boolean inFlushSync = false;

	private void flushSync(boolean force) {
		if (force) {
			this.inFlushSync = true;
			buffer2Lock.lock();
		} else {
			if (buf.isEmpty() && buf2.isEmpty())
				return;
			this.inFlushSync = true;
			if (!buffer2Lock.tryLock())
				return;
		}
		try {
			while (!buf.isEmpty() || !buf2.isEmpty()) {
				while (!buf.isEmpty() || !buf2.isEmpty()) {
					FastCharArray t = buf;
					buf = buf3;
					buf3 = buf2;
					buf2 = t;
					final FastCharArray b = buf3;
					b.lock();
					try {
						b.writeTo(writer);
					} catch (Exception e) {
						errorCount++;
						if (errorCount < 10) {
							e.printStackTrace(System.err);
							System.err.println("SVR: Dropping byte(s) due to write failure");
							if (errorCount == 9)
								System.err.println("SVR: Too many errors , no longer reporting this message.");
						}
					}
					b.reset();
					b.unlock();
				}
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
			}
		} finally {
			buffer2Lock.unlock();
			this.inFlushSync = false;
		}
	}
	@Override
	public void flush() throws IOException {
	}

	@Override
	public void close() {
		try {
			bufferLock.lock();
			flushSync(true);
			IOH.close(writer);
		} finally {
			bufferLock.unlock();
		}
		writer = null;
		if (shutdownThread != null)
			Runtime.getRuntime().removeShutdownHook(shutdownThread);
		flushAsync();
	}

	@Override
	public void run() {
		while (writer != null) {
			notifierLock.lock();
			try {
				notifierCondition.awaitNanos(maxTimeNanos);
			} catch (InterruptedException e) {
			} finally {
				notifierLock.unlock();
			}
			flushSync(false);
		}

	}

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		while (!this.buf.write(cbuf, off, len))
			;
		flushCheck();
	}

	@Override
	public AutoFlushWriter2 append(CharSequence cbuf, int off, int len) throws IOException {
		throw new UnsupportedOperationException();
	}

	private void flushCheck() throws IOException {
		if (shutdownThread == null && (!buf.isEmpty() || !buf2.isEmpty()))
			flushSync(true);
		else if (!buf.isEmpty() || !buf2.isEmpty())
			flushAsync();
	}

	private void flushAsync() {
		if (!inFlushSync) {
			this.inFlushSync = true;
			notifierLock.lock();
			notifierCondition.signal();
			notifierLock.unlock();
		}
	}

	private class ShutdownThread extends Thread {
		public ShutdownThread(String name) {
			super(name);
		}

		@Override
		public void run() {
			shutdownThread = null;
			flushSync(true);
		}
	}

}
