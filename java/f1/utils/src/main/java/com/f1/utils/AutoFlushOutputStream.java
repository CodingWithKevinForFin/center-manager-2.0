/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class AutoFlushOutputStream extends OutputStream implements Runnable {

	final private int maxSize;
	final private long maxTimeMs;
	private OutputStream inner;
	final private ByteArrayOutputStream buffer;
	volatile ShutdownThread shutdownThread;
	private Thread thread;

	public AutoFlushOutputStream(OutputStream inner, int maxSize, long maxTimeMs, boolean autoStart) {
		this.inner = inner;
		this.maxSize = maxSize;
		this.maxTimeMs = maxTimeMs;
		this.buffer = new ByteArrayOutputStream(maxSize * 2);
		thread = new Thread(this, AutoFlushOutputStream.class.getSimpleName() + toString());
		thread.setDaemon(true);
		Runtime.getRuntime().addShutdownHook(shutdownThread = new ShutdownThread(thread.getName() + "-shutdown"));
		if (autoStart)
			start();
	}

	public void start() {
		thread.start();
	}

	public void flushSync() throws IOException {
		if (buffer == null || buffer.size() == 0)
			return;
		synchronized (buffer) {
			if (inner != null)
				buffer.writeTo(inner);
			buffer.reset();
		}
		if (inner != null)
			inner.flush();
	}

	@Override
	public void flush() throws IOException {
		flushSync();
	}

	@Override
	public void close() {
		synchronized (buffer) {
			try {
				flushSync();
			} catch (IOException e) {
			}
			try {
				if (inner != null)
					inner.close();
			} catch (IOException e) {
			}
			try {
				buffer.close();
			} catch (IOException e) {
			}
		}
		inner = null;
		if (shutdownThread != null)
			Runtime.getRuntime().removeShutdownHook(shutdownThread);
		flushAsync();
	}

	@Override
	public void run() {
		while (inner != null) {
			synchronized (this) {
				try {
					wait(maxTimeMs);
				} catch (Exception e) {
				}
			}
			try {
				flushSync();
			} catch (Exception e) {
				e.printStackTrace();
				close();
			}
		}

	}

	private void flushCheck() throws IOException {
		if (shutdownThread == null && buffer.size() > 0)
			flushSync();
		else if (buffer.size() > maxSize)
			flushAsync();
	}

	private void flushAsync() {
		synchronized (this) {
			notify();
		}
	}

	private class ShutdownThread extends Thread {
		public ShutdownThread(String name) {
			super(name);
		}

		@Override
		public void run() {
			shutdownThread = null;
			try {
				flushSync();
			} catch (IOException e) {
			}
		}
	}

	@Override
	public void write(int b) throws IOException {
		synchronized (buffer) {
			buffer.write(b);
			flushCheck();
		}
	}

	@Override
	public void write(byte[] b) throws IOException {
		synchronized (buffer) {
			buffer.write(b);
			flushCheck();
		}
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		synchronized (buffer) {
			buffer.write(b, off, len);
			flushCheck();
		}
	}

}
