/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.converter.bytes;

import java.io.IOException;

import com.f1.base.BasicTypes;
import com.f1.utils.FastDataInput;
import com.f1.utils.FastDataOutput;

public class BooleanToByteArrayConverter extends SimpleByteArrayConverter<Boolean> {

	public BooleanToByteArrayConverter(boolean primitive) {
		super(primitive ? boolean.class : Boolean.class, primitive ? BasicTypes.BOOLEAN : BasicTypes.PRIMITIVE_BOOLEAN);
	}

	@Override
	protected Boolean read(FastDataInput stream) throws IOException {
		return stream.readBoolean();
	}

	@Override
	protected void write(Boolean o, FastDataOutput stream) throws IOException {
		stream.writeBoolean(o);
	}

}
