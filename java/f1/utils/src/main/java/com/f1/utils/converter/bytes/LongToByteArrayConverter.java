/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.converter.bytes;

import java.io.IOException;

import com.f1.base.BasicTypes;
import com.f1.utils.FastDataInput;
import com.f1.utils.FastDataOutput;
import com.f1.utils.OH;

public class LongToByteArrayConverter extends SimpleByteArrayConverter<Long> {

	public LongToByteArrayConverter(boolean primitive) {
		super(primitive ? long.class : Long.class, primitive ? BasicTypes.PRIMITIVE_LONG : BasicTypes.LONG);
	}

	@Override
	protected Long read(FastDataInput stream) throws IOException {
		return OH.valueOf(stream.readLong());
	}

	@Override
	protected void write(Long o, FastDataOutput stream) throws IOException {
		stream.writeLong(o);
	}
}
