package com.f1.utils.converter.json2;

import com.f1.utils.CharReader;

public class BasicFromJsonConverterSession implements FromJsonConverterSession {

	private StringBuilder tempSb = new StringBuilder();
	private CharReader stream;
	private ObjectToJsonConverter converter;

	public BasicFromJsonConverterSession(ObjectToJsonConverter converter, CharReader stream) {
		this.stream = stream;
		this.converter = converter;
	}

	@Override
	public StringBuilder getTempStringBuilder() {
		tempSb.setLength(0);
		return tempSb;
	}

	@Override
	public CharReader getStream() {
		return stream;
	}

	@Override
	public ObjectToJsonConverter getConverter() {
		return converter;
	}

	@Override
	public void skipWhite() {
		stream.skip(ObjectToJsonConverter.WHITE_SPACE);
	}

}
