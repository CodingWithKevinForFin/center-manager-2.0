package com.f1.persist.impl.converter;

import java.io.IOException;

import com.f1.base.BasicTypes;
import com.f1.base.Valued;
import com.f1.persist.PersistException;
import com.f1.persist.PersistReadStore;
import com.f1.persist.PersistWriteStore;
import com.f1.utils.converter.bytes.ByteArrayConverter;
import com.f1.utils.converter.bytes.FromByteArrayConverterSession;
import com.f1.utils.converter.bytes.ToByteArrayConverterSession;
import com.f1.utils.converter.bytes.ValuedToByteArrayConverter;

public class PersistValuedToByteArrayConverter implements ByteArrayConverter<Object> {

	private ValuedToByteArrayConverter valuedByteArrayConverter;

	public PersistValuedToByteArrayConverter() {
		valuedByteArrayConverter = new ValuedToByteArrayConverter();
	}

	@Override
	public Object read(FromByteArrayConverterSession session) throws IOException {
		final PersistReadStore persistStore = ((PersistFromByteArrayConverterSession) session).getPersistStore();
		final long id = session.getStream().readLong();
		if (id < 0) {
			final Object r = persistStore.getObjectById(-id);
			if (r == null)
				throw new PersistException("object not found for id: " + (-id));
			return r;
		} else if (id > 0) {
			final Object r = valuedByteArrayConverter.read(session);
			persistStore.addObject(id, r);
			return r;
		} else
			throw new RuntimeException("id is zero");
	}

	@Override
	public void write(Object o, ToByteArrayConverterSession session) throws IOException {
		final PersistWriteStore persistStore = ((PersistToByteArrayConverterSession) session).getPersistStore();
		long id = persistStore.registerObject(o);
		session.getStream().writeLong(id);
		if (id > 0) {
			valuedByteArrayConverter.write((Valued) o, session);
		} else if (id == 0)
			throw new RuntimeException("id is zero");
	}

	@Override
	public byte getBasicType() {
		return BasicTypes.MESSAGE;
	}

	@Override
	public boolean isCompatible(Class<?> o) {
		return Valued.class.isAssignableFrom(o);
	}
}
