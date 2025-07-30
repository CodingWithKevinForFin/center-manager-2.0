package com.f1.persist.impl.converter;

import java.io.IOException;
import java.util.Map;

import com.f1.base.BasicTypes;
import com.f1.persist.PersistException;
import com.f1.persist.PersistReadStore;
import com.f1.persist.PersistWriteStore;
import com.f1.persist.structs.PersistableHashMap;
import com.f1.persist.structs.PersistableMap;
import com.f1.utils.converter.ConverterException;
import com.f1.utils.converter.bytes.FromByteArrayConverterSession;
import com.f1.utils.converter.bytes.MapToByteArrayConverter;
import com.f1.utils.converter.bytes.ToByteArrayConverterSession;

public class PersistableMapToByteArrayConverter extends MapToByteArrayConverter {

	@Override
	public PersistableMap read(FromByteArrayConverterSession session) throws IOException {
		final PersistReadStore persistStore = ((PersistFromByteArrayConverterSession) session).getPersistStore();
		final long id = session.getStream().readLong();
		if (id < 0) {
			final Object r = persistStore.getObjectById(-id);
			if (r == null)
				throw new PersistException("object not found for id: " + (-id));
			return (PersistableMap) r;
		} else if (id > 0) {
			final PersistableMap r = (PersistableMap) super.read(session);
			persistStore.addObject(id, r);
			return r;
		} else
			throw new ConverterException("id should not be zero");
	}

	@Override
	public void write(Map o, ToByteArrayConverterSession session) throws IOException {
		final PersistWriteStore persistStore = ((PersistToByteArrayConverterSession) session).getPersistStore();
		long id = persistStore.registerObject(o);
		session.getStream().writeLong(id);
		if (id > 0)
			super.write(((PersistableMap) o).getCopyThreadsafe(), session);
		else if (id == 0)
			throw new ConverterException("id should not be zero");
	}

	@Override
	public byte getBasicType() {
		return BasicTypes.PERSISTABLE_MAP;
	}

	@Override
	public boolean isCompatible(Class<?> o) {
		return PersistableMap.class.isAssignableFrom(o);
	}

	@Override
	public Map newMap(int length) {
		return new PersistableHashMap();

	}
}
