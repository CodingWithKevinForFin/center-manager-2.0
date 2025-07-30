package com.f1.utils.converter.json2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import com.f1.base.Getter;
import com.f1.base.IdeableGenerator;
import com.f1.base.LockedException;
import com.f1.base.Message;
import com.f1.base.Transient;
import com.f1.base.Valued;
import com.f1.utils.CH;
import com.f1.utils.CharReader;
import com.f1.utils.CopyOnWriteHashMap;
import com.f1.utils.MH;
import com.f1.utils.OfflineConverter;
import com.f1.utils.SH;
import com.f1.utils.StringFormatException;
import com.f1.utils.converter.ConverterException;
import com.f1.utils.impl.CharMatcher;
import com.f1.utils.impl.StringCharReader;
import com.f1.utils.string.ExpressionParserException;

public class ObjectToJsonConverter implements JsonConverter<Object>, OfflineConverter, Serializable {

	public static final byte MODE_COMPACT = 1;
	public static final byte MODE_CLEAN = 2;
	public static final byte MODE_SEMI = 3;
	public static final ObjectToJsonConverter INSTANCE_CLEAN = new ObjectToJsonConverter();
	public static final ObjectToJsonConverter INSTANCE_COMPACT = new ObjectToJsonConverter();
	public static final ObjectToJsonConverter INSTANCE_SEMI = new ObjectToJsonConverter();
	public static final ObjectToJsonConverter INSTANCE_CLEAN_SORTING = new ObjectToJsonConverter();
	public static final ObjectToJsonConverter INSTANCE_COMPACT_SORTING = new ObjectToJsonConverter();
	public static final ObjectToJsonConverter INSTANCE_SEMI_SORTING = new ObjectToJsonConverter();
	static {
		INSTANCE_CLEAN.setCompactMode(MODE_CLEAN);
		INSTANCE_COMPACT.setCompactMode(MODE_COMPACT);
		INSTANCE_SEMI.setCompactMode(MODE_SEMI);
		INSTANCE_CLEAN_SORTING.setCompactMode(MODE_CLEAN);
		INSTANCE_COMPACT_SORTING.setCompactMode(MODE_COMPACT);
		INSTANCE_SEMI_SORTING.setCompactMode(MODE_SEMI);
		INSTANCE_CLEAN_SORTING.setSortMaps(true);
		INSTANCE_COMPACT_SORTING.setSortMaps(true);
		INSTANCE_SEMI_SORTING.setSortMaps(true);

		INSTANCE_CLEAN.setValuedTypeKeyName(null);
		INSTANCE_COMPACT.setValuedTypeKeyName(null);
		INSTANCE_SEMI.setValuedTypeKeyName(null);
		INSTANCE_CLEAN_SORTING.setValuedTypeKeyName(null);
		INSTANCE_COMPACT_SORTING.setValuedTypeKeyName(null);
		INSTANCE_SEMI_SORTING.setValuedTypeKeyName(null);
	}

	public static ObjectToJsonConverter getInstance(byte mode) {
		switch (mode) {
			case MODE_COMPACT:
				return INSTANCE_COMPACT;
			case MODE_CLEAN:
				return INSTANCE_CLEAN;
			case MODE_SEMI:
				return INSTANCE_SEMI;
			default:
				throw new RuntimeException("Unknown mode: " + mode);

		}
	}

	public static ObjectToJsonConverter getInstanceWithSorting(byte mode) {
		switch (mode) {
			case MODE_COMPACT:
				return INSTANCE_COMPACT_SORTING;
			case MODE_CLEAN:
				return INSTANCE_CLEAN_SORTING;
			case MODE_SEMI:
				return INSTANCE_SEMI_SORTING;
			default:
				throw new RuntimeException("Unknown mode: " + mode);

		}
	}

	private static final long serialVersionUID = -8451460757426154852L;
	private static final char[] NULL = "null".toCharArray();
	public static final CharMatcher WHITE_SPACE = StringCharReader.WHITE_SPACE;
	private ConcurrentMap<Class<?>, JsonConverter<?>> converters = new CopyOnWriteHashMap<Class<?>, JsonConverter<?>>();
	private JsonConverter<String> converter_string;
	private JsonConverter<List> converter_list;
	private JsonConverter<Boolean> converter_boolean;
	private JsonConverter<Map> converter_map;
	private JsonConverter<Number> converter_number;

	private List<JsonConverter> convertersList = new ArrayList<JsonConverter>();
	transient private IdeableGenerator generator;
	private boolean ignoreUnconvertable = false;
	private byte skipTransience = Transient.NONE;
	private byte compactMode = MODE_CLEAN;
	private boolean treatValuedAsMaps = false;
	private boolean treatNanAsNull = false;
	private boolean treatValuedAsMapBackedValued;
	private boolean strictValidation;
	private boolean includeClassName;
	private boolean isLocked;
	private String valuedTypeKeyName = "_";
	// either mark these as transient or make Getter a class that implements Serializable, or use a non-object field.
	private Getter<Valued, String> valuedToTypeGetter;
	private Getter<String, Valued> typeToValuedGetter;
	private boolean sortMaps;
	private int semiCompactMaxLineLength = 120;

	public ObjectToJsonConverter() {
		MapToJsonConverter mapConverter = new MapToJsonConverter();
		registerConverter(new NumberToJsonConverter());
		registerConverter(new StringToJsonConverter());
		registerConverter(new ListToJsonConverter());
		registerConverter(new ArrayToJsonConverter());
		registerConverter(mapConverter);
		registerConverter(new MessageToJsonConverter());
		registerConverter(new BooleanToJsonConverter());
		registerConverter(new CharToJsonConverter());
		registerConverter(new ValuedEnumToJsonConverter());
		registerConverter(new TupleToJsonConverter());
		registerConverter(new ThrowableToJsonConverter());
	}

	@Override
	public void objectToString(Object o, ToJsonConverterSession out) {
		lock();
		if (o == null) {
			out.getStream().append("null");
			return;
		}
		Class type = o.getClass();
		JsonConverter converter = getConverter(type);
		converter.objectToString(o, out);
	}

	@Override
	public Object stringToObject(FromJsonConverterSession session) {
		CharReader stream = session.getStream();
		for (;;) {
			if (skipComments(stream) != null)
				continue;
			char c = stream.peak();
			switch (c) {
				case ' ':
				case '\n':
				case '\r':
				case '\t':
					stream.readChar();
					continue;
				case '[':
					return converter_list.stringToObject(session);
				case '{':
					return converter_map.stringToObject(session);
				case '\"':
				case '\'':// not quite legal, but we'll except it!
					return converter_string.stringToObject(session);
				case 'f':
					return converter_boolean.stringToObject(session);
				case 't':
					return converter_boolean.stringToObject(session);
				case 'n':
					stream.expectSequence(NULL);
					return null;
				case '-':
				case '0':
				case '1':
				case '2':
				case '3':
				case '4':
				case '5':
				case '6':
				case '7':
				case '8':
				case '9':
				case 'N'://NaN
				case 'i'://infinity
					return converter_number.stringToObject(session);
				default:
					return onUnknownType(session);
			}
		}

	}

	protected Object onUnknownType(FromJsonConverterSession session) {
		CharReader stream = session.getStream();
		throw new ExpressionParserException(stream.getCountRead(), "At byte " + stream.getCountRead() + ":unexpected token: " + stream.peak());
	}

	public String skipComments(CharReader stream) {
		if (stream.peakOrEof() == '/') {
			char c[] = new char[2];
			if (stream.peak(c) == 2) {
				if (c[1] == '*') {

					stream.skip('/');
					stream.skip('*');
					StringBuilder sink = new StringBuilder();
					for (;;) {
						stream.readUntil('*', '\\', sink);
						char c1 = stream.expect('*');
						char c2 = stream.readChar();
						if (c2 == '/')
							break;
						sink.append(c1).append(c2);
						if (c2 == '\\')
							sink.append(stream.readChar());
					}
					return sink.toString();
				} else if (c[1] == '/') {
					stream.skip('/');
					stream.skip('/');
					StringBuilder sink = new StringBuilder();
					stream.readUntilAny(new int[] { SH.CHAR_CR, SH.CHAR_LF }, sink);
					return sink.toString();
				}
			}
		}
		return null;
	}

	public void registerConverter(JsonConverter<?> converter) {
		assertNotLocked();
		CH.putOrThrow(converters, converter.getType(), converter);
		convertersList.add(0, converter);
		registerSpecial(converter);
	}
	public void registerConverterLowPriority(JsonConverter<?> converter) {
		assertNotLocked();
		CH.putOrThrow(converters, converter.getType(), converter);
		convertersList.add(converter);
		registerSpecial(converter);
	}

	public void replaceConverter(JsonConverter<?> converter) {
		assertNotLocked();
		JsonConverter<?> existing = CH.getOrThrow(this.converters, converter.getType());
		CH.replace(convertersList, existing, converter);
		converters.put(converter.getType(), converter);
		registerSpecial(converter);
	}
	private void registerSpecial(JsonConverter<?> converter) {
		if (isCompatible(this.converter_string, converter, String.class))
			this.converter_string = (JsonConverter<String>) converter;
		else if (isCompatible(this.converter_list, converter, List.class))
			this.converter_list = (JsonConverter<List>) converter;
		else if (isCompatible(this.converter_map, converter, Map.class))
			this.converter_map = (JsonConverter<Map>) converter;
		else if (isCompatible(this.converter_number, converter, Number.class))
			this.converter_number = (JsonConverter<Number>) converter;
		else if (isCompatible(this.converter_boolean, converter, Boolean.class))
			this.converter_boolean = (JsonConverter<Boolean>) converter;
	}

	private boolean isCompatible(JsonConverter<?> existing, JsonConverter<?> nuw, Class<?> type) {
		if (!nuw.getType().isAssignableFrom(type))
			return false;
		return (existing == null || existing.getType().isAssignableFrom(nuw.getType()));

	}

	public JsonConverter getConverterNoThrow(Class type) {
		if (type == null)
			return this;
		JsonConverter<?> r = converters.get(type);
		if (r != null)
			return r;
		for (JsonConverter c : convertersList)
			if (c.isCompatible(type)) {
				JsonConverter<?> existing = converters.putIfAbsent(type, c);
				return existing != null ? existing : c;
			}
		return null;
	}

	public JsonConverter getConverter(Class type) {
		JsonConverter r = getConverterNoThrow(type);
		if (r != null)
			return r;
		else if (ignoreUnconvertable)
			return NullToJsonConverter.INSTANCE;
		throw new ConverterException("no converter for supplied type").set("supplied type", type.getName()).set("supported types", converters.keySet());
	}

	@Override
	public boolean isCompatible(Class type) {
		return false;
	}

	@Override
	public Class getType() {
		return null;
	}

	public String objectToString(Object o) {
		lock();
		StringBuilder sb = new StringBuilder();
		ToJsonConverterSession out = new BasicToJsonConverterSession(this, sb);
		objectToString(o, out);
		return sb.toString();
	}

	public Object stringToObject(CharSequence str) {
		lock();
		if (str == null)
			throw new NullPointerException("text can not be null");
		StringCharReader reader = new StringCharReader(str);
		try {
			return stringToObject(new BasicFromJsonConverterSession(this, reader));
		} catch (Exception e) {
			throw new StringFormatException("Error parsing ", e, str.toString(), reader.getCountRead());
		}
	}
	public Object stringToObject(CharSequence str, int start, int end) {
		lock();
		if (str == null)
			throw new NullPointerException("text can not be null");
		StringCharReader reader = new StringCharReader(str, start, end - start);
		try {
			return stringToObject(new BasicFromJsonConverterSession(this, reader));
		} catch (Exception e) {
			throw new StringFormatException("Error parsing ", e, str.toString(), reader.getCountRead());
		}
	}

	@Override
	public Object bytes2Object(byte[] in) {
		return stringToObject(new String(in));
	}

	@Override
	public byte[] object2Bytes(Object in) {
		return objectToString(in).getBytes();
	}

	@Override
	public void setIdeableGenerator(IdeableGenerator generator) {
		assertNotLocked();
		this.generator = generator;
	}

	@Override
	public IdeableGenerator getIdeableGenerator() {
		return generator;
	}

	public boolean canConverter(Object value) {
		return value == null || getConverterNoThrow(value.getClass()) != null;
	}

	public void setIgnoreUnconvertable(boolean ignoreUnconvertable) {
		assertNotLocked();
		this.ignoreUnconvertable = ignoreUnconvertable;
	}

	public boolean getIgnoreUnconvertable() {
		return ignoreUnconvertable;
	}

	public void setSkipTransience(byte skipTransience) {
		assertNotLocked();
		this.skipTransience = skipTransience;
	}

	public byte getSkipTransience() {
		return skipTransience;
	}

	@Deprecated
	public void setCompactMode(boolean compactMode) {
		assertNotLocked();
		this.compactMode = compactMode ? MODE_COMPACT : MODE_CLEAN;
	}
	public void setCompactMode(byte compactMode) {
		assertNotLocked();
		this.compactMode = compactMode;
	}

	public byte getCompactMode() {
		return compactMode;
	}

	public void setTreatValuedAsMaps(boolean treatValuedAsMaps) {
		assertNotLocked();
		this.treatValuedAsMaps = treatValuedAsMaps;
	}

	public boolean getTreatValuedAsMaps() {
		return treatValuedAsMaps;
	}
	public void setTreatNanAsNull(boolean treatNanAsNull) {
		assertNotLocked();
		this.treatNanAsNull = treatNanAsNull;
	}

	public boolean getTreatNanAsNull() {
		return treatNanAsNull;
	}

	@Override
	public int getOptions() {
		int r = 0;
		switch (compactMode) {
			case MODE_COMPACT:
				r |= OPTION_COMPACT_MODE;
				break;
			case MODE_SEMI:
				r |= OPTION_COMPACT_SEMI;
				break;
			case MODE_CLEAN:
				break;
		}
		if (this.ignoreUnconvertable)
			r |= OPTION_IGNORE_UNCONVERTABLE;
		if (MH.areAnyBitsSet(this.skipTransience, Transient.PERSIST))
			r |= OPTION_SKIP_TRANSIENT_PERSIST;
		if (MH.areAnyBitsSet(this.skipTransience, Transient.WIRE))
			r |= OPTION_SKIP_TRANSIENT_WIRE;
		if (this.treatValuedAsMaps)
			r |= OPTION_TREAT_VALUED_AS_MAPS;
		if (this.treatValuedAsMapBackedValued)
			r |= OPTION_TREAT_VALUED_AS_MAP_BACKED_VALUED;
		if (this.strictValidation)
			r |= OPTION_STRICT_VALIDATION;
		if (this.includeClassName)
			r |= OPTION_INCLUDE_CLASSNAME;
		if (this.treatNanAsNull)
			r |= OPTION_TREAT_NAN_AS_NULL;
		if (this.sortMaps)
			r |= OPTION_SORT_MAPS;
		return r;
	}

	@Override
	public void setOptions(int options) {
		assertNotLocked();
		if (MH.areAnyBitsSet(options, OPTION_COMPACT_MODE)) {
			this.compactMode = MODE_COMPACT;
		} else if (MH.areAnyBitsSet(options, OPTION_COMPACT_SEMI)) {
			this.compactMode = MODE_SEMI;
		} else
			compactMode = MODE_CLEAN;
		this.includeClassName = MH.areAnyBitsSet(options, OPTION_INCLUDE_CLASSNAME);
		this.ignoreUnconvertable = MH.areAnyBitsSet(options, OPTION_IGNORE_UNCONVERTABLE);
		this.skipTransience = 0;
		this.skipTransience |= (byte) (MH.areAnyBitsSet(options, OPTION_SKIP_TRANSIENT_WIRE) ? Transient.WIRE : Transient.NONE);
		this.skipTransience |= (byte) (MH.areAnyBitsSet(options, OPTION_SKIP_TRANSIENT_PERSIST) ? Transient.PERSIST : Transient.NONE);
		this.treatValuedAsMaps = MH.areAnyBitsSet(options, OPTION_TREAT_VALUED_AS_MAPS);
		this.treatValuedAsMapBackedValued = MH.areAnyBitsSet(options, OPTION_TREAT_VALUED_AS_MAP_BACKED_VALUED);
		this.treatNanAsNull = MH.areAnyBitsSet(options, OPTION_TREAT_NAN_AS_NULL);
		this.strictValidation = MH.areAnyBitsSet(options, OPTION_STRICT_VALIDATION);
		this.sortMaps = MH.areAnyBitsSet(options, OPTION_SORT_MAPS);
	}
	@Override
	public OfflineConverter clone() {
		ObjectToJsonConverter r = new ObjectToJsonConverter();
		r.converters.putAll(converters);
		r.convertersList.addAll(convertersList);
		r.generator = generator;
		r.ignoreUnconvertable = ignoreUnconvertable;
		r.skipTransience = skipTransience;
		r.compactMode = compactMode;
		r.treatValuedAsMapBackedValued = treatValuedAsMapBackedValued;
		r.treatValuedAsMaps = treatValuedAsMaps;
		r.treatNanAsNull = treatNanAsNull;
		r.strictValidation = strictValidation;
		r.sortMaps = sortMaps;
		return r;
	}

	public boolean getTreatValuedAsMapBackedValued() {
		return treatValuedAsMapBackedValued;
	}

	public boolean getStrictValidation() {
		return strictValidation;
	}

	public void setStrictValidation(boolean strictValidation) {
		assertNotLocked();
		this.strictValidation = strictValidation;
	}

	public boolean getIncludeClassName() {
		return includeClassName;
	}

	public void setIncludeClassName(boolean includeClassName) {
		assertNotLocked();
		this.includeClassName = includeClassName;
	}

	@Override
	public void lock() {
		this.isLocked = true;
	}

	@Override
	public boolean isLocked() {
		return this.isLocked;
	}

	protected void assertNotLocked() {
		LockedException.assertNotLocked(this);
	}

	public String getValuedTypeKeyName() {
		return valuedTypeKeyName;
	}

	public void setValuedTypeKeyName(String valuedTypeKeyName) {
		assertNotLocked();
		this.valuedTypeKeyName = valuedTypeKeyName;
	}

	protected String getValuedType(Valued m) {
		if (valuedToTypeGetter != null)
			return valuedToTypeGetter.get(m);
		else if (m instanceof Message)
			return ((Message) m).askIdeableName();
		else
			return m.askSchema().askOriginalType().getName();
	}
	protected Valued nwValuedFromType(String name) {
		if (typeToValuedGetter != null)
			return typeToValuedGetter.get(name);
		else
			return getIdeableGenerator().nw(name);
	}
	public Getter<Valued, String> getValuedToTypeGetter() {
		return valuedToTypeGetter;
	}

	public void setValuedToTypeGetter(Getter<Valued, String> valuedToTypeGetter) {
		assertNotLocked();
		this.valuedToTypeGetter = valuedToTypeGetter;
	}
	public Getter<String, Valued> getTypeToValuedGetter() {
		return typeToValuedGetter;
	}

	public void setTypeToValuedGetter(Getter<String, Valued> typeToValuedGetter) {
		assertNotLocked();
		this.typeToValuedGetter = typeToValuedGetter;
	}

	public boolean hasComplex(Collection<?> values) {
		for (Object o : values)
			if (o != null && !getConverter(o.getClass()).isLeaf())
				return true;
		return false;
	}

	@Override
	public boolean isLeaf() {
		return true;
	}
	public boolean getSortMaps() {
		return sortMaps;
	}
	public boolean setSortMaps(boolean b) {
		assertNotLocked();
		return this.sortMaps = b;
	}

	public int getSemiCompactMaxLineLength() {
		return semiCompactMaxLineLength;
	}
	public void setSemiCompactMaxLineLength(int n) {
		assertNotLocked();
		semiCompactMaxLineLength = n;
	}

}
