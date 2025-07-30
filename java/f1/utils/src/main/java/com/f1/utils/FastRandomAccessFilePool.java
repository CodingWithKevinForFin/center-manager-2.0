package com.f1.utils;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;

public class FastRandomAccessFilePool implements Closeable {
	private class SHUTDOWN_HOOK extends Thread {
		public void run() {
			try {
				if (!isClosed()) {
					IOH.close(FastRandomAccessFilePool.this);
				}
			} catch (Exception e) {
				System.err.print("Error on shutdown for FastRandomAccessFilePool: ");
				e.printStackTrace(System.err);
			}
		}

	}

	private int opened = 0;

	private class Entry implements FastRandomAccessFileListener {
		public FastRandomAccessFile raf;

		@Override
		public void onUsed(FastRandomAccessFile file) {
			if (next == null)
				return;
			prior.next = next;
			next.prior = prior;
			prior = tail;
			tail.next = this;
			next = null;
			tail = this;
		}

		@Override
		public void onOpened(FastRandomAccessFile file) throws IOException {
			OH.assertFalse(isClosed);
			opened++;
			prior = tail;
			tail.next = this;
			tail = this;
			while (opened > maxOpenCount) {
				head.next.raf.close();
			}
		}

		@Override
		public void onClosed(FastRandomAccessFile file) {
			if (isClosed)
				return;
			opened--;
			this.prior.next = next;
			if (this.next != null) {
				this.next.prior = this.prior;
			} else {
				OH.assertEq(this, tail);
				tail = this.prior;
			}
			this.prior = null;
			this.next = null;

		}

		Entry prior, next;

	}

	private Entry head = new Entry();
	private Entry tail = head;

	final private int maxOpenCount;
	private boolean isClosed = false;

	public FastRandomAccessFilePool(int maxOpenCount) {
		this(maxOpenCount, false);
	}
	public FastRandomAccessFilePool(int maxOpenCount, boolean autoCloseOnShutdown) {
		if (autoCloseOnShutdown)
			Runtime.getRuntime().addShutdownHook(new SHUTDOWN_HOOK());
		OH.assertGe(maxOpenCount, 1);
		this.maxOpenCount = maxOpenCount;
	}

	public FastRandomAccessFile open(File url, String mode, int blockSize) throws IOException {
		Entry e = new Entry();
		FastRandomAccessFile r = new FastRandomAccessFile(url, mode, blockSize, false, e);
		e.raf = r;
		return r;
	}

	private boolean isClosed() {
		return isClosed;
	};
	@Override
	public void close() throws IOException {
		if (!isClosed)
			this.isClosed = true;
		for (Entry e = this.head; e != null; e = e.next) {
			IOH.close(e.raf);
		}
		opened = 0;
		this.head.next = null;
		this.tail = this.head;
	}

	public int getMaxOpenCount() {
		return this.maxOpenCount;
	}
}
