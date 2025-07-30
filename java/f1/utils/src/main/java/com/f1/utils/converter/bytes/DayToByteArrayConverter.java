/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.converter.bytes;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.TimeZone;

import com.f1.base.BasicTypes;
import com.f1.base.Day;
import com.f1.utils.BasicDay;

public class DayToByteArrayConverter extends AbstractCircRefByteArrayConverter<Day> {

	public DayToByteArrayConverter() {
		super(Day.class, BasicTypes.DAY);
	}

	@Override
	protected Day read(DataInput stream) throws IOException {
		TimeZone tz = TimeZone.getTimeZone(stream.readUTF());
		long startNanos = stream.readLong();
		short year = stream.readShort();
		byte month = stream.readByte();
		byte day = stream.readByte();
		return BasicDay.createUnchecked(tz, startNanos, year, month, day);
	}

	@Override
	protected void write(Day o, DataOutput stream) throws IOException {
		stream.writeUTF(o.getTimeZone().getID());
		stream.writeLong(o.getStartNanos());
		stream.writeShort(o.getYear());
		stream.writeByte(o.getMonth());
		stream.writeByte(o.getDay());
	}

}
