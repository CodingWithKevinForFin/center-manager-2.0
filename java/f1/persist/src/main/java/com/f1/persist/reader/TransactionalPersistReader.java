package com.f1.persist.reader;

import java.io.DataInput;
import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.f1.base.IdeableGenerator;
import com.f1.base.Valued;
import com.f1.persist.PersistEvent;
import com.f1.persist.PersistException;
import com.f1.persist.PersistReadStore;
import com.f1.persist.PersistReader;
import com.f1.persist.PersistStoreListener;
import com.f1.persist.Persistable;
import com.f1.persist.impl.BasicPersistEvent;
import com.f1.utils.FastByteArrayDataInputStream;
import com.f1.utils.OH;

public class TransactionalPersistReader implements PersistReader {

	final private PersistReadStore store;
	final private BasicPersistReader inner;
	final private List<PersistEvent> eventsCache = new ArrayList<PersistEvent>();
	final private FastByteArrayDataInputStream buffer;
	private byte[] buf = OH.EMPTY_BYTE_ARRAY;

	public TransactionalPersistReader(IdeableGenerator generator) {
		this.store = new BasicPersistReadStore();
		this.buffer = new FastByteArrayDataInputStream(OH.EMPTY_BYTE_ARRAY);
		this.inner = new BasicPersistReader(new BasicPersistReadStore(), buffer, generator);
	}

	@Override
	public void pumpEvent(PersistEvent sink) throws Exception {
		inner.pumpEvent(sink);
	}

	// Consumes the transaction into the buffer, at this point the buffer has
	// not yet been processed
	public boolean consumeTransaction(DataInput in) {

		try {
			if (PersistEvent.WRITE_CHECKSUM) {
				OH.assertEq(in.readByte(), PersistEvent.CHECKSUM1);
				OH.assertEq(in.readByte(), PersistEvent.CHECKSUM2);
			}
			byte command = in.readByte();
			if (command != PersistEvent.BEGIN)
				throw new RuntimeException("expecting " + PersistEvent.BEGIN + " not: " + command);
			int transactionSize = in.readInt();
			if (buf.length < transactionSize)
				buf = new byte[transactionSize];
			buf = new byte[transactionSize];
			in.readFully(buf, 0, transactionSize);
			buffer.reset(buf, 0, transactionSize);
			return true;
		} catch (EOFException e) {
			return false;
		} catch (IOException e) {
			throw new PersistException("Error processing transaction", e);
		}
	}

	// processes the buffer, should be called after consumeTransaction(...)
	public int pumpTransaction() throws Exception {
		int cnt = 0;
		for (;;) {
			PersistEvent event;

			// gets a pooled event, or creates a new one if the pool is
			// exhausted
			if (cnt == eventsCache.size())
				eventsCache.add(event = new BasicPersistEvent());
			else
				event = eventsCache.get(cnt);
			pumpEvent(event);
			if (event.getType() == PersistEvent.COMMIT)
				break;
			cnt++;
		}
		for (int i = 0; i < cnt; i++) {
			PersistEvent event = eventsCache.get(i);
			processEvent(event);
			event.clear();
		}
		return cnt;

	}

	public void processEvent(PersistEvent event) {
		switch (event.getType()) {
			case PersistEvent.ADD:
				store.addObject(event.getId(), event.getTarget());
				for (PersistStoreListener l : listeners)
					l.onObjectAdded(event.getId(), event.getTarget());
				break;
			case PersistEvent.REMOVE:
				store.removeObject(event.getId());
				for (PersistStoreListener l : listeners)
					l.onObjectRemoved(event.getId(), event.getTarget());
				break;
			case PersistEvent.UPDATE_BY_PID:
			case PersistEvent.CURRENT_UPDATE_BY_PID:
				((Valued) event.getTarget()).put(event.getPid(), event.getValue());
				break;
			case PersistEvent.UPDATE_BY_PIN:
			case PersistEvent.CURRENT_UPDATE_BY_PIN:
				((Valued) event.getTarget()).put(event.getPin(), event.getValue());
				break;
			case PersistEvent.UPDATE_KEYED:
			case PersistEvent.CURRENT_UPDATE_KEYED:
				((Persistable) event.getTarget()).updateKeyedParam(event.getKey(), event.getValue());
				break;
			case PersistEvent.ADD_KEYED:
			case PersistEvent.CURRENT_ADD_KEYED:
				((Persistable) event.getTarget()).addKeyedParam(event.getKey(), event.getValue());
				break;
			case PersistEvent.REMOVE_KEYED:
			case PersistEvent.CURRENT_REMOVE_KEYED:
				((Persistable) event.getTarget()).removeKeyedParam(event.getKey());
				break;
			case PersistEvent.CLEAR_KEYED:
			case PersistEvent.CURRENT_CLEAR_KEYED:
				((Persistable) event.getTarget()).clearKeyedParams();
				break;
			case PersistEvent.COMMIT:
				return;
			default:
				throw new RuntimeException("unknown command: " + event.getType());
		}
	}

	final private List<PersistStoreListener> listeners = new ArrayList<PersistStoreListener>();

	public void addListener(PersistStoreListener listener) {
		listeners.add(listener);
	}

	public void removeListener(PersistStoreListener listener) {
		listeners.remove(listener);
	}

	public PersistReadStore getStore() {
		return store;
	}

}
