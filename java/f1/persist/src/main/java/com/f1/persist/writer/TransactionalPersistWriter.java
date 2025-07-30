package com.f1.persist.writer;

import com.f1.base.Transactional;
import com.f1.base.ValuedListenable;
import com.f1.persist.PersistEvent;
import com.f1.persist.PersistSink;
import com.f1.persist.PersistWriter;
import com.f1.persist.Persistable;
import com.f1.utils.ByteHelper;
import com.f1.utils.FastByteArrayDataOutputStream;
import com.f1.utils.FastByteArrayOutputStream;
import com.f1.utils.OH;
import com.f1.utils.converter.bytes.ObjectToByteArrayConverter;

public class TransactionalPersistWriter implements PersistWriter, Transactional {

	final private int id;
	final private BasicPersistWriter inner;
	final private FastByteArrayDataOutputStream buffer;
	private PersistSink sink;
	private BasicPersistWriteStore store;

	public TransactionalPersistWriter(int id, ObjectToByteArrayConverter converter, PersistSink sink) {
		this.buffer = new FastByteArrayDataOutputStream();
		store = new BasicPersistWriteStore((long) id * Integer.MAX_VALUE);
		this.inner = new BasicPersistWriter(converter, store, buffer);
		this.sink = sink;
		OH.assertGe(id, 0);
		this.id = id;
	}

	private Object last = null;

	@Override
	public void writeValued(ValuedListenable valued) {
		last = valued;
		inner.writeValued(valued);
	}

	@Override
	public void writeParam(ValuedListenable valued, byte pid, Object value) {

		valued = checkLast(valued);
		inner.writeParam(valued, pid, value);

	}

	@Override
	public void writeKeyedParamAdded(Persistable valued, Object key, Object value) {
		valued = checkLast(valued);
		inner.writeKeyedParamAdded(valued, key, value);
	}

	@Override
	public void writeKeyedParamChanged(Persistable valued, Object key, Object value) {
		valued = checkLast(valued);
		inner.writeKeyedParamChanged(valued, key, value);
	}

	@Override
	public void writeParam(ValuedListenable valued, String pin, Object value) {
		valued = checkLast(valued);
		inner.writeParam(valued, pin, value);
	}

	private <T> T checkLast(T valued) {
		if (last == valued)
			return null;
		last = valued;
		return valued;
	}

	public boolean commitTransaction() {
		last = null;
		if (buffer.size() > 0) {
			sink.write(toTransaction(buffer));
			buffer.reset();
			return true;
		}
		return false;
	}

	public static byte[] toTransaction(FastByteArrayOutputStream buf) {
		final int transactionSize = buf.size();

		if (PersistEvent.WRITE_CHECKSUM) {
			final byte[] bytes = new byte[transactionSize + 10];// CS1+CS2+BEGIN+TRANSACTION_SIZE+TRANSACTION_ID+CS1+CS2+COMMIT
			bytes[0] = PersistEvent.CHECKSUM1;
			bytes[1] = PersistEvent.CHECKSUM2;
			bytes[2] = PersistEvent.BEGIN;
			ByteHelper.writeInt(transactionSize + 3, bytes, 3);// ADD 3 for CS1+CS2+COMMIT
			buf.toByteArray(bytes, 7, transactionSize);
			bytes[bytes.length - 3] = PersistEvent.CHECKSUM1;
			bytes[bytes.length - 2] = PersistEvent.CHECKSUM2;
			bytes[bytes.length - 1] = PersistEvent.COMMIT;
			return bytes;
		} else {
			final byte[] bytes = new byte[transactionSize + 6];// BEGIN+TRANSACTION_SIZE+TRANSACTION_ID+COMMIT
			bytes[0] = PersistEvent.BEGIN;
			ByteHelper.writeInt(transactionSize + 1, bytes, 1);// ADD 1 for COMMIT
			buf.toByteArray(bytes, 5, transactionSize);
			bytes[bytes.length - 1] = PersistEvent.COMMIT;
			return bytes;
		}
	}

	public PersistSink getSink() {
		return sink;
	}

	public BasicPersistWriteStore getStore() {
		return store;
	}

	@Override
	public void writeValuedRemoved(ValuedListenable valued) {
		valued = checkLast(valued);
		inner.writeValuedRemoved(valued);
	}

	@Override
	public void writeKeyedParamRemoved(Persistable valued, Object key) {
		valued = checkLast(valued);
		inner.writeKeyedParamRemoved(valued, key);
	}

	@Override
	public void writeKeyedParamsCleared(Persistable valued) {
		valued = checkLast(valued);
		inner.writeKeyedParamsCleared(valued);

	}

}
