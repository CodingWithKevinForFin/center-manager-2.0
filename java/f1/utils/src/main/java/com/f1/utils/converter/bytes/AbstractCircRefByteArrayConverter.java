/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.converter.bytes;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public abstract class AbstractCircRefByteArrayConverter<T> implements ByteArrayConverter<T> {

	private final Class type;
	private final byte basicType;

	public AbstractCircRefByteArrayConverter(Class type, byte basicType) {
		this.type = type;
		this.basicType = basicType;
	}

	@Override
	public T read(FromByteArrayConverterSession session) throws IOException {
		int id = session.handleIfAlreadyConverted();
		if (id < 0)
			return (T) session.get(id);
		T r = read(session.getStream());
		session.store(id, r);
		return r;
	}

	@Override
	public void write(T o, ToByteArrayConverterSession session) throws IOException {
		if (session.handleIfAlreadyConverted(o))
			return;
		write(o, session.getStream());
	}

	@Override
	public byte getBasicType() {
		return basicType;
	}

	@Override
	public boolean isCompatible(Class<?> o) {
		return type.isAssignableFrom(o);
	}

	protected abstract T read(DataInput stream) throws IOException;

	protected abstract void write(T o, DataOutput stream) throws IOException;
}
