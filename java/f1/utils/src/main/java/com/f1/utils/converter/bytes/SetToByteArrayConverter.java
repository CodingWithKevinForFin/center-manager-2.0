/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.converter.bytes;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import com.f1.base.BasicTypes;
import com.f1.utils.CH;

public class SetToByteArrayConverter implements ByteArrayConverter<Set> {

	@Override
	public byte getBasicType() {
		return BasicTypes.SET;
	}

	@Override
	public boolean isCompatible(Class<?> o) {
		return Set.class.isAssignableFrom(o);
	}

	@Override
	public Set read(FromByteArrayConverterSession session) throws IOException {
		int id = session.handleIfAlreadyConverted();
		if (id < 0)
			return (Set) session.get(id);
		DataInput stream = session.getStream();
		final int length = stream.readInt();
		final Set<Object> set = newSet(length);
		session.store(id, set);
		if (length == 0)
			return set;
		final byte type = stream.readByte();
		ByteArrayConverter<?> marshaler = session.getConverter().getConverter(type);
		for (int i = 0; i < length; i++)
			set.add(marshaler.read(session));
		return set;
	}

	@Override
	public void write(Set o, ToByteArrayConverterSession session) throws IOException {
		if (session.handleIfAlreadyConverted(o))
			return;
		Set<Object> set = (Set) o;
		DataOutput stream = session.getStream();
		stream.writeInt(set.size());
		if (set.size() == 0)
			return;
		Class sameClass = CH.getClassIfSame(set);
		ByteArrayConverter converter = session.getConverter().getConverter(sameClass);
		stream.writeByte(converter.getBasicType());
		for (final Object o2 : set)
			converter.write(o2, session);
	}

	public Set newSet(int size) {
		return new HashSet(size);
	}
}
