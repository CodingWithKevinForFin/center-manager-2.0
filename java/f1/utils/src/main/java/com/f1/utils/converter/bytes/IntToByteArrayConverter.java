/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.converter.bytes;

import java.io.IOException;

import com.f1.base.BasicTypes;
import com.f1.utils.FastDataInput;
import com.f1.utils.FastDataOutput;
import com.f1.utils.OH;

public class IntToByteArrayConverter extends SimpleByteArrayConverter<Integer> {

	public IntToByteArrayConverter(boolean primitive) {
		super(primitive ? int.class : Integer.class, primitive ? BasicTypes.PRIMITIVE_INT : BasicTypes.INT);
	}

	@Override
	protected Integer read(FastDataInput stream) throws IOException {
		return OH.valueOf(stream.readInt());
	}

	@Override
	protected void write(Integer o, FastDataOutput stream) throws IOException {
		stream.writeInt(o);
	}

}
