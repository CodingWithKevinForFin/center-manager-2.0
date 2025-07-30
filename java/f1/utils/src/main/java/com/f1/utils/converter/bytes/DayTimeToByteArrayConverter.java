/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.converter.bytes;

import java.io.IOException;
import java.util.TimeZone;

import com.f1.base.BasicTypes;
import com.f1.base.DayTime;
import com.f1.utils.BasicDayTime;
import com.f1.utils.FastDataInput;
import com.f1.utils.FastDataOutput;

public class DayTimeToByteArrayConverter extends SimpleByteArrayConverter<DayTime> {

	public DayTimeToByteArrayConverter() {
		super(DayTime.class, BasicTypes.DAYTIME);
	}

	@Override
	protected DayTime read(FastDataInput stream) throws IOException {
		final TimeZone tz = TimeZone.getTimeZone(stream.readUTF());
		return new BasicDayTime(tz, stream.readLong());
	}

	@Override
	protected void write(DayTime o, FastDataOutput stream) throws IOException {
		stream.writeUTF(o.getTimeZone().getID());
		stream.writeLong(o.getTimeNanos());
	}

}
