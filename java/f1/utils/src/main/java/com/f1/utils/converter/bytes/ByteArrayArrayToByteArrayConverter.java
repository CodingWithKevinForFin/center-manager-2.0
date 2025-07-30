/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.converter.bytes;

import java.io.IOException;

import com.f1.base.BasicTypes;
import com.f1.utils.FastDataInput;
import com.f1.utils.FastDataOutput;
import com.f1.utils.OH;

public class ByteArrayArrayToByteArrayConverter extends SimpleByteArrayConverter<byte[][]> {

	public ByteArrayArrayToByteArrayConverter() {
		super(byte[][].class, BasicTypes.BYTE_ARRAY_ARRAY);
	}

	@Override
	protected byte[][] read(FastDataInput stream) throws IOException {
		int n = stream.readInt();
		if (n == 0)
			return OH.EMPTY_BYTE_ARRAY_ARRAY;
		byte[][] r = new byte[n][];
		for (int i = 0; i < r.length; i++)
			r[i] = ByteArrayToByteArrayConverter.readByteArray(stream);
		return r;
	}

	@Override
	protected void write(byte[][] o, FastDataOutput stream) throws IOException {
		stream.writeInt(o.length);
		for (byte[] b : o)
			ByteArrayToByteArrayConverter.writeByteArray(b, stream);
	}

}
