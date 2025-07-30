package com.f1.utils.converter.json2;

import com.f1.utils.CharReader;
import com.f1.utils.SH;

public class NumberToJsonConverter extends AbstractJsonConverter<Number> {

	public NumberToJsonConverter() {
		super(Number.class);
	}

	@Override
	public void objectToString(Number o, ToJsonConverterSession session) {
		StringBuilder out = session.getStream();
		if (o instanceof Double) {
			Double d = (Double) o;
			if (d.isNaN())
				appendNaN(out, SH.NAN, session);
			else if (d.isInfinite()) {
				if (d.equals(Double.NEGATIVE_INFINITY))
					appendNaN(out, SH.NEG_INFINITY, session);
				else
					appendNaN(out, SH.POS_INFINITY, session);
			} else
				out.append(d);
		} else if (o instanceof Float) {
			Float d = (Float) o;
			if (d.isNaN())
				appendNaN(out, SH.NAN, session);
			else if (d.isInfinite()) {
				if (d.equals(Float.NEGATIVE_INFINITY))
					appendNaN(out, SH.NEG_INFINITY, session);
				else
					appendNaN(out, SH.POS_INFINITY, session);
			} else
				out.append(d);
		} else
			out.append(o);
	}

	private void appendNaN(StringBuilder out, String text, ToJsonConverterSession session) {
		if (session.getConverter().getTreatNanAsNull())
			out.append("null");
		else
			out.append(text);
	}

	@Override
	public Object stringToObject(FromJsonConverterSession reader) {
		final CharReader stream = reader.getStream();
		final StringBuilder sb = reader.getTempStringBuilder();
		for (;;) {
			final int c = stream.readCharOrEof();
			switch (c) {
				case ']':
				case '}':
				case ',':
				case ' ':
				case '\n':
				case '\t':
				case '\r':
					stream.pushBack((char) c);
					return SH.parseConstant(sb);
				case CharReader.EOF:
					return SH.parseConstant(sb);
				default:
					sb.append((char) c);
			}
		}
	}

}
