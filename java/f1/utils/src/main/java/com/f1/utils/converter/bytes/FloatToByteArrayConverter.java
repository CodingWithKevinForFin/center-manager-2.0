/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.converter.bytes;

import java.io.IOException;

import com.f1.base.BasicTypes;
import com.f1.utils.FastDataInput;
import com.f1.utils.FastDataOutput;
import com.f1.utils.OH;

public class FloatToByteArrayConverter extends SimpleByteArrayConverter<Float> {

	public FloatToByteArrayConverter(boolean primitive) {
		super(primitive ? float.class : Float.class, primitive ? BasicTypes.PRIMITIVE_FLOAT : BasicTypes.FLOAT);
	}

	@Override
	protected Float read(FastDataInput stream) throws IOException {
		return OH.valueOf(stream.readFloat());
	}

	@Override
	protected void write(Float o, FastDataOutput stream) throws IOException {
		stream.writeFloat(o);
	}

}
