/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.converter.bytes;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import com.f1.base.BasicTypes;
import com.f1.base.ValuedEnum;
import com.f1.base.ValuedEnumCache;
import com.f1.utils.RH;

public class ValuedEnumToByteArrayConverter implements ByteArrayConverter<ValuedEnum> {

	@Override
	public void write(ValuedEnum o, ToByteArrayConverterSession session) throws IOException {
		DataOutput stream = session.getStream();
		stream.writeUTF(o.getClass().getName());
		session.getConverter().write(o.getEnumValue(), session);
	}

	@Override
	public ValuedEnum read(FromByteArrayConverterSession session) throws IOException {
		DataInput stream = session.getStream();
		return ValuedEnumCache.getEnumValue((Class) RH.getClass(stream.readUTF()), session.getConverter().read(session));
	}

	@Override
	public byte getBasicType() {
		return BasicTypes.VALUED_ENUM;
	}

	@Override
	public boolean isCompatible(Class<?> o) {
		return ValuedEnum.class.isAssignableFrom(o);
	}

}
