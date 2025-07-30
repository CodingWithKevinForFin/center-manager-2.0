/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.converter.bytes;

import java.io.IOException;

import com.f1.base.BasicTypes;
import com.f1.utils.FastDataInput;
import com.f1.utils.FastDataOutput;
import com.f1.utils.OH;

public class CharArrayToByteArrayConverter extends SimpleByteArrayConverter<char[]> {

	public CharArrayToByteArrayConverter() {
		super(char[].class, BasicTypes.PRIMITIVE_CHAR_ARRAY);
	}

	@Override
	protected char[] read(FastDataInput stream) throws IOException {
		final int i = stream.readInt();
		if (i == 0)
			return OH.EMPTY_CHAR_ARRAY;
		return stream.readFully(new char[i], 0, i);
	}

	@Override
	protected void write(char[] o, FastDataOutput stream) throws IOException {
		stream.writeInt(o.length);
		stream.write(o, 0, o.length);
	}

}
