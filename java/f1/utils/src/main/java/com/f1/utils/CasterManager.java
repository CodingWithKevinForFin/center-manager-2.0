package com.f1.utils;

import java.util.NoSuchElementException;

import com.f1.base.ValuedEnum;
import com.f1.utils.casters.Caster_Array;
import com.f1.utils.casters.Caster_BasicFixPoint;
import com.f1.utils.casters.Caster_BigDecimal;
import com.f1.utils.casters.Caster_BigInteger;
import com.f1.utils.casters.Caster_Boolean;
import com.f1.utils.casters.Caster_Byte;
import com.f1.utils.casters.Caster_Bytes;
import com.f1.utils.casters.Caster_Character;
import com.f1.utils.casters.Caster_Class;
import com.f1.utils.casters.Caster_Color;
import com.f1.utils.casters.Caster_Complex;
import com.f1.utils.casters.Caster_DateMillis;
import com.f1.utils.casters.Caster_DateNanos;
import com.f1.utils.casters.Caster_Double;
import com.f1.utils.casters.Caster_Enum;
import com.f1.utils.casters.Caster_File;
import com.f1.utils.casters.Caster_FixPoint;
import com.f1.utils.casters.Caster_Float;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.casters.Caster_Long;
import com.f1.utils.casters.Caster_Object;
import com.f1.utils.casters.Caster_Password;
import com.f1.utils.casters.Caster_Short;
import com.f1.utils.casters.Caster_Simple;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.casters.Caster_StringBuilder;
import com.f1.utils.casters.Caster_Timestamp;
import com.f1.utils.casters.Caster_UUID;
import com.f1.utils.casters.Caster_ValuedEnum;

public class CasterManager {

	private static final CopyOnWriteHashMap<Class<?>, AbstractCaster<?>> CASTERS = new CopyOnWriteHashMap<Class<?>, AbstractCaster<?>>();
	static {
		addCaster(Caster_BasicFixPoint.INSTANCE);
		addCaster(Caster_BigDecimal.INSTANCE);
		addCaster(Caster_BigInteger.INSTANCE);
		addCaster(Caster_Boolean.INSTANCE);
		addCaster(Caster_Boolean.PRIMITIVE);
		addCaster(Caster_Byte.INSTANCE);
		addCaster(Caster_Byte.PRIMITIVE);
		addCaster(Caster_Character.INSTANCE);
		addCaster(Caster_Character.PRIMITIVE);
		addCaster(Caster_Class.INSTANCE);
		addCaster(Caster_Double.INSTANCE);
		addCaster(Caster_Double.PRIMITIVE);
		addCaster(Caster_File.INSTANCE);
		addCaster(Caster_FixPoint.INSTANCE);
		addCaster(Caster_Float.INSTANCE);
		addCaster(Caster_Float.PRIMITIVE);
		addCaster(Caster_Integer.INSTANCE);
		addCaster(Caster_Integer.PRIMITIVE);
		addCaster(Caster_Long.INSTANCE);
		addCaster(Caster_Long.PRIMITIVE);
		addCaster(Caster_Short.INSTANCE);
		addCaster(Caster_Short.PRIMITIVE);
		addCaster(Caster_String.INSTANCE);
		addCaster(Caster_StringBuilder.INSTANCE);
		addCaster(Caster_DateMillis.INSTANCE);
		addCaster(Caster_DateNanos.INSTANCE);
		addCaster(Caster_Timestamp.INSTANCE);
		addCaster(Caster_Complex.INSTANCE);
		addCaster(Caster_UUID.INSTANCE);
		addCaster(Caster_Object.INSTANCE);
		addCaster(Caster_Color.INSTANCE);
		addCaster(Caster_Bytes.INSTANCE);
		addCaster(Caster_Password.INSTANCE);
	}

	public static <T> void addCaster(AbstractCaster<T> caster, Class<?> dstClass, boolean force) {
		if (!force && CASTERS.containsKey(dstClass))
			throw new RuntimeException("already exists: " + dstClass + "  " + CASTERS.get(dstClass) + " vs " + caster);
		CASTERS.put(dstClass, caster);
	}
	public static <T> void addCaster(AbstractCaster<T> caster) {
		addCaster(caster, caster.getCastToClass(), false);
	}
	public static <T> void addCasterForce(AbstractCaster<T> caster) {
		addCaster(caster, caster.getCastToClass(), true);
	}
	public static final <C> AbstractCaster<C> getCaster(Class<C> dstClass) {
		if (dstClass == null)
			return null;
		AbstractCaster<?> r = CASTERS.get(dstClass);
		if (r != null)
			return (AbstractCaster<C>) r;
		else if (dstClass == null)
			throw new NullPointerException("type");
		else {
			r = generateCaster(dstClass);
			if (r == null)
				throw new NoSuchElementException(dstClass.getName());
			AbstractCaster<?> existing = CASTERS.putIfAbsent(dstClass, r);
			if (existing != null)
				r = existing;
			return (AbstractCaster<C>) r;
		}
	}

	private static final <C> AbstractCaster<C> generateCaster(Class<C> dstClass) {
		if (dstClass.isArray()) {
			return (AbstractCaster<C>) new Caster_Array(dstClass.getComponentType());
		} else if (dstClass.isEnum()) {
			if (ValuedEnum.class.isAssignableFrom(dstClass)) {
				return (AbstractCaster<C>) new Caster_ValuedEnum((Class<ValuedEnum>) dstClass);
			} else
				return (AbstractCaster<C>) new Caster_Enum((Class<Enum>) dstClass);
		}
		return new Caster_Simple<C>(dstClass);
	}
}
