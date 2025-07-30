/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.converter.bytes;

import java.io.IOException;

import com.f1.base.BasicTypes;
import com.f1.utils.FastDataInput;
import com.f1.utils.FastDataOutput;

public class ByteToByteArrayConverter extends SimpleByteArrayConverter<Byte> {

	public ByteToByteArrayConverter(boolean primitive) {
		super(primitive ? byte.class : Byte.class, primitive ? BasicTypes.PRIMITIVE_BYTE : BasicTypes.BYTE);
	}

	@Override
	protected Byte read(FastDataInput stream) throws IOException {
		return stream.readByte();
	}

	@Override
	protected void write(Byte o, FastDataOutput stream) throws IOException {
		stream.writeByte(o);
	}

}
