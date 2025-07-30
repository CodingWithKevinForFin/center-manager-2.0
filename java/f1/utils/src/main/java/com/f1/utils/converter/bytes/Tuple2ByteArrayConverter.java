/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.converter.bytes;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import com.f1.base.BasicTypes;
import com.f1.utils.structs.Tuple;
import com.f1.utils.structs.TupleFactory;

public class Tuple2ByteArrayConverter implements ByteArrayConverter<Tuple> {

	@Override
	public byte getBasicType() {
		return BasicTypes.TUPLE;
	}

	@Override
	public boolean isCompatible(Class<?> o) {
		return Tuple.class.isAssignableFrom(o);
	}

	@Override
	public Tuple read(FromByteArrayConverterSession session) throws IOException {
		int id = session.handleIfAlreadyConverted();
		if (id < 0)
			return (Tuple) session.get(id);
		DataInput stream = session.getStream();
		int length = stream.readByte();
		Tuple tuple = newTuple(length);
		session.store(id, tuple);
		if (length == 0)
			return tuple;
		byte type = stream.readByte();
		ByteArrayConverter<?> converter = session.getConverter().getConverter(type);
		for (int i = 0; i < length; i++)
			tuple.setAt(i, converter.read(session));
		return tuple;
	}

	@Override
	public void write(Tuple tuple, ToByteArrayConverterSession session) throws IOException {
		if (session.handleIfAlreadyConverted(tuple))
			return;
		DataOutput stream = session.getStream();
		int length = tuple.getSize();
		stream.writeByte(length);
		if (length == 0)
			return;
		ObjectToByteArrayConverter converter = session.getConverter();
		for (final Object o : tuple)
			converter.write(o, session);
	}

	private Tuple newTuple(int length) {
		return TupleFactory.INSTANCE.newTuple(length);
	}
}
