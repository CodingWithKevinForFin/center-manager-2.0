/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.converter.bytes;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import com.f1.base.BasicTypes;
import com.f1.utils.RH;

public class EnumToByteArrayConverter implements ByteArrayConverter<Enum> {

	@Override
	public void write(Enum o, ToByteArrayConverterSession session) throws IOException {
		DataOutput stream = session.getStream();
		stream.writeUTF(o.getClass().getName());
		stream.writeUTF(o.name());
	}

	@Override
	public Enum read(FromByteArrayConverterSession session) throws IOException {
		DataInput stream = session.getStream();
		Class c = RH.getClass(stream.readUTF());
		return Enum.valueOf(c, stream.readUTF());
	}

	@Override
	public byte getBasicType() {
		return BasicTypes.ENUM;
	}

	@Override
	public boolean isCompatible(Class<?> o) {
		return o.isEnum();
	}

}
