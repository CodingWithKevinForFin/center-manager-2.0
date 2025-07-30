package com.sjls.f1.sjlscommon;

import java.io.IOException;

import org.joda.time.DateTime;

import com.f1.utils.converter.bytes.AbstractCustomByteArrayConverter;
import com.f1.utils.converter.bytes.FromByteArrayConverterSession;
import com.f1.utils.converter.bytes.ToByteArrayConverterSession;

public class DateTimeConverter extends AbstractCustomByteArrayConverter<DateTime> {

	public DateTimeConverter() {
		super(DateTime.class, DateTime.class.getSimpleName());
	}

	@Override
	public void write(DateTime o, ToByteArrayConverterSession session) throws IOException {
		session.getStream().writeLong(o.getMillis());
	}

	@Override
	public DateTime read(FromByteArrayConverterSession session) throws IOException {
		return new DateTime((Long) session.getStream().readLong());
	}

}
