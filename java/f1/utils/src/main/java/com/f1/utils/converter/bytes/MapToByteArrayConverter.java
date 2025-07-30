/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.converter.bytes;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.f1.base.BasicTypes;
import com.f1.utils.CH;

public class MapToByteArrayConverter implements ByteArrayConverter<Map> {
	private static final Logger log = Logger.getLogger(MapToByteArrayConverter.class.getName());

	@Override
	public byte getBasicType() {
		return BasicTypes.MAP;
	}

	@Override
	public boolean isCompatible(Class<?> o) {
		return Map.class.isAssignableFrom(o);
	}

	@Override
	public Map read(FromByteArrayConverterSession session) throws IOException {
		int id = session.handleIfAlreadyConverted();
		if (id < 0)
			return (Map) session.get(id);
		DataInput stream = session.getStream();
		int length = stream.readInt();
		Map<Object, Object> map = newMap(length);
		session.store(id, map);
		if (length == 0)
			return map;
		byte keyType = stream.readByte();
		byte valType = stream.readByte();
		ObjectToByteArrayConverter converter = session.getConverter();
		ByteArrayConverter<?> keyConverter = converter.getConverter(keyType);
		ByteArrayConverter<?> valConverter = converter.getConverter(valType);

		for (int i = 0; i < length; i++)
			map.put(keyConverter.read(session), valConverter.read(session));
		return map;
	}

	protected Map<Object, Object> newMap(int length) {
		return new HashMap<Object, Object>(length);
	}

	@Override
	public void write(Map o, ToByteArrayConverterSession session) throws IOException {
		if (session.handleIfAlreadyConverted(o))
			return;
		Map<Object, Object> map = o;
		DataOutput stream = session.getStream();
		stream.writeInt(map.size());
		if (map.size() == 0)
			return;
		Class keyClass = CH.getClassIfSame(map.keySet());
		Class valClass = CH.getClassIfSame(map.values());
		ObjectToByteArrayConverter converter = session.getConverter();
		ByteArrayConverter keyConverter = converter.getConverter(keyClass);
		ByteArrayConverter valConverter = converter.getConverter(valClass);

		stream.writeByte(keyConverter.getBasicType());
		stream.writeByte(valConverter.getBasicType());

		for (Map.Entry o2 : map.entrySet()) {
			keyConverter.write(o2.getKey(), session);
			valConverter.write(o2.getValue(), session);
		}
	}
}
