package com.f1.utils.casters;

import com.f1.base.ValuedEnum;
import com.f1.utils.AbstractCaster;
import com.f1.utils.DetailedException;

public class Caster_Character extends AbstractCaster<Character> {

	public static final Caster_Character INSTANCE = new Caster_Character(false);
	public static final Caster_Character PRIMITIVE = new Caster_Character(true);
	final private boolean primitive;

	public Caster_Character(boolean primitive) {
		super(primitive ? char.class : Character.class);
		this.primitive = primitive;
	}

	@Override
	protected Character castInner(Object o, boolean throwExceptionOnError) {
		Class<?> srcClass = o.getClass();
		if (this.primitive) {
			if (srcClass == String.class) {
				if ("null".equals(o))
					return null;
				String s = (String) o;
				if (s.length() != 1) {
					if (throwExceptionOnError)
						throw new DetailedException("auto-cast failed, length of string must be 1").set("value", o).set("cast from class", srcClass).set("cast to class",
								getCastToClass());
					else
						return null;
				}
				return ((String) o).charAt(0);
			} else if (o instanceof Number) {
				short n = ((Number) o).shortValue();
				return (char) n;
			} else if (srcClass.isEnum()) {
				if (o instanceof ValuedEnum)
					return cast(((ValuedEnum<?>) o).getEnumValue());
				return cast(((Enum<?>) o).ordinal());
			} else if (srcClass == Character.class) {
				return (Character) o;
			}
		} else if (o instanceof Integer) {
			int i = ((Integer) o).intValue();
			return (char) i;
		} else {
			String s = o.toString();
			if (s.length() == 1)
				return s.charAt(0);
			else {
				if (throwExceptionOnError)
					throw new NumberFormatException("cannot be parsed to a char:'" + s + "'");
				else
					return null;
			}
		}
		if (throwExceptionOnError)
			throw new DetailedException("auto-cast failed").set("value", o).set("cast from class", srcClass).set("cast to class", getCastToClass());
		else
			return null;
	}
}
