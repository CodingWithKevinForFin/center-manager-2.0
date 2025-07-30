package com.f1.utils.converter.bytes;

import java.io.IOException;
import java.util.Map;

import com.f1.base.BasicTypes;
import com.f1.utils.CH;
import com.f1.utils.CopyOnWriteHashMap;

public class CustomToByteArrayConverter implements ByteArrayConverter<Object> {

	final private Map<Class, CustomByteArrayConverter> customConverters = new CopyOnWriteHashMap<Class, CustomByteArrayConverter>();
	final private Map<Object, CustomByteArrayConverter> customConvertersByCustomId = new CopyOnWriteHashMap<Object, CustomByteArrayConverter>();

	@Override
	public void write(Object o, ToByteArrayConverterSession session) throws IOException {
		CustomByteArrayConverter cc = CH.getOrThrow(customConverters, o.getClass());
		session.getConverter().write(cc.getCustomId(), session);
		cc.write(o, session);
	}

	@Override
	public Object read(FromByteArrayConverterSession session) throws IOException {
		Object obj = session.getConverter().read(session);
		CustomByteArrayConverter<?> converter = CH.getOrThrow(customConvertersByCustomId, obj, "custom converter not registered for id");
		return converter.read(session);
	}

	@Override
	public byte getBasicType() {
		return BasicTypes.CUSTOM;
	}

	@Override
	public boolean isCompatible(Class<?> o) {
		CustomByteArrayConverter converter = customConverters.get(o);
		if (converter != null)
			return converter != NO_CONVERTER;
		for (CustomByteArrayConverter e : customConvertersByCustomId.values()) {
			if (e.isCompatible(o)) {
				customConverters.put(o, e);
				return true;
			}
		}
		customConverters.put(o, NO_CONVERTER);
		return false;
	}
	public void registerCustomConverter(CustomByteArrayConverter converter) {
		CH.putOrThrow(customConverters, converter.getType(), converter);
		CH.putOrThrow(customConvertersByCustomId, converter.getCustomId(), converter);
	}

	private static final CustomByteArrayConverter NO_CONVERTER = new AbstractCustomByteArrayConverter(Object.class) {

		@Override
		public void write(Object o, ToByteArrayConverterSession session) throws IOException {
			throw new UnsupportedOperationException(o.getClass().getName());
		}

		@Override
		public Object read(FromByteArrayConverterSession session) throws IOException {
			throw new UnsupportedOperationException();
		}
	};
}
