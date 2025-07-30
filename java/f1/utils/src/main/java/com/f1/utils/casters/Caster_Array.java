package com.f1.utils.casters;

import java.lang.reflect.Array;
import java.util.Collection;

import com.f1.base.IterableAndSize;
import com.f1.utils.AbstractCaster;
import com.f1.utils.DetailedException;
import com.f1.utils.OH;

public class Caster_Array<T> extends AbstractCaster<T> {

	public static final Caster_Array<byte[]> BYTE_PRIMITIVE = new Caster_Array(byte.class);
	public static final Caster_Array<Byte[]> BYTE = new Caster_Array(Byte.class);
	public static final Caster_Array<short[]> SHORT_PRIMITIVE = new Caster_Array(short.class);
	public static final Caster_Array<Short[]> SHORT = new Caster_Array(Short.class);
	public static final Caster_Array<int[]> INTEGER_PRIMITIVE = new Caster_Array(int.class);
	public static final Caster_Array<Integer[]> INTEGER = new Caster_Array(Integer.class);
	public static final Caster_Array<long[]> LONG_PRIMITIVE = new Caster_Array(long.class);
	public static final Caster_Array<Long[]> LONG = new Caster_Array(Long.class);
	public static final Caster_Array<float[]> FLOAT_PRIMITIVE = new Caster_Array(float.class);
	public static final Caster_Array<Float[]> FLOAT = new Caster_Array(Float.class);
	public static final Caster_Array<double[]> DOUBLE_PRIMITIVE = new Caster_Array(double.class);
	public static final Caster_Array<Double[]> DOUBLE = new Caster_Array(Double.class);
	public static final Caster_Array<boolean[]> BOOLEAN_PRIMITIVE = new Caster_Array(boolean.class);
	public static final Caster_Array<Boolean[]> BOOLEAN = new Caster_Array(Boolean.class);
	public static final Caster_Array<char[]> CHARACTER_PRIMITIVE = new Caster_Array(char.class);
	public static final Caster_Array<Character[]> CHARACTER = new Caster_Array(Character.class);

	public static final Caster_Array<String[]> STRING = new Caster_Array(String.class);
	public static final Caster_Array<Object[]> OBJECT = new Caster_Array(Object.class);

	final private Class<?> componentType;

	public Caster_Array(Class<?> componentType) {
		super((Class<T>) Array.newInstance(componentType, 0).getClass());
		this.componentType = componentType;
	}

	@Override
	protected T castInner(Object o, boolean throwExceptionOnError) {
		Class<? extends Object> srcClass = o.getClass();
		if (o instanceof IterableAndSize) {
			IterableAndSize c = (IterableAndSize) o;
			Class<?> componentType = this.componentType;
			Object r = Array.newInstance(componentType, c.size());
			int pos = 0;
			for (Object obj : c)
				Array.set(r, pos++, OH.getCaster(componentType).cast(obj, false, throwExceptionOnError));
			return (T) r;
		} else if (o instanceof Collection) {
			Collection c = (Collection) o;
			Class<?> componentType = this.componentType;
			Object r = Array.newInstance(componentType, c.size());
			int pos = 0;
			for (Object obj : c)
				Array.set(r, pos++, OH.getCaster(componentType).cast(obj, false, throwExceptionOnError));
			return (T) r;
		} else if (srcClass.isArray()) {
			Class<?> componentType = this.componentType;
			int len = Array.getLength(o);
			Object r = Array.newInstance(componentType, len);
			int pos = 0;
			for (int i = 0; i < len; i++) {
				Array.set(r, pos++, OH.getCaster(componentType).cast(Array.get(o, i), false, throwExceptionOnError));
			}
			return (T) r;
		}
		if (throwExceptionOnError)
			throw new DetailedException("auto-cast failed").set("value", o).set("cast from class", srcClass).set("cast to class", getCastToClass());
		else
			return null;
	}

}
