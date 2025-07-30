/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.converter.json2;

import com.f1.utils.SH;

public class BasicToJsonConverterSession implements ToJsonConverterSession {

	private StringBuilder stream;
	private ObjectToJsonConverter converter;
	private int depth;

	public BasicToJsonConverterSession(ObjectToJsonConverter converter, StringBuilder stream) {
		this.converter = converter;
		this.stream = stream;
	}

	@Override
	public StringBuilder getStream() {
		return stream;
	}

	@Override
	public ObjectToJsonConverter getConverter() {
		return converter;
	}

	@Override
	public int getDepth() {
		return depth;
	}

	@Override
	public int pushDepth() {
		return ++depth;
	}

	@Override
	public int popDepth() {
		return --depth;
	}

	@Override
	public void appendPrefix() {
		if (converter.getCompactMode() != ObjectToJsonConverter.MODE_COMPACT)
			SH.repeat(' ', depth * 2, stream);
	}

	@Override
	public void appendNewLine() {
		if (converter.getCompactMode() != ObjectToJsonConverter.MODE_COMPACT)
			stream.append(SH.NEWLINE);
	}
}
