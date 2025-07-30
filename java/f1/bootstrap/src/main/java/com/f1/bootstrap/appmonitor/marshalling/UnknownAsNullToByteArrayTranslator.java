/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.bootstrap.appmonitor.marshalling;

import java.io.IOException;
import java.util.logging.Logger;

import com.f1.base.BasicTypes;
import com.f1.utils.FastDataOutput;
import com.f1.utils.converter.bytes.ByteArrayConverterTranslator;
import com.f1.utils.converter.bytes.FromByteArrayConverterSession;
import com.f1.utils.converter.bytes.StringToByteArrayConverter;
import com.f1.utils.converter.bytes.ToByteArrayConverterSession;

public class UnknownAsNullToByteArrayTranslator extends ByteArrayConverterTranslator<Object, Object> {
	private static final Logger log = Logger.getLogger(UnknownAsNullToByteArrayTranslator.class.getName());

	public UnknownAsNullToByteArrayTranslator() {
		super(Object.class, BasicTypes.STRING, Object.class, BasicTypes.NULL);
	}

	@Override
	public void writeObject(Object o, ToByteArrayConverterSession session) throws IOException {
		FastDataOutput stream = session.getStream();
		String s;
		try {
			s = o.toString();
		} catch (Exception e) {
			s = "toString() threw exception for:" + o.getClass().getName();
		}
		StringToByteArrayConverter.writeString(s, stream);
	}

	@Override
	public String readObject(FromByteArrayConverterSession session) throws IOException {
		return null;
	}

}
