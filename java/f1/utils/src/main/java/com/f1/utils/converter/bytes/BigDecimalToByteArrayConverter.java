/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.converter.bytes;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

import com.f1.base.BasicTypes;
import com.f1.utils.FastDataInput;
import com.f1.utils.FastDataOutput;

public class BigDecimalToByteArrayConverter extends SimpleByteArrayConverter<BigDecimal> {

	private static final BigInteger MAX_LONG = BigInteger.valueOf(Long.MAX_VALUE);
	private static final BigInteger MIN_LONG = BigInteger.valueOf(Long.MIN_VALUE);

	public BigDecimalToByteArrayConverter() {
		super(BigDecimal.class, BasicTypes.BIGDECIMAL);
	}

	@Override
	protected BigDecimal read(FastDataInput stream) throws IOException {
		final int scale = BigIntegerToByteArrayConverter.readInt(stream);
		final byte[] bytes = new byte[BigIntegerToByteArrayConverter.readInt(stream)];
		stream.readFully(bytes);
		return new BigDecimal(new BigInteger(bytes), scale);
	}
	@Override
	protected void write(BigDecimal o, FastDataOutput stream) throws IOException {
		BigInteger intValue = o.unscaledValue();
		final byte[] bytes = intValue.toByteArray();
		BigIntegerToByteArrayConverter.writeInt(stream, o.scale());
		BigIntegerToByteArrayConverter.writeInt(stream, bytes.length);
		stream.write(bytes);
	}
}
