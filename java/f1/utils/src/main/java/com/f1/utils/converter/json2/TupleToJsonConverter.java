package com.f1.utils.converter.json2;

import com.f1.utils.DetailedException;
import com.f1.utils.SH;
import com.f1.utils.structs.Tuple;

public class TupleToJsonConverter extends AbstractJsonConverter<Tuple> {

	private static final long serialVersionUID = 8687822809268799432L;

	public TupleToJsonConverter() {
		super(Tuple.class);
	}

	@Override
	public void objectToString(Tuple m, ToJsonConverterSession session) {
		StringBuilder out = session.getStream();
		boolean ignoreUnconvertable = session.getConverter().getIgnoreUnconvertable();
		out.append('{');
		session.pushDepth();
		session.appendNewLine();
		session.appendPrefix();
		out.append("\"_\":\"Tuple").append(m.getSize()).append(SH.CHAR_QUOTE);
		for (int i = 0, l = m.getSize(); i < l; i++) {
			try {
				final Object value = m.getAt(i);
				if (value == null)
					continue;
				if (ignoreUnconvertable && !session.getConverter().canConverter(value))
					continue;
				out.append(',');
				session.appendNewLine();
				session.appendPrefix();
				out.append(SH.CHAR_QUOTE).append(i).append(SH.CHAR_QUOTE);
				out.append(':');
				session.getConverter().objectToString(value, session);
			} catch (Exception e) {
				throw new DetailedException("Error handle param for message", e).set("index", i).set("tuple", m);
			}
		}
		session.popDepth();
		session.appendNewLine();
		session.appendPrefix();
		out.append('}');
	}

	@Override
	public Object stringToObject(FromJsonConverterSession session) {
		throw new IllegalStateException("json doesn't have tuples, just maps");
	}
	@Override
	public boolean isLeaf() {
		return false;
	}

}
