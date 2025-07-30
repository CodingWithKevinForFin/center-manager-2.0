package com.f1.utils.converter.bytes;

import java.io.IOException;

import com.f1.base.BasicTypes;
import com.f1.utils.FastDataInput;
import com.f1.utils.FastDataOutput;
import com.f1.utils.OH;

public class NumberToByteArrayConverter extends SimpleByteArrayConverter<Number> {

	private byte valueType = BasicTypes.UNDEFINED;

	public NumberToByteArrayConverter() {
		super(null, (byte) 1000);
	}

	@Override
	protected void write(Number o, FastDataOutput stream) throws IOException {
		if (o instanceof Double) {
			this.valueType = BasicTypes.DOUBLE;
			stream.writeDouble((Double) o);
		}

	}

	@Override
	protected Number read(FastDataInput stream) throws IOException {

		switch (this.valueType) {
			case BasicTypes.DOUBLE:
				OH.valueOf(stream.readDouble());
			default:
				throw new UnsupportedOperationException();
		}
	}

}
