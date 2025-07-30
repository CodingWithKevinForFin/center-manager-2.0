/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.converter.bytes;

import java.io.IOException;

import com.f1.base.BasicTypes;
import com.f1.base.Bytes;
import com.f1.utils.FastDataInput;
import com.f1.utils.FastDataOutput;

public class BytesToByteArrayConverter extends SimpleByteArrayConverter<Bytes> {

	public BytesToByteArrayConverter() {
		super(Bytes.class, BasicTypes.BYTES);
	}

	@Override
	protected Bytes read(FastDataInput stream) throws IOException {
		final int i = stream.readInt();
		if (i == 0)
			return Bytes.EMPTY;
		byte[] r = new byte[i];
		stream.readFully(r);
		return new Bytes(r);
	}

	@Override
	protected void write(Bytes o, FastDataOutput stream) throws IOException {
		stream.writeInt(o.length());
		stream.write(o.getBytes());
	}

}
