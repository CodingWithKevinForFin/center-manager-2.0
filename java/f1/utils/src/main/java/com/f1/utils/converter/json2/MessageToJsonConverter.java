package com.f1.utils.converter.json2;

import com.f1.base.Valued;
import com.f1.base.ValuedParam;
import com.f1.base.ValuedSchema;
import com.f1.utils.DetailedException;

public class MessageToJsonConverter extends AbstractJsonConverter<Valued> {

	public MessageToJsonConverter() {
		super(Valued.class);
	}

	@Override
	public void objectToString(Valued m, ToJsonConverterSession session) {
		final StringBuilder out = session.getStream();
		final ObjectToJsonConverter converter = session.getConverter();
		final boolean ignoreUnconvertable = converter.getIgnoreUnconvertable();
		final byte skipTransient = converter.getSkipTransience();
		out.append('{');
		session.pushDepth();
		final ValuedSchema<? extends Valued> schema = m.askSchema();
		session.appendNewLine();
		session.appendPrefix();
		StringToJsonConverter.INSTANCE.objectToString(converter.getValuedTypeKeyName(), session);
		out.append(':');
		StringToJsonConverter.INSTANCE.objectToString(converter.getValuedType(m), session);

		for (ValuedParam param : schema.askValuedParams()) {
			try {
				final Object value;
				if ((param.getTransience() & skipTransient) != 0)
					continue;
				value = param.getValue(m);
				if (value == null)
					continue;
				if (ignoreUnconvertable && !converter.canConverter(value))
					continue;
				out.append(',');
				session.appendNewLine();
				session.appendPrefix();
				StringToJsonConverter.INSTANCE.objectToString(param.getName(), session);
				out.append(':');
				converter.objectToString(value, session);
			} catch (Exception e) {
				throw new DetailedException("Error handle param for message", e).set("param", param).set("message", m.askSchema().askOriginalType().getName());
			}
		}
		session.popDepth();
		session.appendNewLine();
		session.appendPrefix();
		out.append('}');
	}

	@Override
	public Object stringToObject(FromJsonConverterSession session) {
		throw new IllegalStateException("json doesn't have messages, just maps");
	}

	@Override
	public boolean isLeaf() {
		return false;
	}

}
