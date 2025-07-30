/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.converter.bytes;

import java.io.IOException;

import com.f1.base.BasicTypes;
import com.f1.utils.FastDataInput;
import com.f1.utils.FastDataOutput;
import com.f1.utils.OH;

public class ShortToByteArrayConverter extends SimpleByteArrayConverter<Short> {

	public ShortToByteArrayConverter(boolean primitive) {
		super(primitive ? short.class : Short.class, primitive ? BasicTypes.PRIMITIVE_SHORT : BasicTypes.SHORT);
	}

	@Override
	protected Short read(FastDataInput stream) throws IOException {
		return OH.valueOf(stream.readShort());
	}

	@Override
	protected void write(Short o, FastDataOutput stream) throws IOException {
		stream.writeShort(o);
	}

}
