package com.f1.persist.sinks;

import com.f1.persist.PersistEvent;
import com.f1.persist.PersistSink;
import com.f1.persist.writer.TransactionalPersistWriter;
import com.f1.utils.FastByteArrayOutputStream;

public class DeltaBufferingPersistSink implements PersistSink {
	private volatile FastByteArrayOutputStream buffer = new FastByteArrayOutputStream();
	private final PersistSink inner;
	private final Object semephore = new Object();

	public DeltaBufferingPersistSink(PersistSink inner) {
		this.inner = inner;
	}

	@Override
	public void write(byte[] data) {
		if (buffer == null)
			inner.write(data);
		else
			synchronized (semephore) {
				if (buffer == null)
					inner.write(data);
				else {
					if (data[0] != PersistEvent.BEGIN)
						throw new RuntimeException("expecting BEGIN: " + data[0]);
					// Take all of the transactions and combine them into one
					// uber transaction
					if (PersistEvent.WRITE_CHECKSUM) {
						buffer.write(data, 7, data.length - 10);
					} else {
						buffer.write(data, 5, data.length - 6);
					}
				}
			}
	}

	public void writeSnapshotDelta(byte[] data) {
		inner.write(data);
		byte[] buf;
		for (;;) {
			synchronized (semephore) {
				buf = TransactionalPersistWriter.toTransaction(buffer);
				buffer.reset();
				if (buf.length < 1000000240) {
					inner.write(buf);
					buffer = null;
					break;
				}// else free the lock so more data can be appended to the
					// buffer while we send across the wire
			}
			inner.write(buf);
		}
	}

}
