package com.sjls.f1.sjlscommon;

import java.io.IOException;
import java.util.Date;

import org.joda.time.DateTime;

import com.f1.utils.converter.bytes.AbstractCustomByteArrayConverter;
import com.f1.utils.converter.bytes.FromByteArrayConverterSession;
import com.f1.utils.converter.bytes.ObjectToByteArrayConverter;
import com.f1.utils.converter.bytes.ToByteArrayConverterSession;
import com.sjls.algos.eo.common.BinTradeData;
import com.sjls.algos.eo.common.IBinTradeData;

public class BinDataConverter extends AbstractCustomByteArrayConverter<IBinTradeData> {

	public BinDataConverter() {
		super(IBinTradeData.class, IBinTradeData.class.getSimpleName());
	}

	@Override
	public void write(IBinTradeData o, ToByteArrayConverterSession session) throws IOException {
		final ObjectToByteArrayConverter converter = session.getConverter();
		converter.write(o.getStartTime(), session);
		session.getStream().writeInt(o.getBinLengthInSecs());
		session.getStream().writeInt(o.getShares());
	}

	@Override
	public IBinTradeData read(FromByteArrayConverterSession session) throws IOException {
		final ObjectToByteArrayConverter converter = session.getConverter();
		final Date date = (Date) converter.read(session);
		return new BinTradeData(date == null ? null : new DateTime(date.getTime()), session.getStream().readInt(), session.getStream().readInt());
	}

}
