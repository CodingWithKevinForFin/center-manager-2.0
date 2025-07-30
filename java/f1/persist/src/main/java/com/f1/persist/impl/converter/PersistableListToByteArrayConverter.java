package com.f1.persist.impl.converter;

import java.io.IOException;
import java.util.List;

import com.f1.base.BasicTypes;
import com.f1.persist.PersistException;
import com.f1.persist.PersistReadStore;
import com.f1.persist.PersistWriteStore;
import com.f1.persist.structs.PersistableArrayList;
import com.f1.persist.structs.PersistableList;
import com.f1.utils.converter.bytes.FromByteArrayConverterSession;
import com.f1.utils.converter.bytes.ListToByteArrayConverter;
import com.f1.utils.converter.bytes.ToByteArrayConverterSession;

public class PersistableListToByteArrayConverter extends ListToByteArrayConverter {

	@Override
	public PersistableList read(FromByteArrayConverterSession session) throws IOException {
		final PersistReadStore persistStore = ((PersistFromByteArrayConverterSession) session).getPersistStore();
		final long id = session.getStream().readLong();
		if (id < 0) {
			final Object r = persistStore.getObjectById(-id);
			if (r == null)
				throw new PersistException("object not found for id: " + (-id));
			return (PersistableList) r;
		} else if (id > 0) {
			final PersistableList r = (PersistableList) super.read(session);
			persistStore.addObject(id, r);
			return r;
		} else
			throw new RuntimeException("id is zero");
	}

	@Override
	public void write(List o, ToByteArrayConverterSession session) throws IOException {
		final PersistWriteStore persistStore = ((PersistToByteArrayConverterSession) session).getPersistStore();
		long id = persistStore.registerObject(o);
		session.getStream().writeLong(id);
		if (id > 0)
			super.write(((PersistableList) o).getCopyThreadsafe(), session);
		else if (id == 0)
			throw new RuntimeException("id is zero");
	}

	@Override
	public byte getBasicType() {
		return BasicTypes.PERSISTABLE_LIST;
	}

	@Override
	public boolean isCompatible(Class<?> o) {
		return PersistableList.class.isAssignableFrom(o);
	}

	@Override
	public List newList(int length) {
		return new PersistableArrayList(length);

	}
}
