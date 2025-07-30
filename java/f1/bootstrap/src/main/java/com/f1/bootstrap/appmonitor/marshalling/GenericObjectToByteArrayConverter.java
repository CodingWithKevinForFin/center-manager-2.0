package com.f1.bootstrap.appmonitor.marshalling;

import com.f1.base.BasicTypes;
import com.f1.base.Valued;
import com.f1.base.ValuedEnum;
import com.f1.utils.converter.bytes.ByteArrayConverter;
import com.f1.utils.converter.bytes.ObjectToByteArrayConverter;

public class GenericObjectToByteArrayConverter extends ObjectToByteArrayConverter {

	final private ClassAsStringToByteArrayTranslator classWriter;
	final private EnumAsStringToByteArrayTranslator enumWriter;
	final private ValuedAsMapToByteArrayTranslator valuedWriter;
	final private ValuedEnumAsStringToByteArrayTranslator valuedEnumWriter;
	final private UnknownAsNullToByteArrayTranslator objectWriter;
	private ArrayAsListToByteArrayTranslator arrayAsListToByteArrayTranslator;

	public GenericObjectToByteArrayConverter() {
		classWriter = new ClassAsStringToByteArrayTranslator();
		enumWriter = new EnumAsStringToByteArrayTranslator();
		valuedWriter = new ValuedAsMapToByteArrayTranslator();
		valuedEnumWriter = new ValuedEnumAsStringToByteArrayTranslator();
		objectWriter = new UnknownAsNullToByteArrayTranslator();
		arrayAsListToByteArrayTranslator = new ArrayAsListToByteArrayTranslator();

		super.removeConverter(BasicTypes.CUSTOM);
		super.removeConverter(BasicTypes.MAPMESSAGE);
		super.replaceConverter(classWriter.asReader());
		super.replaceConverter(enumWriter.asReader());
		super.replaceConverter(valuedWriter.asReader());
		super.replaceConverter(valuedEnumWriter.asReader());
	}
	protected ByteArrayConverter findConverter(Class type) {
		if (Valued.class.isAssignableFrom(type))
			return valuedWriter.asWriter();
		else if (ValuedEnum.class.isAssignableFrom(type))
			return valuedEnumWriter.asWriter();
		else if (Class.class.isAssignableFrom(type))
			return classWriter.asWriter();
		else if (type.isEnum())
			return enumWriter.asWriter();
		else {
			ByteArrayConverter<?> r = super.findConverter(type);
			if (r == null) {
				if (type.isArray()) {
					return arrayAsListToByteArrayTranslator.asWriter();
				}
				return objectWriter.asWriter();
			}
			return r;
		}
	}

}
