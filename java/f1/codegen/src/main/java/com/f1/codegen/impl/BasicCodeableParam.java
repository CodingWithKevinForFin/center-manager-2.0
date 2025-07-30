/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.codegen.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import com.f1.base.Transient;
import com.f1.base.UnsupportedField;
import com.f1.base.Valued;
import com.f1.base.ValuedListenable;
import com.f1.codegen.CodeableParam;
import com.f1.utils.BasicTypeHelper;
import com.f1.utils.CH;
import com.f1.utils.FixPoint;
import com.f1.utils.OH;
import com.f1.utils.RH;
import com.f1.utils.SH;

public class BasicCodeableParam implements CodeableParam {

	private final String name;
	private final String varname;
	private Class type;
	private Class boxedType;
	private final String upperName;
	private final boolean isAbstract;
	private byte transience;
	private final boolean valid;
	private byte basicType;
	private Map<String, String> annotations;
	private int position;
	private boolean unsupported;

	public BasicCodeableParam(Method getter, Method setter) {
		this.upperName = OH.noNull(getter, setter).getName().substring(3);
		this.name = SH.lowercaseFirstChar(upperName);
		this.varname = "_" + this.name;
		isAbstract = (getter != null && Modifier.isAbstract(getter.getModifiers())) || (setter != null && Modifier.isAbstract(setter.getModifiers()));
		Class<?> setParam;
		if (setter != null) {
			if (isAbstract != Modifier.isAbstract(setter.getModifiers())) {
				valid = false;
				return;
			}
			Class<?>[] setterParams = setter.getParameterTypes();
			if (setterParams.length != 1) {
				valid = false;
				return;
			}
			setParam = setterParams[0];
		} else
			setParam = null;
		Class<?> getParam = getter == null ? null : getter.getReturnType();
		if (setParam == null || (getParam != null && getParam.isAssignableFrom(setParam)))
			this.type = getParam;
		else if (getParam == null || setParam.isAssignableFrom(getParam))
			this.type = setParam;
		else {
			valid = false;
			return;
		}
		valid = true;
		this.boxedType = OH.getBoxed(type);
		basicType = BasicTypeHelper.toType(type);
		annotations = getAnnotations(getter);
		if (setter != null) {
			Map<String, String> annotations2 = getAnnotations(setter);
			for (String s : CH.comm(annotations.keySet(), annotations2.keySet(), false, false, true))
				OH.assertEq(annotations.get(s), annotations2.get(s));
			annotations.putAll(annotations2);
		}
		transience = Byte.parseByte(CH.getOr(annotations, Transient.class.getSimpleName() + "_value", "0"));
		unsupported = annotations.containsKey(UnsupportedField.class.getSimpleName());
	}

	public static Map<String, String> getAnnotations(AnnotatedElement getter) {
		Map<String, String> r = new HashMap<String, String>();
		if (getter != null)
			for (Annotation a : getter.getDeclaredAnnotations()) {
				String name = a.annotationType().getSimpleName();
				r.put(name, "");
				for (Map.Entry<String, Object> e : RH.getAnnotationProperties(a).entrySet())
					r.put(name + "_" + e.getKey(), OH.toString(e.getValue()));
			}
		return r;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Class getType() {
		return type;
	}

	@Override
	public String getUpperCasedName() {
		return upperName;
	}

	@Override
	public boolean isAbstract() {
		return isAbstract;
	}

	public boolean isValid() {
		return valid;
	}

	public boolean getSupported() {
		return !unsupported;

	}

	@Override
	public Class getBoxedType() {
		return boxedType;
	}

	@Override
	public byte getBasicType() {
		return basicType;
	}

	@Override
	public Map<String, String> getAnnotations() {
		return annotations;
	}

	@Override
	public String getDefaultValue() {
		if (type.isPrimitive()) {
			if (boxedType == Byte.class)
				return "(byte)0";
			else if (boxedType == Short.class)
				return "(short)0";
			else if (boxedType == Integer.class)
				return "0";
			else if (boxedType == Long.class)
				return "0L";
			else if (boxedType == Float.class)
				return "0F";
			else if (boxedType == Double.class)
				return "0D";
			else if (boxedType == Character.class)
				return "(char)0";
			else if (boxedType == Boolean.class)
				return "false";
		}
		return "null";
	}

	@Override
	public String getStreamName() {
		if (type.isPrimitive()) {
			if (type == char.class)
				return "Char";
			if (type == int.class)
				return "Int";
			return boxedType.getSimpleName();
		}
		return "Object";
	}

	@Override
	public boolean getIsBoxedType() {
		return OH.isBoxed(getType());
	}

	@Override
	public boolean getIsImmutable() {
		return OH.isImmutableClass(type);
	}

	@Override
	public boolean getIsString() {
		return type == String.class;
	}

	@Override
	public boolean getIsFixPoint() {
		return FixPoint.class.isAssignableFrom(type);
	}

	@Override
	public int getPosition() {
		return position;
	}

	@Override
	public void setPosition(int position) {
		this.position = position;
	}

	@Override
	public boolean getIsBoolean() {
		return type == boolean.class;
	}

	@Override
	public boolean getIsByte() {
		return type == byte.class;
	}

	@Override
	public boolean getIsChar() {
		return type == char.class;
	}

	@Override
	public boolean getIsShort() {
		return type == short.class;
	}

	@Override
	public boolean getIsInt() {
		return type == int.class;
	}

	@Override
	public boolean getIsLong() {
		return type == long.class;
	}

	@Override
	public boolean getIsDouble() {
		return type == double.class;
	}

	@Override
	public boolean getIsFloat() {
		return type == float.class;
	}

	@Override
	public byte getTransience() {
		return transience;
	}

	@Override
	public String getVarname() {
		return varname;
	}

	@Override
	public boolean isValuedType() {
		return Valued.class.isAssignableFrom(type);
	}

	@Override
	public boolean getIsValuedListenable() {
		return ValuedListenable.class.isAssignableFrom(type);
	}

}

