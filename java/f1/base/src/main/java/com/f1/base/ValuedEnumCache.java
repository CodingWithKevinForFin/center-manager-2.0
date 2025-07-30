package com.f1.base;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 
 * Caches maps for each {@link ValuedEnum}s so that you can quickly map between their enumerated items and the values they represent. See {@link #getCache(Class)}
 * <P>
 * This is similar to the {@link EnumSet} put for ValuedEnums.
 * 
 */
public class ValuedEnumCache<R, E extends ValuedEnum> {

	private static final long MAX_PRIMITVE_ARRAY_LENGTH = 1000;
	private final Map<R, E> _values;
	private final Class<R> _returnType;
	private final Class<? extends ValuedEnum> _clazz;
	private final String message;
	private E[] valuesByPrimitive;
	private long minByPrimitive, maxByPrimitive;

	private ValuedEnumCache(Class<? extends E> clazz_, Map<Object, E> values_, Class<?> type_) {
		_values = (Map<R, E>) values_;
		_returnType = (Class<R>) type_;
		_clazz = clazz_;
		this.message = "undefined value for enum " + _clazz.getSimpleName();
		if (!values_.isEmpty()) {
			if (Integer.class.isAssignableFrom(type_) || Short.class.isAssignableFrom(type_) || Byte.class.isAssignableFrom(type_) || Long.class.isAssignableFrom(type_))
				buildPrimitiveMap(false);
			else if (Character.class.isAssignableFrom(type_))
				buildPrimitiveMap(true);
		}
	}
	private void buildPrimitiveMap(boolean isChar) {
		long min = 0, max = 0;
		boolean first = true;
		for (Map.Entry<R, E> e : _values.entrySet()) {
			long i = isChar ? (((Character) e.getKey()).charValue()) : ((Number) e.getKey()).longValue();
			if (first) {
				min = max = i;
				first = false;
			}
			if (min > i)
				min = i;
			if (max < i)
				max = i;
		}
		minByPrimitive = min;
		maxByPrimitive = max - min < MAX_PRIMITVE_ARRAY_LENGTH ? max : (min + MAX_PRIMITVE_ARRAY_LENGTH - 1);
		valuesByPrimitive = (E[]) new ValuedEnum[(int) (maxByPrimitive - minByPrimitive) + 1];
		for (Map.Entry<R, E> e : _values.entrySet()) {
			long i = isChar ? (((Character) e.getKey()).charValue()) : ((Number) e.getKey()).longValue();
			if (isBetween(i, minByPrimitive, maxByPrimitive))
				valuesByPrimitive[(int) (i - minByPrimitive)] = e.getValue();
		}
	}

	private boolean isBetween(long i, long min, long max) {
		return i >= min && i <= max;
	}

	/**
	 * @return get the enum value associated with an underlying value (only works if the ValueEnum is a number type: Long, Int, etc)
	 */
	public E getValueByPrimitive(long i, E default_) {
		if (isBetween(i, minByPrimitive, maxByPrimitive)) {
			E r = valuesByPrimitive[(int) (i - minByPrimitive)];
			return r == null ? default_ : r;
		}
		return getValue(cast(i, _returnType), default_);
	}
	public E getValueByPrimitive(long i) {
		if (isBetween(i, minByPrimitive, maxByPrimitive)) {
			E r = valuesByPrimitive[(int) (i - minByPrimitive)];
			if (r != null)
				return r;
		}
		return getValue(cast(i, _returnType));
	}

	/**
	 * @return get all of the values for a given ValuedEnum
	 */
	public Set<R> getValues() {
		return _values.keySet();
	}

	/**
	 * @return get the type of values assoicated with this enum
	 */
	public Class<R> getReturnType() {
		return _returnType;
	}

	/**
	 * @return get the enum item for a given value, and if the given value doesnt exist in the enum, return the default
	 */
	public E getValue(R value_, E default_) {

		E r = _values.get(value_);
		return r != null ? r : default_;
	}
	/**
	 * @return get the enum item for a given value
	 */
	public E getValue(R value_) {
		final E r = _values.get(value_);
		if (r == null)
			throw new RuntimeException(message + " supplied key: " + value_ + "   permissible values: " + _values.keySet());
		return r;
	}

	private static final ConcurrentMap<Class, ValuedEnumCache> class2values = new ConcurrentHashMap<Class, ValuedEnumCache>();

	/**
	 * Returns the cached mapping for a given ValuedEnum
	 */
	@SuppressWarnings("restriction")
	public static <R, E extends ValuedEnum<R>> ValuedEnumCache<R, E> getCache(Class<E> clazz) {
		ValuedEnumCache<R, E> cache = class2values.get(clazz);
		if (cache == null) {
			HashMap<Object, E> values = new HashMap<Object, E>();
			@SuppressWarnings("unchecked")
			ValuedEnum[] set = (ValuedEnum[]) clazz.getEnumConstants();
			Class type = null;
			for (ValuedEnum e : set) {
				if (type == null)
					type = e.getEnumValue().getClass();
				else if (type != e.getEnumValue().getClass())
					throw new RuntimeException("inconsistent types for " + clazz.getName() + ": " + type + " != " + e.getEnumValue().getClass());
				if (values.put(e.getEnumValue(), (E) e) != null)
					throw new RuntimeException("duplicate key for valued enum " + clazz.getName() + ": " + e.getEnumValue());
			}
			ValuedEnumCache<R, E> existing = class2values.putIfAbsent(clazz, cache = new ValuedEnumCache(clazz, values, type));
			if (existing != null)
				cache = existing;
		}
		return cache;
	}

	/**
	 * Convenience function for directly getting the value of an enum and and it's item
	 */
	public static <E extends ValuedEnum> E getEnumValue(Class<E> clazz, Object value) {
		ValuedEnumCache cache = getCache(clazz);
		return (E) cache.getValue(value);
	}

	/**
	 * Convenience function for directly getting the enum value of an enum and and it's value
	 */
	public static <E extends ValuedEnum> E getEnumValue(Class<E> clazz, Object value, E dflt) {
		ValuedEnumCache cache = getCache(clazz);
		return (E) cache.getValue(value, dflt);
	}

	/**
	 * @return get the return type of items in a valued enum
	 */
	public static Class getEnumValueType(Class<? extends ValuedEnum> clazz) {
		return getCache(clazz).getReturnType();
	}

	/**
	 * @return get list of currently cached enums
	 */
	public static Set<Class> getCached() {
		return class2values.keySet();
	}

	static private <R> R cast(long i, Class<R> clazz) {
		if (clazz == Integer.class)
			return (R) (Integer) (int) i;
		if (clazz == Long.class)
			return (R) (Long) (long) i;
		if (clazz == Byte.class)
			return (R) (Byte) (byte) i;
		if (clazz == Short.class)
			return (R) (Short) (short) i;
		if (clazz == Character.class)
			return (R) (Character) (char) i;
		throw new RuntimeException("can not cast to " + clazz.getName() + ": " + i);
	}
}
