package com.f1.persist.impl.converter;

import java.io.IOException;
import java.util.Set;

import com.f1.base.BasicTypes;
import com.f1.persist.PersistException;
import com.f1.persist.PersistReadStore;
import com.f1.persist.PersistWriteStore;
import com.f1.persist.structs.PersistableHashSet;
import com.f1.persist.structs.PersistableSet;
import com.f1.utils.converter.bytes.FromByteArrayConverterSession;
import com.f1.utils.converter.bytes.SetToByteArrayConverter;
import com.f1.utils.converter.bytes.ToByteArrayConverterSession;

public class PersistableSetToByteArrayConverter extends SetToByteArrayConverter {

	@Override
	public PersistableSet read(FromByteArrayConverterSession session) throws IOException {
		final PersistReadStore persistStore = ((PersistFromByteArrayConverterSession) session).getPersistStore();
		final long id = session.getStream().readLong();
		if (id < 0) {
			final Object r = persistStore.getObjectById(-id);
			if (r == null)
				throw new PersistException("object not found for id: " + (-id));
			return (PersistableSet) r;
		} else if (id > 0) {
			final PersistableSet r = (PersistableSet) super.read(session);
			persistStore.addObject(id, r);
			return r;
		} else
			throw new RuntimeException("id is zero");
	}

	@Override
	public void write(Set o, ToByteArrayConverterSession session) throws IOException {
		final PersistWriteStore persistStore = ((PersistToByteArrayConverterSession) session).getPersistStore();
		long id = persistStore.registerObject(o);
		session.getStream().writeLong(id);
		if (id > 0)
			super.write(((PersistableSet) o).getCopyThreadsafe(), session);
		else if (id == 0)
			throw new RuntimeException("id is zero");
	}

	@Override
	public byte getBasicType() {
		return BasicTypes.PERSISTABLE_SET;
	}

	@Override
	public boolean isCompatible(Class<?> o) {
		return PersistableSet.class.isAssignableFrom(o);
	}

	@Override
	public Set newSet(int length) {
		return new PersistableHashSet(length);

	}
}
