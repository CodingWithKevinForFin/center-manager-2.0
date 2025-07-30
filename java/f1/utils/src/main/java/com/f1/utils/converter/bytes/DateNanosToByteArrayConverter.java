/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.converter.bytes;

import java.io.IOException;

import com.f1.base.BasicTypes;
import com.f1.base.DateNanos;
import com.f1.utils.FastDataInput;
import com.f1.utils.FastDataOutput;

public class DateNanosToByteArrayConverter extends SimpleByteArrayConverter<DateNanos> {

	public DateNanosToByteArrayConverter() {
		super(DateNanos.class, BasicTypes.DATE_NANOS);
	}

	@Override
	protected DateNanos read(FastDataInput stream) throws IOException {
		return new DateNanos(stream.readLong());
	}

	@Override
	protected void write(DateNanos o, FastDataOutput stream) throws IOException {
		stream.writeLong(o.getTimeNanos());
	}

}
