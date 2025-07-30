/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.converter.bytes;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.f1.base.BasicTypes;
import com.f1.utils.CH;

public class ListToByteArrayConverter implements ByteArrayConverter<List> {

	@Override
	public byte getBasicType() {
		return BasicTypes.LIST;
	}

	@Override
	public boolean isCompatible(Class<?> o) {
		return List.class.isAssignableFrom(o);
	}

	@Override
	public List read(FromByteArrayConverterSession session) throws IOException {
		int id = session.handleIfAlreadyConverted();
		if (id < 0)
			return (List) session.get(id);
		DataInput stream = session.getStream();
		int length = stream.readInt();
		List<Object> list = newList(length);
		session.store(id, list);
		if (length == 0)
			return list;
		byte type = stream.readByte();
		ByteArrayConverter<?> converter = session.getConverter().getConverter(type);
		for (int i = 0; i < length; i++)
			list.add(converter.read(session));
		return list;
	}

	@Override
	public void write(List list, ToByteArrayConverterSession session) throws IOException {
		if (session.handleIfAlreadyConverted(list))
			return;
		DataOutput stream = session.getStream();
		int length = list.size();
		stream.writeInt(length);
		if (length == 0)
			return;
		Class sameClass = CH.getClassIfSame(list);
		ByteArrayConverter converter = session.getConverter().getConverter(sameClass);
		stream.writeByte(converter.getBasicType());
		if (list instanceof ArrayList)
			for (int i = 0; i < length; i++)
				converter.write(list.get(i), session);
		else
			for (final Object o : list)
				converter.write(o, session);
	}

	public List newList(int length) {
		return new ArrayList(length);
	}

}
