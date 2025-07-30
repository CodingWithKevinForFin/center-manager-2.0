/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.bootstrap.appmonitor.marshalling;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import com.f1.base.BasicTypes;
import com.f1.utils.converter.bytes.ByteArrayConverter;
import com.f1.utils.converter.bytes.ByteArrayConverterTranslator;
import com.f1.utils.converter.bytes.FromByteArrayConverterSession;
import com.f1.utils.converter.bytes.ToByteArrayConverterSession;

public class ArrayAsListToByteArrayTranslator extends ByteArrayConverterTranslator<String, Object[]> {

	public ArrayAsListToByteArrayTranslator() {
		super(String.class, BasicTypes.LIST, Object[].class, BasicTypes.OBJECT);
	}

	@Override
	public String readObject(FromByteArrayConverterSession session) throws IOException {
		DataInput stream = session.getStream();
		String c = stream.readUTF();
		return c + "." + stream.readUTF();
	}

	@Override
	public void writeObject(Object[] array, ToByteArrayConverterSession session) throws IOException {
		DataOutput stream = session.getStream();
		if (session.handleIfAlreadyConverted(array))
			return;
		int length = array.length;
		stream.writeInt(length);
		if (length == 0)
			return;
		ByteArrayConverter converter = session.getConverter();
		stream.writeByte(converter.getBasicType());
		for (final Object o : array)
			converter.write(o, session);
	}
}
