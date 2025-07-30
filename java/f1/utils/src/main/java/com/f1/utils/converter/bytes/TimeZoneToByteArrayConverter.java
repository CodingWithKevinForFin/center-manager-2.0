/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.converter.bytes;

import java.io.IOException;
import java.util.TimeZone;

import com.f1.base.BasicTypes;
import com.f1.utils.FastDataInput;
import com.f1.utils.FastDataOutput;

public class TimeZoneToByteArrayConverter extends SimpleByteArrayConverter<TimeZone> {

	public TimeZoneToByteArrayConverter() {
		super(TimeZone.class, BasicTypes.TIME_ZONE);
	}

	@Override
	protected TimeZone read(FastDataInput stream) throws IOException {
		return TimeZone.getTimeZone(stream.readUTF());
	}

	@Override
	protected void write(TimeZone o, FastDataOutput stream) throws IOException {
		stream.writeUTF(o.getID());
	}

}
