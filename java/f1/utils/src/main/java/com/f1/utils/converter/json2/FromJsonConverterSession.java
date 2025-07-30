package com.f1.utils.converter.json2;

import com.f1.utils.CharReader;

public interface FromJsonConverterSession {

	public StringBuilder getTempStringBuilder();

	public CharReader getStream();

	public ObjectToJsonConverter getConverter();

	public void skipWhite();

}
