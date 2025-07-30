package com.f1.utils.converter.bytes;

import java.io.IOException;

import com.f1.base.BasicTypes;
import com.f1.utils.FastDataInput;
import com.f1.utils.FastDataOutput;

public class StringBuilderToByteArrayConverter extends SimpleByteArrayConverter<StringBuilder> {

	public StringBuilderToByteArrayConverter() {
		super(StringBuilder.class, BasicTypes.STRING_BUILDER);
	}
	@Override
	protected StringBuilder read(FastDataInput stream) throws IOException {
		final int capacity = stream.readInt();
		if (capacity == -1)
			return null;

		StringBuilder sb = new StringBuilder(capacity);

		return sb.append(stream.readUTF());
	}

	@Override
	protected void write(StringBuilder o, FastDataOutput stream) throws IOException {
		if (o == null) {
			stream.writeInt(-1);
			return;
		}
		stream.writeInt(o.capacity());
		stream.writeUTFSupportLarge(o);

	}

}
