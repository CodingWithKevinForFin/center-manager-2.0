/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.bootstrap.appmonitor.marshalling;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import com.f1.base.BasicTypes;
import com.f1.base.ValuedEnum;
import com.f1.utils.converter.bytes.ByteArrayConverterTranslator;
import com.f1.utils.converter.bytes.FromByteArrayConverterSession;
import com.f1.utils.converter.bytes.ToByteArrayConverterSession;

public class ValuedEnumAsStringToByteArrayTranslator extends ByteArrayConverterTranslator<String, ValuedEnum> {

	public ValuedEnumAsStringToByteArrayTranslator() {
		super(String.class, BasicTypes.STRING, ValuedEnum.class, BasicTypes.VALUED_ENUM);
	}

	@Override
	public String readObject(FromByteArrayConverterSession session) throws IOException {
		DataInput stream = session.getStream();
		String c = stream.readUTF();
		return c + "." + stream.readUTF();
	}

	@Override
	public void writeObject(ValuedEnum o, ToByteArrayConverterSession session) throws IOException {
		DataOutput stream = session.getStream();
		String s = o.getClass().getName() + "." + o.getEnumValue();
		stream.writeInt(s.length());
		stream.writeChars(s);
	}

}
