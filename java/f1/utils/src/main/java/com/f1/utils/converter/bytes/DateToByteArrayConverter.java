/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.converter.bytes;

import java.io.IOException;
import java.util.Date;

import com.f1.base.BasicTypes;
import com.f1.utils.FastDataInput;
import com.f1.utils.FastDataOutput;

public class DateToByteArrayConverter extends SimpleByteArrayConverter<Date> {

	public DateToByteArrayConverter() {
		super(Date.class, BasicTypes.DATE);
	}

	@Override
	protected Date read(FastDataInput stream) throws IOException {
		return new Date(stream.readLong());
	}

	@Override
	protected void write(Date o, FastDataOutput stream) throws IOException {
		stream.writeLong(o.getTime());
	}

}
