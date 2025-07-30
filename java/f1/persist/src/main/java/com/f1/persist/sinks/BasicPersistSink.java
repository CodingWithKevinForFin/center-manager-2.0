package com.f1.persist.sinks;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

import com.f1.persist.PersistException;
import com.f1.persist.PersistSink;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.concurrent.FastQueue;

public class BasicPersistSink implements PersistSink, Runnable {
	private static final Logger log = Logger.getLogger(BasicPersistSink.class.getName());

	final private List<PersistSink> sinks = new CopyOnWriteArrayList<PersistSink>();
	final private List<PersistSink> asyncSinks = new CopyOnWriteArrayList<PersistSink>();
	final private FastQueue<byte[]> asyncQueue = new FastQueue<byte[]>();
	final private Object asyncSemephore = new Object();
	private boolean running = false;
	private Thread runningThread;

	@Override
	public void write(byte[] data) {
		synchronized (this) {
			if (asyncSinks.size() > 0) {
				if (!running)
					throw new PersistException("not running");
				asyncQueue.put(data);
				synchronized (asyncSemephore) {
					asyncSemephore.notify();
				}
			}

			for (int i = 0, l = sinks.size(); i < l; i++) {
				try {
					sinks.get(i).write(data);
				} catch (Exception e) {
					LH.severe(log, "Error writing to sink. Closing: ", sinks.get(i), e);
					sinks.remove(i--);
				}
			}
		}
	}

	public void addSink(PersistSink sink) {
		sinks.add(sink);
	}

	public void addAsyncSink(PersistSink sink) {
		if (!running)
			start();
		asyncSinks.add(sink);
	}

	public void start() {
		if (running)
			throw new PersistException("already running");
		running = true;

		runningThread = new Thread(this, "AsyncPersister");
		runningThread.setDaemon(false);
		runningThread.start();

	}

	public void stop() {
		if (!running)
			throw new PersistException("not running");
		running = false;
		runningThread.interrupt();
	}

	@Override
	public void run() {
		while (running) {
			if (asyncQueue.isEmpty()) {
				synchronized (asyncSemephore) {
					if (asyncQueue.isEmpty())
						OH.wait(asyncSemephore);
				}
			}
			byte[] data = asyncQueue.get();
			if (data != null) {
				for (PersistSink sink : asyncSinks) {
					try {
						sink.write(data);
					} catch (Exception e) {
						LH.severe(log, "Error writing to sink. Closing: ", sink, e);
						// TODO: remove item from queue
					}
				}
			}
		}
	}

}
