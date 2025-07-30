package com.f1.persist.impl.converter;

import com.f1.persist.PersistWriteStore;
import com.f1.utils.FastDataOutput;
import com.f1.utils.converter.bytes.BasicToByteArrayConverterSession;
import com.f1.utils.converter.bytes.ObjectToByteArrayConverter;

public class PersistToByteArrayConverterSession extends BasicToByteArrayConverterSession {

	public PersistToByteArrayConverterSession(ObjectToByteArrayConverter converter, FastDataOutput stream, PersistWriteStore persistStore, boolean supportCircRefs) {
		super(converter, stream, supportCircRefs);
		this.persistStore = persistStore;
	}

	final private PersistWriteStore persistStore;

	public PersistWriteStore getPersistStore() {
		return persistStore;
	}
}
