/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.converter.bytes;

import java.io.DataInput;
import java.io.IOException;

import com.f1.base.BasicTypes;
import com.f1.utils.FastDataInput;
import com.f1.utils.FastDataOutput;

public class StringToByteArrayConverter extends SimpleByteArrayConverter<String> {

	final public static int UTF_MAXLENGTH = 65535;

	public StringToByteArrayConverter() {
		super(String.class, BasicTypes.STRING);
	}

	static public String readString(DataInput stream) throws IOException {
		return stream.readUTF();
	}

	static public void writeString(String o, FastDataOutput stream) throws IOException {
		stream.writeUTFSupportLarge(o);
	}

	@Override
	protected String read(FastDataInput stream) throws IOException {
		return readString(stream);
	}

	@Override
	protected void write(String o, FastDataOutput stream) throws IOException {
		writeString(o, stream);
	}
}
