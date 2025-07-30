/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.converter.bytes;

import java.io.IOException;

import com.f1.base.BasicTypes;
import com.f1.utils.BasicFixPoint;
import com.f1.utils.FastDataInput;
import com.f1.utils.FastDataOutput;
import com.f1.utils.FixPoint;

public class FixPointToByteArrayConverter extends SimpleByteArrayConverter<FixPoint> {

	public FixPointToByteArrayConverter() {
		super(FixPoint.class, BasicTypes.FIXPOINT);
	}

	@Override
	protected FixPoint read(FastDataInput stream) throws IOException {
		return BasicFixPoint.nuw(stream.readLong());
	}

	@Override
	protected void write(FixPoint o, FastDataOutput stream) throws IOException {
		stream.writeLong(o.getBytes());
	}

}
