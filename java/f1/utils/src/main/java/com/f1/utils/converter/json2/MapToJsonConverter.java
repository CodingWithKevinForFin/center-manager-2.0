package com.f1.utils.converter.json2;

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.logging.Logger;

import com.f1.base.Message;
import com.f1.base.ObjectGeneratorForClass;
import com.f1.base.ValuedParam;
import com.f1.utils.AH;
import com.f1.utils.CH;
import com.f1.utils.CharReader;
import com.f1.utils.DetailedException;
import com.f1.utils.LH;
import com.f1.utils.MapBackedValued;
import com.f1.utils.OH;
import com.f1.utils.RH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.structs.Tuple;
import com.f1.utils.structs.TupleFactory;

public class MapToJsonConverter extends AbstractJsonConverter<Map<?, ?>> implements ObjectGeneratorForClass<Map> {
	private static final long serialVersionUID = -2601423463036000477L;
	private static final Logger log = Logger.getLogger(MapToJsonConverter.class.getName());
	public static final int[] COMMA_OR_CLOSE = new int[] { ',', '}' };
	public static final int[] SPACE_OR_COLON = new int[] { ':', ' ' };
	private ObjectGeneratorForClass<? extends Map> mapGenerator;

	public MapToJsonConverter() {
		super((Class) Map.class);
		setMapGenerator(this);
	}

	@Override
	public void objectToString(Map m, ToJsonConverterSession session) {
		ObjectToJsonConverter converter = session.getConverter();
		boolean ignoreUnconvertable = converter.getIgnoreUnconvertable();
		StringBuilder out = session.getStream();
		out.append('{');
		Collection<Entry> entries = sortIfRequired(session, m);
		if (converter.getCompactMode() == ObjectToJsonConverter.MODE_SEMI && !converter.hasComplex(m.values())) {
			boolean first = true;
			StringBuilder stream = session.getStream();
			int startPos = stream.length();
			boolean tooLong = false;
			for (Map.Entry e : entries) {
				if (ignoreUnconvertable && !converter.canConverter(e.getValue()))
					continue;
				if (first == true)
					first = false;
				else
					out.append(',');
				StringToJsonConverter.INSTANCE.objectToString(e.getKey().toString(), session);
				out.append(':');
				converter.objectToString(e.getValue(), session);
				if (m.size() > 1 && stream.length() - startPos > converter.getSemiCompactMaxLineLength()) {
					tooLong = true;
					break;
				}
			}
			if (tooLong)
				stream.setLength(startPos);
			else {
				out.append('}');
				return;
			}
		}
		session.pushDepth();
		boolean first = true;
		for (Map.Entry<?, ?> e : entries) {
			if (ignoreUnconvertable && !converter.canConverter(e.getValue()))
				continue;
			if (first == true)
				first = false;
			else
				out.append(',');
			session.appendNewLine();
			session.appendPrefix();
			if (e.getKey() != null)
				StringToJsonConverter.INSTANCE.objectToString(e.getKey().toString(), session);
			else
				out.append("null");
			out.append(':');
			converter.objectToString(e.getValue(), session);
		}
		session.popDepth();
		session.appendNewLine();
		session.appendPrefix();
		out.append('}');
	}

	private static final Comparator<Map.Entry> SORTER = new java.util.Comparator<Map.Entry>() {

		@Override
		public int compare(Entry o1, Entry o2) {
			return OH.compare((Comparable) o1.getKey(), (Comparable) o2.getKey());
		}

	};

	private Collection<Map.Entry> sortIfRequired(ToJsonConverterSession session, Map map) {
		if (!session.getConverter().getSortMaps() || map instanceof SortedMap)
			return map.entrySet();
		Set<Map.Entry> entrySet = map.entrySet();
		if (CH.isSorted(entrySet, SORTER))
			return entrySet;
		else
			return CH.sort(entrySet, SORTER);
	}
	@Override
	public Object stringToObject(FromJsonConverterSession session) {
		CharReader stream = session.getStream();
		ObjectToJsonConverter converter = session.getConverter();
		Map<Object, Object> r = mapGenerator.nw();
		stream.expect('{');
		boolean first = true;
		StringBuilder keyBuf = new StringBuilder();
		for (;;) {
			session.skipWhite();
			if (first) {
				first = false;
				if (stream.peak() == '}') {
					stream.readChar();
					break;
				}
			} else if (stream.expectAny(COMMA_OR_CLOSE) == '}')
				break;
			keyBuf.setLength(0);

			converter.skipComments(stream);
			session.skipWhite();
			String key = (String) StringToJsonConverter.INSTANCE.stringToObject(session);
			//			switch (stream.peak()) {
			//				case '\'':
			//					stream.expect('\'');
			//					stream.readUntil('\'', '\\', keyBuf);
			//					stream.expect('\'');
			//					break;
			//				case '\"':
			//					stream.expect('\"');
			//					stream.readUntil('\"', '\\', keyBuf);
			//					stream.expect('\"');
			//					break;
			//				default:
			//					stream.readUntilAny(SPACE_OR_COLON, '\\', keyBuf);
			//					break;
			//			}
			//			String key = keyBuf.toString();
			session.skipWhite();
			stream.expect(':');
			session.skipWhite();
			try {
				Object val = session.getConverter().stringToObject(session);
				r.put(key, val);
			} catch (Exception e) {
				throw new RuntimeException("Error building map for field: " + key, e);
			}
		}
		final String typeKey = converter.getValuedTypeKeyName();
		if (typeKey != null) {
			Object type = r.get(typeKey);
			if (type instanceof String) {
				String typeName = (String) type;
				if (typeName.startsWith("Tuple")) {
					int size = Integer.parseInt(SH.stripPrefix(typeName, "Tuple", true));
					final Tuple r2 = TupleFactory.INSTANCE.newTuple(size);
					for (Map.Entry<Object, Object> e : r.entrySet())
						if (!e.getKey().equals(typeKey))
							r2.setAt(Caster_Integer.INSTANCE.cast(e.getKey()), e.getValue());
					return r2;
				} else if (session.getConverter().getTreatValuedAsMapBackedValued()) {
					return new MapBackedValued(RH.getClass(typeName), (Map) r);
				} else if (!session.getConverter().getTreatValuedAsMaps() && session.getConverter().getIdeableGenerator() != null) {
					boolean strict = session.getConverter().getStrictValidation();
					Message r2 = (Message) session.getConverter().nwValuedFromType(typeName);
					r.remove(typeKey);
					for (ValuedParam param : r2.askSchema().askValuedParams()) {
						Object value = strict ? r.remove(param.getName()) : r.get(param.getName());
						if (value != null) {
							try {
								param.setValue(r2, param.getCaster().cast(value));
							} catch (Exception e) {
								LH.warning(log, "Error setting param ", r2.askSchema().askOriginalType().getName(), "::", param.getName(), e);
								// TODO: handle
							}
						}
					}
					if (strict && !r.isEmpty())
						throw new DetailedException("values not supported by valued schema").set("schema", r2.askSchema().askOriginalType()).set("unknown fields", r.keySet());
					return r2;
				}
			}
		}
		return r;

	}

	@Override
	public Class<Map> askType() {
		return Map.class;
	}

	@Override
	public Map nw() {
		return new LinkedHashMap();
	}

	@Override
	public Map nw(Object[] args_) {
		if (AH.isEmpty(args_))
			return nw();
		throw new UnsupportedOperationException();
	}

	@Override
	public Map nwCast(Class[] types_, Object[] args_) {
		if (AH.isEmpty(args_))
			return nw();
		throw new UnsupportedOperationException();
	}

	public void setMapGenerator(ObjectGeneratorForClass<? extends Map> mapGenerator) {
		assertNotLocked();
		this.mapGenerator = mapGenerator;
	}

	public ObjectGeneratorForClass<? extends Map> getMapGenerator() {
		return mapGenerator;
	}
	@Override
	public boolean isLeaf() {
		return false;
	}

}
