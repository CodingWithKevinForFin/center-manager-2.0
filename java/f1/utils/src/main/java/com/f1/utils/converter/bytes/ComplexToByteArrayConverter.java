/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.converter.bytes;

import java.io.IOException;

import com.f1.base.BasicTypes;
import com.f1.base.Complex;
import com.f1.utils.FastDataInput;
import com.f1.utils.FastDataOutput;

public class ComplexToByteArrayConverter extends SimpleByteArrayConverter<Complex> {

	public ComplexToByteArrayConverter() {
		super(Complex.class, BasicTypes.COMPLEX);
	}

	@Override
	protected Complex read(FastDataInput stream) throws IOException {
		return new Complex(stream.readDouble(), stream.readDouble());
	}

	@Override
	protected void write(Complex o, FastDataOutput stream) throws IOException {
		stream.writeDouble(o.real());
		stream.writeDouble(o.imaginary());
	}

}
