/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.converter.bytes;

import java.io.IOException;

import com.f1.base.BasicTypes;
import com.f1.utils.FastDataInput;
import com.f1.utils.FastDataOutput;

public class NullToByteArrayConverter extends SimpleByteArrayConverter<Object> {

	public NullToByteArrayConverter() {
		super(null, BasicTypes.NULL);
	}

	@Override
	protected Float read(FastDataInput stream) throws IOException {
		return null;
	}

	@Override
	protected void write(Object o, FastDataOutput stream) throws IOException {
	}

	@Override
	public boolean isCompatible(Class<?> o) {
		return o == null;
	}
}
