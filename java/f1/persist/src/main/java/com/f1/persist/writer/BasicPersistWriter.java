package com.f1.persist.writer;

import java.io.IOException;

import com.f1.base.ValuedListenable;
import com.f1.persist.PersistEvent;
import com.f1.persist.PersistException;
import com.f1.persist.PersistWriteStore;
import com.f1.persist.PersistWriter;
import com.f1.persist.Persistable;
import com.f1.persist.impl.converter.PersistToByteArrayConverterSession;
import com.f1.utils.DetailedException;
import com.f1.utils.FastDataOutput;
import com.f1.utils.OH;
import com.f1.utils.converter.bytes.ObjectToByteArrayConverter;

/**
 * This is not thread safe
 * 
 * @author rcooke
 * 
 */
public class BasicPersistWriter implements PersistWriter {

	final private FastDataOutput out;
	final private PersistToByteArrayConverterSession session;
	final private PersistWriteStore store;
	final private ObjectToByteArrayConverter converter;

	public BasicPersistWriter(ObjectToByteArrayConverter converter, PersistWriteStore store, FastDataOutput dataOutput) {
		this.out = dataOutput;
		this.converter = converter;
		this.session = new PersistToByteArrayConverterSession(converter, this.out, store, true);
		this.store = store;
	}

	public void writeParam(ValuedListenable valued, byte pid, Object value) {
		try {
			if (valued != null) {
				writeOperation(PersistEvent.UPDATE_BY_PID);
				final Long id = store.getIdByObject(valued);
				if (id == null)
					throw new PersistException("valued not registered.").set("valued", value).set("field", pid).set("value", value);
				out.writeLong(id);
			} else
				writeOperation(PersistEvent.CURRENT_UPDATE_BY_PID);
			converter.write(value, session);
			out.writeByte(pid);
		} catch (IOException e) {
			throw OH.toRuntime(e);
		}
	}

	@Override
	public void writeValued(ValuedListenable valued) {
		try {
			writeOperation(PersistEvent.ADD);
			writeValue(valued);

		} catch (IOException e) {
			throw OH.toRuntime(e);
		}
	}

	@Override
	public void writeKeyedParamAdded(Persistable valued, Object key, Object value) {

		try {
			if (valued == null) {
				writeOperation(PersistEvent.CURRENT_ADD_KEYED);
			} else {
				writeOperation(PersistEvent.ADD_KEYED);
				final Long id = store.getIdByObject(valued);
				if (id == null)
					throw new PersistException("valued not registered.").set("valued", valued).set("key", key).set("value", value);
				out.writeLong(id);
			}
			writeValue(key);
			writeValue(value);
		} catch (IOException e) {
			throw OH.toRuntime(e);
		}
	}

	@Override
	public void writeKeyedParamChanged(Persistable valued, Object key, Object value) {

		try {
			if (valued == null) {
				writeOperation(PersistEvent.CURRENT_UPDATE_KEYED);
			} else {
				writeOperation(PersistEvent.UPDATE_KEYED);
				final Long id = store.getIdByObject(valued);
				if (id == null)
					throw new PersistException("valued not registered.").set("valued", valued).set("key", key).set("value", value);
				out.writeLong(id);
			}
			writeValue(key);
			writeValue(value);
		} catch (IOException e) {
			throw OH.toRuntime(e);
		}
	}

	@Override
	public void writeParam(ValuedListenable valued, String pin, Object value) {
		try {
			if (valued == null) {
				writeOperation(PersistEvent.CURRENT_UPDATE_BY_PIN);
			} else {
				writeOperation(PersistEvent.UPDATE_BY_PIN);
				final Long id = store.getIdByObject(valued);
				if (id == null)
					throw new PersistException("valued not registered.").set("valued", valued).set("field", pin).set("value", value);
				out.writeLong(id);
			}
			converter.write(value, session);
			out.writeUTF(pin);

		} catch (IOException e) {
			throw OH.toRuntime(e);
		}

	}

	private void writeValue(Object key) {
		try {
			converter.write(key, session);
		} catch (IOException e) {
			throw OH.toRuntime(e);
		}
	}

	private void writeOperation(byte command) throws IOException {
		if (PersistEvent.WRITE_CHECKSUM) {
			out.writeByte(PersistEvent.CHECKSUM1);
			out.writeByte(PersistEvent.CHECKSUM2);
		}
		out.writeByte(command);

	}

	@Override
	public void writeValuedRemoved(ValuedListenable valued) {
		try {
			if (valued == null) {
				writeOperation(PersistEvent.CURRENT_REMOVE);
			} else {
				Long id = store.removeObject(valued);
				if (id == null)
					throw new DetailedException("valued not found for remove").set("valued", valued);
				writeOperation(PersistEvent.REMOVE);
				out.writeLong(id);
			}
		} catch (IOException e) {
			throw OH.toRuntime(e);
		}
	}

	@Override
	public void writeKeyedParamRemoved(Persistable valued, Object key) {
		try {
			if (valued == null) {
				writeOperation(PersistEvent.CURRENT_REMOVE_KEYED);
			} else {
				writeOperation(PersistEvent.REMOVE_KEYED);
				final Long id = store.getIdByObject(valued);
				if (id == null)
					throw new PersistException("valued not registered.").set("valued", valued).set("key", key);
				out.writeLong(id);
			}
			writeValue(key);
		} catch (IOException e) {
			throw OH.toRuntime(e);
		}
	}

	@Override
	public void writeKeyedParamsCleared(Persistable valued) {
		try {
			if (valued == null) {
				writeOperation(PersistEvent.CURRENT_CLEAR_KEYED);
			} else {
				writeOperation(PersistEvent.CLEAR_KEYED);
				final Long id = store.getIdByObject(valued);
				if (id == null)
					throw new PersistException("valued not registered.").set("valued", valued);
				out.writeLong(id);
			}
		} catch (IOException e) {
			throw OH.toRuntime(e);
		}
	}

}
