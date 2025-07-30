/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.converter.bytes;

import java.io.IOException;

import com.f1.utils.FastDataInput;
import com.f1.utils.FastDataOutput;

public abstract class SimpleByteArrayConverter<T> implements ByteArrayConverter<T> {

	private final Class type;
	private final byte basicType;

	public SimpleByteArrayConverter(Class type, byte basicType) {
		this.type = type;
		this.basicType = basicType;
	}

	@Override
	public T read(FromByteArrayConverterSession session) throws IOException {
		return read(session.getStream());
	}

	@Override
	public void write(T o, ToByteArrayConverterSession session) throws IOException {
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

	protected abstract T read(FastDataInput stream) throws IOException;

	protected abstract void write(T o, FastDataOutput stream) throws IOException;
}
