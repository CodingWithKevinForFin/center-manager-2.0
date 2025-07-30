/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Reflection Helper
 */
public class RH {

	public static final Object VOID_RETURN = new Object() {
		@Override
		public String toString() {
			return "VOID_RETURN";
		}
	};
	public static final Package PACKAGE_JAVA_LANG = java.lang.String.class.getPackage();
	public static final ClassLoader CLASSLOADER = java.lang.String.class.getClassLoader();
	public static final String NULL = "null";
	public static final String TRUE = "true";
	public static final String FALSE = "false";
	public static final String THIS = "this";
	public static final String NEW = "new";
	public static final String FOR = "for";
	public static final String IMPORT = "import";
	public static final String CLASS = "class";
	public static final String LENGTH = "length";
	public static final String INSTANCEOF = "instanceof";
	public static final String WHILE = "while";
	public static final String THROWS = "throws";
	public static final String DO = "do";
	public static final String IF = "if";
	public static final String ELSE = "else";
	public static final String RETURN = "return";
	public static final String BREAK = "break";
	public static final String THROW = "throw";
	public static final String CATCH = "catch";
	public static final String CONTINUE = "continue";

	public static final char JAVA_SIGNATURE_BOOLEAN = 'Z';
	public static final char JAVA_SIGNATURE_BYTE = 'B';
	public static final char JAVA_SIGNATURE_CHAR = 'C';
	public static final char JAVA_SIGNATURE_SHORT = 'S';
	public static final char JAVA_SIGNATURE_INT = 'I';
	public static final char JAVA_SIGNATURE_LONG = 'J';
	public static final char JAVA_SIGNATURE_FLOAT = 'F';
	public static final char JAVA_SIGNATURE_DOUBLE = 'D';
	public static final char JAVA_SIGNATURE_CLASS = 'L';
	public static final char JAVA_SIGNATURE_ARRAY = '[';
	public static final char JAVA_SIGNATURE_VOID = 'V';

	static public void setField(Object target, String fieldName, Object value) {
		try {

			Field field = findField(target.getClass(), fieldName);
			if ((field.getModifiers() & (Modifier.PRIVATE | Modifier.PROTECTED)) != 0)
				field.setAccessible(true);
			field.set(target, value);
		} catch (Exception e) {
			throw new ReflectionException("Field not found for setting: " + target.getClass().getName() + "::" + fieldName + " to " + value, e);
		}
	}
	static public void setStaticField(Class target, String fieldName, Object value) {
		try {

			Field field = findField(target, fieldName);
			if ((field.getModifiers() & (Modifier.PRIVATE | Modifier.PROTECTED)) != 0)
				field.setAccessible(true);
			field.set(null, value);
		} catch (Exception e) {
			throw new ReflectionException("Field not found for setting: " + target.getName() + "::" + fieldName + " to " + value, e);
		}
	}

	static public Object getStaticField(Class target, String fieldName) {
		Field field = findField(target, fieldName);
		try {
			if ((field.getModifiers() & Modifier.PUBLIC) == 0)
				field.setAccessible(true);
			return field.get(null);
		} catch (Exception e) {
			throw new ReflectionException("Static Field not found for getting: " + target.getName() + "::" + fieldName, e);
		}
	}

	static public Object getField(Object target, String fieldName) {
		try {
			if (LENGTH.equals(fieldName))
				return Array.getLength(target);
			Field field = findField(target.getClass(), fieldName);
			if ((field.getModifiers() & (Modifier.PRIVATE | Modifier.PROTECTED)) != 0)
				field.setAccessible(true);
			return field.get(target);
		} catch (Exception e) {
			throw new ReflectionException("Field not found for inspection: " + target.getClass().getName() + "::" + fieldName, e);
		}
	}

	static public Class getDeclaredClass(Class c, String name) {
		for (Class declaredClass : c.getDeclaredClasses()) {
			if (declaredClass.getSimpleName().equals(name))
				return declaredClass;
		}
		throw new ReflectionException("declared class '" + name + "'not found in " + c.getName());
	}

	static public Field findField(Class c, String fieldName) {
		Class c2 = c;
		while (c2 != null) {
			for (Field f : c2.getDeclaredFields())
				if (fieldName.equals(f.getName()))
					return f;
			c2 = c2.getSuperclass();
		}
		throw new ReflectionException("field '" + fieldName + "'not found in " + c.getName());
	}
	static public Field findFieldNoThrow(Class c, String fieldName) {
		Class c2 = c;
		while (c2 != null) {
			for (Field f : c2.getDeclaredFields())
				if (fieldName.equals(f.getName()))
					return f;
			c2 = c2.getSuperclass();
		}
		return null;
	}

	public static String toLegibleString(Class clazz) {
		return toLegibleString(clazz, new StringBuilder()).toString();
	}

	public static String toLegibleString(Field field, boolean modifiers, boolean returnType) {
		return toLegibleString(field, modifiers, returnType, new StringBuilder()).toString();
	}

	public static StringBuilder toLegibleString(Field m, boolean modifiers, boolean returnType, StringBuilder sb) {
		if (modifiers)
			sb.append(Modifier.toString(m.getModifiers())).append(' ');
		if (returnType)
			sb.append(RH.toLegibleString(m.getType())).append(' ');
		sb.append(m.getName());
		return sb;
	}

	public static StringBuilder toLegibleString(Class clazz, StringBuilder sb) {
		if (clazz == null)
			return sb.append("null");
		if (clazz.isArray())
			return toLegibleString(clazz.getComponentType(), sb).append("[]");
		if (clazz.getPackage() == PACKAGE_JAVA_LANG)
			return sb.append(clazz.getSimpleName());
		if (clazz.getDeclaringClass() != null)
			return toLegibleString(clazz.getDeclaringClass(), sb).append('.').append(clazz.getSimpleName());
		return sb.append(clazz.getName());
	}

	public static String toLegibleString(Method m, boolean modifiers, boolean returnType) {
		return toLegibleString(m, modifiers, returnType, OH.EMPTY_STRING_ARRAY, new StringBuilder()).toString();
	}

	public static String toLegibleString(Method m, boolean modifiers, boolean returnType, String[] paramNames) {
		return toLegibleString(m, modifiers, returnType, paramNames, new StringBuilder()).toString();
	}

	public static StringBuilder toLegibleString(Method m, boolean modifiers, boolean returnType, StringBuilder sb) {
		return toLegibleString(m, modifiers, returnType, OH.EMPTY_STRING_ARRAY, sb);
	}

	public static StringBuilder toLegibleString(Method m, boolean modifiers, boolean returnType, String paramNames[], StringBuilder sb) {
		if (modifiers)
			sb.append(Modifier.toString(m.getModifiers())).append(' ');
		if (returnType)
			sb.append(RH.toLegibleString(m.getReturnType())).append(' ');
		sb.append(m.getName());
		sb.append('(');
		Class c[] = m.getParameterTypes();
		for (int i = 0; i < c.length; i++) {
			if (i > 0)
				sb.append(',');
			sb.append(RH.toLegibleString(c[i]));
			if (c.length == paramNames.length)
				sb.append(' ').append(paramNames[i]);
		}
		sb.append(')');
		if (m.getExceptionTypes().length > 0) {
			sb.append(' ').append(THROWS).append(' ');
			boolean first = true;
			for (Class<?> excType : m.getExceptionTypes()) {
				if (first)
					first = false;
				else
					sb.append(", ");
				toLegibleString(excType, sb);
			}
		}
		return sb;
	}

	public static Method findMethod(Class clazz, String methodName, Class[] args) {
		if (clazz == null)
			throw new NullPointerException("class");
		if (methodName == null)
			throw new NullPointerException("methodName");
		if (args == null)
			args = new Class[0];
		Method found = null;
		Class cl = clazz;
		while (cl != null) {
			outer: for (Method m : cl.getDeclaredMethods()) {
				if (!m.getName().equals(methodName))
					continue;
				Class[] mParams = m.getParameterTypes();
				if (m.isVarArgs()) {
					if (args.length + 1 < mParams.length)
						continue;
					inner: for (int i = 0; i < mParams.length - 1; i++)
						if (args[i] != null && !OH.isCoercable(mParams[i], args[i]))
							continue outer;
					Class varArgType = AH.last(mParams).getComponentType();
					for (int i = mParams.length - 1; i < args.length; i++)
						if (args[i] != null && !OH.isCoercable(varArgType, args[i]))
							continue outer;
				} else {
					if (args.length != mParams.length)
						continue;
					inner: for (int i = 0; i < mParams.length; i++)
						if (args[i] != null && !OH.isCoercable(mParams[i], args[i]))
							continue outer;
				}
				if (found != null) {
					if (m.isVarArgs() != found.isVarArgs()) {
						if (found.isVarArgs())
							found = m;
						continue outer;
					}
					Class<?>[] t1 = m.getParameterTypes();
					Class<?>[] t2 = found.getParameterTypes();
					if (Arrays.equals(t1, t2)) {
						continue outer; // this is an Overridden method
					}
					Class[] narrower = findNarrower(t1, t2);
					if (narrower == null)
						throw new ReflectionException(
								"ambiguos method: " + clazz.getName() + "." + methodName + "(" + SH.join(',', getClassNames(args)) + "); : " + m + " and " + found);
					if (narrower == t1)
						found = m;
					continue outer; // this is an Overridden method
				}
				found = m;
			}
			cl = cl.getSuperclass();
		}
		if (found != null)
			return found;
		throw new ReflectionException("method not found: " + clazz.getName() + "." + methodName + "(" + SH.join(',', getClassNames(args)) + ");");
	}

	public static Object invokeStaticMethod(String clazz, String methodName, Object... params) {
		return invokeStaticMethod(getClass(clazz), methodName, params);
	}

	public static Object invokeStaticMethod(Class clazz, String methodName, Object... params) {
		return invokeMethod(clazz, null, methodName, params);
	}

	public static Object invokeMethod(Object target, String methodName, Object... params) {
		return invokeMethod(target.getClass(), target, methodName, params);
	}

	private static Object invokeMethod(Class c, Object target, String methodName, Object... params) {
		Class[] fields = getClasses(params);
		Method method = RH.findMethod(c, methodName, fields);
		if (!method.isAccessible())
			method.setAccessible(true);
		try {
			if (method.isVarArgs()) {
				Class<?>[] types = method.getParameterTypes();
				int len = types.length;
				if (len == params.length + 1) {
					// dummy up the varargs array param
					params = AH.insert(params, params.length, Array.newInstance(types[types.length - 1].getComponentType(), 0));
				} else if (len <= params.length && params[len - 1] != null && !params[len - 1].getClass().isArray()) {
					Class<?> componentType = AH.last(types).getComponentType();
					final Object[] params2 = new Object[len];
					final Object varargs = Array.newInstance(componentType, params.length - len + 1);
					AH.arraycopy(params, 0, params2, 0, len - 1);
					for (int i = len - 1, j = 0; i < params.length; i++, j++)
						Array.set(varargs, j, params[i]);
					params2[params2.length - 1] = varargs;
					params = params2;
				}
			}
			if (!Modifier.isStatic(method.getModifiers()) && target == null)
				throw new ReflectionException("not static method: " + c.getName() + "(" + SH.join(',', getClassNames(fields)) + ")");
			return invokeMethod(method, target, params);
		} catch (Exception e) {
			throw toRuntime(e);
		}

	}

	private static Object invokeMethod(Method method, Object target, Object[] params) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		Object r = method.invoke(target, params);
		return method.getReturnType() == void.class ? VOID_RETURN : r;

	}

	public static Object invokeConstructor(String className_) {
		try {
			return getClass(className_).newInstance();
		} catch (Exception e) {
			throw toRuntime(e);
		}
	}

	public static Object invokeConstructor(String className_, Object... params) {
		return invokeConstructor(getClass(className_), params);
	}

	public static <C> C invokeConstructor(Class<C> clazz, Object... params) {

		final Class[] fields = getClasses(params);
		return invokeConstructor(findConstructor(clazz, fields), params);
	}

	public static <C> C invokeConstructor(Constructor<C> constructor, Object... params) {
		try {
			return constructor.newInstance(params);
		} catch (Exception e) {
			throw toRuntime(e);
		}
	}

	public static <C> Constructor<C> findConstructor(Class<C> clazz, Class[] paramTypes) {
		if (AH.isEmpty(paramTypes))
			try {
				return clazz.getConstructor(OH.EMPTY_CLASS_ARRAY);
			} catch (Exception e) {
				throw toRuntime(e);
			}
		int paramsCount = paramTypes.length;
		Constructor<C> found = null;
		CONSTRUCTOR_LOOP: for (Constructor c : clazz.getConstructors()) {
			Class[] constructorTypes = c.getParameterTypes();
			if (constructorTypes.length == paramsCount) {
				for (int i = 0; i < paramsCount; i++)
					if (paramTypes[i] != null && !OH.isCoercable(constructorTypes[i], paramTypes[i]))
						continue CONSTRUCTOR_LOOP;
				if (found == null) {
					found = c;
				} else {
					Class[] foundTypes = found.getParameterTypes();
					Class[] closer = findNarrower(foundTypes, constructorTypes);
					if (closer == null)
						throw new ReflectionException("ambigous params: " + clazz.getName() + "(" + SH.join(',', getClassNames(paramTypes)) + ")");
					if (closer == constructorTypes)
						found = c;
				}
			}
		}
		if (found == null)
			throw new ReflectionException("constructor not found: " + clazz.getName() + "(" + SH.join(',', getClassNames(paramTypes)) + ")");
		return found;

	}

	public static <C> C findConstructorAndInvoke(Class<C> clazz, Class[] paramTypes, Object[] params) {
		try {
			if (paramTypes == null && params != null)
				return invokeConstructor(findConstructor(clazz, getClasses(params)), params);
			else
				return invokeConstructor(findConstructor(clazz, paramTypes), params);
		} catch (Exception e) {
			throw toRuntime(e);
		}
	}

	private static Class[] findNarrower(Class[] lefts, Class[] rights) {
		Class[] narrower = null;
		for (int i = 0, l = lefts.length; i < l; i++) {
			Class left = lefts[i], right = rights[i];
			if (left != right) {
				boolean b1 = OH.isCoercable(left, right), b2 = OH.isCoercable(right, left);
				if (b1 == b2)
					continue;
				Class[] t = b2 ? lefts : rights;
				if (narrower == null)
					narrower = t;
				else if (narrower == t)
					return null;
			}
		}
		return narrower;
	}

	private static RuntimeException toRuntime(Exception e) {
		if (e instanceof InvocationTargetException)
			return OH.toRuntime(((InvocationTargetException) e).getTargetException());
		if (e instanceof ClassNotFoundException)
			throw new ReflectionException("class not found: " + e.getMessage(), e);
		return OH.toRuntime(e);
	}

	private static String[] getClassNames(Class[] classes) {
		final String[] r = new String[classes.length];
		for (int i = 0; i < r.length; i++)
			r[i] = classes[i] == null ? NULL : classes[i].getName();
		return r;
	}

	public static Class[] getClasses(Object[] objects) {
		final Class[] r = new Class[objects.length];
		for (int i = 0; i < r.length; i++)
			r[i] = objects[i] == null ? null : objects[i].getClass();
		return r;
	}

	public static Class getClass(String clazz) {
		try {
			return Class.forName(clazz);
		} catch (Exception e) {
			throw toRuntime(e);
		}
	}

	public static Object newArray(String name, int size) {
		return Array.newInstance(getClass(name), size);
	}

	public static Object newArray(String name, Integer... size) {
		return newArrayNd(getClass(name), size);
	}

	public static Object newArrayNd(Class clazz, Integer... size) {
		if (size.length < 1 || size[0] == null)
			throw new ReflectionException("invalid sizes for array: " + clazz.getName() + "[" + SH.join("][", (Object[]) size) + "]");
		for (int i = 1; i < size.length; i++)
			if (size[i] == null) {
				for (i++; i < size.length; i++)
					if (size[i] != null)
						throw new ReflectionException("invalid sizes for array: " + clazz.getName() + "[" + SH.join("][", (Object[]) size) + "]");
			}
		Object inner;

		for (int j = 0; j < size.length; j++) {
			if (size[j] == null || (j > 0 && size[j - 1] == 0)) {
				for (; j < size.length; j++) {
					clazz = Array.newInstance(clazz, 0).getClass();
					size[j] = null;
				}
				break;
			}
		}
		return newArray(clazz, 0, size);
	}

	public static Object newArray(Class clazz, int offset, Integer... size) {
		if (offset + 1 == size.length || size[offset + 1] == null)
			return Array.newInstance(clazz, size[offset]);
		int s = size[offset];
		Object first = newArray(clazz, offset + 1, size);
		Object r = Array.newInstance(first.getClass(), s);
		Array.set(r, 0, first);
		for (int i = 1; i < s; i++)
			Array.set(r, i, newArray(clazz, offset + 1, size));
		return r;
	}

	private static final Object NOT_EXISTS = new Object();
	private static final ConcurrentMap<String, Object> classes = new ConcurrentHashMap<String, Object>();

	public static Class<?> getClassNoThrow(String name) {
		Object r = classes.get(name);
		if (r != null)
			return r == NOT_EXISTS ? null : (Class<?>) r;
		try {
			Object existing = classes.putIfAbsent(name, r = Class.forName(name));
			return (Class<?>) (existing != null ? existing : r);
		} catch (ClassNotFoundException e) {
			classes.put(name, NOT_EXISTS);
			return null;
		}
	}

	public static String describe(Class<? extends Object> o) {
		return describe(o, new StringBuilder()).toString();
	}

	public static String describe(Object o) {
		return describe(o, new StringBuilder()).toString();
	}

	public static StringBuilder describe(Class<? extends Object> o, StringBuilder sb) {
		return describe(o, null, sb);
	}

	public static StringBuilder describe(Object ob, StringBuilder sb) {
		if (ob == null)
			sb.append(NULL + SH.NEWLINE);
		else
			describe(ob.getClass(), ob, sb);
		return sb;
	}

	// TODO: handle arrays
	private static StringBuilder describe(Class<? extends Object> clazz, Object ob, StringBuilder sb) {
		RH.toLegibleString(clazz, sb);
		sb.append(" {" + SH.NEWLINE);
		for (Class c = clazz; c != null; c = c.getSuperclass()) {
			sb.append(SH.NEWLINE + "  // Methods From ").append(c.getSimpleName()).append(SH.NEWLINE);
			Method[] methods = c.getDeclaredMethods();
			if (methods.length > 0) {
				for (Method m : c.getDeclaredMethods()) {
					sb.append("  ");
					RH.toLegibleString(m, true, true, sb);
					sb.append(";" + SH.NEWLINE);
				}
			}
			Field[] fields = c.getDeclaredFields();
			if (fields.length > 0) {
				sb.append(SH.NEWLINE + "  // Fields From ").append(c.getSimpleName()).append(SH.NEWLINE);
				for (Field m : fields) {
					sb.append("  ");
					toLegibleString(m, true, true, sb);
					if (ob != null || Modifier.isStatic(m.getModifiers())) {
						sb.append(" = ");
						try {
							if (!m.isAccessible())
								m.setAccessible(true);
							Object value = m.get(ob);
							if (value instanceof String) {
								String s = SH.ddd(SH.CHAR_QUOTE + SH.toStringEncode(value.toString(), SH.CHAR_QUOTE) + SH.CHAR_QUOTE, 80);
								sb.append(s);
							} else if (value instanceof Character)
								SH.toStringEncode((Character) value, SH.CHAR_SINGLE_QUOTE, sb.append(SH.CHAR_SINGLE_QUOTE)).append(SH.CHAR_SINGLE_QUOTE);
							else if (value == null || OH.isBoxed(value.getClass()))
								sb.append(value);
							else if (value.getClass().isArray()) {
								toLegibleString(value.getClass().getComponentType(), sb.append('('));
								sb.append('[').append(Array.getLength(value)).append(']');
								sb.append('@').append(System.identityHashCode(value)).append(')');
							} else
								toLegibleString(value.getClass(), sb.append('(')).append('@').append(System.identityHashCode(value)).append(')');
						} catch (Exception e) {
							throw toRuntime(e);
						}
					}
					sb.append(";" + SH.NEWLINE);
				}
			}
		}
		sb.append("}" + SH.NEWLINE);
		return sb;
	}

	public static Map<String, Object> getAnnotationProperties(Annotation a) {
		if (a == null)
			return null;
		try {
			Map<String, Object> r = new HashMap<String, Object>();
			for (Method m : a.annotationType().getDeclaredMethods())
				r.put(m.getName(), invokeMethod(m, a, null));
			return r;
		} catch (Exception e) {
			throw new ReflectionException("error invoking properties from annotaion: " + a, e);
		}
	}

	public static boolean isVoidReturn(Object result) {
		return result == VOID_RETURN;
	}

	public static List<Field> getFields(Class<?> clazz, List<Field> sink) {
		for (; clazz != null; clazz = clazz.getSuperclass())
			CH.l(sink, clazz.getDeclaredFields());
		return sink;
	}

	public static List<Field> getFields(Class<?> clazz) {
		return getFields(clazz, new ArrayList<Field>());

	}

	public static List<Method> getMethods(Class<?> clazz) {
		return getMethods(clazz, new ArrayList<Method>());
	}

	private static List<Method> getMethods(Class<?> clazz, ArrayList<Method> sink) {
		for (; clazz != null; clazz = clazz.getSuperclass())
			CH.l(sink, clazz.getDeclaredMethods());
		return sink;
	}

	public static <T> T newInstance(String className, Class<T> returnType) {
		return newInstance(className, OH.EMPTY_OBJECT_ARRAY, returnType);
	}
	public static <T> T newInstance(String className, Object arguments[], Class<T> returnType) {
		if (className == null)
			throw new NullPointerException("className");
		if (returnType == null)
			throw new NullPointerException("returnType");
		Constructor<T> constructor;
		try {
			Class<?> t = Class.forName(className);
			if (!returnType.isAssignableFrom(t))
				throw new ClassCastException(className + " does not implement " + returnType.getName());
			Class<T> r = (Class<T>) t;
			if (arguments.length == 0)
				try {
					return r.newInstance();
				} catch (Exception e) {
					throw OH.toRuntime(e);
				}
			constructor = (Constructor<T>) findConstructor(r, getClasses(arguments));
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Class not found for implementing " + returnType.getName() + ": " + className, e);
		}
		try {
			return constructor.newInstance(arguments);
		} catch (Exception e) {
			throw OH.toRuntime(e);
		}
	}
	public static void getImplementedClassesAndInterfaces(Class clazz, Set<Class<?>> sink) {
		final Class<?> sc = clazz.getSuperclass();
		if (sc != null && sink.add(sc))
			getImplementedClassesAndInterfaces(sc, sink);
		for (final Class<?> c : clazz.getInterfaces())
			if (sink.add(c))
				getImplementedClassesAndInterfaces(c, sink);
	}
}
