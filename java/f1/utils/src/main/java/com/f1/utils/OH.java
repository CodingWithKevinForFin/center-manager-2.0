/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils;

import java.io.File;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.f1.base.BasicTypes;
import com.f1.base.Bytes;
import com.f1.base.Caster;
import com.f1.base.Clock;
import com.f1.base.Complex;
import com.f1.base.DateMillis;
import com.f1.base.DateNanos;
import com.f1.base.IterableAndSize;
import com.f1.base.Lockable;
import com.f1.base.Password;
import com.f1.base.UUID;
import com.f1.base.ValuedEnum;
import com.f1.base.ValuedEnumCache;
import com.f1.utils.casters.Caster_Boolean;
import com.f1.utils.casters.Caster_Byte;
import com.f1.utils.casters.Caster_Character;
import com.f1.utils.casters.Caster_Double;
import com.f1.utils.casters.Caster_Float;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.casters.Caster_Long;
import com.f1.utils.casters.Caster_Object;
import com.f1.utils.casters.Caster_Short;
import com.f1.utils.casters.Caster_Simple;

@SuppressWarnings("unchecked")
/**
 * Object Helper
 */
public class OH {
	public static class Prim {
		public final Class<?> primitiveClass;
		public final Class<?> boxedClass;
		public final Caster<?> primitiveCaster;
		public final Caster<?> boxedCaster;
		public final Object defaultValue;
		public final String defaultValueString;
		public final char code;
		public final byte basicTypePrimitive;
		public final byte basicTypeBoxed;
		public final Class<?>[] canCastFrom;
		public final Set<Class<?>> canCastFromSet;
		public final boolean isWholeNumber;
		public final boolean isDecimal;
		public final int size;

		public Prim(boolean isWholeNumber, boolean isDecimal, Class<?> primitive, Class<?> boxed, Caster<?> primCaster, Caster<?> boxedCaster, Object defaultValue,
				String defaultValueString, char code, byte basicTypePrimitive, byte basicTypeBoxed, Class<?>[] canCastFrom, int size) {
			this.isWholeNumber = isWholeNumber;
			this.isDecimal = isDecimal;
			this.primitiveClass = primitive;
			this.boxedClass = boxed;
			this.primitiveCaster = primCaster;
			this.boxedCaster = boxedCaster;
			this.defaultValue = defaultValue;
			this.defaultValueString = defaultValueString;
			this.code = code;
			this.basicTypeBoxed = basicTypeBoxed;
			this.basicTypePrimitive = basicTypePrimitive;
			this.canCastFrom = canCastFrom;
			this.canCastFromSet = CH.s(canCastFrom);
			this.size = size;
		}

	}

	public static final byte DEFAULT_BYTE = 0;
	public static final short DEFAULT_SHORT = 0;
	public static final int DEFAULT_INT = 0;
	public static final long DEFAULT_LONG = 0L;
	public static final float DEFAULT_FLOAT = 0F;
	public static final double DEFAULT_DOUBLE = 0D;
	public static final char DEFAULT_CHAR = 0;
	public static final boolean DEFAULT_BOOLEAN = false;

	public static final Byte DEFAULT_BYTE_BOXED = DEFAULT_BYTE;
	public static final Short DEFAULT_SHORT_BOXED = DEFAULT_SHORT;
	public static final Integer DEFAULT_INT_BOXED = DEFAULT_INT;
	public static final Long DEFAULT_LONG_BOXED = DEFAULT_LONG;
	public static final Float DEFAULT_FLOAT_BOXED = DEFAULT_FLOAT;
	public static final Double DEFAULT_DOUBLE_BOXED = DEFAULT_DOUBLE;
	public static final Character DEFAULT_CHAR_BOXED = DEFAULT_CHAR;
	public static final Boolean DEFAULT_BOOLEAN_BOXED = DEFAULT_BOOLEAN;

	public static final String DEFAULT_BYTE_STRING = "(byte)0";
	public static final String DEFAULT_SHORT_STRING = "(short)0";
	public static final String DEFAULT_INT_STRING = "0";
	public static final String DEFAULT_LONG_STRING = "0L";
	public static final String DEFAULT_FLOAT_STRING = "0.0F";
	public static final String DEFAULT_DOUBLE_STRING = "0.0D";
	public static final String DEFAULT_CHAR_STRING = "(char)0";
	public static final String DEFAULT_BOOLEAN_STRING = "false";
	public static final String DEFAULT_VOID_STRING = "void";

	private static final Prim[] PRIMS;

	public static final Map[] EMPTY_MAP_ARRAY = new Map[0];
	public static final Set[] EMPTY_SET_ARRAY = new Set[0];
	public static final Collection[] EMPTY_COLLECTION_ARRAY = new Collection[0];
	public static final String[] EMPTY_STRING_ARRAY = new String[0];
	public static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];
	public static final Class<?>[] EMPTY_CLASS_ARRAY = new Class[0];
	public static final boolean[] EMPTY_BOOLEAN_ARRAY = new boolean[0];
	public static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
	public static final char[] EMPTY_CHAR_ARRAY = new char[0];
	public static final short[] EMPTY_SHORT_ARRAY = new short[0];
	public static final int[] EMPTY_INT_ARRAY = new int[0];
	public static final float[] EMPTY_FLOAT_ARRAY = new float[0];
	public static final long[] EMPTY_LONG_ARRAY = new long[0];
	public static final double[] EMPTY_DOUBLE_ARRAY = new double[0];
	public static final File[] EMPTY_FILE_ARRAY = new File[0];
	public static final byte[][] EMPTY_BYTE_ARRAY_ARRAY = new byte[0][];

	public static final Map<Class<?>, Prim> PRIMITIVE_CLASS_TO_PRIM;
	private static final Prim[] PRIMITIVE_BASICTYPE_TO_PRIM = new Prim[255];
	public static final Map<String, Prim> PRIMITIVE_CLASSNAME_TO_PRIM;

	public static final Map<Class<?>, Prim> BOXED_CLASS_TO_PRIM;
	private static final Prim[] BOXED_BASICTYPE_TO_PRIM = new Prim[255];
	public static final Map<String, Prim> BOXED_CLASSNAME_TO_PRIM;

	public static final Map<Class<?>, Prim> CLASS_TO_PRIM;
	private static final Prim[] BASICTYPE_TO_PRIM = new Prim[255];
	public static final Map<String, Prim> CLASSNAME_TO_PRIM;
	private static final Prim[] CODE_TO_PRIM = new Prim[256];
	public static final int OBJECT_REF_SIZE = 4;//TODO: this is wrong on 64 bit!
	private static final Set<Class<?>> JAVA_LANG_CLASSES = new HashSet<Class<?>>();
	private static final Map<String, Class<?>> JAVA_LANG_CLASSES_BY_SIMPLE_NAME = new HashMap<String, Class<?>>();

	static {
		JAVA_LANG_CLASSES.add(Appendable.class);
		JAVA_LANG_CLASSES.add(CharSequence.class);
		JAVA_LANG_CLASSES.add(Cloneable.class);
		JAVA_LANG_CLASSES.add(Comparable.class);
		JAVA_LANG_CLASSES.add(Iterable.class);
		JAVA_LANG_CLASSES.add(Readable.class);
		JAVA_LANG_CLASSES.add(Runnable.class);
		JAVA_LANG_CLASSES.add(Thread.UncaughtExceptionHandler.class);
		JAVA_LANG_CLASSES.add(Boolean.class);
		JAVA_LANG_CLASSES.add(Byte.class);
		JAVA_LANG_CLASSES.add(Character.class);
		JAVA_LANG_CLASSES.add(Class.class);
		JAVA_LANG_CLASSES.add(ClassLoader.class);
		JAVA_LANG_CLASSES.add(Double.class);
		JAVA_LANG_CLASSES.add(Enum.class);
		JAVA_LANG_CLASSES.add(Float.class);
		JAVA_LANG_CLASSES.add(InheritableThreadLocal.class);
		JAVA_LANG_CLASSES.add(Integer.class);
		JAVA_LANG_CLASSES.add(Long.class);
		JAVA_LANG_CLASSES.add(Math.class);
		JAVA_LANG_CLASSES.add(Number.class);
		JAVA_LANG_CLASSES.add(Object.class);
		JAVA_LANG_CLASSES.add(Package.class);
		JAVA_LANG_CLASSES.add(Process.class);
		JAVA_LANG_CLASSES.add(ProcessBuilder.class);
		JAVA_LANG_CLASSES.add(Runtime.class);
		JAVA_LANG_CLASSES.add(RuntimePermission.class);
		JAVA_LANG_CLASSES.add(SecurityManager.class);
		JAVA_LANG_CLASSES.add(Short.class);
		JAVA_LANG_CLASSES.add(StackTraceElement.class);
		JAVA_LANG_CLASSES.add(StrictMath.class);
		JAVA_LANG_CLASSES.add(String.class);
		JAVA_LANG_CLASSES.add(StringBuffer.class);
		JAVA_LANG_CLASSES.add(StringBuilder.class);
		JAVA_LANG_CLASSES.add(System.class);
		JAVA_LANG_CLASSES.add(Thread.class);
		JAVA_LANG_CLASSES.add(ThreadGroup.class);
		JAVA_LANG_CLASSES.add(ThreadLocal.class);
		JAVA_LANG_CLASSES.add(Throwable.class);
		JAVA_LANG_CLASSES.add(Void.class);
		JAVA_LANG_CLASSES.add(ArithmeticException.class);
		JAVA_LANG_CLASSES.add(ArrayIndexOutOfBoundsException.class);
		JAVA_LANG_CLASSES.add(ArrayStoreException.class);
		JAVA_LANG_CLASSES.add(ClassCastException.class);
		JAVA_LANG_CLASSES.add(ClassNotFoundException.class);
		JAVA_LANG_CLASSES.add(CloneNotSupportedException.class);
		JAVA_LANG_CLASSES.add(EnumConstantNotPresentException.class);
		JAVA_LANG_CLASSES.add(Exception.class);
		JAVA_LANG_CLASSES.add(IllegalAccessException.class);
		JAVA_LANG_CLASSES.add(IllegalArgumentException.class);
		JAVA_LANG_CLASSES.add(IllegalMonitorStateException.class);
		JAVA_LANG_CLASSES.add(IllegalStateException.class);
		JAVA_LANG_CLASSES.add(IllegalThreadStateException.class);
		JAVA_LANG_CLASSES.add(IndexOutOfBoundsException.class);
		JAVA_LANG_CLASSES.add(InstantiationException.class);
		JAVA_LANG_CLASSES.add(InterruptedException.class);
		JAVA_LANG_CLASSES.add(NegativeArraySizeException.class);
		JAVA_LANG_CLASSES.add(NoSuchFieldException.class);
		JAVA_LANG_CLASSES.add(NoSuchMethodException.class);
		JAVA_LANG_CLASSES.add(NullPointerException.class);
		JAVA_LANG_CLASSES.add(NumberFormatException.class);
		JAVA_LANG_CLASSES.add(RuntimeException.class);
		JAVA_LANG_CLASSES.add(SecurityException.class);
		JAVA_LANG_CLASSES.add(StringIndexOutOfBoundsException.class);
		JAVA_LANG_CLASSES.add(TypeNotPresentException.class);
		JAVA_LANG_CLASSES.add(UnsupportedOperationException.class);
		JAVA_LANG_CLASSES.add(AbstractMethodError.class);
		JAVA_LANG_CLASSES.add(AssertionError.class);
		JAVA_LANG_CLASSES.add(ClassCircularityError.class);
		JAVA_LANG_CLASSES.add(ClassFormatError.class);
		JAVA_LANG_CLASSES.add(Error.class);
		JAVA_LANG_CLASSES.add(ExceptionInInitializerError.class);
		JAVA_LANG_CLASSES.add(IllegalAccessError.class);
		JAVA_LANG_CLASSES.add(IncompatibleClassChangeError.class);
		JAVA_LANG_CLASSES.add(InstantiationError.class);
		JAVA_LANG_CLASSES.add(InternalError.class);
		JAVA_LANG_CLASSES.add(LinkageError.class);
		JAVA_LANG_CLASSES.add(NoClassDefFoundError.class);
		JAVA_LANG_CLASSES.add(NoSuchFieldError.class);
		JAVA_LANG_CLASSES.add(NoSuchMethodError.class);
		JAVA_LANG_CLASSES.add(OutOfMemoryError.class);
		JAVA_LANG_CLASSES.add(StackOverflowError.class);
		JAVA_LANG_CLASSES.add(ThreadDeath.class);
		JAVA_LANG_CLASSES.add(UnknownError.class);
		JAVA_LANG_CLASSES.add(UnsatisfiedLinkError.class);
		JAVA_LANG_CLASSES.add(UnsupportedClassVersionError.class);
		JAVA_LANG_CLASSES.add(VerifyError.class);
		JAVA_LANG_CLASSES.add(VirtualMachineError.class);
		JAVA_LANG_CLASSES.add(Deprecated.class);
		JAVA_LANG_CLASSES.add(Override.class);
		JAVA_LANG_CLASSES.add(SuppressWarnings.class);
		for (Class<?> i : JAVA_LANG_CLASSES) {
			JAVA_LANG_CLASSES_BY_SIMPLE_NAME.put(i.getSimpleName(), i);
		}

		PRIMS = new Prim[9];
		PRIMS[0] = new Prim(false, false, boolean.class, Boolean.class, Caster_Boolean.PRIMITIVE, Caster_Boolean.INSTANCE, DEFAULT_BOOLEAN_BOXED, DEFAULT_BOOLEAN_STRING, 'Z',
				BasicTypes.PRIMITIVE_BOOLEAN, BasicTypes.BOOLEAN, EMPTY_CLASS_ARRAY, 1);

		PRIMS[1] = new Prim(true, false, byte.class, Byte.class, Caster_Byte.PRIMITIVE, Caster_Byte.INSTANCE, DEFAULT_BYTE_BOXED, DEFAULT_BYTE_STRING, 'B',
				BasicTypes.PRIMITIVE_BYTE, BasicTypes.BYTE, EMPTY_CLASS_ARRAY, 1);

		PRIMS[2] = new Prim(true, false, char.class, Character.class, Caster_Character.PRIMITIVE, Caster_Character.INSTANCE, DEFAULT_CHAR_BOXED, DEFAULT_CHAR_STRING, 'C',
				BasicTypes.PRIMITIVE_CHAR, BasicTypes.CHAR, EMPTY_CLASS_ARRAY, 2);

		PRIMS[3] = new Prim(true, false, short.class, Short.class, Caster_Short.PRIMITIVE, Caster_Short.INSTANCE, DEFAULT_SHORT_BOXED, DEFAULT_SHORT_STRING, 'S',
				BasicTypes.PRIMITIVE_SHORT, BasicTypes.SHORT, new Class[] { Byte.class, Short.class }, 2);

		PRIMS[4] = new Prim(true, false, int.class, Integer.class, Caster_Integer.PRIMITIVE, Caster_Integer.INSTANCE, DEFAULT_INT_BOXED, DEFAULT_INT_STRING, 'I',
				BasicTypes.PRIMITIVE_INT, BasicTypes.INT, new Class[] { Byte.class, Short.class, Character.class }, 4);

		PRIMS[5] = new Prim(false, true, float.class, Float.class, Caster_Float.PRIMITIVE, Caster_Float.INSTANCE, DEFAULT_FLOAT_BOXED, DEFAULT_FLOAT_STRING, 'F',
				BasicTypes.PRIMITIVE_FLOAT, BasicTypes.FLOAT, new Class[] { Byte.class, Short.class, Character.class, Integer.class, Long.class }, 4);

		PRIMS[6] = new Prim(false, true, double.class, Double.class, Caster_Double.PRIMITIVE, Caster_Double.INSTANCE, DEFAULT_DOUBLE_BOXED, DEFAULT_DOUBLE_STRING, 'D',
				BasicTypes.PRIMITIVE_DOUBLE, BasicTypes.DOUBLE, new Class[] { Byte.class, Short.class, Character.class, Integer.class, Long.class, Float.class }, 8);

		PRIMS[7] = new Prim(true, false, long.class, Long.class, Caster_Long.PRIMITIVE, Caster_Long.INSTANCE, DEFAULT_LONG_BOXED, DEFAULT_LONG_STRING, 'J',
				BasicTypes.PRIMITIVE_LONG, BasicTypes.LONG, new Class[] { Byte.class, Short.class, Character.class, Integer.class, Long.class }, 8);
		PRIMS[8] = new Prim(false, false, void.class, Void.class, Caster_Simple.OBJECT, Caster_Simple.OBJECT, null, DEFAULT_VOID_STRING, 'V', BasicTypes.PRIMITIVE_VOID,
				BasicTypes.VOID, new Class[] {}, 0);

		Map<Class<?>, Prim> classToPrim = new HashMap<Class<?>, Prim>();
		Map<String, Prim> classnameToPrim = new HashMap<String, Prim>();
		Map<Class<?>, Prim> boxedClassToPrim = new HashMap<Class<?>, Prim>();
		Map<String, Prim> boxedClassnameToPrim = new HashMap<String, Prim>();
		Map<Class<?>, Prim> primitiveClassToPrim = new HashMap<Class<?>, Prim>();
		Map<String, Prim> primitiveClassnameToPrim = new HashMap<String, Prim>();

		for (Prim p : PRIMS) {
			classToPrim.put(p.boxedClass, p);
			classToPrim.put(p.primitiveClass, p);
			classnameToPrim.put(p.boxedClass.getName(), p);
			classnameToPrim.put(p.primitiveClass.getName(), p);
			BASICTYPE_TO_PRIM[p.basicTypeBoxed] = p;
			BASICTYPE_TO_PRIM[p.basicTypePrimitive] = p;
			CODE_TO_PRIM[p.code] = p;
			boxedClassToPrim.put(p.boxedClass, p);
			primitiveClassToPrim.put(p.primitiveClass, p);
			boxedClassnameToPrim.put(p.boxedClass.getName(), p);
			primitiveClassnameToPrim.put(p.primitiveClass.getName(), p);
			BOXED_BASICTYPE_TO_PRIM[p.basicTypeBoxed] = p;
			PRIMITIVE_BASICTYPE_TO_PRIM[p.basicTypePrimitive] = p;
		}
		CLASS_TO_PRIM = Collections.unmodifiableMap(classToPrim);
		CLASSNAME_TO_PRIM = Collections.unmodifiableMap(classnameToPrim);
		PRIMITIVE_CLASS_TO_PRIM = Collections.unmodifiableMap(primitiveClassToPrim);
		PRIMITIVE_CLASSNAME_TO_PRIM = Collections.unmodifiableMap(primitiveClassnameToPrim);
		BOXED_CLASS_TO_PRIM = Collections.unmodifiableMap(boxedClassToPrim);
		BOXED_CLASSNAME_TO_PRIM = Collections.unmodifiableMap(boxedClassnameToPrim);

	}

	/**
	 * 
	 * Given a class code, returns the corresponding primitive type.
	 * 
	 * @param code
	 *            Input class code
	 * @return Corresponding primitive type. If corresponding type is null, then return null.
	 */

	static public Class<?> getTypeForClassCode(char code) {
		Prim prim = CODE_TO_PRIM[code];
		return prim == null ? null : prim.primitiveClass;
	}
	static public Caster<?> getCasterForClassCode(char code) {
		Prim prim = CODE_TO_PRIM[code];
		return prim == null ? Caster_Simple.OBJECT : prim.primitiveCaster;
	}

	/**
	 * 
	 * Given a class, return the corresponding JVM code.
	 * 
	 * @param clazz
	 *            Input class
	 * @return Corresponding JVM code. If corresponding primitive class is null, return 'L'.
	 */

	static public char getClassJvmCode(Class<?> clazz) {
		Prim prim = CLASS_TO_PRIM.get(clazz);
		if (prim == null)
			return 'L';
		else
			return prim.code;
	}

	/**
	 * 
	 * Given a primited class, returns corresponding primitive type. If the given class is not a primited type, throws a ClassCastException.
	 * 
	 * @param clazz
	 *            Input class
	 * @return Primitive type corresponding to clazz
	 */

	static public byte getBasicTypeForPrimitive(Class<?> clazz) {
		Prim prim = CLASS_TO_PRIM.get(clazz);
		if (prim == null)
			throw new ClassCastException("not a primited: " + clazz);
		return clazz.isPrimitive() ? prim.basicTypePrimitive : prim.basicTypeBoxed;
	}

	/**
	 * 
	 * Compares two objects, left and right. Returns true if left <= right. Otherwise, throws AssersionException.
	 * 
	 * @param left
	 * @param right
	 * @return true if left <= right.
	 * @throws AssertionException
	 */

	public static <T> Comparable<T> assertLe(Comparable<T> left, Comparable<T> right) throws AssertionException {
		if (compare(left, right) <= 0)
			return left;
		throw new AssertionException(" not true: " + left + " <= " + right);
	}
	public static <T> void assertEmpty(T[] t) {
		if (t != null && t.length != 0)
			throw new AssertionException(" not empty: " + t);
	}
	public static void assertEmpty(Collection<?> t) {
		if (t != null && !t.isEmpty())
			throw new AssertionException(" not empty: " + t);
	}
	public static void assertEmpty(Map<?, ?> t) {
		if (t != null && !t.isEmpty())
			throw new AssertionException(" not empty: " + t);
	}

	/**
	 * 
	 * Compares two objects, left and right. Returns true if left < right. Otherwise, throws AssersionException.
	 * 
	 * @param left
	 * @param right
	 * @return true if left < right.
	 * @throws AssertionException
	 */
	public static <T> Comparable<T> assertLt(Comparable<T> left, Comparable<T> right) throws AssertionException {
		if (compare(left, right) < 0)
			return left;
		throw new AssertionException(" not true: " + left + " < " + right);
	}

	/**
	 * 
	 * Compares two objects, left and right. Returns true if left >= right. Otherwise, throws AssersionException.
	 * 
	 * @param left
	 * @param right
	 * @return true if left >= right.
	 * @throws AssertionException
	 */
	public static <T> Comparable<T> assertGe(Comparable<T> left, Comparable<T> right) throws AssertionException {
		if (compare(left, right) >= 0)
			return left;
		throw new AssertionException(" not true: " + left + " >= " + right);
	}

	/**
	 * 
	 * Compares two objects, left and right. Returns true if left > right. Otherwise, throws AssersionException.
	 * 
	 * @param left
	 * @param right
	 * @return true if left > right.
	 * @throws AssertionException
	 */
	public static <T> Comparable<T> assertGt(Comparable<T> left, Comparable<T> right) throws AssertionException {
		if (compare(left, right) > 0)
			return left;
		throw new AssertionException(" not true: " + left + " > " + right);
	}

	/**
	 * 
	 * Compares two objects, left and right. Returns true if left <= right. Otherwise, throws AssersionException. <BR>
	 * User can input error message.
	 * 
	 * @param left
	 * @param right
	 * @param message
	 *            User-specified error message
	 * @return true if left <= right.
	 * @throws AssertionException
	 */
	public static <T> Comparable<T> assertLe(Comparable<T> left, Comparable<T> right, String message) throws AssertionException {
		if (compare(left, right) <= 0)
			return left;
		throw new AssertionException(message + " not true: " + left + " <= " + right);
	}
	/**
	 * 
	 * Compares two objects, left and right. Returns true if left < right. Otherwise, throws AssersionException. <BR>
	 * User can input error message.
	 * 
	 * @param left
	 * @param right
	 * @param message
	 *            User-specified error message
	 * @return true if left < right.
	 * @throws AssertionException
	 */

	public static <T> Comparable<T> assertLt(Comparable<T> left, Comparable<T> right, String message) throws AssertionException {
		if (compare(left, right) < 0)
			return left;
		throw new AssertionException(message + " not true: " + left + " < " + right);
	}
	/**
	 * 
	 * Compares two objects, left and right. Returns true if left >= right. Otherwise, throws AssersionException. <BR>
	 * User can input error message.
	 * 
	 * @param left
	 * @param right
	 * @param message
	 *            User-specified error message
	 * @return true if left >= right.
	 * @throws AssertionException
	 */

	public static <T> Comparable<T> assertGe(Comparable<T> left, Comparable<T> right, String message) throws AssertionException {
		if (compare(left, right) >= 0)
			return left;
		throw new AssertionException(message + " not true: " + left + " >= " + right);
	}
	/**
	 * 
	 * Compares two objects, left and right. Returns true if left > right. Otherwise, throws AssersionException. <BR>
	 * User can input error message.
	 * 
	 * @param left
	 * @param right
	 * @param message
	 *            User-specified error message
	 * @return true if left > right.
	 * @throws AssertionException
	 */

	public static <T> Comparable<T> assertGt(Comparable<T> left, Comparable<T> right, String message) throws AssertionException {
		if (compare(left, right) > 0)
			return left;
		throw new AssertionException(message + " not true: " + left + " > " + right);
	}

	/**
	 * returns true if objects left and right are equal (either by comparing references via == or by calling {@link Object#equals(Object)}<BR>
	 * Note, two null references are considered to be equal.
	 * 
	 * @param left
	 *            object to be compared for equality to right
	 * @param right
	 *            object to be compared for equality to left
	 * @return true if left==right or left.equals(right)
	 */
	public static boolean eq(Object left, Object right) {
		return left == right || (left != null && right != null && left.equals(right));
	}

	/**
	 * 
	 * Returns true if objects left and right have equal hash codes (either by comparing references via == or by calling {@link Object#equals(Object)}<BR>
	 * Note, two null references are considered to be equal.
	 * 
	 * @param left
	 * @param right
	 * @return true if left == right OR (left.hashCode() == right.hashCode() && left.equals(right))
	 */

	public static boolean eqUseHashCode(Object left, Object right) {
		return left == right || (left != null && right != null && left.hashCode() == right.hashCode() && left.equals(right));
	}

	/**
	 * 
	 * Compares two double values, left and right. Returns true if left == right.
	 * 
	 * @param left
	 * @param right
	 * @return true if left == right
	 */

	public static boolean eq(double left, double right) {
		return left == right;
	}

	/**
	 * 
	 * Compares two float values, left and right. Returns true if left == right.
	 * 
	 * @param left
	 * @param right
	 * @return true if left == right
	 */

	public static boolean eq(float left, float right) {
		return left == right;
	}

	/**
	 * 
	 * Compares two int values, left and right. Returns true if left == right.
	 * 
	 * @param left
	 * @param right
	 * @return true if left == right
	 */

	public static boolean eq(int left, int right) {
		return left == right;
	}

	/**
	 * 
	 * Compares two long values, left and right. Returns true if left == right.
	 * 
	 * @param left
	 * @param right
	 * @return true if left == right
	 */

	public static boolean eq(long left, long right) {
		return left == right;
	}

	/**
	 * 
	 * Compares two short values, left and right. Returns true if left == right.
	 * 
	 * @param left
	 * @param right
	 * @return true if left == right
	 */

	public static boolean eq(short left, short right) {
		return left == right;
	}

	/**
	 * 
	 * Compares two byte values, left and right. Returns true if left == right.
	 * 
	 * @param left
	 * @param right
	 * @return true if left == right
	 */

	public static boolean eq(byte left, byte right) {
		return left == right;
	}

	/**
	 * 
	 * Compares two char values, left and right. Returns true if left == right.
	 * 
	 * @param left
	 * @param right
	 * @return true if left == right
	 */

	public static boolean eq(char left, char right) {
		return left == right;
	}

	/**
	 * 
	 * Compares two boolean values, left and right. Returns true if left == right.
	 * 
	 * @param left
	 * @param right
	 * @return true if left == right
	 */

	public static boolean eq(boolean left, boolean right) {
		return left == right;
	}

	/**
	 * 
	 * Compares two double values, left and right. Returns true if left != right.
	 * 
	 * @param left
	 * @param right
	 * @return true if left == right
	 */

	public static boolean ne(double left, double right) {
		return left != right;
	}

	/**
	 * 
	 * Compares two float values, left and right. Returns true if left != right.
	 * 
	 * @param left
	 * @param right
	 * @return true if left == right
	 */

	public static boolean ne(float left, float right) {
		return left != right;
	}

	/**
	 * 
	 * Compares two int values, left and right. Returns true if left != right.
	 * 
	 * @param left
	 * @param right
	 * @return true if left == right
	 */

	public static boolean ne(int left, int right) {
		return left != right;
	}

	/**
	 * 
	 * Compares two long values, left and right. Returns true if left != right.
	 * 
	 * @param left
	 * @param right
	 * @return true if left == right
	 */

	public static boolean ne(long left, long right) {
		return left != right;
	}

	/**
	 * 
	 * Compares two short values, left and right. Returns true if left != right.
	 * 
	 * @param left
	 * @param right
	 * @return true if left == right
	 */

	public static boolean ne(short left, short right) {
		return left != right;
	}

	/**
	 * 
	 * Compares two byte values, left and right. Returns true if left != right.
	 * 
	 * @param left
	 * @param right
	 * @return true if left == right
	 */

	public static boolean ne(byte left, byte right) {
		return left != right;
	}

	/**
	 * 
	 * Compares two char values, left and right. Returns true if left != right.
	 * 
	 * @param left
	 * @param right
	 * @return true if left == right
	 */

	public static boolean ne(char left, char right) {
		return left != right;
	}

	/**
	 * 
	 * Compares two boolean values, left and right. Returns true if left != right.
	 * 
	 * @param left
	 * @param right
	 * @return true if left == right
	 */

	public static boolean ne(boolean left, boolean right) {
		return left != right;
	}

	/**
	 * 
	 * Compares two objects, left and right. Returns true if left > right. Two null values are considered equal. <BR>
	 * If nullsLower is set to true, null input values are considered less than all other values.
	 * 
	 * @param left
	 * @param right
	 * @param nullsLower
	 * @return true if left > right
	 */

	public static <T> boolean gt(Comparable<T> left, Comparable<T> right, boolean nullsLower) {
		return OH.compare(left, right, nullsLower) > 0;
	}

	/**
	 * 
	 * Compares two objects, left and right. Returns true if left >= right. Two null values are considered equal. <BR>
	 * If nullsLower is set to true, null input values are considered less than all other values.
	 * 
	 * @param left
	 * @param right
	 * @param nullsLower
	 * @return true if left >= right
	 */

	public static <T> boolean ge(Comparable<T> left, Comparable<T> right, boolean nullsLower) {
		return left == right || OH.compare(left, right, nullsLower) >= 0;
	}

	/**
	 * 
	 * Compares two objects, left and right. Returns true if left < right. Two null values are considered equal. <BR>
	 * If nullsLower is set to true, null input values are considered less than all other values.
	 * 
	 * @param left
	 * @param right
	 * @param nullsLower
	 * @return true if left < right
	 */

	public static <T> boolean lt(Comparable<T> left, Comparable<T> right, boolean nullsLower) {
		return OH.compare(left, right, nullsLower) < 0;
	}

	/**
	 * 
	 * Compares two objects, left and right. Returns true if left >= right. Two null values are considered equal. <BR>
	 * If nullsLower is set to true, null input values are considered less than all other values.
	 * 
	 * @param left
	 * @param right
	 * @param nullsLower
	 * @return true if left <= right
	 */

	public static <T> boolean le(Comparable<T> left, Comparable<T> right, boolean nullsLower) {
		return left == right || OH.compare(left, right, nullsLower) <= 0;
	}

	/**
	 * 
	 * Compares two objects, left and right. Returns true if left > right. If either of input values are null, throws NullPointerException
	 * 
	 * @param left
	 * @param right
	 * @return true if left > right
	 */

	public static <T extends Comparable<T>> boolean gt(T left, T right) {
		if (left == null)
			throw new NullPointerException("left");
		if (right == null)
			throw new NullPointerException("right");
		return left.compareTo(right) > 0;
	}

	/**
	 * 
	 * Compares two objects, left and right. Returns true if left >= right. If either of input values are null, throws NullPointerException
	 * 
	 * @param left
	 * @param right
	 * @return true if left >= right
	 */

	public static <T extends Comparable<T>> boolean ge(T left, T right) {
		if (left == null)
			throw new NullPointerException("left");
		if (right == null)
			throw new NullPointerException("right");
		return left == right || left.compareTo(right) >= 0;
	}

	/**
	 * 
	 * Compares two objects, left and right. Returns true if left < right. If either of input values are null, throws NullPointerException
	 * 
	 * @param left
	 * @param right
	 * @return true if left < right
	 */

	public static <T extends Comparable<T>> boolean lt(T left, T right) {
		if (left == null)
			throw new NullPointerException("left");
		if (right == null)
			throw new NullPointerException("right");
		return left.compareTo(right) < 0;
	}

	/**
	 * 
	 * Compares two objects, left and right. Returns true if left <= right. If either of input values are null, throws NullPointerException
	 * 
	 * @param left
	 * @param right
	 * @return true if left <= right
	 */

	public static <T extends Comparable<T>> boolean le(T left, T right) {
		if (left == null)
			throw new NullPointerException("left");
		if (right == null)
			throw new NullPointerException("right");
		return left == right || left.compareTo(right) <= 0;
	}

	/**
	 * 
	 * Compares two objects, left and right. Returns the smaller of the two objects.
	 * 
	 * @param left
	 * @param right
	 * @return left if left <= right. Otherwise, return right.
	 */

	public static <T extends Comparable<T>> T min(T left, T right) {
		return le(left, right) ? left : right;
	}

	/**
	 * 
	 * Compares two objects, left and right. Returns the greater of the two objects.
	 * 
	 * @param left
	 * @param right
	 * @return left if left >= right. Otherwise, return right.
	 */

	public static <T extends Comparable<T>> T max(T left, T right) {
		return ge(left, right) ? left : right;
	}

	/**
	 * returns true if objects left and right are not equal (either by comparing references via == or by calling {@link Object#equals(Object)} Note, two null references are
	 * considered to be equal.
	 * 
	 * @param left
	 *            object to be compared for inequality to right
	 * @param right
	 *            object to be compared for inequality to left
	 * @return true if left!=right or !left.equals(right)
	 */
	public static boolean ne(Object left, Object right) {
		return left != right && (left == null || right == null || !left.equals(right));
	}

	/**
	 * 
	 * Returns a class object associated with the specified class name.
	 * 
	 * @param className
	 *            Specified class name
	 * @return If className corresponds to a defined class, return a class object of that class. Otherwise, throw ClassNotFoundException.
	 * @throws ClassNotFoundException
	 */

	public static Class<?> forName(String className) throws ClassNotFoundException {
		try {
			if (className.indexOf('.') != -1)
				return Class.forName(className);
			Prim r = PRIMITIVE_CLASSNAME_TO_PRIM.get(className);
			if (r != null)
				return r.primitiveClass;
			Class<?> r2 = JAVA_LANG_CLASSES_BY_SIMPLE_NAME.get(className);
			if (r2 != null)
				return r2;
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw e;
		} catch (Exception ex) {
			throw new ClassNotFoundException(className, ex);
		}
	}
	public static Class<?> forNameNoThrow(String className) {
		try {
			if (className.indexOf('.') != -1)
				return Class.forName(className);
			Prim r = PRIMITIVE_CLASSNAME_TO_PRIM.get(className);
			if (r != null)
				return r.primitiveClass;
			Class<?> r2 = JAVA_LANG_CLASSES_BY_SIMPLE_NAME.get(className);
			if (r2 != null)
				return r2;
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			return null;
		}
	}
	public static Caster<?> forNameCaster(String className) throws ClassNotFoundException {
		try {
			if (className.indexOf('.') != -1)
				return OH.getCaster(Class.forName(className));
			Prim r = PRIMITIVE_CLASSNAME_TO_PRIM.get(className);
			if (r != null)
				return r.primitiveCaster;
			Class<?> r2 = JAVA_LANG_CLASSES_BY_SIMPLE_NAME.get(className);
			if (r2 != null)
				return OH.getCaster(r2);
			return OH.getCaster(Class.forName(className));
		} catch (ClassNotFoundException e) {
			throw e;
		} catch (Exception ex) {
			throw new ClassNotFoundException(className, ex);
		}
	}

	/**
	 * 
	 * Compares two Comparable objects, l and r. By default, null input objects are considered to be less than all other inputs.
	 * 
	 * @param l
	 * @param r
	 * @return If l > r, return 1. <BR>
	 *         If l < r, return -1. <BR>
	 *         If l == r, return 0.
	 */

	public static <T extends Comparable<?>> int compare(T l, T r) {
		return compare(l, r, true);

	}

	/**
	 * 
	 * Compare two ints, l and r.
	 * 
	 * @param l
	 * @param r
	 * @return If l > r, return 1. <BR>
	 *         If l < r, return -1. <BR>
	 *         If l == r, return 0
	 */

	public static int compare(int l, int r) {
		return l == r ? 0 : l > r ? 1 : -1;

	}

	/**
	 * 
	 * Compare two longs, l and r.
	 * 
	 * @param l
	 * @param r
	 * @return If l > r, return 1. <BR>
	 *         If l < r, return -1. <BR>
	 *         If l == r, return 0
	 */

	public static int compare(long l, long r) {
		return l == r ? 0 : l > r ? 1 : -1;
	}

	/**
	 * 
	 * Compare two doubles, l and r.
	 * 
	 * @param l
	 * @param r
	 * @return If l > r, return 1. <BR>
	 *         If l < r, return -1. <BR>
	 *         If l == r, return 0
	 */

	public static int compare(double l, double r) {
		return l == r ? 0 : l > r ? 1 : -1;
	}

	/**
	 * 
	 * Compare two floats, l and r.
	 * 
	 * @param l
	 * @param r
	 * @return If l > r, return 1. <BR>
	 *         If l < r, return -1. <BR>
	 *         If l == r, return 0
	 */

	public static int compare(float l, float r) {
		return l == r ? 0 : l > r ? 1 : -1;
	}

	/**
	 * 
	 * Compare two shorts, l and r.
	 * 
	 * @param l
	 * @param r
	 * @return If l > r, return 1. <BR>
	 *         If l < r, return -1. <BR>
	 *         If l == r, return 0
	 */

	public static int compare(short l, short r) {
		return l == r ? 0 : l > r ? 1 : -1;
	}

	/**
	 * 
	 * Compare two bytes, l and r.
	 * 
	 * @param l
	 * @param r
	 * @return If l > r, return 1. <BR>
	 *         If l < r, return -1. <BR>
	 *         If l == r, return 0
	 */

	public static int compare(byte l, byte r) {
		return l == r ? 0 : l > r ? 1 : -1;
	}

	/**
	 * 
	 * Compare two chars, l and r.
	 * 
	 * @param l
	 * @param r
	 * @return If l > r, return 1. <BR>
	 *         If l < r, return -1. <BR>
	 *         If l == r, return 0
	 */

	public static int compare(char l, char r) {
		return l == r ? 0 : l > r ? 1 : -1;
	}

	/**
	 * 
	 * Compare two booleans, l and r.
	 * 
	 * @param l
	 * @param r
	 * @return If l > r, return 1. <BR>
	 *         If l < r, return -1. <BR>
	 *         If l == r, return 0
	 */

	public static int compare(boolean l, boolean r) {
		return l == r ? 0 : l ? 1 : -1;
	}

	/**
	 * 
	 * Compares two Comparable objects, l and r. <BR>
	 * 
	 * @param l
	 * @param r
	 * @param nullsLower
	 *            When set to true, null input values are seen as less than all other input values. Otherwise, null input values are seen as greater than all other input values.
	 * @return If l > r, return 1. <BR>
	 *         If l < r, return -1. <BR>
	 *         If l == r, return 0.
	 */

	public static <T extends Comparable> int compare(T l, T r, boolean nullsLower) {
		if (l == r)
			return 0;
		else if (l == null)
			return nullsLower ? -1 : 1;
		else
			return r == null ? (nullsLower ? 1 : -1) : l.compareTo(r);
	}

	/**
	 * 
	 * Compares two Comparable objects, l and r, with respect to the specified Comparator object.
	 * 
	 * @param l
	 * @param r
	 * @param nullsLower
	 *            When set to true, null input values are seen as less than all other input values. Otherwise, null input values are seen as greater than all other input values.
	 * @param c
	 *            Specified Comparator object
	 * @return If l > r, return 1. <BR>
	 *         If l < r, return -1. <BR>
	 *         If l == r, return 0.
	 */

	public static <T> int compare(T l, T r, boolean nullsLower, Comparator<T> c) {
		if (l == r)
			return 0;
		else if (l == null)
			return nullsLower ? -1 : 1;
		else
			return r == null ? (nullsLower ? 1 : -1) : c.compare(l, r);
	}

	/**
	 * similar to {@link Class#isAssignableFrom(Class)} such that left.isAssignable(right). The only difference is in the handling of primitives. For example
	 * <I>isAssignable(Number.class,int.class)</I> will return true.
	 * 
	 * @param left
	 *            to test for super class / interface
	 * @param right
	 *            to test for sub class / interface
	 * @return true if left is super or same as right
	 */
	public static boolean isAssignableFrom(Class<?> left, Class<?> right) {
		return left == right || getBoxed(left).isAssignableFrom(getBoxed(right));
	}

	/**
	 * returns true if a value of type after can be coerced into a value of type before per the java specification. Auto-boxing and unboxing is taken into account.<BR>
	 * For example: <BR>
	 * 1. An int can be coerced into an int,long,Integer,Long,Object,Number but not a byte,char, short, etc...<BR>
	 * 2. Anything can be coerced to an Object so isCoercable(Object.class,Anthing) will return true 2. An object can only be coerced to an Object so
	 * isCoercable(AnythingBesidesObject,Object.class) will return false
	 * 
	 * @param after
	 *            type to coerce from
	 * @param before
	 *            type to coerce to
	 * @return true if instances of type after can be coerced into type before.
	 */
	public static boolean isCoercable(Class<?> after, Class<?> before) {
		if (after == before)
			return true;
		final Prim afterPrim = CLASS_TO_PRIM.get(after);
		if (afterPrim == null)
			return after.isAssignableFrom(getBoxed(before));
		final Prim rightPrim = CLASS_TO_PRIM.get(before);
		if (rightPrim == null)
			return false;
		return afterPrim == rightPrim || afterPrim.canCastFromSet.contains(rightPrim.boxedClass) || afterPrim.boxedClass.isAssignableFrom(rightPrim.boxedClass);
	}

	/**
	 * returns true iff this class is not primitive (see {@link Class#isPrimitive()} but is boxed and hence has a primitive counter part
	 * 
	 * @param p
	 * @return
	 */
	public static boolean isBoxed(Class<?> p) {
		return BOXED_CLASS_TO_PRIM.containsKey(p);
	}

	/**
	 * returns the boxed representation of a class. If the clazz is null, or not a primitive then the clazz is simply returned
	 * 
	 * @param clazz
	 *            the clazz to get the boxed representation.
	 * @return a clazz guaranteed to not be primitive(see {@link Class#isPrimitive()}) (null if null supplied as the clazz)
	 */
	public static Class<?> getBoxed(Class<?> clazz) {
		if (clazz == null || !clazz.isPrimitive())
			return clazz;
		return PRIMITIVE_CLASS_TO_PRIM.get(clazz).boxedClass;
	}

	/**
	 * 
	 * Given a boxed class, returns the corresponding primitive class.
	 * 
	 * @param clazz
	 *            Input class
	 * @return Primitive class
	 */

	public static Class<?> getPrimitive(Class<?> clazz) {
		return BOXED_CLASS_TO_PRIM.get(clazz).primitiveClass;
	}

	/**
	 * the f1 framework defines certain class types as 'basic' types each of which has a well-known and unique one byte code. See {@link BasicTypes} for details. This method
	 * converts a class type to it's 'basic' code. Note, classes without a related basic type will result in {@link BasicTypes#UNDEFINED}. passing in a null class will result in
	 * {@link BasicTypes#NULL}
	 * 
	 * @param type
	 *            class type to find the basic type code representation for
	 * @return basic code (see {@link BasicTypes})
	 */
	public static byte getBasicType(Class<?> type) {
		if (type == null)
			return BasicTypes.NULL;
		Prim r = CLASS_TO_PRIM.get(type);
		if (r != null)
			return r.basicTypeBoxed;
		if (type == String.class)
			return BasicTypes.STRING;
		if (Map.class.isAssignableFrom(type))
			return BasicTypes.MAP;
		if (List.class.isAssignableFrom(type))
			return BasicTypes.LIST;
		if (Set.class.isAssignableFrom(type))
			return BasicTypes.SET;
		if (FixPoint.class.isAssignableFrom(type))
			return BasicTypes.FIXPOINT;
		if (BigDecimal.class.isAssignableFrom(type))
			return BasicTypes.BIGDECIMAL;
		if (BigInteger.class.isAssignableFrom(type))
			return BasicTypes.BIGINTEGER;
		if (ValuedEnum.class.isAssignableFrom(type))
			return BasicTypes.VALUED_ENUM;
		if (Date.class.isAssignableFrom(type))
			return BasicTypes.DATE;
		if (Date.class.isAssignableFrom(type))
			return BasicTypes.DATE;
		if (DateMillis.class.isAssignableFrom(type))
			return BasicTypes.DATE_MILLIS;
		if (Bytes.class.isAssignableFrom(type))
			return BasicTypes.BYTES;
		if (UUID.class.isAssignableFrom(type))
			return BasicTypes.UUID;
		if (Complex.class.isAssignableFrom(type))
			return BasicTypes.COMPLEX;
		if (DateNanos.class.isAssignableFrom(type))
			return BasicTypes.DATE_NANOS;
		if (Password.class.isAssignableFrom(type))
			return BasicTypes.PASSWORD;
		return BasicTypes.UNDEFINED;
	}

	/**
	 * 
	 * See {@link #cast(Object, Class, boolean)}, but catches RuntimeException. User can input exception message.
	 * 
	 * @param o
	 * @param dstClass
	 * @param required
	 * @param description
	 *            User-specified message displayed when exception is thrown.
	 */

	public static <C> C cast(Object o, Class<C> dstClass, boolean required, String description) {
		try {
			return cast(o, dstClass);
		} catch (Exception e) {
			throw new RuntimeException(description + " is not a " + dstClass.getName(), e);
		}
	}

	/**
	 * See {@link #cast(Object, Class, boolean)} but with required set to false meaning that if o is null, then null will simply be returned
	 */
	public static <C> C cast(Object o, Class<C> c) {
		return cast(o, c, false, true);
	}

	/**
	 * 
	 * See {@link #cast(Object, Class, boolean, boolean)}, but with required and throwExceptionOnError both set to false.
	 * 
	 */

	public static <C> C castNoThrow(Object o, Class<C> c) {
		return cast(o, c, false, false);
	}

	/**
	 * Tries to intelligently cast an object to another type. This powerful method handles (at a minimum) all primitives,boxed types, enums and strings. It has specific times it
	 * should be used, and specific times it shouldn't be used!
	 * <P>
	 * <B>When to use:</B><BR>
	 * One common case is if you don't know (at code writing time) what type of value you need to cast from or to. <BR>
	 * Another common use case for this method is to defer the casting of options and values into some helper function or class
	 * <P>
	 * 
	 * <B>When not to use:</B><BR>
	 * Due to the runtime reflective nature and heavy use of instanceof this method should be avoided within performance related code. <BR>
	 * It also, by definition, does not have type safety so anywhere you need strick checking this should be avoided as well.
	 * <P>
	 * If the required option is true, then o may not be null, otherwise a {@link NullPointerException} is thrown
	 * 
	 * @param <C>
	 *            type of class to cast to
	 * @param o
	 *            the object to cast to dstClass
	 * @param dstClass
	 *            type of class to cast to
	 * @param required
	 *            if false, o may be null
	 * @return the value as cast to class. (will be null iff required is false and o is null)
	 */
	public static <C> C cast(Object o, Class<C> dstClass, boolean required) {
		return cast(o, dstClass, required, true);
	}

	public static <C> Caster<C> getCaster(Class<C> dstClass) {
		return CasterManager.getCaster(dstClass);
	}
	public static <C> C cast2(Object o, Class<C> dstClass, boolean required, boolean throwExceptionOnError) {
		return getCaster(dstClass).cast(o, required, throwExceptionOnError);
	}
	public static <C> C cast2(Object o, Class<C> dstClass) {
		return getCaster(dstClass).cast(o);
	}

	/**
	 * 
	 * Same as {@link #cast(Object, Class, boolean)}, but with the option to specify whether or not to throw an exception upon encountering an error.
	 * 
	 * @param o
	 * @param dstClass
	 * @param required
	 * @param throwExceptionOnError
	 *            If true, throw an exception when an error occurs.
	 */

	public static <C> C cast(Object o, Class<C> dstClass, boolean required, boolean throwExceptionOnError) {
		if (o == null) {
			if (!required || !throwExceptionOnError)
				return null;
			throw new NullPointerException();
		}
		Class<?> srcClass = o.getClass();
		try {
			if (dstClass == srcClass || dstClass.isAssignableFrom(srcClass)) // 1
				return (C) o;
			else if (dstClass == String.class)
				return (C) o.toString();
			else if (Number.class.isAssignableFrom(dstClass)) { // 2
				if (o instanceof Number) { // 3
					if (dstClass == Number.class) // 4
						return (C) o;
					if (dstClass == Integer.class)
						return (C) (Object) ((Number) o).intValue();
					if (dstClass == Long.class)
						return (C) (Object) ((Number) o).longValue();
					if (dstClass == Double.class)
						return (C) (Object) ((Number) o).doubleValue();
					if (dstClass == Float.class)
						return (C) (Object) ((Number) o).floatValue();
					if (dstClass == Byte.class)
						return (C) (Object) ((Number) o).byteValue();
					if (dstClass == Short.class)
						return (C) (Object) ((Number) o).shortValue();
					if (dstClass == FixPoint.class || dstClass == BasicFixPoint.class) {
						if (o instanceof Double || o instanceof Float)
							return (C) BasicFixPoint.nuw(4, ((Number) o).doubleValue());
						else
							return (C) BasicFixPoint.nuw(4, ((Number) o).longValue());
					}
					if (dstClass == BigDecimal.class)
						return (C) new BigDecimal(o.toString());
					if (dstClass == BigInteger.class) {
						return (C) new BigInteger(SH.beforeFirst(o.toString(), "."));
					}
					if (dstClass == Character.class)
						return (C) (Character) (char) ((Number) o).intValue();
					if (dstClass == DateMillis.class)
						return (C) new DateMillis(((Number) o).longValue());
				} else if (srcClass == String.class) {
					String s = (String) o;
					if ("null".equals(o))
						return null;
					if (dstClass == Integer.class)
						return (C) valueOf(SH.parseIntSafe(s, throwExceptionOnError, false));
					if (dstClass == Long.class)
						return (C) valueOf(SH.parseLongSafe(s, throwExceptionOnError));
					if (dstClass == Double.class)
						return (C) valueOf(SH.parseDoubleSafe(s, throwExceptionOnError));
					if (dstClass == Float.class)
						return (C) valueOf(SH.parseFloatSafe(s, throwExceptionOnError));
					if (dstClass == Byte.class)
						return (C) valueOf(SH.parseByteSafe(s, throwExceptionOnError, false));
					if (dstClass == Short.class)
						return (C) valueOf(SH.parseShortSafe(s, throwExceptionOnError, false));
					if (dstClass == FixPoint.class || dstClass == BasicFixPoint.class)
						return (C) BasicFixPoint.nuw(s);
					if (dstClass == BigDecimal.class)
						return (C) new BigDecimal(s);
					if (dstClass == BigInteger.class)
						return (C) new BigInteger(s);
				} else if (CharSequence.class.isAssignableFrom(srcClass)) {
					if (SH.equals("null", (CharSequence) o))
						return null;
					if (dstClass == Integer.class)
						return (C) valueOf(SH.parseIntSafe((CharSequence) o, throwExceptionOnError, false));
					if (dstClass == Long.class)
						return (C) valueOf(SH.parseLongSafe((CharSequence) o, throwExceptionOnError));
					if (dstClass == Double.class)
						return (C) valueOf(SH.parseDoubleSafe((CharSequence) o, throwExceptionOnError));
					if (dstClass == Float.class)
						return (C) valueOf(SH.parseFloatSafe((CharSequence) o, throwExceptionOnError));
					if (dstClass == Byte.class)
						return (C) valueOf(SH.parseByteSafe((CharSequence) o, throwExceptionOnError, false));//TODO:
					if (dstClass == Short.class)
						return (C) valueOf(SH.parseShortSafe((CharSequence) o, throwExceptionOnError, false));//TODO:
					if (dstClass == FixPoint.class || dstClass == BasicFixPoint.class)
						return (C) BasicFixPoint.nuw(o.toString());
					if (dstClass == BigDecimal.class)
						return (C) new BigDecimal(o.toString());
					if (dstClass == BigInteger.class)
						return (C) new BigInteger(o.toString());
				} else if (srcClass.isEnum()) {
					if (o instanceof ValuedEnum)
						return cast(((ValuedEnum<?>) o).getEnumValue(), dstClass);
					return cast(((Enum<?>) o).toString(), dstClass);
				}
			} else if (dstClass.isPrimitive()) {
				if (o instanceof Number) {
					if ("null".equals(o))
						return null;
					if (dstClass == int.class)
						return (C) (Object) ((Number) o).intValue();
					if (dstClass == long.class)
						return (C) (Object) ((Number) o).longValue();
					if (dstClass == double.class)
						return (C) (Object) ((Number) o).doubleValue();
					if (dstClass == float.class)
						return (C) (Object) ((Number) o).floatValue();
					if (dstClass == byte.class)
						return (C) (Object) ((Number) o).byteValue();
					if (dstClass == short.class)
						return (C) (Object) ((Number) o).shortValue();
					if (dstClass == boolean.class)
						return (C) Boolean.valueOf(((Number) o).intValue() != 0);
				} else if (srcClass == String.class) {
					if ("null".equals(o))
						return null;
					if (dstClass == int.class)
						return (C) valueOf(SH.parseIntSafe((String) o, throwExceptionOnError, false));
					if (dstClass == long.class)
						return (C) valueOf(SH.parseLongSafe((String) o, throwExceptionOnError));
					if (dstClass == double.class)
						return (C) valueOf(SH.parseDoubleSafe((String) o, throwExceptionOnError));
					if (dstClass == float.class)
						return (C) valueOf(SH.parseFloatSafe((String) o, throwExceptionOnError));
					if (dstClass == byte.class)
						return (C) valueOf(SH.parseByteSafe((String) o, throwExceptionOnError, false));
					if (dstClass == short.class)
						return (C) valueOf(SH.parseShortSafe((String) o, throwExceptionOnError, false));
					if (dstClass == char.class) {
						String s = (String) o;
						if (s.length() != 1) {
							if (throwExceptionOnError)
								throw new DetailedException("auto-cast failed, length of string must be 1").set("value", o).set("cast from class", srcClass).set("cast to class",
										dstClass);
							else
								return null;
						}
						return (C) (Character) ((String) o).charAt(0);
					}
					if (dstClass == boolean.class) {
						if (((String) o).equals("true"))
							return (C) Boolean.TRUE;
						else if (((String) o).equals("false"))
							return (C) Boolean.FALSE;
						else if (!throwExceptionOnError)
							return null;
					}
				} else if (CharSequence.class.isAssignableFrom(srcClass)) {
					if (SH.equals("null", (CharSequence) o))
						return null;
					if (dstClass == int.class)
						return (C) valueOf(SH.parseIntSafe((CharSequence) o, throwExceptionOnError, false));
					if (dstClass == long.class)
						return (C) valueOf(SH.parseLongSafe((CharSequence) o, throwExceptionOnError));
					if (dstClass == double.class)
						return (C) valueOf(SH.parseDoubleSafe((CharSequence) o, throwExceptionOnError));
					if (dstClass == float.class)
						return (C) valueOf(SH.parseFloatSafe((CharSequence) o, throwExceptionOnError));
					if (dstClass == byte.class)
						return (C) valueOf(SH.parseByteSafe((CharSequence) o, throwExceptionOnError, false));
					if (dstClass == short.class)
						return (C) valueOf(SH.parseShortSafe((CharSequence) o, throwExceptionOnError, false));
					if (dstClass == char.class) {
						String s = (String) o;
						if (s.length() != 1) {
							if (throwExceptionOnError)
								throw new DetailedException("auto-cast failed, length of string must be 1").set("value", o).set("cast from class", srcClass).set("cast to class",
										dstClass);
							else
								return null;
						}
						return (C) (Character) ((String) o).charAt(0);
					}
					if (dstClass == boolean.class) {
						if (SH.equals((CharSequence) o, "true"))
							return (C) Boolean.TRUE;
						else if (SH.equals((CharSequence) o, "false"))
							return (C) Boolean.FALSE;
						else if (!throwExceptionOnError)
							return null;
					}
				} else if (srcClass.isEnum()) {
					if (o instanceof ValuedEnum)
						return cast(((ValuedEnum<?>) o).getEnumValue(), dstClass);
					return cast(((Enum<?>) o).ordinal(), dstClass);
				} else if (srcClass == Boolean.class && dstClass == boolean.class) {
					return (C) o;
				} else if (srcClass == Character.class && dstClass == char.class) {
					return (C) o;
				}

			} else if (dstClass.isEnum()) {
				if (ValuedEnum.class.isAssignableFrom(dstClass)) {
					Class type = ValuedEnumCache.getEnumValueType((Class<ValuedEnum<?>>) dstClass);
					return (C) ValuedEnumCache.getEnumValue((Class<ValuedEnum<?>>) dstClass, cast(o, type));
				}
				String s = o.toString();
				if (s.length() > 0 && isBetween(s.charAt(0), '0', '9')) {
					return (C) sun.misc.SharedSecrets.getJavaLangAccess().getEnumConstantsShared((Class) dstClass)[Integer.parseInt(s)];
				}
				return (C) valueOfEnum((Class) dstClass, o.toString(), throwExceptionOnError);
			} else if (dstClass == Boolean.class || dstClass == boolean.class) {
				String s = SH.s(o);
				if (s.equals("true"))
					return (C) Boolean.TRUE;
				else if (s.equals("false"))
					return (C) Boolean.FALSE;
			} else if (dstClass == Character.class || dstClass == char.class) {
				String s = o.toString();
				if (s.length() == 1)
					return (C) (Character) s.charAt(0);
				else {
					if (throwExceptionOnError)
						throw new NumberFormatException("cannot be parsed to a char:'" + s + "'");
					else
						return null;
				}
			} else if (dstClass == File.class) {
				return (C) new File(o.toString());
			} else if (dstClass == StringBuilder.class) {
				return (C) new StringBuilder(o.toString());
			} else if (dstClass == Class.class) {
				return (C) OH.forName(o.toString());
			} else if (dstClass.isArray()) {
				if (o instanceof IterableAndSize) {
					IterableAndSize c = (IterableAndSize) o;
					Class<?> componentType = dstClass.getComponentType();
					Object r = Array.newInstance(componentType, c.size());
					int pos = 0;
					for (Object obj : c)
						Array.set(r, pos++, cast(obj, componentType, false, throwExceptionOnError));
					return (C) r;
				} else if (o instanceof Collection) {
					Collection c = (Collection) o;
					Class<?> componentType = dstClass.getComponentType();
					Object r = Array.newInstance(componentType, c.size());
					int pos = 0;
					for (Object obj : c)
						Array.set(r, pos++, cast(obj, componentType, false, throwExceptionOnError));
					return (C) r;
				} else if (srcClass.isArray()) {
					Class<?> componentType = dstClass.getComponentType();
					int len = Array.getLength(o);
					Object r = Array.newInstance(componentType, len);
					int pos = 0;
					for (int i = 0; i < len; i++)
						Array.set(r, pos++, cast(Array.get(o, i), componentType, false, throwExceptionOnError));
					return (C) r;
				}
			}
		} catch (Exception e) {
			if (throwExceptionOnError)
				throw new DetailedException("auto-cast failed", e).set("value", o).set("cast from class", srcClass).set("cast to class", dstClass);
			else
				return null;
		}
		if (throwExceptionOnError)
			throw new DetailedException("auto-cast failed").set("value", o).set("cast from class", srcClass).set("cast to class", dstClass);
		else
			return null;
	}
	/**
	 * 
	 * Returns the enum constant of the specified enum type with the specified name. The name must match exactly an identifier used to declare an enum constant in this type.
	 * 
	 * @param enumType
	 * @param name
	 * @return
	 */

	public static <C extends Enum<C>> C valueOfEnum(Class<C> enumType, String name) {
		return valueOfEnum(enumType, name, true);
	}

	/**
	 * 
	 * Returns the enum constant of the specified enum type with the specified name. The name must match exactly an identifier used to declare an enum constant in this type.
	 * 
	 * @param enumType
	 * @param name
	 * @param throwOnError
	 * @return
	 */

	public static <C extends Enum<C>> C valueOfEnum(Class<C> enumType, String name, boolean throwOnError) {
		try {
			return Enum.valueOf(enumType, name);
		} catch (IllegalArgumentException e) {
			if (!throwOnError)
				return null;
			StringBuilder sb = new StringBuilder();
			EnumSet<C> set = EnumSet.allOf(enumType);
			if (set.size() > F1GlobalProperties.getMaxCollectionToDelineate())
				throw e;
			for (C value : set) {
				if (sb.length() > 0)
					sb.append(',');
				sb.append(value.name());
			}
			throw new IllegalArgumentException(e.getMessage() + ", existing values=[" + sb + "]");
		}
	}

	/**
	 * allows you to safely dereference an object for its hashcode value. if the object is null, 0 is returned
	 * 
	 * @param object
	 *            the object to call {@link Object#hashCode()} on.
	 * @return the hashcode of an object (see {@link Object#hashCode()}) or 0 if object is null
	 */
	public static int hashCode(Object o) {
		return o == null ? 0 : o.hashCode();
	}
	public static int hashCode(boolean value) {
		return value ? 1231 : 1237;
	}
	public static int hashCode(char value) {
		return value;
	}
	public static int hashCode(byte value) {
		return value;
	}
	public static int hashCode(short value) {
		return value;
	}
	public static int hashCode(int value) {
		return value;
	}
	public static int hashCode(long value) {
		return (int) (value ^ (value >>> 32));
	}
	public static int hashCode(double value) {
		long bits = Double.doubleToLongBits(value);
		return (int) (bits ^ (bits >>> 32));
	}
	public static int hashCode(float value) {
		return Float.floatToIntBits(value);
	}

	public static int hashCode(Object o, Object o2) {
		return hashCode(o) + 31 * hashCode(o2);
	}

	public static int hashCode(Object o, Object o2, Object o3) {
		return hashCode(o) + 31 * hashCode(o2) + 31 * 31 * hashCode(o3);
	}

	public static int hashCode(Object o, Object o2, Object o3, Object o4) {
		return hashCode(o) + 31 * hashCode(o2) + 31 * 31 * hashCode(o3) + 31 * 31 * 31 * hashCode(o4);
	}
	public static int hashCode(Object o, Object o2, Object o3, Object o4, Object o5) {
		return hashCode(o) + 31 * hashCode(o2) + 31 * 31 * hashCode(o3) + 31 * 31 * 31 * hashCode(o4) + 31 * 31 * 31 * 31 * hashCode(o5);
	}
	public static int hashCode(int h, Object o, Object o2, Object o3, Object o4, Object o5) {
		return h + 31 * hashCode(o) + 31 * 31 * hashCode(o2) + 31 * 31 * 31 * hashCode(o3) + 31 * 31 * 31 * 31 * hashCode(o4) + 31 * 31 * 31 * 31 * 31 * hashCode(o5);
	}
	public static int hashCode(int o, int o2) {
		return o + 31 * o2;
	}
	public static int hashCode(int currentHash, Object o2) {
		return currentHash + 31 * hashCode(o2);
	}
	public static int hashCode(int o, int o2, int o3) {
		return o + 31 * o2 + 31 * 31 * o3;
	}
	public static int hashCode(int o, int o2, int o3, int o4) {
		return o + (31 * o2) + (31 * 31 * o3) + (31 * 31 * 31 * o4);
	}
	public static int hashCode(int o, int o2, int o3, int o4, int o5) {
		return o + (31 * o2) + (31 * 31 * o3) + (31 * 31 * 31 * o4) + (31 * 31 * 31 * 31 * o5);
	}
	public static int hashCode(int o, int o2, int o3, int o4, int o5, int o6) {
		return o + (31 * o2) + (31 * 31 * o3) + (31 * 31 * 31 * o4) + (31 * 31 * 31 * 31 * o5) + (31 * 31 * 31 * 31 * 31 * o6);
	}
	public static int hashCode(int o, int o2, int o3, int o4, int o5, int o6, int o7) {
		return o + (31 * o2) + (31 * 31 * o3) + (31 * 31 * 31 * o4) + (31 * 31 * 31 * 31 * o5) + (31 * 31 * 31 * 31 * 31 * o6) + (31 * 31 * 31 * 31 * 31 * 31 * o7);
	}

	public static long hashCode64(long o, long o2) {
		return o + 31 * o2;
	}
	public static long hashCode64(long o, long o2, long o3) {
		return o + 31 * o2 + 31 * 31 * o3;
	}
	public static long hashCode64(long o, long o2, long o3, long o4) {
		return o + (31 * o2) + (31 * 31 * o3) + (31 * 31 * 31 * o4);
	}
	public static long hashCode64(long o, long o2, long o3, long o4, long o5) {
		return o + (31 * o2) + (31 * 31 * o3) + (31 * 31 * 31 * o4) + (31 * 31 * 31 * 31 * o5);
	}
	public static long hashCode64(long o, long o2, long o3, long o4, long o5, long o6) {
		return o + (31 * o2) + (31 * 31 * o3) + (31 * 31 * 31 * o4) + (31 * 31 * 31 * 31 * o5) + (31 * 31 * 31 * 31 * 31 * o6);
	}
	public static long hashCode64(long o, long o2, long o3, long o4, long o5, long o6, long o7) {
		return o + (31 * o2) + (31 * 31 * o3) + (31 * 31 * 31 * o4) + (31 * 31 * 31 * 31 * o5) + (31 * 31 * 31 * 31 * 31 * o6) + (31 * 31 * 31 * 31 * 31 * 31 * o7);
	}

	/**
	 * allows you to safely deference an object for its class type. if the object is null, null is returned
	 * 
	 * @param object
	 *            the object to call {@link Object#getClass()} on.
	 * @return the class of an object (see {@link Object#getClass()}) or null if object is null
	 */
	public static Class<?> getClass(Object object) {
		return object == null ? null : object.getClass();
	}

	/**
	 * allows to safely deference an object for its string value. if the object is null, null is returned
	 * 
	 * @param object
	 *            the object to call {@link Object#toString()} on.
	 * @return the string representation of object (see {@link Object#toString()}) or null if object is null
	 */
	public static String toString(Object object) {
		return object == null ? null : object.toString();
	}

	/**
	 * Convenience method to typically replace a reference with some potentially null value with some default.
	 * <P>
	 * for example:<BR>
	 * <B>login=noNull(login,"defaultuser");</B>// if login was null, now its "defaultuser"
	 * 
	 * @param <T>
	 * @param value
	 *            value to test for null (and return if not null)
	 * @param onNull
	 *            what to return if value si null.
	 * @return if value is null then onNull, else value. (will return null iff value and onNull are both null)
	 */
	public static <T> T noNull(T value, T onNull) {
		return value == null ? onNull : value;
	}
	public static long noNull(Long value, long onNull) {
		return value == null ? onNull : value.longValue();
	}
	public static int noNull(Integer value, int onNull) {
		return value == null ? onNull : value.intValue();
	}
	public static short noNull(Short value, short onNull) {
		return value == null ? onNull : value.shortValue();
	}
	public static byte noNull(Byte value, byte onNull) {
		return value == null ? onNull : value.byteValue();
	}
	public static double noNull(Double value, double onNull) {
		return value == null ? onNull : value.doubleValue();
	}
	public static float noNull(Float value, float onNull) {
		return value == null ? onNull : value.floatValue();
	}
	public static char noNull(Character value, char onNull) {
		return value == null ? onNull : value.charValue();
	}
	public static boolean noNull(Boolean value, boolean onNull) {
		return value == null ? onNull : value.booleanValue();
	}

	/**
	 * randomly sleep for a period of time up to some maximum amount. This is useful when testing multi-threaded applications because you may want to simulate spurious thread
	 * swapping.<BR>
	 * For example, your code to test could look like this:<BR>
	 * <I> <B>OH.sleepRand(10,100)</B>;<BR>
	 * while(something){<BR>
	 * <B>OH.sleepRand(10,100)</B>;<BR>
	 * block.wait();<BR>
	 * <B>OH.sleepRand(10,100)</B>;<BR>
	 * atomic.getAndIncrement();<BR>
	 * <B>OH.sleepRand(10,100)</B>;<BR>
	 * }<BR>
	 * </I>
	 * <P>
	 * In that example, we are simulating a 10% chance that there will be an apx. 50 millisecond thread swap between every statement.
	 * 
	 * @param pctChance
	 *            chance it will sleep (0 = never, 50 = fifty percent chance, 100 = every time)
	 * @param maxTime
	 *            max time to sleep in milliseconds
	 * @return false if interrupted.
	 */
	public static boolean sleepRand(int pctChance, long maxTime) {
		if (Math.random() * 100 >= (100 - pctChance)) {
			return sleep(1L + (long) (Math.random() * maxTime));
		}
		return true;
	}

	/**
	 * sleep for period of time, see {@link Thread#sleep(long)}. does not throw an interrupted exception, instead returns false
	 * 
	 * @param unit
	 *            the unit to sleep in
	 * @param count
	 *            amount of time to sleep in specified units
	 * @return true if guaranteed to have slept at least specified time. false if {@link Thread#interrupt()} was called
	 */
	public static boolean sleep(TimeUnit unit, long count) {
		return sleep(TimeUnit.MILLISECONDS.convert(count, unit));
	}

	/**
	 * sleep for period of time, see {@link Thread#sleep(long)}. does not throw an interrupted exception, instead returns false
	 * 
	 * @param millis
	 *            number of milliseconds to sleep
	 * @return true if guaranteed to have slept at least specified time. false if {@link Thread#interrupt()} was called
	 */
	public static boolean sleep(long millis) {
		if (millis <= 0)
			return true;
		try {
			Thread.sleep(millis);
			return true;
		} catch (InterruptedException e) {
			return false;
		}
	}

	/**
	 * assert that b is true (or throw an {@link AssertionException}).
	 * 
	 * @param b
	 *            value to test for true
	 * @return b param (which is always true)
	 * @throws AssertionException
	 *             if b is false
	 */
	public static boolean assertTrue(boolean b) throws AssertionException {
		if (!b)
			throw new AssertionException("is false");
		return true;
	}

	/**
	 * assert that b is true (or throw an {@link AssertionException}). <BR>
	 * User may specify an error message.
	 * 
	 * @param b
	 *            value to test for true
	 * @param message
	 *            Error message specified by user
	 * @return b param (which is always true)
	 * @throws AssertionException
	 *             if b is false
	 */
	public static boolean assertTrue(boolean b, String message) throws AssertionException {
		if (!b)
			throw new AssertionException(message + " is false");
		return true;
	}

	/**
	 * assert that b is false (or throw an {@link AssertionException}).
	 * 
	 * @param b
	 *            value to test for false
	 * @return b param (which is always false)
	 * @throws AssertionException
	 *             if b is true
	 */
	public static boolean assertFalse(boolean b) throws AssertionException {
		if (b)
			throw new AssertionException("is true");
		return false;
	}

	/**
	 * assert that b is false (or throw an {@link AssertionException}). <BR>
	 * User may specify an error message.
	 * 
	 * @param b
	 *            value to test for false
	 * @param message
	 *            Error message specified by user
	 * @return b param (which is always false)
	 * @throws AssertionException
	 *             if b is true
	 */
	public static boolean assertFalse(boolean b, String message) throws AssertionException {
		if (b)
			throw new AssertionException(message + " is true");
		return false;
	}

	/**
	 * assert that left is equal to right (or throw an {@link AssertionException}). see {@link #eq(Object, Object)} for details on comparison
	 * 
	 * @param <T>
	 *            type of left and return value
	 * @param left
	 *            left param of comparison
	 * @param right
	 *            right param of comparison
	 * @return left param (may be null, if right is also null)
	 * @throws AssertionException
	 *             if left and right are equal
	 */
	public static <T> T assertEq(T left, Object right) throws AssertionException {
		if (ne(left, right))
			throw new AssertionException("" + left + "!=" + right);
		return left;
	}

	/**
	 * 
	 * Assert that left is equal to right (or throw {@link AssertionException}).
	 * 
	 * @param left
	 * @param right
	 * @return If left == right, return left.
	 * @throws AssertionException
	 *             Throws when left != right.
	 */

	public static <T> T assertEqIdentity(T left, Object right) throws AssertionException {
		if (left != right)
			throw new AssertionException("" + left + "!==" + right);
		return left;
	}

	/**
	 * Assert that left is equal to right (or throw {@link AssertionException}).
	 * 
	 * @param left
	 * @param right
	 * @param description
	 *            User-specified message to be displayed when AssertionException is thrown
	 * @return If left == right, return left.
	 * @throws AssertionException
	 *             Throws when left != right
	 */

	public static <T> T assertEqIdentity(T left, Object right, String description) throws AssertionException {
		if (left != right)
			throw new AssertionException(description + ": " + left + "!==" + right);
		return left;
	}

	/**
	 * Assert that left is not equal to right (or throw {@link AssertionException}).
	 * 
	 * @param left
	 * @param right
	 * @return If left != right, return left.
	 * @throws AssertionException
	 *             Throws when left == right.
	 */

	public static <T> T assertNeIdentity(T left, Object right) throws AssertionException {
		if (left == right)
			throw new AssertionException("" + left + "!==" + right);
		return left;
	}
	/**
	 * Assert that left is not equal to right (or throw {@link AssertionException}).
	 * 
	 * @param left
	 * @param right
	 * @param description
	 *            User-specified message to be displayed when AssertionException is thrown
	 * @return If left != right, return left.
	 * @throws AssertionException
	 *             Throws when left == right
	 */

	public static <T> T assertNeIdentity(T left, Object right, String description) throws AssertionException {
		if (left == right)
			throw new AssertionException(description + ": " + left + "!==" + right);
		return left;
	}
	/**
	 * assert that left is equal to right (or throw an {@link AssertionException} with the supplied message). see {@link #eq(Object, Object)} for details on comparison
	 * 
	 * @param <T>
	 *            type of left and return value
	 * @param left
	 *            left param of comparison
	 * @param right
	 *            right param of comparison
	 * @param message
	 *            message to be included in {@link AssertionException} if one is thrown.
	 * @return left param (may be null, if right is also null)
	 * @throws AssertionException
	 *             if left and right are equal
	 */
	public static <T> T assertEq(T left, Object right, String message) throws AssertionException {
		if (ne(left, right))
			throw new AssertionException(message + ": " + left + "!=" + right);
		return left;
	}

	/**
	 * 
	 * Assert that value is between min and max, inclusive (or throw an {@link AssertionException}).
	 * 
	 * @param value
	 *            Value to be tested
	 * @param min
	 *            Minimum value
	 * @param max
	 *            Maximum value
	 * @return If (min <= value <= max), return value.
	 * @throws AssertionException
	 *             Throw if value is not between min and max, inclusive
	 */

	public static int assertBetween(int value, int min, int max) throws AssertionException {
		if (!isBetween(value, min, max))
			throw new AssertionException(" not between [ " + min + " ... " + max + " ] : " + value);
		return value;
	}

	/**
	 * 
	 * Assert that value is between min and max, exclusive (or throw an {@link AssertionException}).
	 * 
	 * @param value
	 *            Value to be tested
	 * @param min
	 *            Minimum value
	 * @param max
	 *            Maximum value
	 * @return If (min < value < max), return value.
	 * @throws AssertionException
	 *             Throw if value is not between min and max, exclusive
	 */

	public static int assertBetweenExcluding(int value, int min, int max) throws AssertionException {
		if (!isBetweenExcluding(value, min, max))
			throw new AssertionException(" not between [ " + min + " ... " + (max - 1) + " ] : " + value);
		return value;
	}

	/**
	 * 
	 * Assert that value is between min and max, inclusive (or throw an {@link AssertionException}).
	 * 
	 * @param value
	 *            Value to be tested
	 * @param min
	 *            Minimum value
	 * @param max
	 *            Maximum value
	 * @return If (min <= value <= max), return value.
	 * @throws AssertionException
	 *             Throw if value is not between min and max, inclusive
	 */

	public static <T extends Comparable<T>> T assertBetween(T value, T min, T max) throws AssertionException {
		if (!isBetween(value, min, max))
			throw new AssertionException(" not between [ " + min + " ... " + max + " ] : " + value);
		return value;
	}

	/**
	 * 
	 * Assert that value is between min and max, inclusive (or throw an {@link AssertionException}).
	 * 
	 * @param value
	 *            Value to be tested
	 * @param min
	 *            Minimum value
	 * @param max
	 *            Maximum value
	 * @return If (min <= value <= max), return value.
	 * @throws AssertionException
	 *             Throw if value is not between min and max, inclusive
	 */

	public static long assertBetween(long value, long min, long max) throws AssertionException {
		if (!isBetween(value, min, max))
			throw new AssertionException(" not between [ " + min + " ... " + max + " ] : " + value);
		return value;
	}

	/**
	 * 
	 * Assert that value is between min and max, inclusive (or throw an {@link AssertionException}).
	 * 
	 * @param value
	 *            Value to be tested
	 * @param min
	 *            Minimum value
	 * @param max
	 *            Maximum value
	 * @return If (min <= value <= max), return value.
	 * @throws AssertionException
	 *             Throw if value is not between min and max, inclusive
	 */

	public static double assertBetween(double value, double min, double max) throws AssertionException {
		if (!isBetween(value, min, max))
			throw new AssertionException(" not between [ " + min + " ... " + max + " ] : " + value);
		return value;
	}

	/**
	 * 
	 * Assert that value is between min and max, inclusive (or throw an {@link AssertionException}).
	 * 
	 * @param value
	 *            Value to be tested
	 * @param min
	 *            Minimum value
	 * @param max
	 *            Maximum value
	 * @return If (min <= value <= max), return value.
	 * @throws AssertionException
	 *             Throw if value is not between min and max, inclusive
	 */

	public static int assertBetween(int value, int min, int max, String description) throws AssertionException {
		if (!isBetween(value, min, max))
			throw new AssertionException(description + " not between [ " + min + " ... " + max + " ] : " + value);
		return value;
	}

	/**
	 * 
	 * Assert that value is between min and max, inclusive (or throw an {@link AssertionException}).
	 * 
	 * @param value
	 *            Value to be tested
	 * @param min
	 *            Minimum value
	 * @param max
	 *            Maximum value
	 * @return If (min <= value <= max), return value.
	 * @throws AssertionException
	 *             Throw if value is not between min and max, inclusive
	 */
	public static long assertBetween(long value, long min, long max, String description) throws AssertionException {
		if (!isBetween(value, min, max))
			throw new AssertionException(description + " not between [ " + min + " ... " + max + " ] : " + value);
		return value;
	}

	/**
	 * 
	 * Assert that value is between min and max, inclusive (or throw an {@link AssertionException}).
	 * 
	 * @param value
	 *            Value to be tested
	 * @param min
	 *            Minimum value
	 * @param max
	 *            Maximum value
	 * @return If (min <= value <= max), return value.
	 * @throws AssertionException
	 *             Throw if value is not between min and max, inclusive
	 */
	public static double assertBetween(double value, double min, double max, String description) throws AssertionException {
		if (!isBetween(value, min, max))
			throw new AssertionException(description + " not between [ " + min + " ... " + max + " ] : " + value);
		return value;
	}

	/**
	 * returns true iff value is between min and max values (inclusive). In otherwords, returns false if value is less then min or greater than max
	 * 
	 * @param value
	 *            value to compare
	 * @param min
	 *            min of range
	 * @param max
	 *            max of range
	 * @return true iff between
	 */
	public static boolean isBetween(char value, char min, char max) {
		return min <= value && value <= max;
	}

	/**
	 * 
	 * Returns true if value is between min and max (inclusive).
	 * 
	 * @param value
	 *            Value to be tested
	 * @param min
	 *            Minimum value
	 * @param max
	 *            Maximum value
	 * @return If (min <= value <= max), return true. <BR>
	 *         Otherwise, return false.
	 */

	public static <T extends Comparable<T>> boolean isBetween(T value, T min, T max) {
		return ge(value, min) && le(value, max);
	}

	/**
	 * returns true iff value is between min and max values (inclusive). In otherwords, returns false if value is less then min or greater than max
	 * 
	 * @param value
	 *            value to compare
	 * @param min
	 *            min of range(inclusive)
	 * @param max
	 *            max of range(inclusive)
	 * @return true iff between
	 */
	public static boolean isBetween(int value, int min, int max) {
		return min <= value && value <= max;
	}
	/**
	 * returns true iff value is between min and max values (exclusive). In otherwords, returns false if value is less then min or greater than max or equal to max
	 * 
	 * @param value
	 *            value to compare
	 * @param min
	 *            min of range(inclusive)
	 * @param max
	 *            max of range(inclusive)
	 * @return true iff between
	 */
	public static boolean isBetweenExcluding(int value, int min, int maxPlusOne) {
		return min <= value && value < maxPlusOne;
	}

	/**
	 * returns true iff value is between min and max values (inclusive). In otherwords, returns false if value is less then min or greater than max
	 * 
	 * @param value
	 *            value to compare
	 * @param min
	 *            min of range(inclusive)
	 * @param max
	 *            max of range(inclusive)
	 * @return true iff between
	 */
	public static boolean isBetween(long value, long min, long max) {
		return min <= value && value <= max;
	}

	/**
	 * returns true iff value is between min and max values (inclusive). In otherwords, returns false if value is less then min or greater than max
	 * 
	 * @param value
	 *            value to compare
	 * @param min
	 *            min of range(inclusive)
	 * @param max
	 *            max of range(inclusive)
	 * @return true iff between
	 */
	public static boolean isBetween(double value, double min, double max) {
		return min <= value && value <= max;
	}

	/**
	 * returns true if the object is null or can be guranteed to be non-immutable, hence thread safe and can avoid cloning. Note: A return value of false doesn't guarnatee that it
	 * is mutable
	 * 
	 * @param o
	 *            the object to have it's class inspected for imutability
	 * @return true if guaranteed to be immutable, false if not guaranteed to be immutable
	 */
	public static boolean isImmutable(Object o) {
		return o == null || isImmutableClass(o.getClass()) || (o instanceof Lockable && ((Lockable) o).isLocked());
	}

	/**
	 * returns true if instances of the class are guaranteed to be non-immutable, hence thread safe and can avoid cloning instances of the class. Note: A return value of false
	 * doesn't guarantee that it is mutable
	 * 
	 * @param c
	 *            the class to inspect for imutability
	 * @return true if guaranteed to immutable, false if not guaranteed to be immutable
	 */
	public static boolean isImmutableClass(Class<?> c) {
		return c.isPrimitive() || c == String.class || c == Character.class || c == Boolean.class || c == Object.class || c == Class.class || c.isEnum()
				|| Number.class.isAssignableFrom(c) || AnnotatedElement.class.isAssignableFrom(c) || Immutable.class.isAssignableFrom(c);
	}

	/**
	 * see {@link #getWidest(Class, Class)} except it can operate on more than two classes
	 */
	public static Class<?> getWidest(List<? extends Class<?>> classes) {
		if (CH.isEmpty(classes))
			return null;
		Class<?> r = null;
		for (Class<?> c : classes)
			if ((r = r == null ? c : getWidest(c, r)) == Object.class)
				break;
		return r;
	}

	public static Caster<?> getWidest(Caster<?> c1, Caster<?> c2) {
		if (c1 == c2)
			return c1;
		final Class<?> class1 = c1.getCastToClass();
		final Class<?> class2 = c2.getCastToClass();
		final Class<?> r = getWidest(class1, class2);
		if (r == class1)
			return c1;
		if (r == class2)
			return c2;
		return CasterManager.getCaster(r);

	}
	/**
	 * gets the wider of the two classes. Will always return one of the two classes or {@link Object}.class for example,
	 * 
	 * @param class1
	 *            first object
	 * @param class2
	 *            second object
	 * @return either class1, class2 or Object.class
	 */
	public static Class<?> getWidest(Class<?> class1, Class<?> class2) {
		if (class1 == class2)
			return class1;
		if (class1.isPrimitive() && class2.isPrimitive()) {
			if (class1 == double.class || class2 == double.class)
				return double.class;
			else if (class1 == float.class || class2 == float.class)
				return float.class;
			else if (class1 == long.class || class2 == long.class)
				return long.class;
			else if (class1 == int.class || class2 == int.class)
				return int.class;
			else if (class1 == short.class || class2 == short.class)
				return short.class;
			else if (class1 == byte.class || class2 == byte.class)
				return byte.class;
		}

		class1 = getBoxed(class1);
		class2 = getBoxed(class2);
		if (class1 != Object.class && class1.isAssignableFrom(class2))
			return class1;
		else if (class2 != Object.class && class2.isAssignableFrom(class1))
			return class2;
		else if (Number.class.isAssignableFrom(class1) && Number.class.isAssignableFrom(class2)) {
			if (class1 == Complex.class || class2 == Complex.class)
				return Complex.class;
			if (class1 == BigDecimal.class || class2 == BigDecimal.class)
				return BigDecimal.class;
			else if (class1 == Double.class || class2 == Double.class)
				return Double.class;
			else if (class1 == Float.class || class2 == Float.class)
				return Float.class;
			if (class1 == BigInteger.class || class2 == BigInteger.class)
				return BigInteger.class;
			else if (class1 == Long.class || class2 == Long.class)
				return Long.class;
			else if (class1 == DateMillis.class || class2 == DateMillis.class)
				return Long.class;
			else if (class1 == DateNanos.class || class2 == DateNanos.class)
				return Long.class;
			else if (class1 == Integer.class || class2 == Integer.class)
				return Integer.class;
			else if (class1 == Short.class || class2 == Short.class)
				return Short.class;
			else if (class1 == Byte.class || class2 == Byte.class)
				return Byte.class;
		}
		if (class1 == String.class || class2 == String.class || CharSequence.class.isAssignableFrom(class1) || CharSequence.class.isAssignableFrom(class2))
			return String.class;
		return Object.class;
	}

	/**
	 * 
	 * Same as {@link getWidest}, but ignores null inputs.
	 * 
	 * @param c1
	 *            first object
	 * @param c2
	 *            second object
	 * @return Either c1, c2, or Object.class
	 */

	public static Class<?> getWidestIgnoreNull(Class c1, Class<?> c2) {
		if (c1 == null)
			return c2;
		else if (c2 == null)
			return c1;
		else
			return getWidest(c1, c2);

	}

	public static Caster<?> getWidestIgnoreNull(Caster<?> c1, Caster<?> c2) {
		if (c1 == null)
			return c2;
		else if (c2 == null)
			return c1;
		else
			return getWidest(c1, c2);//TODO: optimize this

	}
	/**
	 * The default value (represented as a string)for a class (see {@link Prim#defaultValueString}
	 * 
	 * @param c
	 *            the class to get the default value for
	 * @return the string representation
	 */
	public static Object getDefaultValue(Class<?> c) {
		if (c.isPrimitive()) {
			return PRIMITIVE_CLASS_TO_PRIM.get(c).defaultValue;
		}
		return null;
	}

	/**
	 * The default value (represented as a string)for a class (see {@link Prim#defaultValueString}
	 * 
	 * @param c
	 *            the class to get the default value for
	 * @return the string representation (wont be null)
	 */
	public static String getDefaultValueString(Class<?> c) {
		if (c.isPrimitive())
			return PRIMITIVE_CLASS_TO_PRIM.get(c).defaultValueString;
		return "null";
	}

	/** see {@link ValuedEnumHelper#getEnumValue(Class, Object)} */
	public static <E extends ValuedEnum<?>> E getValuedEnum(Class<E> c, Object value) {
		return ValuedEnumCache.getEnumValue(c, value);
	}

	/**
	 * safely converts a long to a byte. throws an {@link AssertionException} if its outside the range on a byte
	 * 
	 * @param value
	 *            value to cast
	 * @return the value as a byte
	 * @throws AssertionException
	 *             if the value is outside the accepted range of a byte
	 */
	public static byte toByte(long value) {
		assertBetween(value, Byte.MIN_VALUE, Byte.MAX_VALUE);
		return (byte) value;
	}

	/**
	 * safely converts a long to a short. throws an {@link AssertionException} if its outside the range on a short
	 * 
	 * @param value
	 *            value to cast
	 * @return the value as a short
	 * @throws AssertionException
	 *             if the value is outside the accepted range of a short
	 */
	public static short toShort(long value) {
		assertBetween(value, Short.MIN_VALUE, Short.MAX_VALUE);
		return (short) value;
	}

	/**
	 * safely converts a long to an int. throws an {@link AssertionException} if its outside the range on an int
	 * 
	 * @param value
	 *            value to cast
	 * @return the value as an int
	 * @throws AssertionException
	 *             if the value is outside the accepted range of an int
	 */
	public static int toInt(long value) {
		assertBetween(value, Integer.MIN_VALUE, Integer.MAX_VALUE);
		return (int) value;
	}

	/**
	 * safely converts a long to an integer. If the long is outside the range of an integer, then it is sent to the extent (instead of wrapping).
	 * 
	 * @param value
	 *            value to cast
	 * @return the value as an int
	 */
	public static int toIntNoThrow(long value) {
		if (value < Integer.MIN_VALUE)
			return Integer.MIN_VALUE;
		if (value > Integer.MAX_VALUE)
			return Integer.MAX_VALUE;
		return (int) value;
	}

	/**
	 * safely converts a double to a byte. throws an {@link AssertionException} if its outside the range on a byte
	 * 
	 * @param value
	 *            value to cast
	 * @return the value as a byte
	 * @throws AssertionException
	 *             if the value is outside the accepted range of a byte
	 */
	public static byte toByte(double value) {
		assertBetween(value, Byte.MIN_VALUE, Byte.MAX_VALUE);
		return (byte) value;
	}

	/**
	 * safely converts a double to a short. throws an {@link AssertionException} if its outside the range on a short
	 * 
	 * @param value
	 *            value to cast
	 * @return the value as a short
	 * @throws AssertionException
	 *             if the value is outside the accepted range of a short
	 */
	public static short toShort(double value) {
		assertBetween(value, Short.MIN_VALUE, Short.MAX_VALUE);
		return (short) value;
	}

	/**
	 * safely converts a double to an int. throws an {@link AssertionException} if its outside the range on an int
	 * 
	 * @param value
	 *            value to cast
	 * @return the value as an int
	 * @throws AssertionException
	 *             if the value is outside the accepted range of an int
	 */
	public static int toInt(double value) {
		assertBetween(value, Integer.MIN_VALUE, Integer.MAX_VALUE);
		return (int) value;
	}

	/**
	 * safely converts a double to a long. throws an {@link AssertionException} if its outside the range on a long
	 * 
	 * @param value
	 *            value to cast
	 * @return the value as a long
	 * @throws AssertionException
	 *             if the value is outside the accepted range of a long
	 */
	public static long toLong(double value) throws AssertionException {
		assertBetween(value, Long.MIN_VALUE, Long.MAX_VALUE);
		return (long) value;
	}

	/**
	 * converts any throwable to a runtime exception... If e is already a runtime exception, it's just passed back
	 * 
	 * @param e
	 *            a throwable to wrap.
	 * @return a {@link RuntimeException}
	 */
	public static RuntimeException toRuntime(Throwable e) {
		return CheckedRuntimeException.wrap(e);
	}
	public static Exception toException(Throwable e) {
		if (e instanceof Exception)
			return (Exception) e;
		return new Exception(e);
	}

	/**
	 * convenience method for finding and invoking a constructor. Please note, the argumentTypes are used to identify which constructor to call on an object see
	 * {@link RH#findConstructor(Class, Class[]) for details.
	 * 
	 * @param <C>
	 *            type of object to construct
	 * @param classs
	 *            the class to create
	 * @param argumentTypes
	 *            the types of arguments of the constructor's param list
	 * @param constructorParameters
	 *            the parameters to pass into the constructor
	 * @return the newly created object
	 */

	public static <C> C nw(Class<C> classs, Class<?>[] argumentTypes, Object[] constructorParameters) {
		return RH.findConstructorAndInvoke(classs, argumentTypes, constructorParameters);

	}

	/**
	 * assert that left is greater than or equal to right (or throw an {@link AssertionException}).
	 * 
	 * @param left
	 *            left param of comparison
	 * @param right
	 *            right param of comparison
	 * @return left
	 * @throws AssertionException
	 *             if left is not greater than or equal to right
	 */
	public static int assertGe(int left, int right) throws AssertionException {
		if (left < right)
			throw new AssertionException(" not true: " + left + " >= " + right);
		return left;
	}

	/**
	 * assert that left is greater than or equal to right (or throw an {@link AssertionException}). User can input an error message (String message).
	 * 
	 * @param left
	 *            left param of comparison
	 * @param right
	 *            right param of comparison
	 * @param message
	 *            User defined error message
	 * @return left
	 * @throws AssertionException
	 *             if left is not greater than or equal to right
	 */
	public static int assertGe(int left, int right, String message) throws AssertionException {
		if (left < right)
			throw new AssertionException(message + " not true: " + left + " >= " + right);
		return left;
	}

	/**
	 * assert that left is greater than right (or throw an {@link AssertionException}).
	 * 
	 * @param <T>
	 *            type of left and return value
	 * @param left
	 *            left param of comparison
	 * @param right
	 *            right param of comparison
	 * @return left
	 * @throws AssertionException
	 *             if left is not greater than right
	 */
	public static int assertGt(int left, int right) throws AssertionException {
		if (left <= right)
			throw new AssertionException(" not true: " + left + " > " + right);
		return left;
	}

	/**
	 * assert that left is greater than right (or throw an {@link AssertionException}). User can input an error message (String message).
	 * 
	 * @param left
	 *            left param of comparison
	 * @param right
	 *            right param of comparison
	 * @param message
	 *            User defined error message
	 * @return left
	 * @throws AssertionException
	 *             if left is not greater than right
	 */
	public static int assertGt(int left, int right, String message) throws AssertionException {
		if (left <= right)
			throw new AssertionException(message + " not true: " + left + " > " + right);
		return left;
	}

	/**
	 * assert that left is less than or equal to right (or throw an {@link AssertionException}).
	 * 
	 * @param <T>
	 *            type of left and return value
	 * @param left
	 *            left param of comparison
	 * @param right
	 *            right param of comparison
	 * @return left
	 * @throws AssertionException
	 *             if left is not less than or equal to right
	 */
	public static int assertLe(int left, int right) throws AssertionException {
		if (left > right)
			throw new AssertionException(" not true: " + left + " <= " + right);
		return left;
	}

	/**
	 * assert that left is less than or equal to right (or throw an {@link AssertionException}). User can input an error message (String message).
	 * 
	 * @param left
	 *            left param of comparison
	 * @param right
	 *            right param of comparison
	 * @param message
	 *            User defined error message
	 * @return left
	 * @throws AssertionException
	 *             if left is not less than or equal to right
	 */
	public static int assertLe(int left, int right, String message) throws AssertionException {
		if (left > right)
			throw new AssertionException(message + " not true: " + left + " <= " + right);
		return left;
	}

	/**
	 * assert that left is less than right (or throw an {@link AssertionException}).
	 * 
	 * @param <T>
	 *            type of left and return value
	 * @param left
	 *            left param of comparison
	 * @param right
	 *            right param of comparison
	 * @return left
	 * @throws AssertionException
	 *             if left is not less than right
	 */
	public static int assertLt(int left, int right) throws AssertionException {
		if (left >= right)
			throw new AssertionException(" not true: " + left + " < " + right);
		return left;
	}

	/**
	 * assert that left is less than right (or throw an {@link AssertionException}). User can input an error message (String message).
	 * 
	 * @param left
	 *            left param of comparison
	 * @param right
	 *            right param of comparison
	 * @param message
	 *            User defined error message
	 * @return left
	 * @throws AssertionException
	 *             if left is not less than right
	 */
	public static int assertLt(int left, int right, String message) throws AssertionException {
		if (left >= right)
			throw new AssertionException(message + " not true: " + left + " < " + right);
		return left;
	}

	/**
	 * assert that left is equal to right (or throw an {@link AssertionException}).
	 * 
	 * @param <T>
	 *            type of left and return value
	 * @param left
	 *            left param of comparison
	 * @param right
	 *            right param of comparison
	 * @return left
	 * @throws AssertionException
	 *             if left and right are not equal
	 */
	public static int assertEq(int left, int right) throws AssertionException {
		if (left != right)
			throw new AssertionException(" not true: " + left + " == " + right);
		return left;
	}

	/**
	 * assert that left is equal to right (or throw an {@link AssertionException}). User can input an error message (String message).
	 * 
	 * @param left
	 *            left param of comparison
	 * @param right
	 *            right param of comparison
	 * @param message
	 *            User defined error message
	 * @return left
	 * @throws AssertionException
	 *             if left is not equal to right
	 */
	public static int assertEq(int left, int right, String message) throws AssertionException {
		if (left != right)
			throw new AssertionException(message + " not true: " + left + " == " + right);
		return left;
	}

	/**
	 * assert that left is not equal to right (or throw an {@link AssertionException}).
	 * 
	 * @param <T>
	 *            type of left and return value
	 * @param left
	 *            left param of comparison
	 * @param right
	 *            right param of comparison
	 * @return left
	 * @throws AssertionException
	 *             if left and right are not equal
	 */
	public static int assertNe(int left, int right) throws AssertionException {
		if (left == right)
			throw new AssertionException(" not true: " + left + " != " + right);
		return left;
	}

	/**
	 * assert that left is not equal to right (or throw an {@link AssertionException}). User can input an error message (String message).
	 * 
	 * @param left
	 *            left param of comparison
	 * @param right
	 *            right param of comparison
	 * @param message
	 *            User defined error message
	 * @return left
	 * @throws AssertionException
	 *             if left is equal to right
	 */
	public static int assertNe(int left, int right, String message) throws AssertionException {
		if (left == right)
			throw new AssertionException(message + " not true: " + left + " != " + right);
		return left;
	}

	/**
	 * assert that left is greater than or equal to right (or throw an {@link AssertionException}).
	 * 
	 * @param <T>
	 *            type of left and return value
	 * @param left
	 *            left param of comparison
	 * @param right
	 *            right param of comparison
	 * @return left
	 * @throws AssertionException
	 *             if left is not greater than or equal to right
	 */
	public static long assertGe(long left, long right) throws AssertionException {
		if (left < right)
			throw new AssertionException(" not true: " + left + " >= " + right);
		return left;
	}

	/**
	 * assert that left is greater than or equal to right (or throw an {@link AssertionException}). User can input an error message (String message).
	 * 
	 * @param left
	 *            left param of comparison
	 * @param right
	 *            right param of comparison
	 * @param message
	 *            User defined error message
	 * @return left
	 * @throws AssertionException
	 *             if left is not greater than or equal to right
	 */
	public static long assertGe(long left, long right, String message) throws AssertionException {
		if (left < right)
			throw new AssertionException(message + " not true: " + left + " >= " + right);
		return left;
	}

	/**
	 * assert that left is greater than right (or throw an {@link AssertionException}).
	 * 
	 * @param <T>
	 *            type of left and return value
	 * @param left
	 *            left param of comparison
	 * @param right
	 *            right param of comparison
	 * @return left
	 * @throws AssertionException
	 *             if left is not greater than right
	 */
	public static long assertGt(long left, long right) throws AssertionException {
		if (left <= right)
			throw new AssertionException(" not true: " + left + " > " + right);
		return left;
	}

	/**
	 * assert that left is greater than right (or throw an {@link AssertionException}). User can input an error message (String message).
	 * 
	 * @param left
	 *            left param of comparison
	 * @param right
	 *            right param of comparison
	 * @param message
	 *            User defined error message
	 * @return left
	 * @throws AssertionException
	 *             if left is not greater than right
	 */
	public static long assertGt(long left, long right, String message) throws AssertionException {
		if (left <= right)
			throw new AssertionException(message + " not true: " + left + " > " + right);
		return left;
	}

	/**
	 * assert that left is less than or equal to right (or throw an {@link AssertionException}).
	 * 
	 * @param <T>
	 *            type of left and return value
	 * @param left
	 *            left param of comparison
	 * @param right
	 *            right param of comparison
	 * @return left
	 * @throws AssertionException
	 *             if left is not less than or equal to right
	 */
	public static long assertLe(long left, long right) throws AssertionException {
		if (left > right)
			throw new AssertionException(" not true: " + left + " <= " + right);
		return left;
	}

	/**
	 * assert that left is less than or equal to right (or throw an {@link AssertionException}). User can input an error message (String message).
	 * 
	 * @param left
	 *            left param of comparison
	 * @param right
	 *            right param of comparison
	 * @param message
	 *            User defined error message
	 * @return left
	 * @throws AssertionException
	 *             if left is not less than or equal to right
	 */
	public static long assertLe(long left, long right, String message) throws AssertionException {
		if (left > right)
			throw new AssertionException(message + " not true: " + left + " <= " + right);
		return left;
	}

	/**
	 * assert that left is less than right (or throw an {@link AssertionException}).
	 * 
	 * @param <T>
	 *            type of left and return value
	 * @param left
	 *            left param of comparison
	 * @param right
	 *            right param of comparison
	 * @return left
	 * @throws AssertionException
	 *             if left is not less than right
	 */
	public static long assertLt(long left, long right) throws AssertionException {
		if (left >= right)
			throw new AssertionException(" not true: " + left + " < " + right);
		return left;
	}

	/**
	 * assert that left is less than right (or throw an {@link AssertionException}). User can input an error message (String message).
	 * 
	 * @param left
	 *            left param of comparison
	 * @param right
	 *            right param of comparison
	 * @param message
	 *            User defined error message
	 * @return left
	 * @throws AssertionException
	 *             if left is not less than right
	 */
	public static long assertLt(long left, long right, String message) throws AssertionException {
		if (left >= right)
			throw new AssertionException(message + " not true: " + left + " < " + right);
		return left;
	}

	/**
	 * assert that left is equal to right (or throw an {@link AssertionException}).
	 * 
	 * @param <T>
	 *            type of left and return value
	 * @param left
	 *            left param of comparison
	 * @param right
	 *            right param of comparison
	 * @return left
	 * @throws AssertionException
	 *             if left and right are not equal
	 */
	public static long assertEq(long left, long right) throws AssertionException {
		if (left != right)
			throw new AssertionException(" not true: " + left + " == " + right);
		return left;
	}

	/**
	 * assert that left is equal to right (or throw an {@link AssertionException}). User can input an error message (String message).
	 * 
	 * @param left
	 *            left param of comparison
	 * @param right
	 *            right param of comparison
	 * @param message
	 *            User defined error message
	 * @return left
	 * @throws AssertionException
	 *             if left is not equal to right
	 */
	public static long assertEq(long left, long right, String message) throws AssertionException {
		if (left != right)
			throw new AssertionException(message + " not true: " + left + " == " + right);
		return left;
	}

	/**
	 * assert that left is not equal to right (or throw an {@link AssertionException}).
	 * 
	 * @param <T>
	 *            type of left and return value
	 * @param left
	 *            left param of comparison
	 * @param right
	 *            right param of comparison
	 * @return left
	 * @throws AssertionException
	 *             if left and right are not equal
	 */
	public static long assertNe(long left, long right) throws AssertionException {
		if (left == right)
			throw new AssertionException(" not true: " + left + " != " + right);
		return left;
	}

	/**
	 * assert that left is equal to right (or throw an {@link AssertionException}). User can input an error message (String message).
	 * 
	 * @param left
	 *            left param of comparison
	 * @param right
	 *            right param of comparison
	 * @param message
	 *            User defined error message
	 * @return left
	 * @throws AssertionException
	 *             if left is equal to right
	 */
	public static long assertNe(long left, long right, String message) throws AssertionException {
		if (left == right)
			throw new AssertionException(message + " not true: " + left + " != " + right);
		return left;
	}

	/**
	 * assert that left is greater than or equal to right (or throw an {@link AssertionException}). warning, comparison of floating point numbers need care taken
	 * 
	 * @param <T>
	 *            type of left and return value
	 * @param left
	 *            left param of comparison
	 * @param right
	 *            right param of comparison
	 * @return left
	 * @throws AssertionException
	 *             if left is not greater than or equal to right
	 */
	public static double assertGe(double left, double right) throws AssertionException {
		if (left < right)
			throw new AssertionException(" not true: " + left + " >= " + right);
		return left;
	}

	/**
	 * assert that left is greater than or equal to right (or throw an {@link AssertionException}). User can input an error message (String message).
	 * 
	 * @param left
	 *            left param of comparison
	 * @param right
	 *            right param of comparison
	 * @param message
	 *            User defined error message
	 * @return left
	 * @throws AssertionException
	 *             if left is not greater than or equal to right
	 */
	public static double assertGe(double left, double right, String message) throws AssertionException {
		if (left < right)
			throw new AssertionException(message + " not true: " + left + " >= " + right);
		return left;
	}

	/**
	 * assert that left is greater than right (or throw an {@link AssertionException}). warning, comparison of floating point numbers need care taken
	 * 
	 * @param <T>
	 *            type of left and return value
	 * @param left
	 *            left param of comparison
	 * @param right
	 *            right param of comparison
	 * @return left
	 * @throws AssertionException
	 *             if left is not greater than right
	 */
	public static double assertGt(double left, double right) throws AssertionException {
		if (left <= right)
			throw new AssertionException(" not true: " + left + " > " + right);
		return left;
	}

	/**
	 * assert that left is greater than right (or throw an {@link AssertionException}). User can input an error message (String message).
	 * 
	 * @param left
	 *            left param of comparison
	 * @param right
	 *            right param of comparison
	 * @param message
	 *            User defined error message
	 * @return left
	 * @throws AssertionException
	 *             if left is not greater than right
	 */
	public static double assertGt(double left, double right, String message) throws AssertionException {
		if (left <= right)
			throw new AssertionException(message + " not true: " + left + " > " + right);
		return left;
	}

	/**
	 * assert that left is less than or equal to right (or throw an {@link AssertionException}). warning, comparison of floating point numbers need care taken
	 * 
	 * @param <T>
	 *            type of left and return value
	 * @param left
	 *            left param of comparison
	 * @param right
	 *            right param of comparison
	 * @return left
	 * @throws AssertionException
	 *             if left is not less than or equal to right
	 */
	public static double assertLe(double left, double right) throws AssertionException {
		if (left > right)
			throw new AssertionException(" not true: " + left + " <= " + right);
		return left;
	}

	/**
	 * assert that left is less than or equal to right (or throw an {@link AssertionException}). User can input an error message (String message).
	 * 
	 * @param left
	 *            left param of comparison
	 * @param right
	 *            right param of comparison
	 * @param message
	 *            User defined error message
	 * @return left
	 * @throws AssertionException
	 *             if left is not less than or equal to right
	 */
	public static double assertLe(double left, double right, String message) throws AssertionException {
		if (left > right)
			throw new AssertionException(message + " not true: " + left + " <= " + right);
		return left;
	}

	/**
	 * assert that left is less than right (or throw an {@link AssertionException}). warning, comparison of floating point numbers need care taken
	 * 
	 * @param <T>
	 *            type of left and return value
	 * @param left
	 *            left param of comparison
	 * @param right
	 *            right param of comparison
	 * @return left
	 * @throws AssertionException
	 *             if left is not less than right
	 */
	public static double assertLt(double left, double right) throws AssertionException {
		if (left >= right)
			throw new AssertionException(" not true: " + left + " < " + right);
		return left;
	}

	/**
	 * assert that left is less than right (or throw an {@link AssertionException}). User can input an error message (String message).
	 * 
	 * @param left
	 *            left param of comparison
	 * @param right
	 *            right param of comparison
	 * @param message
	 *            User defined error message
	 * @return left
	 * @throws AssertionException
	 *             if left is not less than right
	 */
	public static double assertLt(double left, double right, String message) throws AssertionException {
		if (left >= right)
			throw new AssertionException(message + " not true: " + left + " < " + right);
		return left;
	}

	/**
	 * assert that left is equal to right (or throw an {@link AssertionException}). warning, comparison of floating point numbers need care taken
	 * 
	 * @param <T>
	 *            type of left and return value
	 * @param left
	 *            left param of comparison
	 * @param right
	 *            right param of comparison
	 * @return left
	 * @throws AssertionException
	 *             if left and right are not equal
	 */
	public static double assertEq(double left, double right) throws AssertionException {
		if (left != right && (left == left || right == right))
			throw new AssertionException(" not true: " + left + " == " + right);
		return left;
	}

	/**
	 * assert that left is equal to right (or throw an {@link AssertionException}). User can input an error message (String message).
	 * 
	 * @param left
	 *            left param of comparison
	 * @param right
	 *            right param of comparison
	 * @param message
	 *            User defined error message
	 * @return left
	 * @throws AssertionException
	 *             if left is not equal to right
	 */
	public static double assertEq(double left, double right, String message) throws AssertionException {
		if (left != right && (left == left || right == right))
			throw new AssertionException(message + " not true: " + left + " == " + right);
		return left;
	}

	/**
	 * assert that left is not equal to right (or throw an {@link AssertionException}). warning, comparison of floating point numbers need care taken
	 * 
	 * @param <T>
	 *            type of left and return value
	 * @param left
	 *            left param of comparison
	 * @param right
	 *            right param of comparison
	 * @return left
	 * @throws AssertionException
	 *             if left and right are not equal
	 */
	public static double assertNe(double left, double right) throws AssertionException {
		if (left == right || (left != left && right != right))
			throw new AssertionException(" not true: " + left + " != " + right);
		return left;
	}

	/**
	 * assert that left is not equal to right (or throw an {@link AssertionException}). User can input an error message (String message).
	 * 
	 * @param left
	 *            left param of comparison
	 * @param right
	 *            right param of comparison
	 * @param message
	 *            User defined error message
	 * @return left
	 * @throws AssertionException
	 *             if left is equal to right
	 */
	public static double assertNe(double left, double right, String message) throws AssertionException {
		if (left == right || (left != left && right != right))
			throw new AssertionException(message + " not true: " + left + " != " + right);
		return left;
	}

	/**
	 * assert that left is greater than or equal to right (or throw an {@link AssertionException}). warning, comparison of floating point numbers need care taken
	 * 
	 * @param <T>
	 *            type of left and return value
	 * @param left
	 *            left param of comparison
	 * @param right
	 *            right param of comparison
	 * @return left
	 * @throws AssertionException
	 *             if left is not greater than or equal to right
	 */
	public static float assertGe(float left, float right) throws AssertionException {
		if (left < right)
			throw new AssertionException(" not true: " + left + " >= " + right);
		return left;
	}

	/**
	 * assert that left is greater than or equal to right (or throw an {@link AssertionException}). User can input an error message (String message).
	 * 
	 * @param left
	 *            left param of comparison
	 * @param right
	 *            right param of comparison
	 * @param message
	 *            User defined error message
	 * @return left
	 * @throws AssertionException
	 *             if left is not greater than or equal to right
	 */
	public static float assertGe(float left, float right, String message) throws AssertionException {
		if (left < right)
			throw new AssertionException(message + " not true: " + left + " >= " + right);
		return left;
	}

	/**
	 * assert that left is greater than right (or throw an {@link AssertionException}). warning, comparison of floating point numbers need care taken
	 * 
	 * @param <T>
	 *            type of left and return value
	 * @param left
	 *            left param of comparison
	 * @param right
	 *            right param of comparison
	 * @return left
	 * @throws AssertionException
	 *             if left is not greater than right
	 */
	public static float assertGt(float left, float right) throws AssertionException {
		if (left <= right)
			throw new AssertionException(" not true: " + left + " > " + right);
		return left;
	}

	/**
	 * assert that left is greater than right (or throw an {@link AssertionException}). User can input an error message (String message).
	 * 
	 * @param left
	 *            left param of comparison
	 * @param right
	 *            right param of comparison
	 * @param message
	 *            User defined error message
	 * @return left
	 * @throws AssertionException
	 *             if left is not greater than right
	 */
	public static float assertGt(float left, float right, String message) throws AssertionException {
		if (left <= right)
			throw new AssertionException(message + " not true: " + left + " > " + right);
		return left;
	}

	/**
	 * assert that left is less than or equal to right (or throw an {@link AssertionException}). warning, comparison of floating point numbers need care taken
	 * 
	 * @param <T>
	 *            type of left and return value
	 * @param left
	 *            left param of comparison
	 * @param right
	 *            right param of comparison
	 * @return left
	 * @throws AssertionException
	 *             if left is not less than or equal to right
	 */
	public static float assertLe(float left, float right) throws AssertionException {
		if (left > right)
			throw new AssertionException(" not true: " + left + " <= " + right);
		return left;
	}

	/**
	 * assert that left is less than or equal to right (or throw an {@link AssertionException}). User can input an error message (String message).
	 * 
	 * @param left
	 *            left param of comparison
	 * @param right
	 *            right param of comparison
	 * @param message
	 *            User defined error message
	 * @return left
	 * @throws AssertionException
	 *             if left is not less than or equal to right
	 */
	public static float assertLe(float left, float right, String message) throws AssertionException {
		if (left > right)
			throw new AssertionException(message + " not true: " + left + " <= " + right);
		return left;
	}

	/**
	 * assert that left is less than right (or throw an {@link AssertionException}). warning, comparison of floating point numbers need care taken
	 * 
	 * @param <T>
	 *            type of left and return value
	 * @param left
	 *            left param of comparison
	 * @param right
	 *            right param of comparison
	 * @return left
	 * @throws AssertionException
	 *             if left is not less than right
	 */
	public static float assertLt(float left, float right) throws AssertionException {
		if (left >= right)
			throw new AssertionException(" not true: " + left + " < " + right);
		return left;
	}

	/**
	 * assert that left is less than right (or throw an {@link AssertionException}). User can input an error message (String message).
	 * 
	 * @param left
	 *            left param of comparison
	 * @param right
	 *            right param of comparison
	 * @param message
	 *            User defined error message
	 * @return left
	 * @throws AssertionException
	 *             if left is not less than right
	 */
	public static float assertLt(float left, float right, String message) throws AssertionException {
		if (left >= right)
			throw new AssertionException(message + " not true: " + left + " < " + right);
		return left;
	}

	/**
	 * assert that left is equal to right (or throw an {@link AssertionException}). warning, comparison of floating point numbers need care taken
	 * 
	 * @param <T>
	 *            type of left and return value
	 * @param left
	 *            left param of comparison
	 * @param right
	 *            right param of comparison
	 * @return left
	 * @throws AssertionException
	 *             if left and right are not equal
	 */
	public static float assertEq(float left, float right) throws AssertionException {
		if (left != right && (left == left || right == right))
			throw new AssertionException(" not true: " + left + " == " + right);
		return left;
	}

	/**
	 * assert that left is equal to right (or throw an {@link AssertionException}). User can input an error message (String message).
	 * 
	 * @param left
	 *            left param of comparison
	 * @param right
	 *            right param of comparison
	 * @param message
	 *            User defined error message
	 * @return left
	 * @throws AssertionException
	 *             if left is not equal to right
	 */
	public static float assertEq(float left, float right, String message) throws AssertionException {
		if (left != right && (left == left || right == right))
			throw new AssertionException(message + " not true: " + left + " == " + right);
		return left;
	}

	/**
	 * assert that left is not equal to right (or throw an {@link AssertionException}). warning, comparison of floating point numbers need care taken
	 * 
	 * @param <T>
	 *            type of left and return value
	 * @param left
	 *            left param of comparison
	 * @param right
	 *            right param of comparison
	 * @return left
	 * @throws AssertionException
	 *             if left and right are not equal
	 */
	public static float assertNe(float left, float right) throws AssertionException {
		if (left == right || (left != left && right != right))
			throw new AssertionException(" not true: " + left + " != " + right);
		return left;
	}

	/**
	 * assert that left is not equal to right (or throw an {@link AssertionException}). User can input an error message (String message).
	 * 
	 * @param left
	 *            left param of comparison
	 * @param right
	 *            right param of comparison
	 * @param message
	 *            User defined error message
	 * @return left
	 * @throws AssertionException
	 *             if left is equal to right
	 */
	public static float assertNe(float left, float right, String message) throws AssertionException {
		if (left == right || (left != left && right != right))
			throw new AssertionException(message + " not true: " + left + " != " + right);
		return left;
	}

	/**
	 * assert that left is not equal to right (or throw an {@link AssertionException}). see {@link #eq(Object, Object)} for details on comparison
	 * 
	 * @param <T>
	 *            type of left and return value
	 * @param left
	 *            left param of comparison
	 * @param right
	 *            right param of comparison
	 * @return left (may be null, but only possible if right is not null)
	 * @throws AssertionException
	 *             if left and right are not equal
	 */
	public static <T> T assertNe(T left, Object right) throws AssertionException {
		if (eq(left, right))
			throw new AssertionException(" not true: " + left + " != " + right);
		return left;
	}

	/**
	 * assert that left is not equal to right (or throw an {@link AssertionException}). see {@link #eq(Object, Object)} for details on comparison User can input an error message
	 * (String message).
	 * 
	 * @param <T>
	 *            type of left and return value
	 * @param left
	 *            left param of comparison
	 * @param right
	 *            right param of comparison
	 * @param message
	 *            User-defined error message
	 * @return left (may be null, but only possible if right is not null)
	 * @throws AssertionException
	 *             if left and right are not equal
	 */
	public static <T> T assertNe(T left, Object right, String message) throws AssertionException {
		if (eq(left, right))
			throw new AssertionException(message + " not true: " + left + " != " + right);
		return left;
	}

	/**
	 * assert the value is not null (or throw an {@link AssertionException})
	 * 
	 * @param <T>
	 *            the type of object
	 * @param object
	 *            reference to check for null
	 * @return object param for convenience(never null)
	 * @throws AssertionException
	 *             if object is null
	 */
	public static <T> T assertNotNull(T object) throws AssertionException {
		if (object == null)
			throw new AssertionException("is null");
		return object;
	}

	/**
	 * assert the value is not null (or throw an {@link AssertionException}) User can input an error message (String message).
	 * 
	 * @param <T>
	 *            the type of object
	 * @param object
	 *            reference to check for null
	 * @param message
	 *            User-defined error message
	 * @return object param for convenience(never null)
	 * @throws AssertionException
	 *             if object is null
	 */
	public static <T> T assertNotNull(T object, String message) throws AssertionException {
		if (object == null)
			throw new AssertionException(message + " is null");
		return object;
	}

	/**
	 * assert the value is null (or throw an {@link AssertionException})
	 * 
	 * @param <T>
	 *            the type of object
	 * @param object
	 *            reference to check for null
	 * @return object param for convenience(always null)
	 * @throws AssertionException
	 *             if object is null
	 */
	public static <T> T assertNull(T object) throws AssertionException {
		if (object != null)
			throw new AssertionException("not null: " + object);
		return null;
	}

	/**
	 * assert the value is null (or throw an {@link AssertionException}). User can input error message (String message).
	 * 
	 * @param <T>
	 *            the type of object
	 * @param object
	 *            reference to check for null
	 * @param message
	 *            User-defined error message
	 * @return object param for convenience(always null)
	 * @throws AssertionException
	 *             if object is null
	 */
	public static <T> T assertNull(T object, String message) throws AssertionException {
		if (object != null)
			throw new AssertionException(message + " not null: " + object);
		return null;
	}

	/**
	 * 
	 * Calls o.wait(), handles InterruptedException.
	 * 
	 * @param o
	 *            Input object
	 * @return true unless InterruptedException occurs, in which case false is returned.
	 */

	public static boolean wait(Object o) {
		try {
			o.wait();
			return true;
		} catch (InterruptedException e) {
			return false;
		}
	}

	/**
	 * 
	 * Calls o.wait(timeoutMs), handles InterruptedException.
	 * 
	 * @param o
	 *            object to call wait on (must be synchronized on)
	 * @param timeoutMs
	 *            timeout duration
	 * @return true if it timed out, false if interrupt
	 */
	public static boolean wait(Object o, long timeoutMs) {
		try {
			o.wait(timeoutMs);
			return true;
		} catch (InterruptedException e) {
			return false;
		}
	}

	/**
	 * 
	 * Call wait on an object from time start to time (start + timeoutMs).
	 * 
	 * @param o
	 *            object to call wait on (must be synchronized on)
	 * @param start
	 *            startTime time in millis when wait was first called
	 * @param timeoutMs
	 *            timeout duration in millis when wait was first called
	 * @return false if time has already expired, else true after wait has concluded
	 */
	public static boolean waitFromStart(Object o, long start, long timeoutMs) {
		long remaining = start + timeoutMs - EH.currentTimeMillis();
		if (remaining <= 0)
			return false;
		wait(o, remaining);
		return true;
	}

	/**
	 * 
	 * Call wait on an object from time start to time (start + timeoutMs).
	 * 
	 * @param o
	 *            object to call wait on (must be synchronized on)
	 * @param start
	 *            startTime time in millis when wait was first called
	 * @param timeoutMs
	 *            timeout duration in millis when wait was first called
	 * @param c
	 *            Clock from which the current time is retrieved
	 * @return false if time has already expired, else true after wait has concluded
	 */
	public static boolean waitFromStart(Object o, long start, long timeoutMs, Clock c) {
		long remaining = start + timeoutMs - c.getNow();
		if (remaining <= 0)
			return false;
		wait(o, remaining);
		return true;
	}

	/**
	 * 
	 * Make thread sleep from time start to time (start + timeoutMs).
	 * 
	 * @param start
	 *            Start time
	 * @param timeoutMs
	 *            Sleep duration
	 * @param c
	 *            Clock from which the current time is retrieved
	 * @return false if time has already expired, else true after sleep has concluded
	 */

	public static boolean sleepFromStart(long start, long timeoutMs, Clock c) {
		long remaining = start + timeoutMs - c.getNow();
		if (remaining <= 0)
			return false;
		sleep(remaining);
		return true;
	}

	/**
	 * 
	 * Calls o_.notify after synchronizing o_
	 * 
	 * @param o_
	 *            Object on which notify() is called
	 */

	public static void notify(Object o_) {
		synchronized (o_) {
			o_.notify();
		}
	}

	/**
	 * 
	 * Sorts the specified array of objects according to the order induced by the specified comparator. Returns the sorted array of objects.
	 * 
	 * @param objects
	 *            Input array of objects of class T
	 * @param t
	 *            Input comparator of class T
	 * @return Sorted version of input array
	 */

	public static <T> T[] sort(T[] objects, Comparator<T> t) {
		Arrays.sort(objects, t);
		return objects;
	}

	/**
	 * 
	 * Checks to see if item is in the specified array of elements.
	 * 
	 * @param item
	 *            item to be checked
	 * @param elements
	 *            Array of elements to be checked against item
	 * @return true if item is in elements, false otherwise
	 */

	public static <T> boolean in(T item, T... elements) {
		for (int i = 0; i < elements.length; i++)
			if (eq(item, elements[i]))
				return true;
		return false;
	}

	/**
	 * 
	 * Checks to see if item exists in the specified array of booleans.
	 * 
	 * @param item
	 *            item to be checked
	 * @param elements
	 *            Array of booleans to be checked against item
	 * @return true if item is in elements, false otherwise
	 */

	public static boolean inBooleans(boolean item, boolean... elements) {
		for (int i = 0; i < elements.length; i++)
			if (item == elements[i])
				return true;
		return false;
	}

	/**
	 * 
	 * Checks to see if item exists in the specified array of longs.
	 * 
	 * @param item
	 *            item to be checked
	 * @param elements
	 *            Array of longs to be checked against item
	 * @return true if item is in elements, false otherwise
	 */

	public static boolean inLongs(long item, long... elements) {
		for (int i = 0; i < elements.length; i++)
			if (item == elements[i])
				return true;
		return false;
	}

	/**
	 * 
	 * Checks to see if item exists in the specified array of ints.
	 * 
	 * @param item
	 *            item to be checked
	 * @param elements
	 *            Array of ints to be checked against item
	 * @return true if item is in elements, false otherwise
	 */

	public static boolean inInts(int item, int... elements) {
		for (int i = 0; i < elements.length; i++)
			if (item == elements[i])
				return true;
		return false;
	}

	/**
	 * 
	 * Checks to see if item exists in the specified array of shorts.
	 * 
	 * @param item
	 *            item to be checked
	 * @param elements
	 *            Array of shorts to be checked against item
	 * @return true if item is in elements, false otherwise
	 */

	public static boolean inShorts(short item, short... elements) {
		for (int i = 0; i < elements.length; i++)
			if (item == elements[i])
				return true;
		return false;
	}

	/**
	 * 
	 * Checks to see if item exists in the specified array of chars.
	 * 
	 * @param item
	 *            item to be checked
	 * @param elements
	 *            Array of chars to be checked against item
	 * @return true if item is in elements, false otherwise
	 */

	public static boolean inChars(char item, char... elements) {
		for (int i = 0; i < elements.length; i++)
			if (item == elements[i])
				return true;
		return false;
	}

	/**
	 * 
	 * Checks to see if item exists in the specified array of bytes.
	 * 
	 * @param item
	 *            item to be checked
	 * @param elements
	 *            Array of bytes to be checked against item
	 * @return true if item is in elements, false otherwise
	 */

	public static boolean inBytes(byte item, byte... elements) {
		for (int i = 0; i < elements.length; i++)
			if (item == elements[i])
				return true;
		return false;
	}

	/**
	 * 
	 * Checks to see if item exists in the specified array of floats.
	 * 
	 * @param item
	 *            item to be checked
	 * @param elements
	 *            Array of floats to be checked against item
	 * @return true if item is in elements, false otherwise
	 */

	public static boolean inFloats(float item, float... elements) {
		for (int i = 0; i < elements.length; i++)
			if (item == elements[i])
				return true;
		return false;
	}

	/**
	 * 
	 * Checks to see if item exists in the specified array of doubles.
	 * 
	 * @param item
	 *            item to be checked
	 * @param elements
	 *            Array of doubles to be checked against item
	 * @return true if item is in elements, false otherwise
	 */

	public static boolean inDoubles(double item, double... elements) {
		for (int i = 0; i < elements.length; i++)
			if (item == elements[i])
				return true;
		return false;
	}

	/**
	 * 
	 * Locks and returns target, as long as target is not null.
	 * 
	 * @param target
	 *            Input object to be locked
	 * @return If target != null, return locked target. Otherwise, return original target.
	 */

	static public <T extends Lockable> T lock(T target) {
		if (target != null)
			target.lock();
		return target;
	}

	/**
	 * 
	 * Returns the simple name of the input class.
	 * 
	 * @param o
	 *            Input class
	 * @return Simple name of o. If o == null, return null.
	 */

	public static String getSimpleName(Class<?> o) {
		return o == null ? null : o.getSimpleName();
	}

	/**
	 * 
	 * Returns the name of the input class.
	 * 
	 * @param o
	 *            Input class
	 * @return Name of o. If o == null, return null.
	 */

	public static String getName(Class<?> o) {
		return o == null ? null : o.getName();
	}

	/**
	 * 
	 * Returns the simple name of the input class.
	 * 
	 * @param o
	 *            Input class
	 * @return Simple name of o. If o == null, return null.
	 */

	public static String getSimpleClassName(Object o) {
		return o == null ? null : o.getClass().getSimpleName();
	}

	/**
	 * 
	 * Returns the name of the class of the input object o.
	 * 
	 * @param o
	 *            Input object
	 * @return Name of class of object o. If o is null, returns null.
	 */

	public static String getClassName(Object o) {
		return getName(getClass(o));
	}

	/**
	 * 
	 * Checks to see if left is equal to right within a specified tolerance, delta. Throws NullPointerException if any of the inputs are null.
	 * 
	 * @param left
	 *            Object to be compared to right
	 * @param right
	 *            Object to be compared to left
	 * @param delta
	 *            Tolerance for comparison
	 * @return Return true if left = right +/- delta.
	 */

	public static boolean eq(double left, double right, double delta) {
		return isBetween(left, right - delta, right + delta);
	}

	/**
	 * 
	 * Returns the size of the given class.
	 * 
	 * @param type
	 *            Input class type
	 * @return If type can be converted to Prim type, return the size of the corresponding Prim type. Otherwise, return an int value of 4 as the default size.
	 */

	public static int getSize(Class<?> type) {
		Prim prim = PRIMITIVE_CLASS_TO_PRIM.get(type);
		return prim == null ? OBJECT_REF_SIZE : prim.size;
	}

	/**
	 * 
	 * Checks to see if value is not between min and max (inclusive).
	 * 
	 * @param value
	 *            Test value
	 * @param min
	 *            Lower bound
	 * @param max
	 *            Upper bound
	 * @return Return true if value is not in the range [min, max]
	 */

	public static <T extends Comparable<T>> boolean isntBetween(T value, T min, T max) {
		return !isBetween(value, min, max);
	}

	/**
	 * 
	 * Checks to see if value is not between min and max (inclusive).
	 * 
	 * @param value
	 *            Test value
	 * @param min
	 *            Lower bound
	 * @param max
	 *            Upper bound
	 * @return Return true if (value < min || value > max)
	 */

	public static boolean isntBetween(float value, float min, float max) {
		return !isBetween(value, min, max);
	}

	/**
	 * 
	 * Checks to see if value is not between min and max (inclusive).
	 * 
	 * @param value
	 *            Test value
	 * @param min
	 *            Lower bound
	 * @param max
	 *            Upper bound
	 * @return Return true if (value < min || value > max)
	 */

	public static boolean isntBetween(double value, double min, double max) {
		return !isBetween(value, min, max);
	}

	/**
	 * 
	 * Checks to see if value is not between min and max (inclusive).
	 * 
	 * @param value
	 *            Test value
	 * @param min
	 *            Lower bound
	 * @param max
	 *            Upper bound
	 * @return Return true if (value < min || value > max)
	 */

	public static boolean isntBetween(long value, long min, long max) {
		return !isBetween(value, min, max);
	}

	/**
	 * 
	 * Checks to see if value is not between min and max (inclusive).
	 * 
	 * @param value
	 *            Test value
	 * @param min
	 *            Lower bound
	 * @param max
	 *            Upper bound
	 * @return Return true if (value < min || value > max)
	 */

	public static boolean isntBetween(int value, int min, int max) {
		return !isBetween(value, min, max);
	}

	/**
	 * 
	 * Checks to see if value is not between min and max (inclusive).
	 * 
	 * @param value
	 *            Test value
	 * @param min
	 *            Lower bound
	 * @param max
	 *            Upper bound
	 * @return Return true if (value < min || value > max)
	 */

	public static boolean isntBetween(short value, short min, short max) {
		return !isBetween(value, min, max);
	}

	/**
	 * 
	 * Checks to see if value is not between min and max (inclusive).
	 * 
	 * @param value
	 *            Test value
	 * @param min
	 *            Lower bound
	 * @param max
	 *            Upper bound
	 * @return Return true if (value < min || value > max)
	 */

	public static boolean isntBetween(byte value, byte min, byte max) {
		return !isBetween(value, min, max);
	}

	/**
	 * 
	 * Checks to see if value is not between min and max (inclusive).
	 * 
	 * @param value
	 *            Test value
	 * @param min
	 *            Lower bound
	 * @param max
	 *            Upper bound
	 * @return Return true if (value < min || value > max)
	 */

	public static boolean isntBetween(char value, char min, char max) {
		return !isBetween(value, min, max);
	}

	/**
	 * 
	 * Associates Throwable exception with Exception cause.
	 * 
	 * @param exception
	 *            Object of class E. E extends Throwable
	 * @param cause
	 *            Object of class Exception
	 * @return exception
	 */

	public static <E extends Throwable> E setCause(E exception, Exception cause) {
		exception.initCause(cause);
		return exception;
	}

	/**
	 * 
	 * Given an Object o, returns false.
	 * 
	 * @param o
	 *            Input object
	 * @return false
	 */

	public static boolean FALSE(Object o) {
		return false;
	}

	/**
	 * 
	 * Given an Object o, returns null.
	 * 
	 * @param o
	 *            Input object
	 * @return null
	 */

	public static <T> T NULL(Object o) {
		return null;
	}

	/**
	 * 
	 * Given an Object o, returns true.
	 * 
	 * @param o
	 *            Input object
	 * @return true
	 */

	public static boolean TRUE(Object o) {
		return true;
	}

	/**
	 * 
	 * Checks to see if Throwable e is an instance of the class represented by typeToFind. If it is not, e is updated. The same check is repeatedly performed until either e is
	 * found to be an instance of the class represented by typeToFind, or e == null. If e is null, the function returns null.
	 * 
	 * @param e
	 *            Object to check
	 * @param typeToFind
	 *            Class to check against
	 * @return If e is found to be an instance of the class represented by typeToFind, return e cast as an object of this class. If e is found to be null, return null.
	 */

	public static <T extends Throwable> T findInnerException(Throwable e, Class<T> typeToFind) {
		while (e != null)
			if (typeToFind.isInstance(e))
				return typeToFind.cast(e);
			else
				e = e.getCause();
		return null;

	}

	/**
	 * 
	 * Iterates through all objects in source. Adds to sink a version of each object in source which has been cast as type to. Returns sink.
	 * <P>
	 * If the required option is true, then objects in source may not be null. Otherwise, a {@link NullPointerException} is thrown.
	 * 
	 * @param source
	 *            Iterable through collection of objects to cast to class to
	 * @param sink
	 *            Collection used to hold cast objects
	 * @param to
	 *            type of class to cast to
	 * @param required
	 *            If false, elements in source may be null
	 * @param <T>
	 *            type of iterable through source objects
	 * @param <V>
	 *            type of class to cast to
	 * @param <C>
	 *            type of sink collection
	 * @return sink
	 * 
	 */

	public static <T, V, C extends Collection<V>> C castAll(Iterable<T> source, C sink, Class<V> to, boolean required) {
		Caster<V> caster = getCaster(to);
		for (T i : source)
			sink.add(caster.cast(i, required));
		return sink;
	}

	/**
	 * 
	 * Iterates through all objects in source. Adds to a sink collection a version of each object in source which has been cast as type to. Returns the sink collection.
	 * <P>
	 * If the required option is true, then objects in source may not be null. Otherwise, a {@link NullPointerException} is thrown.
	 * 
	 * @param source
	 *            Iterable through collection of objects to cast to class to
	 * @param to
	 *            type of class to cast to
	 * @param required
	 *            If false, elements in source may be null
	 * @param <T>
	 *            type of iterable through source objects
	 * @param <V>
	 *            type of class to cast to
	 * @return sink collection of cast objects
	 * 
	 */

	public static <T, V> List<V> castAll(Iterable<T> source, Class<V> to, boolean required) {
		Caster<V> caster = getCaster(to);
		List<V> sink = new ArrayList<V>();
		for (T i : source)
			sink.add(caster.cast(i, required));
		return sink;
	}
	public static Caster<?>[] getAllCasters(Class<?>[] types) {
		int length = types.length;
		Caster<?>[] output = new Caster<?>[length];
		for (int i = 0; i < length; i++) {
			if (types[i] == null)
				output[i] = Caster_Object.INSTANCE;
			else
				output[i] = getCaster(types[i]);
		}
		return output;
	}

	public static List<Caster<?>> getAllCasters(List<Class> types) {
		int length = types.size();
		List<Caster<?>> output = new ArrayList<Caster<?>>();
		for (int i = 0; i < length; i++) {
			output.add(getCaster(types.get(i)));
		}
		return output;
	}

	private static final Integer[] CACHE_POSITIVE_INTEGER = new Integer[0x10000];
	private static final Integer[] CACHE_NEGATIVE_INTEGER = new Integer[0x100];
	private static final Long[] CACHE_POSITIVE_LONG = new Long[0x10000];
	private static final Long[] CACHE_NEGATIVE_LONG = new Long[0x100];
	private static final Short[] CACHE_SHORT = new Short[0x10000];
	private static final Character[] CACHE_CHAR = new Character[0x10000];
	private static int FLOAT_CACHE_SIZE = 0x1fff;//8191 = 0b1_1111_1111_1111
	private static Float[] CACHE_FLOAT = new Float[FLOAT_CACHE_SIZE + 1];
	private static Double[] CACHE_DOUBLE = new Double[FLOAT_CACHE_SIZE + 1];

	public static final int CACHE_MAX_INTEGER = CACHE_POSITIVE_INTEGER.length;
	public static final int CACHE_MIN_INTEGER = CACHE_NEGATIVE_INTEGER.length;
	public static final int CACHE_MAX_LONG = CACHE_POSITIVE_LONG.length;
	public static final int CACHE_MIN_LONG = CACHE_NEGATIVE_LONG.length;

	public static final int MIN_FLOAT_SIZE = -0x100;
	public static final int MAX_FLOAT_SIZE = 0x10000;

	public static final int MIN_FLOAT_WITH_PRECISION_SIZE = -100;
	public static final int MAX_FLOAT_WITH_PRECISION_SIZE = 1000;
	public static final int FLOAT_PRECISION = 100;

	static {
		for (int i = 0; i < CACHE_MAX_INTEGER; i++)
			CACHE_POSITIVE_INTEGER[i] = Integer.valueOf(i);
		for (int i = 0; i < CACHE_MIN_INTEGER; i++)
			CACHE_NEGATIVE_INTEGER[i] = Integer.valueOf(-i);
		for (int i = 0; i < CACHE_MAX_LONG; i++)
			CACHE_POSITIVE_LONG[i] = Long.valueOf(i);
		for (int i = 0; i < CACHE_MIN_LONG; i++)
			CACHE_NEGATIVE_LONG[i] = Long.valueOf(-i);

		for (int i = Short.MIN_VALUE; i <= Short.MAX_VALUE; i++)
			CACHE_SHORT[i - Short.MIN_VALUE] = Short.valueOf((short) i);

		for (int i = Character.MIN_VALUE; i <= Character.MAX_VALUE; i++)
			CACHE_CHAR[i] = Character.valueOf((char) i);
	}

	/**
	 * 
	 * Given a boolean i, returns a Boolean instance representing the specified boolean value.
	 * 
	 * @param i
	 *            boolean input value
	 * @return Boolean.TRUE if i == true. <BR>
	 *         Boolean.FALSE if i == false.
	 */

	public static Boolean valueOf(boolean i) {
		return Boolean.valueOf(i);
	}

	/**
	 * 
	 * Given a float i, returns a Float instance representing the specified float value.
	 * 
	 * @param i
	 *            float input value
	 * @return a Float instance representing i
	 */

	public static Float valueOf(float i) {
		int n = Float.floatToRawIntBits(i);
		n = (n ^ n >>> 16) & FLOAT_CACHE_SIZE;
		Float r = CACHE_FLOAT[n];
		if (r != null) {
			if (r.floatValue() == i)
				return r;
			return Float.valueOf(i);
		} else {
			return CACHE_FLOAT[n] = Float.valueOf(i);
		}
	}

	/**
	 * 
	 * Given a double i, returns a Double instance representing the specified double value.
	 * 
	 * @param i
	 *            double input value
	 * @return a Double instance representing i
	 */
	public static Double valueOf(double i) {
		long l = Double.doubleToRawLongBits(i);
		int n = (int) (l ^ (l >>> 32));
		n = (n ^ n >>> 16) & FLOAT_CACHE_SIZE;
		Double r = CACHE_DOUBLE[(int) n];
		if (r != null) {
			if (r.doubleValue() == i)
				return r;
			return Double.valueOf(i);
		} else {
			return CACHE_DOUBLE[n] = Double.valueOf(i);
		}
	}

	/**
	 * 
	 * Given a long i, returns a Long instance representing the specified long value.
	 * 
	 * @param i
	 *            long input value
	 * @return a Long instance representing i
	 */
	public static Long valueOf(long i) {
		if (i >= 0)
			return i < CACHE_MAX_LONG ? CACHE_POSITIVE_LONG[(int) i] : Long.valueOf(i);
		else
			return i > -CACHE_MIN_LONG ? CACHE_NEGATIVE_LONG[(int) -i] : Long.valueOf(i);
	}

	/**
	 * 
	 * Given an int i, returns an Integer instance representing the specified int value.
	 * 
	 * @param i
	 *            int input value
	 * @return a Integer instance representing i
	 */
	public static Integer valueOf(int i) {
		if (i >= 0)
			return i < CACHE_MAX_INTEGER ? CACHE_POSITIVE_INTEGER[i] : Integer.valueOf(i);
		else
			return i > -CACHE_MIN_INTEGER ? CACHE_NEGATIVE_INTEGER[-i] : Integer.valueOf(i);
	}

	/**
	 * 
	 * Given a short i, returns a Short instance representing the specified short value.
	 * 
	 * @param i
	 *            short input value
	 * @return a Short instance representing i
	 */
	public static Short valueOf(short i) {
		return CACHE_SHORT[i - Short.MIN_VALUE];
	}

	/**
	 * 
	 * Given a char i, returns a Character instance representing the specified char value.
	 * 
	 * @param i
	 *            char input value
	 * @return a Character instance representing i
	 */
	public static Character valueOf(char i) {
		return CACHE_CHAR[i];
	}

	/**
	 * 
	 * Given a byte i, returns a Byte instance representing the specified byte value.
	 * 
	 * @param i
	 *            byte input value
	 * @return a Byte instance representing i
	 */
	public static Byte valueOf(byte i) {
		return Byte.valueOf(i);
	}

	/**
	 * 
	 * Casts obj as clazz if obj is an instance of clazz. Otherwise, return null.
	 * 
	 * @param obj
	 *            Object to be checked
	 * @param clazz
	 *            Class to be checked against
	 * @return If obj is an instance of clazz, cast obj as clazz and return obj. <BR>
	 *         If obj is not an instance of clazz, return null.
	 */

	public static <T> T castIfInstance(Object obj, Class<T> clazz) {
		if (clazz.isInstance(obj))
			return clazz.cast(obj);
		return null;
	}

	static public <T> T assertInstanceOf(Object o, Class<T> type) {
		if (type.isInstance(o))
			return type.cast(o);
		throw new AssertionException(getClassName(o) + " not instanceof " + getName(type));
	}

	public static Class<?> getWidestIgnoreNull(Iterable<?> list) {
		Iterator<?> i = list.iterator();
		Class<?> r = null;
		while (i.hasNext())
			r = getWidestIgnoreNull(r, getClass(i.next()));
		return r;
	}

	private static final byte[] NULL_BYTES = new byte[] { -1 };
	private static final byte[] FALSE_BYTES = new byte[] { 0 };
	private static final byte[] TRUE_BYTES = new byte[] { 1 };

	public static byte[] toBytes(Object object) {
		if (object == null)
			return NULL_BYTES;
		else if (object instanceof Bytes)
			return ((Bytes) object).getBytes();
		else if (object instanceof CharSequence)
			return object.toString().getBytes();
		else if (object instanceof Number) {
			if (object instanceof DateMillis)
				return ByteHelper.asBytes(((DateMillis) object).getDate());
			else if (object instanceof DateNanos)
				return ByteHelper.asBytes(((DateMillis) object).getDate());
			byte[] r = ByteHelper.asBytes((Number) object);
			if (r != null)
				return r;
		} else if (object instanceof Boolean)
			return (Boolean) object ? TRUE_BYTES : FALSE_BYTES;
		else if (object instanceof Character)
			return ByteHelper.asBytes((Character) object);
		return ByteHelper.asBytes(object.hashCode());
	}

	public static <T extends Comparable<T>> T max(Iterable<T> values) {
		Iterator<T> it = values.iterator();
		if (!it.hasNext())
			return null;
		T r = it.next();
		while (it.hasNext())
			r = max(it.next(), r);
		return r;
	}
	public static <T extends Comparable<T>> T min(Iterable<T> values) {
		Iterator<T> it = values.iterator();
		if (!it.hasNext())
			return null;
		T r = it.next();
		while (it.hasNext())
			r = min(it.next(), r);
		return r;
	}
	/**
	 * Returns A \ B (Set of all elements which are in A but not in B)
	 * 
	 * @param A
	 * @param B
	 * @return
	 */
	public static Set<?> setDifference(Set<?> A, Set<?> B) {
		Set<Object> output = new HashSet<Object>();
		for (Object e : A) {
			if (!B.contains(e)) {
				output.add(e);
			}
		}
		return output;
	}
	public static boolean isWholeNumber(Class<? extends Number> type) {
		if (type.isPrimitive())
			return type == int.class || type == long.class || type == byte.class || type == short.class;
		return type == Integer.class || type == Long.class || type == Byte.class || type == Short.class || type == BigInteger.class || type == DateMillis.class
				|| type == DateNanos.class;
	}
	public static <T> T minAvoidNull(Comparator<T> pm, T a, T b) {
		if (a == null)
			return b;
		else if (b == null)
			return a;
		return pm.compare(a, b) <= 0 ? a : b;
	}
	public static <T> T maxAvoidNull(Comparator<T> pm, T a, T b) {
		if (a == null)
			return b;
		else if (b == null)
			return a;
		return pm.compare(a, b) >= 0 ? a : b;
	}
	public static boolean isFloat(Class<?> type) {
		return type == Float.class || type == float.class || type == Double.class || type == double.class || type == BigDecimal.class;
	}

	public static void main(String args[]) {
		//		System.out.println(System.identityHashCode(valueOf(123d)));
		//		System.out.println(System.identityHashCode(valueOf(123d)));
		int check = 0;
		double x = 0;
		for (int n = 0; n < 100; n++) {
			long str = System.currentTimeMillis();
			for (double i = 0; i < 1000000; i += .07531) {
				Float f = valueOf((float) i);
				Double d = valueOf(i);
				assertEq((float) f, (float) i);
				assertEq((double) d, i);
				x = x + f;
				x = x + d;
				check++;
			}
			long end = System.currentTimeMillis();
			System.out.println(end - str);
		}

		System.out.println(x);
		System.out.println(check);
		int fempty = 0;
		int dempty = 0;
		for (int i = 0; i < FLOAT_CACHE_SIZE; i++) {
			if (CACHE_FLOAT[i] == null)
				fempty++;
			if (CACHE_DOUBLE[i] == null)
				dempty++;
		}

		System.out.println(fempty);
		System.out.println(dempty);

	}

	public static Prim[] getPrims() {
		return OH.PRIMS;
	}
}
