package com.f1.utils.converter.bytes;

import java.io.IOException;

public abstract class AbstractCustomByteArrayConverter<T> implements CustomByteArrayConverter<T> {

	final private Class<T> clazz;
	final private Object id;

	public AbstractCustomByteArrayConverter(Class<T> clazz) {
		this(clazz, clazz);
	}

	public AbstractCustomByteArrayConverter(Class<T> clazz, Object id) {
		this.clazz = clazz;
		this.id = id;
	}

	@Override
	abstract public void write(T o, ToByteArrayConverterSession session) throws IOException;

	@Override
	abstract public T read(FromByteArrayConverterSession session) throws IOException;

	@Override
	public Class<T> getType() {
		return clazz;
	}

	@Override
	public Object getCustomId() {
		return id;
	}

	@Override
	public boolean isCompatible(Class<?> o) {
		return clazz.isAssignableFrom(o);
	}

}
