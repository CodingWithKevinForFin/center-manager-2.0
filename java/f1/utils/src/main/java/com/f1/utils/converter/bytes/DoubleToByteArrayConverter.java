/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.converter.bytes;

import java.io.IOException;

import com.f1.base.BasicTypes;
import com.f1.utils.FastDataInput;
import com.f1.utils.FastDataOutput;
import com.f1.utils.OH;

public class DoubleToByteArrayConverter extends SimpleByteArrayConverter<Double> {

	public DoubleToByteArrayConverter(boolean primitive) {
		super(primitive ? double.class : Double.class, primitive ? BasicTypes.PRIMITIVE_DOUBLE : BasicTypes.DOUBLE);
	}

	@Override
	protected Double read(FastDataInput stream) throws IOException {
		return OH.valueOf(stream.readDouble());
	}

	@Override
	protected void write(Double o, FastDataOutput stream) throws IOException {
		stream.writeDouble(o);
	}

}
