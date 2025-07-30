package com.f1.persist.impl.converter;

import com.f1.persist.PersistReadStore;
import com.f1.utils.FastDataInput;
import com.f1.utils.converter.bytes.BasicFromByteArrayConverterSession;
import com.f1.utils.converter.bytes.ObjectToByteArrayConverter;

public class PersistFromByteArrayConverterSession extends BasicFromByteArrayConverterSession {

	public PersistFromByteArrayConverterSession(ObjectToByteArrayConverter converter, FastDataInput stream, PersistReadStore persistStore) {
		super(converter, stream);
		this.persistStore = persistStore;
	}

	final private PersistReadStore persistStore;

	public PersistReadStore getPersistStore() {
		return persistStore;
	}

}
