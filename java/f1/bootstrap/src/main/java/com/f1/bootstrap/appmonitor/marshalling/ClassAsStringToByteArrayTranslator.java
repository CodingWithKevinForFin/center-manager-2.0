/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.bootstrap.appmonitor.marshalling;

import java.io.IOException;
import java.util.logging.Logger;

import com.f1.base.BasicTypes;
import com.f1.utils.converter.bytes.ByteArrayConverterTranslator;
import com.f1.utils.converter.bytes.FromByteArrayConverterSession;
import com.f1.utils.converter.bytes.StringToByteArrayConverter;
import com.f1.utils.converter.bytes.ToByteArrayConverterSession;

public class ClassAsStringToByteArrayTranslator extends ByteArrayConverterTranslator<String, Class> {
	private static final Logger log = Logger.getLogger(ClassAsStringToByteArrayTranslator.class.getName());

	public ClassAsStringToByteArrayTranslator() {
		super(String.class, BasicTypes.STRING, Class.class, BasicTypes.CLASS);
	}

	@Override
	public String readObject(FromByteArrayConverterSession session) throws IOException {
		return StringToByteArrayConverter.readString(session.getStream());
	}

	@Override
	public void writeObject(Class o, ToByteArrayConverterSession session) throws IOException {
		StringToByteArrayConverter.writeString(o.getName(), session.getStream());

	}

}
