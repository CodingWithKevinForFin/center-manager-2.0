package com.f1.utils.converter.json2;

import com.f1.utils.CharReader;

public class BooleanToJsonConverter extends AbstractJsonConverter<Boolean> {
	private static final int[] TF = new int[] { 't', 'f' };
	private static final char[] RUE = "rue".toCharArray();
	private static final char[] ALSE = "alse".toCharArray();

	public BooleanToJsonConverter() {
		super(Boolean.class);
	}

	@Override
	public void objectToString(Boolean o, ToJsonConverterSession session) {
		StringBuilder out = session.getStream();
		out.append(o);
	}

	@Override
	public Object stringToObject(FromJsonConverterSession reader) {
		CharReader stream = reader.getStream();
		switch (stream.expectAny(TF)) {
			case 't':
				stream.expectSequence(RUE);
				return Boolean.TRUE;
			case 'f':
				stream.expectSequence(ALSE);
				return Boolean.FALSE;
			default:
				throw new RuntimeException("at " + stream.getCountRead() + ":expecting true or false");
		}
	}
}
