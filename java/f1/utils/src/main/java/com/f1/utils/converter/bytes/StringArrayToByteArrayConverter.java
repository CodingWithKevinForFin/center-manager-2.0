/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.converter.bytes;

import java.io.IOException;

import com.f1.base.BasicTypes;
import com.f1.utils.FastDataInput;
import com.f1.utils.FastDataOutput;

public class StringArrayToByteArrayConverter extends SimpleByteArrayConverter<String[]> {

	final public static int UTF_MAXLENGTH = 65535;
	final public static StringToByteArrayConverter STRING_CONVERTER = new StringToByteArrayConverter();

	public StringArrayToByteArrayConverter() {
		super(String[].class, BasicTypes.STRING_ARRAY);
	}

	static public String[] readStringArray(FastDataInput stream) throws IOException {
		final int length = stream.readInt();
		if (length == -1)
			return null;

		String sa[] = new String[length];
		for (int i = 0; i < length; i++)
			sa[i] = STRING_CONVERTER.readString(stream);

		return sa;
	}

	static public void writeStringArray(String[] o, FastDataOutput stream) throws IOException {
		if (o == null) {
			stream.writeInt(-1);
			return;
		}
		stream.writeInt(o.length);
		for (String s : o)
			STRING_CONVERTER.writeString(s, stream);
	}

	@Override
	protected String[] read(FastDataInput stream) throws IOException {
		return readStringArray(stream);
	}

	@Override
	protected void write(String[] o, FastDataOutput stream) throws IOException {
		writeStringArray(o, stream);
	}
}
