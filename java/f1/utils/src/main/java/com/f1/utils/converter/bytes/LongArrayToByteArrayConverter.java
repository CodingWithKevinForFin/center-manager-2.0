/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.converter.bytes;

import java.io.DataInput;
import java.io.IOException;

import com.f1.base.BasicTypes;
import com.f1.utils.FastDataInput;
import com.f1.utils.FastDataOutput;
import com.f1.utils.OH;

public class LongArrayToByteArrayConverter extends SimpleByteArrayConverter<long[]> {

	public LongArrayToByteArrayConverter() {
		super(long[].class, BasicTypes.PRIMITIVE_LONG_ARRAY);
	}

	@Override
	protected long[] read(FastDataInput stream) throws IOException {
		return readLongs(stream);
	}

	public static long[] readLongs(DataInput stream) throws IOException {
		final int i = stream.readInt();
		if (i == 0)
			return OH.EMPTY_LONG_ARRAY;
		if (i == -1)
			return null;
		long r[] = new long[i];
		for (int j = 0; j < i; j++)
			r[j] = stream.readLong();
		return r;
	}

	@Override
	protected void write(long[] o, FastDataOutput stream) throws IOException {
		writeLongs(o, stream);
	}

	public static void writeLongs(long[] o, FastDataOutput stream) throws IOException {
		if (o == null) {
			stream.writeInt(-1);
			return;
		}
		stream.writeInt(o.length);
		for (int i = 0; i < o.length; i++)
			stream.writeLong(o[i]);
	}

}
