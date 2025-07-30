/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.converter.bytes;

import java.io.IOException;

import com.f1.base.BasicTypes;
import com.f1.utils.FastDataInput;
import com.f1.utils.FastDataOutput;
import com.f1.utils.OH;

public class IntArrayToByteArrayConverter extends SimpleByteArrayConverter<int[]> {

	public IntArrayToByteArrayConverter() {
		super(int[].class, BasicTypes.PRIMITIVE_INT_ARRAY);
	}

	@Override
	protected int[] read(FastDataInput stream) throws IOException {
		final int i = stream.readInt();
		if (i == 0)
			return OH.EMPTY_INT_ARRAY;
		return stream.readFully(new int[i], 0, i);
	}

	@Override
	protected void write(int[] o, FastDataOutput stream) throws IOException {
		stream.writeInt(o.length);
		stream.write(o, 0, o.length);
	}

}
