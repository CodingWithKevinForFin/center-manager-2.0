/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.converter.bytes;

import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;

import com.f1.base.BasicTypes;
import com.f1.base.IdeableGenerator;
import com.f1.base.Transient;
import com.f1.utils.CH;
import com.f1.utils.CopyOnWriteHashMap;
import com.f1.utils.FastByteArrayDataInputStream;
import com.f1.utils.FastByteArrayDataOutputStream;
import com.f1.utils.OH;
import com.f1.utils.OfflineConverter;
import com.f1.utils.converter.ConverterException;

public class ObjectToByteArrayConverter implements ByteArrayConverter<Object>, OfflineConverter {

	private static final Logger log = Logger.getLogger(ObjectToByteArrayConverter.class.getName());
	private static final long serialVersionUID = -2644965809514542601L;
	private ConcurrentMap<Class<?>, ByteArrayConverter<?>> converters = new CopyOnWriteHashMap<Class<?>, ByteArrayConverter<?>>();
	private ByteArrayConverter[] convertersByType = new ByteArrayConverter[255];
	private List<ByteArrayConverter> convertersList = new ArrayList<ByteArrayConverter>();
	transient private IdeableGenerator generator;
	private CustomToByteArrayConverter customerConverter;
	private byte skipTransience = Transient.WIRE;

	public ObjectToByteArrayConverter() {
		this(true);
	}

	public ObjectToByteArrayConverter(boolean init) {
		registerConverter(this);
		if (init) {
			registerConverter(customerConverter = new CustomToByteArrayConverter());
			registerConverter(new EnumToByteArrayConverter());
			registerConverter(new ThrowableToByteArrayConverter());
			registerConverter(new NullToByteArrayConverter());
			registerConverter(new ObjToByteArrayConverter());
			registerConverter(new ClassToByteArrayConverter());

			registerConverter(new BooleanToByteArrayConverter(true));
			registerConverter(new CharToByteArrayConverter(true));
			registerConverter(new ByteToByteArrayConverter(true));
			registerConverter(new ShortToByteArrayConverter(true));
			registerConverter(new IntToByteArrayConverter(true));
			registerConverter(new FloatToByteArrayConverter(true));
			registerConverter(new LongToByteArrayConverter(true));
			registerConverter(new DoubleToByteArrayConverter(true));

			registerConverter(new BooleanToByteArrayConverter(false));
			registerConverter(new CharToByteArrayConverter(false));
			registerConverter(new ByteToByteArrayConverter(false));
			registerConverter(new ShortToByteArrayConverter(false));
			registerConverter(new IntToByteArrayConverter(false));
			registerConverter(new FloatToByteArrayConverter(false));
			registerConverter(new LongToByteArrayConverter(false));
			registerConverter(new DoubleToByteArrayConverter(false));

			registerConverter(new LongKeyMapToByteArrayConverter());
			registerConverter(new ListToByteArrayConverter());
			registerConverter(new StringToByteArrayConverter());
			registerConverter(new StringArrayToByteArrayConverter());
			registerConverter(new MapToByteArrayConverter());
			registerConverter(new SetToByteArrayConverter());
			registerConverter(new TimeZoneToByteArrayConverter());
			registerConverter(new ValuedToByteArrayConverter());
			registerConverter(new TableToByteArrayConverter());
			registerConverter(new ColumnarTableToByteArrayConverter());
			registerConverter(new FixPointToByteArrayConverter());
			registerConverter(new ByteArrayToByteArrayConverter());
			registerConverter(new ByteArrayArrayToByteArrayConverter());
			registerConverter(new LongArrayToByteArrayConverter());
			registerConverter(new CharArrayToByteArrayConverter());
			registerConverter(new IntArrayToByteArrayConverter());
			registerConverter(new FloatArrayToByteArrayConverter());
			registerConverter(new DoubleArrayToByteArrayConverter());
			registerConverter(new DateToByteArrayConverter());
			registerConverter(new DateMillisToByteArrayConverter());
			registerConverter(new DateNanosToByteArrayConverter());
			registerConverter(new DayToByteArrayConverter());
			registerConverter(new DayTimeToByteArrayConverter());
			registerConverter(new ValuedEnumToByteArrayConverter());
			registerConverter(new BigDecimalToByteArrayConverter());
			registerConverter(new BigIntegerToByteArrayConverter());
			registerConverter(new BytesToByteArrayConverter());
			registerConverter(new ComplexToByteArrayConverter());
			registerConverter(new UUIDToByteArrayConverter());
			registerConverter(new PasswordToByteArrayConverter());
			registerConverter(new StringBuilderToByteArrayConverter());
			registerConverter(new ColorGradientToByteArrayConverter());
		}
	}

	@Override
	public Object read(FromByteArrayConverterSession session) throws IOException {
		byte type = session.getStream().readByte();
		switch (type) {
			case BasicTypes.PRIMITIVE_BOOLEAN:
				return session.getStream().readBoolean();
			case BasicTypes.PRIMITIVE_BYTE:
				return session.getStream().readByte();
			case BasicTypes.PRIMITIVE_CHAR:
				return session.getStream().readChar();
			case BasicTypes.PRIMITIVE_DOUBLE:
				return session.getStream().readDouble();
			case BasicTypes.PRIMITIVE_FLOAT:
				return session.getStream().readFloat();
			case BasicTypes.PRIMITIVE_INT:
				return session.getStream().readInt();
			case BasicTypes.PRIMITIVE_LONG:
				return session.getStream().readLong();
			case BasicTypes.PRIMITIVE_SHORT:
				return session.getStream().readShort();
			default:
				ByteArrayConverter c = convertersByType[type];
				if (c == null)
					throw new ConverterException("unknown type: " + type + " at byte " + ((FastByteArrayDataInputStream) session.getStream()).getPosition());
				return c.read(session);
		}

	}

	@Override
	public void write(Object o, ToByteArrayConverterSession session) throws IOException {
		if (o == null)
			session.getStream().writeByte(BasicTypes.NULL);
		else {
			ByteArrayConverter c = getConverter(o.getClass());
			session.getStream().writeByte(c.getBasicType());
			c.write(o, session);
		}
	}

	public void write(boolean o, DataOutput out) throws IOException {
		out.writeByte(BasicTypes.PRIMITIVE_BOOLEAN);
		out.writeBoolean(o);
	}

	public void write(byte o, DataOutput out) throws IOException {
		out.writeByte(BasicTypes.PRIMITIVE_BYTE);
		out.writeByte(o);
	}

	public void write(char o, DataOutput out) throws IOException {
		out.writeByte(BasicTypes.PRIMITIVE_CHAR);
		out.writeChar(o);
	}

	public void write(short o, DataOutput out) throws IOException {
		out.writeByte(BasicTypes.PRIMITIVE_SHORT);
		out.writeShort(o);
	}

	public void write(int o, DataOutput out) throws IOException {
		out.writeByte(BasicTypes.PRIMITIVE_INT);
		out.writeInt(o);
	}

	public void write(long o, DataOutput out) throws IOException {
		out.writeByte(BasicTypes.PRIMITIVE_LONG);
		out.writeLong(o);
	}

	public void write(float o, DataOutput out) throws IOException {
		out.writeByte(BasicTypes.PRIMITIVE_FLOAT);
		out.writeFloat(o);
	}

	public void write(double o, DataOutput out) throws IOException {
		out.writeByte(BasicTypes.PRIMITIVE_DOUBLE);
		out.writeDouble(o);
	}

	@Override
	public Object bytes2Object(byte[] in) {
		try {
			BasicFromByteArrayConverterSession s = createFromByteArrayConverterSession();
			FastByteArrayDataInputStream stream = (FastByteArrayDataInputStream) s.getStream();
			stream.reset(in);
			return read(s);
		} catch (Exception e) {
			throw new ConverterException(e);
		}
	}

	@Override
	public byte[] object2Bytes(Object in) {
		try {
			return object2Bytes(in, false);
		} catch (IOException e) {
			throw new ConverterException(e);
		}
	}

	public byte[] object2Bytes(Object in, boolean supportCircRefs) throws IOException {
		BasicToByteArrayConverterSession s = createToByteArrayConverterSession(supportCircRefs);
		write(in, s);
		return ((FastByteArrayDataOutputStream) s.getStream()).toByteArray();
	}

	public BasicToByteArrayConverterSession createToByteArrayConverterSession(boolean supportCircRefs) {
		FastByteArrayDataOutputStream buf = new FastByteArrayDataOutputStream();
		return new BasicToByteArrayConverterSession(this, buf, supportCircRefs);
	}

	public BasicFromByteArrayConverterSession createFromByteArrayConverterSession() {
		return new BasicFromByteArrayConverterSession(this, new FastByteArrayDataInputStream(OH.EMPTY_BYTE_ARRAY));
	}

	public void registerConverter(ByteArrayConverter<?> converter) {
		int basicType = converter.getBasicType();
		if (convertersByType[basicType] != null)
			throw new ConverterException("duplicate type: " + basicType);
		convertersByType[converter.getBasicType()] = converter;
		convertersList.add(0, converter);
	}
	public ByteArrayConverter removeConverter(byte basicType) {
		ByteArrayConverter converter = convertersByType[basicType];
		if (converter == null)
			return null;
		convertersByType[converter.getBasicType()] = null;
		convertersList.remove(converter);
		return converter;
	}

	public void replaceConverter(ByteArrayConverter<?> converter) {
		int basicType = converter.getBasicType();
		ByteArrayConverter<?> existing = convertersByType[basicType];
		convertersByType[basicType] = converter;
		CH.replace(convertersList, existing, converter);
	}

	public ByteArrayConverter getConverterNoThrow(Class type) {
		if (type == null)
			return this;
		ByteArrayConverter<?> r = converters.get(type);
		if (r != null)
			return r;
		r = findConverter(type);
		if (r != null) {
			ByteArrayConverter<?> existing = converters.putIfAbsent(type, r);
			return existing != null ? existing : r;
		}
		return null;
	}
	public ByteArrayConverter getConverter(Class type) {
		ByteArrayConverter r = getConverterNoThrow(type);
		if (r != null)
			return r;
		else
			throw new ConverterException("no converter for " + type.getName() + ". supported converters include " + converters);
	}

	protected ByteArrayConverter findConverter(Class type) {
		for (ByteArrayConverter c : convertersList)
			if (c.isCompatible(type)) {
				return c;
			}
		return null;
	}

	@Override
	public byte getBasicType() {
		return BasicTypes.UNDEFINED;
	}

	@Override
	public boolean isCompatible(Class<?> o) {
		return false;
	}

	public ByteArrayConverter<?> getConverter(byte type) {
		return convertersByType[type];
	}

	public IdeableGenerator getGenerator() {
		return generator;
	}

	@Override
	public void setIdeableGenerator(IdeableGenerator generator) {
		this.generator = generator;
	}

	@Override
	public IdeableGenerator getIdeableGenerator() {
		return generator;
	}

	@Override
	public int getOptions() {
		return 0;
	}

	@Override
	public void setOptions(int options_) {
	}

	@Override
	public OfflineConverter clone() {
		ObjectToByteArrayConverter r = new ObjectToByteArrayConverter(false);
		r.generator = this.generator;
		for (int i = convertersList.size() - 1; i >= 0; i--) {
			ByteArrayConverter t = convertersList.get(i);
			if (t.getBasicType() != getBasicType())
				r.registerConverter(t);
		}
		r.customerConverter = customerConverter;
		r.skipTransience = skipTransience;
		return r;
	}

	public void registerCustomConverter(CustomByteArrayConverter converter) {
		customerConverter.registerCustomConverter(converter);
	}

	public byte getSkipTransience() {
		return skipTransience;
	}

	public void setSkipTransience(byte skipTransience) {
		this.skipTransience = skipTransience;
	}

	public List<ByteArrayConverter> getConverters() {
		return convertersList;
	}

}
