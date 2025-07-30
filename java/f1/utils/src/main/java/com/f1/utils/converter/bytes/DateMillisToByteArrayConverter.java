/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.converter.bytes;

import java.io.IOException;

import com.f1.base.BasicTypes;
import com.f1.base.DateMillis;
import com.f1.utils.FastDataInput;
import com.f1.utils.FastDataOutput;

public class DateMillisToByteArrayConverter extends SimpleByteArrayConverter<DateMillis> {

	public DateMillisToByteArrayConverter() {
		super(DateMillis.class, BasicTypes.DATE_MILLIS);
	}

	@Override
	protected DateMillis read(FastDataInput stream) throws IOException {
		return new DateMillis(stream.readLong());
	}

	@Override
	protected void write(DateMillis o, FastDataOutput stream) throws IOException {
		stream.writeLong(o.getDate());
	}

}
