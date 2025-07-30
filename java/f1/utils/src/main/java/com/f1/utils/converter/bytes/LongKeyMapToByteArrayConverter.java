/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.converter.bytes;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import com.f1.base.BasicTypes;
import com.f1.utils.CH;
import com.f1.utils.structs.LongKeyMap;
import com.f1.utils.structs.LongKeyMap.Node;

public class LongKeyMapToByteArrayConverter implements ByteArrayConverter<LongKeyMap<?>> {

	@Override
	public byte getBasicType() {
		return BasicTypes.LONG_KEY_MAP;
	}

	@Override
	public boolean isCompatible(Class<?> o) {
		return LongKeyMap.class.isAssignableFrom(o);
	}

	@Override
	public LongKeyMap<?> read(FromByteArrayConverterSession session) throws IOException {
		int id = session.handleIfAlreadyConverted();
		if (id < 0)
			return (LongKeyMap<?>) session.get(id);
		DataInput stream = session.getStream();
		int length = stream.readInt();
		LongKeyMap<Object> map = newMap(length);
		session.store(id, map);
		if (length == 0)
			return map;
		byte valType = stream.readByte();
		ObjectToByteArrayConverter converter = session.getConverter();
		ByteArrayConverter<?> valConverter = converter.getConverter(valType);

		for (int i = 0; i < length; i++)
			map.put(stream.readLong(), valConverter.read(session));
		return map;
	}

	protected LongKeyMap<Object> newMap(int length) {
		return new LongKeyMap<Object>(length);
	}

	@Override
	public void write(LongKeyMap<?> map, ToByteArrayConverterSession session) throws IOException {
		if (session.handleIfAlreadyConverted(map))
			return;
		DataOutput stream = session.getStream();
		stream.writeInt(map.size());
		if (map.size() == 0)
			return;
		Class<?> valClass = CH.getClassIfSame(map.values());
		ObjectToByteArrayConverter converter = session.getConverter();
		ByteArrayConverter valConverter = converter.getConverter(valClass);

		stream.writeByte(valConverter.getBasicType());

		for (Node<?> o2 : map) {
			stream.writeLong(o2.getLongKey());
			valConverter.write(o2.getValue(), session);
		}
	}
}
