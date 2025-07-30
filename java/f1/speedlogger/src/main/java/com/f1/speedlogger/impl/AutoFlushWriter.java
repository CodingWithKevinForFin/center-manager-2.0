/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.speedlogger.impl;

import java.io.IOException;
import java.io.Writer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import com.f1.utils.IOH;

public class AutoFlushWriter extends Writer implements Runnable {

	final private int maxSize;
	final private long maxTimeNanos;
	private Writer writer;
	volatile private BufferedWriter buffer;
	volatile private BufferedWriter buffer2;
	volatile ShutdownThread shutdownThread;
	final private Thread thread;
	final private ReentrantLock bufferLock = new ReentrantLock(false);
	final private ReentrantLock buffer2Lock = new ReentrantLock(false);
	final private ReentrantLock notifierLock = new ReentrantLock(false);
	final private Condition notifierCondition = notifierLock.newCondition();
	final private int maxBufferSize;

	public AutoFlushWriter(Writer writer, int maxSize, long maxTimeMs) {
		this.writer = writer;
		this.maxSize = maxSize;
		this.maxBufferSize = this.maxSize << 1;
		this.maxTimeNanos = maxTimeMs * 1000000L;
		this.buffer = new BufferedWriter(maxSize * 2);
		this.buffer2 = new BufferedWriter(maxSize * 2);
		thread = new Thread(this, AutoFlushWriter.class.getSimpleName() + toString());
		thread.setDaemon(true);
		Runtime.getRuntime().addShutdownHook(shutdownThread = new ShutdownThread(thread.getName() + "-shutdown"));
	}

	public AutoFlushWriter start() {
		thread.start();
		return this;
	}

	private int errorCount = 0;
	volatile private boolean inFlushSync = false;

	private void flushSync(boolean force) {
		if (force) {
			buffer2Lock.lock();
		} else {
			if (buffer.getSize() == 0)
				return;
			if (!buffer2Lock.tryLock())
				return;
		}
		try {
			this.inFlushSync = true;
			while (buffer.getSize() > 0) {
				while (buffer.getSize() > 0) {
					final BufferedWriter b = buffer;
					bufferLock.lock();
					buffer = buffer2;
					bufferLock.unlock();
					buffer2 = b;
					try {
						b.writeTo(writer);
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
			IOH.close(buffer);
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
		try {
			bufferLock.lock();
			buffer.write(cbuf, off, len);
		} finally {
			bufferLock.unlock();
		}
		flushCheck();
	}

	@Override
	public AutoFlushWriter append(CharSequence cbuf, int off, int len) throws IOException {
		try {
			bufferLock.lock();
			buffer.append(cbuf, off, len);
		} finally {
			bufferLock.unlock();
		}
		flushCheck();
		return this;
	}

	private void flushCheck() throws IOException {
		if (shutdownThread == null && buffer.getSize() > 0)
			flushSync(true);
		else if (buffer.getSize() > maxSize)
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
