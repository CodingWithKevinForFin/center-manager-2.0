/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.converter.bytes;

import java.io.IOException;

import com.f1.base.BasicTypes;
import com.f1.utils.FastDataInput;
import com.f1.utils.FastDataOutput;
import com.f1.utils.OH;

public class CharToByteArrayConverter extends SimpleByteArrayConverter<Character> {

	public CharToByteArrayConverter(boolean primitive) {
		super(primitive ? char.class : Character.class, primitive ? BasicTypes.CHAR : BasicTypes.PRIMITIVE_CHAR);
	}

	@Override
	protected Character read(FastDataInput stream) throws IOException {
		return OH.valueOf(stream.readChar());
	}

	@Override
	protected void write(Character o, FastDataOutput stream) throws IOException {
		stream.writeChar(o);
	}

}
