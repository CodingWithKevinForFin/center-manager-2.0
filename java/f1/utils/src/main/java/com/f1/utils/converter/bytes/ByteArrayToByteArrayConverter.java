/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.converter.bytes;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import com.f1.base.BasicTypes;
import com.f1.utils.FastDataInput;
import com.f1.utils.FastDataOutput;
import com.f1.utils.OH;

public class ByteArrayToByteArrayConverter extends SimpleByteArrayConverter<byte[]> {

	public ByteArrayToByteArrayConverter() {
		super(byte[].class, BasicTypes.PRIMITIVE_BYTE_ARRAY);
	}

	public static void writeByteArray(byte[] o, DataOutput stream) throws IOException {
		stream.writeInt(o.length);
		stream.write(o);
	}

	@Override
	protected byte[] read(FastDataInput stream) throws IOException {
		return readByteArray(stream);
	}

	public static byte[] readByteArray(DataInput stream) throws IOException {
		int i = stream.readInt();
		if (i == 0)
			return OH.EMPTY_BYTE_ARRAY;
		byte[] r = new byte[i];
		stream.readFully(r);
		return r;
	}

	@Override
	protected void write(byte[] o, FastDataOutput stream) throws IOException {
		writeByteArray(o, stream);
	}

}
