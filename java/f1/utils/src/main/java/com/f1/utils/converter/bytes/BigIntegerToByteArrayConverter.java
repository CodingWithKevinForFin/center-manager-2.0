/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.converter.bytes;

import java.io.IOException;
import java.math.BigInteger;

import com.f1.base.BasicTypes;
import com.f1.utils.FastDataInput;
import com.f1.utils.FastDataOutput;

public class BigIntegerToByteArrayConverter extends SimpleByteArrayConverter<BigInteger> {

	public BigIntegerToByteArrayConverter() {
		super(BigInteger.class, BasicTypes.BIGINTEGER);
	}

	@Override
	protected BigInteger read(FastDataInput stream) throws IOException {
		final byte[] bytes = new byte[readInt(stream)];
		stream.readFully(bytes);
		return new BigInteger(bytes);
	}
	@Override
	protected void write(BigInteger o, FastDataOutput stream) throws IOException {
		final byte[] bytes = o.toByteArray();
		writeInt(stream, (int) bytes.length);
		stream.write(bytes);
	}

	public static void writeInt(FastDataOutput stream, int n) throws IOException {
		if (n > Byte.MIN_VALUE && n < Byte.MAX_VALUE)
			stream.writeByte((byte) n);
		else {
			stream.write(Byte.MAX_VALUE);
			stream.writeInt(n);
		}
	}
	public static int readInt(FastDataInput stream) throws IOException {
		byte n = stream.readByte();
		if (n != Byte.MAX_VALUE)
			return n;
		return stream.readInt();
	}
}
