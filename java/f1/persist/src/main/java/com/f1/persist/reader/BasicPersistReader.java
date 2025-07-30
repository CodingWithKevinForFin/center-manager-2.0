package com.f1.persist.reader;

import java.io.IOException;

import com.f1.base.IdeableGenerator;
import com.f1.base.Transient;
import com.f1.base.ValuedListenable;
import com.f1.persist.PersistEvent;
import com.f1.persist.PersistReadStore;
import com.f1.persist.PersistReader;
import com.f1.persist.impl.converter.PersistFromByteArrayConverterSession;
import com.f1.persist.impl.converter.PersistValuedToByteArrayConverter;
import com.f1.persist.impl.converter.PersistableListToByteArrayConverter;
import com.f1.persist.impl.converter.PersistableMapToByteArrayConverter;
import com.f1.persist.impl.converter.PersistableSetToByteArrayConverter;
import com.f1.utils.FastDataInput;
import com.f1.utils.OH;
import com.f1.utils.converter.bytes.BasicFromByteArrayConverterSession;
import com.f1.utils.converter.bytes.ObjectToByteArrayConverter;
import com.f1.utils.converter.bytes.ValuedToByteArrayConverter;

public class BasicPersistReader implements PersistReader {

	final private FastDataInput in;
	final private BasicFromByteArrayConverterSession session;
	final private PersistReadStore store;
	final private ObjectToByteArrayConverter converter;
	private ValuedToByteArrayConverter valuedConverter;
	private ValuedListenable targetObject;
	private Long targetId;

	public BasicPersistReader(PersistReadStore store, FastDataInput dataInput, IdeableGenerator generator) {
		this.in = dataInput;
		this.converter = new ObjectToByteArrayConverter();
		this.converter.setSkipTransience(Transient.PERSIST);
		this.converter.setIdeableGenerator(generator);
		this.converter.replaceConverter(new PersistValuedToByteArrayConverter());
		this.converter.registerConverter(new PersistableMapToByteArrayConverter());
		this.converter.registerConverter(new PersistableListToByteArrayConverter());
		this.converter.registerConverter(new PersistableSetToByteArrayConverter());
		this.store = store;
		this.session = new PersistFromByteArrayConverterSession(converter, this.in, store);
		this.valuedConverter = new ValuedToByteArrayConverter();
	}

	@Override
	public void pumpEvent(PersistEvent sink) throws Exception {
		String i = in.toString();
		if (PersistEvent.WRITE_CHECKSUM) {
			if (in.readByte() != PersistEvent.CHECKSUM1) {
				throw new RuntimeException("checksum failed for transaction");
			}
			if (in.readByte() != PersistEvent.CHECKSUM2) {
				throw new RuntimeException("checksum failed for transaction");
			}
		}
		sink.clear();
		byte command = in.readByte();
		switch (command) {
			case PersistEvent.ADD:
				readAdd(sink);
				break;
			case PersistEvent.REMOVE:
				readRemove(sink);
				break;
			case PersistEvent.UPDATE_BY_PID:
				readUpdateByPid(sink, false);
				break;
			case PersistEvent.UPDATE_BY_PIN:
				readUpdateByPin(sink, false);
				break;
			case PersistEvent.UPDATE_KEYED:
				readUpdateKeyed(sink, false);
				break;
			case PersistEvent.REMOVE_KEYED:
				readUpdateKeyedRemoved(sink, false);
				break;
			case PersistEvent.CLEAR_KEYED:
				readUpdateKeysCleared(sink, false);
				break;
			case PersistEvent.ADD_KEYED:
				readAddKeyed(sink, false);
				break;
			case PersistEvent.CURRENT_UPDATE_BY_PID:
				readUpdateByPid(sink, true);
				break;
			case PersistEvent.CURRENT_UPDATE_BY_PIN:
				readUpdateByPin(sink, true);
				break;
			case PersistEvent.CURRENT_REMOVE_KEYED:
				readUpdateKeyedRemoved(sink, true);
				break;
			case PersistEvent.CURRENT_CLEAR_KEYED:
				readUpdateKeysCleared(sink, true);
				break;
			case PersistEvent.CURRENT_ADD_KEYED:
				readAddKeyed(sink, true);
				break;
			case PersistEvent.CURRENT_UPDATE_KEYED:
				readUpdateKeyed(sink, true);
				break;
			case PersistEvent.COMMIT:
				readCommit(sink);
				break;
			default:
				throw new RuntimeException("unknown command: " + command);
		}
		sink.setType(command);
	}
	private void readCommit(PersistEvent sink) {
		targetId = 0L;
		targetObject = null;
	}

	private void readUpdateKeyed(PersistEvent sink, boolean current) throws IOException {
		if (!current) {
			targetId = in.readLong();
			targetObject = (ValuedListenable) store.getObjectById(targetId);
		}
		sink.setTarget(targetObject);
		sink.setId(targetId);
		final Object key = converter.read(session);
		final Object value = converter.read(session);
		sink.setKey(key);
		sink.setValue(value);
	}

	private void readAddKeyed(PersistEvent sink, boolean current) throws IOException {
		if (!current) {
			targetId = in.readLong();
			targetObject = (ValuedListenable) store.getObjectById(targetId);
		}
		sink.setTarget(targetObject);
		sink.setId(targetId);
		final Object key = converter.read(session);
		final Object value = converter.read(session);
		sink.setKey(key);
		sink.setValue(value);
	}

	private void readUpdateKeysCleared(PersistEvent sink, boolean current) throws IOException {
		if (!current) {
			targetId = in.readLong();
			targetObject = (ValuedListenable) store.getObjectById(targetId);
		}
		sink.setTarget(targetObject);
		sink.setId(targetId);
	}

	private void readUpdateKeyedRemoved(PersistEvent sink, boolean current) throws IOException {
		if (!current) {
			targetId = in.readLong();
			targetObject = (ValuedListenable) store.getObjectById(targetId);
		}
		sink.setTarget(targetObject);
		sink.setId(targetId);
		final Object key = converter.read(session);
		sink.setKey(key);
	}

	private void readUpdateByPin(PersistEvent sink, boolean current) throws IOException {
		if (!current) {
			targetId = in.readLong();
			targetObject = (ValuedListenable) store.getObjectById(targetId);
		}
		final Object value = converter.read(session);
		final String pin = in.readUTF();
		sink.setTarget(targetObject);
		sink.setId(targetId);
		sink.setPin(pin);
		sink.setValue(value);
	}

	private void readUpdateByPid(PersistEvent sink, boolean current) throws IOException {
		if (!current) {
			targetId = in.readLong();
			targetObject = (ValuedListenable) store.getObjectById(targetId);
		}
		final Object value = converter.read(session);
		final byte pid = in.readByte();
		sink.setTarget(targetObject);
		sink.setId(targetId);
		sink.setPid(pid);
		sink.setValue(value);
	}

	private void readRemove(PersistEvent sink) {
		try {
			sink.setId(in.readLong());
		} catch (IOException e) {
			OH.toRuntime(e);
		}
	}

	private void readAdd(PersistEvent sink) throws IOException {

		targetObject = (ValuedListenable) converter.read(session);
		targetId = store.getIdByObject(targetObject);
		sink.setTarget(targetObject);
		sink.setId(targetId);
	}

}
