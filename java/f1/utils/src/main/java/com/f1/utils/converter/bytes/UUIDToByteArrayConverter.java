/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.converter.bytes;

import java.io.IOException;

import com.f1.base.BasicTypes;
import com.f1.base.UUID;
import com.f1.utils.FastDataInput;
import com.f1.utils.FastDataOutput;

public class UUIDToByteArrayConverter extends SimpleByteArrayConverter<UUID> {

	public UUIDToByteArrayConverter() {
		super(UUID.class, BasicTypes.UUID);
	}

	@Override
	protected UUID read(FastDataInput stream) throws IOException {
		return new UUID(stream.readLong(), stream.readLong());
	}

	@Override
	protected void write(UUID o, FastDataOutput stream) throws IOException {
		stream.writeLong(o.getMostSignificantBits());
		stream.writeLong(o.getLeastSignificantBits());
	}

}
