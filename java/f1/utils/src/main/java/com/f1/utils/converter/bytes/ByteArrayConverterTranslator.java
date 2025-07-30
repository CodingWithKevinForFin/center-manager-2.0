package com.f1.utils.converter.bytes;

import java.io.IOException;

public abstract class ByteArrayConverterTranslator<READ_TYPE, WRITE_TYPE> {

	final private byte readBasicType;
	final private byte writeBasicType;
	final private Class<READ_TYPE> readClassType;
	final private Class<WRITE_TYPE> writeClassType;
	private Writer writer;
	private Reader reader;

	public ByteArrayConverterTranslator(Class<READ_TYPE> readClassType, byte writeBasicType, Class<WRITE_TYPE> writeClassType, byte readBasicType) {
		this.readBasicType = readBasicType;
		this.writeBasicType = writeBasicType;
		this.readClassType = readClassType;
		this.writeClassType = writeClassType;
		this.writer = new Writer();
		this.reader = new Reader();
	}

	abstract public void writeObject(WRITE_TYPE o, ToByteArrayConverterSession session) throws IOException;

	abstract public READ_TYPE readObject(FromByteArrayConverterSession session) throws IOException;

	public byte getReadType() {
		return readBasicType;
	}
	public boolean isWriteCompatible(Class clazz) {
		return writeClassType.isAssignableFrom(clazz);
	}

	public ByteArrayConverter<WRITE_TYPE> asWriter() {
		return writer;
	}
	public ByteArrayConverter<READ_TYPE> asReader() {
		return reader;
	}

	public class Writer implements ByteArrayConverter<WRITE_TYPE> {
		@Override
		public void write(WRITE_TYPE o, ToByteArrayConverterSession session) throws IOException {
			writeObject(o, session);
		}

		@Override
		public WRITE_TYPE read(FromByteArrayConverterSession session) throws IOException {
			throw new RuntimeException("can only write... use reader!");
		}

		@Override
		public byte getBasicType() {
			return writeBasicType;
		}

		@Override
		public boolean isCompatible(Class<?> o) {
			return isWriteCompatible(o);
		}
	}

	public class Reader implements ByteArrayConverter<READ_TYPE> {
		@Override
		public void write(READ_TYPE o, ToByteArrayConverterSession session) throws IOException {
			throw new RuntimeException("can only read... use writer!");
		}

		@Override
		public READ_TYPE read(FromByteArrayConverterSession session) throws IOException {
			return readObject(session);
		}

		@Override
		public byte getBasicType() {
			return readBasicType;
		}

		@Override
		public boolean isCompatible(Class<?> o) {
			return false;
		}
	}

}
