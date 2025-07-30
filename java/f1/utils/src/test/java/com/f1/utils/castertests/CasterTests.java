package com.f1.utils.castertests;

import java.io.File;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JComboBox;

import org.junit.Ignore;
import org.junit.Test;

import com.f1.base.DateMillis;
import com.f1.base.ValuedEnum;
import com.f1.utils.BasicFixPoint;
import com.f1.utils.CH;
import com.f1.utils.FixPoint;
import com.f1.utils.OH;

import junit.framework.Assert;

public class CasterTests {

	private static final String STRING_HEX = "0x123";
	private static final String STRING_POS_INFINITY = "+infinity";
	private static final String STRING_NEG_INFINITY = "-infinity";
	private static final String STRING_NAN = "nan";
	private static final String STRING_HEX_NEG = "-0x15";
	private static final String STRING_HEX_POS = "+0x15";
	private static final String STRING_FLOAT = "15f";
	private static final String STRING_DOUBLE = "15d";
	private static final String STRING_LONG = "15l";
	private static final String STRING_INT = "15i";
	private static final String STRING_WORD = "word";
	private static final String STRING_TRUE = "true";
	private static final String STRING_FALSE = "false";
	private static final String STRING_NULL = "null";
	private static final String CHAR_SEQUENCE_NULL = "null";

	private static final byte PRIM_BYTE = 23;
	private static final short PRIM_SHORT = 23;
	private static final int PRIM_INT = 23;
	private static final long PRIM_LONG = 23L;
	private static final float PRIM_FLOAT = 23.45f;
	private static final double PRIM_DOUBLE = 23.45d;
	private static final boolean PRIM_BOOLEAN_TRUE = true;
	private static final boolean PRIM_BOOLEAN_FALSE = false;
	private static final char PRIM_CHAR = 'c';
	private static final Object OBJECT = new Object();
	private static final File FILE = new File("pathname");
	private static final FixPoint FIXPOINT = new BasicFixPoint(12345L);
	private static final BigInteger BIGINTEGER = new BigInteger("5000000");
	private static final BigDecimal BIGDECIMAL = new BigDecimal(234.23654);
	private static final DateMillis DATE_MILLIS = new DateMillis(567235734L);
	private static final StringBuilder STRINGBUILDER1 = new StringBuilder();
	private static final StringBuilder STRINGBUILDER2 = new StringBuilder();
	private static final Number NUMBER = new Integer(5);
	private static final Map<String, Integer> MAP = CH.m("x", 1, "y", 2, "z", 3);
	private static final List<Integer> LIST = CH.l(1, 2, 3);

	static {
		STRINGBUILDER2.append("xyz");
	}

	private static final byte[] ARRAY_PRIM_BYTE = { 1, 2, 3 };
	private static final short[] ARRAY_PRIM_SHORT = { 1, 2, 3 };
	private static final int[] ARRAY_PRIM_INT = { 1, 2, 3 };
	private static final long[] ARRAY_PRIM_LONG = { 1, 2, 3 };
	private static final float[] ARRAY_PRIM_FLOAT = { 1, 2, 3 };
	private static final double[] ARRAY_PRIM_DOUBLE = { 1, 2, 3 };
	private static final boolean[] ARRAY_PRIM_BOOLEAN = { true, false, true };
	private static final char[] ARRAY_PRIM_CHAR = { 'x', 'y', 'z' };
	private static final Byte[] ARRAY_BOXED_BYTE = { 1, 2, 3 };
	private static final Short[] ARRAY_BOXED_SHORT = { 1, 2, 3 };
	private static final Integer[] ARRAY_BOXED_INT = { 1, 2, 3 };
	private static final Long[] ARRAY_BOXED_LONG = { 1L, 2L, 3L };
	private static final Float[] ARRAY_BOXED_FLOAT = { 1f, 2f, 3f };
	private static final Double[] ARRAY_BOXED_DOUBLE = { 1d, 2D, 3D };
	private static final Boolean[] ARRAY_BOXED_BOOLEAN = { true, false, true };
	private static final Character[] ARRAY_BOXED_CHAR = { 'x', 'y', 'z' };
	private static final String[] ARRAY_STRING = { "x", "y", "z" };
	private static final Object[] ARRAY_OBJECT = { new Object(), "y", 22.0, new Object() };
	private static final Primitives[] ARRAY_PRIMITIVES = { Primitives.PRIM_BOOLEAN_FALSE, Primitives.PRIM_BYTE, Primitives.PRIM_CHAR };
	private static final StringBuilder[] ARRAY_STRINGBUILDER = { new StringBuilder(), STRINGBUILDER1, STRINGBUILDER2 };
	private static final ValuedEnumTest[] ARRAY_VALUED_ENUM_TEST = { ValuedEnumTest.X, ValuedEnumTest.Y, ValuedEnumTest.Z };
	private static final File[] ARRAY_FILE = { new File("x"), new File("y"), new File("z") };
	private static final DateMillis[] ARRAY_DATE_MILLIS = { new DateMillis(123L), new DateMillis(456L), new DateMillis(100000000000000L) };
	private static final BasicFixPoint[] ARRAY_BASICFIXPOINT = { new BasicFixPoint(123L), new BasicFixPoint(456L), new BasicFixPoint(100000000000000L) };
	private static final Class<?>[] ARRAY_CLASS = { Boolean.class, Integer.class, StringBuilder.class };
	private static final BigInteger[] ARRAY_BIGINTEGER = { BigInteger.TEN, BigInteger.ZERO, BigInteger.ONE, new BigInteger("3456") };
	private static final BigDecimal[] ARRAY_DECIMAL = { new BigDecimal(123L), new BigDecimal(456L), new BigDecimal(100000000000000L) };
	private static final Number[] ARRAY_NUMBER = { new Integer(123), new Double(4.5), new Byte((byte) 10) };

	private static final List<Object> PRIM_OBJECTS = new ArrayList<Object>();
	static {
		PRIM_OBJECTS.add(PRIM_BYTE);
		PRIM_OBJECTS.add(PRIM_SHORT);
		PRIM_OBJECTS.add(PRIM_INT);
		PRIM_OBJECTS.add(PRIM_LONG);
		PRIM_OBJECTS.add(PRIM_FLOAT);
		PRIM_OBJECTS.add(PRIM_DOUBLE);
		PRIM_OBJECTS.add(PRIM_BOOLEAN_TRUE);
		PRIM_OBJECTS.add(PRIM_BOOLEAN_FALSE);
		PRIM_OBJECTS.add(PRIM_CHAR);
	}
	private static final List<Number> NUMBER_OBJECTS = new ArrayList<Number>();
	static {
		NUMBER_OBJECTS.add(new Byte(PRIM_BYTE));
		NUMBER_OBJECTS.add(new Short(PRIM_SHORT));
		NUMBER_OBJECTS.add(new Integer(PRIM_INT));
		NUMBER_OBJECTS.add(new Long(PRIM_LONG));
		NUMBER_OBJECTS.add(new Float(PRIM_FLOAT));
		NUMBER_OBJECTS.add(new Double(PRIM_DOUBLE));
		NUMBER_OBJECTS.add(FIXPOINT);
		NUMBER_OBJECTS.add(BIGINTEGER);
		NUMBER_OBJECTS.add(BigInteger.ONE);
		NUMBER_OBJECTS.add(BigInteger.TEN);
		NUMBER_OBJECTS.add(BigInteger.ZERO);
		NUMBER_OBJECTS.add(BIGDECIMAL);
		NUMBER_OBJECTS.add(BigDecimal.ONE);
		NUMBER_OBJECTS.add(BigDecimal.TEN);
		NUMBER_OBJECTS.add(BigDecimal.ZERO);
		NUMBER_OBJECTS.add(BigDecimal.ROUND_CEILING);
		NUMBER_OBJECTS.add(BigDecimal.ROUND_DOWN);
		NUMBER_OBJECTS.add(BigDecimal.ROUND_FLOOR);
		NUMBER_OBJECTS.add(BigDecimal.ROUND_HALF_DOWN);
		NUMBER_OBJECTS.add(BigDecimal.ROUND_HALF_EVEN);
		NUMBER_OBJECTS.add(BigDecimal.ROUND_HALF_UP);
		NUMBER_OBJECTS.add(BigDecimal.ROUND_UNNECESSARY);
		NUMBER_OBJECTS.add(BigDecimal.ROUND_UP);
		NUMBER_OBJECTS.add(DATE_MILLIS);
		NUMBER_OBJECTS.add(NUMBER);
	}
	private static final List<CharSequence> STRING_OBJECTS = new ArrayList<CharSequence>();
	static {
		STRING_OBJECTS.add(STRING_WORD);
		STRING_OBJECTS.add(STRING_HEX);
		STRING_OBJECTS.add(STRING_POS_INFINITY);
		STRING_OBJECTS.add(STRING_NEG_INFINITY);
		STRING_OBJECTS.add(STRING_NAN);
		STRING_OBJECTS.add(STRING_HEX_NEG);
		STRING_OBJECTS.add(STRING_HEX_POS);
		STRING_OBJECTS.add(STRING_FLOAT);
		STRING_OBJECTS.add(STRING_DOUBLE);
		STRING_OBJECTS.add(STRING_LONG);
		STRING_OBJECTS.add(STRING_INT);
		STRING_OBJECTS.add(STRING_TRUE);
		STRING_OBJECTS.add(STRING_FALSE);
		STRING_OBJECTS.add(STRING_NULL);
		STRING_OBJECTS.add(CHAR_SEQUENCE_NULL);
		STRING_OBJECTS.add(STRINGBUILDER1);
		STRING_OBJECTS.add(STRINGBUILDER2);
	}
	private static final List<Enum<?>> ENUM_OBJECTS = new ArrayList<Enum<?>>();
	static {
		ENUM_OBJECTS.add(Primitives.PRIM_BYTE);
		ENUM_OBJECTS.add(Primitives.PRIM_CHAR);
		ENUM_OBJECTS.add(Primitives.PRIM_DOUBLE);
		ENUM_OBJECTS.add(ValuedEnumTest.X);
		ENUM_OBJECTS.add(ValuedEnumTest.Y);
		ENUM_OBJECTS.add(ValuedEnumTest.Z);
	}
	private static final List<Object> MISC_OBJECTS = new ArrayList<Object>();
	static {
		MISC_OBJECTS.add(OBJECT);
		MISC_OBJECTS.add(FILE);
		MISC_OBJECTS.add(new Character(PRIM_CHAR));
		MISC_OBJECTS.add(new Boolean(PRIM_BOOLEAN_TRUE));
		MISC_OBJECTS.add(new Boolean(PRIM_BOOLEAN_FALSE));
		MISC_OBJECTS.add(Class.class);
		MISC_OBJECTS.add(Object.class);
		MISC_OBJECTS.add(String.class);
	}

	private static final List<Object> SRC_OBJECTS = new ArrayList<Object>();
	static {
		SRC_OBJECTS.addAll(MISC_OBJECTS);
		SRC_OBJECTS.addAll(STRING_OBJECTS);
		SRC_OBJECTS.addAll(PRIM_OBJECTS);
		SRC_OBJECTS.addAll(NUMBER_OBJECTS);

		// Enum objects
		SRC_OBJECTS.addAll(ENUM_OBJECTS);

		// Arrays
		SRC_OBJECTS.add(ARRAY_PRIM_BYTE);
		SRC_OBJECTS.add(ARRAY_PRIM_SHORT);
		SRC_OBJECTS.add(ARRAY_PRIM_INT);
		SRC_OBJECTS.add(ARRAY_PRIM_LONG);
		SRC_OBJECTS.add(ARRAY_PRIM_FLOAT);
		SRC_OBJECTS.add(ARRAY_PRIM_DOUBLE);
		SRC_OBJECTS.add(ARRAY_PRIM_BOOLEAN);
		SRC_OBJECTS.add(ARRAY_PRIM_CHAR);
		SRC_OBJECTS.add(ARRAY_BOXED_BYTE);
		SRC_OBJECTS.add(ARRAY_BOXED_SHORT);
		SRC_OBJECTS.add(ARRAY_BOXED_INT);
		SRC_OBJECTS.add(ARRAY_BOXED_LONG);
		SRC_OBJECTS.add(ARRAY_BOXED_FLOAT);
		SRC_OBJECTS.add(ARRAY_BOXED_DOUBLE);
		SRC_OBJECTS.add(ARRAY_BOXED_BOOLEAN);
		SRC_OBJECTS.add(ARRAY_BOXED_CHAR);
		SRC_OBJECTS.add(ARRAY_STRING);
		SRC_OBJECTS.add(ARRAY_OBJECT);
		SRC_OBJECTS.add(ARRAY_PRIMITIVES);
		SRC_OBJECTS.add(ARRAY_STRINGBUILDER);
		SRC_OBJECTS.add(ARRAY_VALUED_ENUM_TEST);
		SRC_OBJECTS.add(ARRAY_FILE);
		SRC_OBJECTS.add(ARRAY_DATE_MILLIS);
		SRC_OBJECTS.add(ARRAY_BASICFIXPOINT);
		SRC_OBJECTS.add(ARRAY_CLASS);
		SRC_OBJECTS.add(ARRAY_BIGINTEGER);
		SRC_OBJECTS.add(ARRAY_DECIMAL);
		SRC_OBJECTS.add(ARRAY_NUMBER);
		SRC_OBJECTS.add(MAP);
		SRC_OBJECTS.add(LIST);

		// Make arrays from other data types
	}
	private static final List<Class<?>> DST_CLASSES = new ArrayList<Class<?>>();
	static {
		DST_CLASSES.add(Object.class);

		// Primitive types (8)
		DST_CLASSES.add(int.class);
		DST_CLASSES.add(long.class);
		DST_CLASSES.add(double.class);
		DST_CLASSES.add(float.class);
		DST_CLASSES.add(byte.class);
		DST_CLASSES.add(short.class);
		DST_CLASSES.add(boolean.class);
		DST_CLASSES.add(char.class);

		// Boxed primitives (8)
		DST_CLASSES.add(Integer.class);
		DST_CLASSES.add(Long.class);
		DST_CLASSES.add(Double.class);
		DST_CLASSES.add(Float.class);
		DST_CLASSES.add(Byte.class);
		DST_CLASSES.add(Short.class);
		DST_CLASSES.add(Boolean.class);
		DST_CLASSES.add(Character.class);

		// Other numeric types
		DST_CLASSES.add(Number.class);
		DST_CLASSES.add(FixPoint.class);
		DST_CLASSES.add(BasicFixPoint.class);
		DST_CLASSES.add(BigDecimal.class);
		DST_CLASSES.add(BigInteger.class);
		DST_CLASSES.add(DateMillis.class);

		// Enum
		DST_CLASSES.add(Primitives.class); // Enum example
		DST_CLASSES.add(ValuedEnumTest.class); // ValuedEnum example

		// String-related
		DST_CLASSES.add(String.class);
		DST_CLASSES.add(StringBuilder.class);

		// Misc
		DST_CLASSES.add(File.class);
		DST_CLASSES.add(Class.class);
		DST_CLASSES.add(Void.class);
		DST_CLASSES.add(Map.class);
		DST_CLASSES.add(List.class);
		DST_CLASSES.add(JComboBox.class);
		DST_CLASSES.add(LinkedHashMap.class);

		// ARRAYS 

		DST_CLASSES.add(Object[].class);

		// Primitive Arrays 
		DST_CLASSES.add(int[].class);
		DST_CLASSES.add(long[].class);
		DST_CLASSES.add(double[].class);
		DST_CLASSES.add(float[].class);
		DST_CLASSES.add(byte[].class);
		DST_CLASSES.add(short[].class);
		DST_CLASSES.add(boolean[].class);
		DST_CLASSES.add(char[].class);

		// Boxed Primitive Arrays 
		DST_CLASSES.add(Integer[].class);
		DST_CLASSES.add(Long[].class);
		DST_CLASSES.add(Double[].class);
		DST_CLASSES.add(Float[].class);
		DST_CLASSES.add(Byte[].class);
		DST_CLASSES.add(Short[].class);
		DST_CLASSES.add(Boolean[].class);
		DST_CLASSES.add(Character[].class);

		// Other numeric types
		DST_CLASSES.add(Number[].class);
		DST_CLASSES.add(FixPoint[].class);
		DST_CLASSES.add(BasicFixPoint[].class);
		DST_CLASSES.add(BigDecimal[].class);
		DST_CLASSES.add(BigInteger[].class);
		DST_CLASSES.add(DateMillis[].class);

		// Enum
		DST_CLASSES.add(Primitives[].class); // Enum example
		DST_CLASSES.add(ValuedEnumTest[].class); // ValuedEnum example

		// String-related
		DST_CLASSES.add(String[].class);
		DST_CLASSES.add(StringBuilder[].class);

		// Misc
		DST_CLASSES.add(File[].class);
		DST_CLASSES.add(Class[].class);
	}

	public static void main(String[] args) {

		int numSrcClasses = SRC_OBJECTS.size();
		Set<Class<?>> srcClassesUnique = new HashSet<Class<?>>();
		// Count number of source object types
		for (int i = 0; i < SRC_OBJECTS.size(); i++) {
			srcClassesUnique.add(SRC_OBJECTS.get(i).getClass());
		}
		int numSrcClassesUnique = srcClassesUnique.size();

		// Count number of destination classes
		int numDstClasses = DST_CLASSES.size();
		Set<Class<?>> dstClassesUnique = new HashSet<Class<?>>();
		dstClassesUnique.addAll(DST_CLASSES);
		int numDstClassesUnique = dstClassesUnique.size();

		System.out.println("Num src classes: " + numSrcClasses);
		System.out.println("Num dst classes: " + numDstClasses);
		System.out.println("Num src classes (unique): " + numSrcClassesUnique);
		System.out.println("Num dst classes (unique): " + numDstClassesUnique);
		System.out.println("Num tests: " + (numSrcClasses * numDstClasses));
		System.out.println("srcClassesUnique \\ dstClassesUnique : " + OH.setDifference(srcClassesUnique, dstClassesUnique));
		Set<Class<?>> setDifference = (Set<Class<?>>) OH.setDifference(dstClassesUnique, srcClassesUnique);
		Set<Class<?>> ignore = new HashSet<Class<?>>();
		ignore.add(int.class);
		ignore.add(boolean.class);
		ignore.add(short.class);
		ignore.add(char.class);
		ignore.add(long.class);
		ignore.add(FixPoint.class);
		ignore.add(FixPoint[].class);
		ignore.add(float.class);
		ignore.add(byte.class);
		ignore.add(double.class);
		setDifference = (Set<Class<?>>) OH.setDifference(setDifference, ignore);
		System.out.println("dstClassesUnique \\ srcClassesUnique : " + setDifference);
		for (Class<?> c : setDifference) {
			System.out.println(c.getSimpleName());
		}
		System.out.println(MAP);
		System.out.println("***");
		Map<String, String> x = (Map<String, String>) null;
		System.out.println(x);
		System.out.println("$$$$$$$$$$$$$$$$$$");
		Double o = new Double(5.0);
		Class<?> srcClass = Number.class;
		Class<?> dstClass = Double.class;
		System.out.println("dstClass == srcClass : " + (dstClass == srcClass));
		System.out.println("dstClass.isAssignableFrom(srcClass) : " + (dstClass.isAssignableFrom(srcClass)));
		System.out.println("(o instanceof dstClass) : " + (o instanceof Number));

		OH.cast("xyz", Number.class, false, false);
		for (int b = 0; b < 2; b++) {
			System.out.println(b);
		}

	}

	private enum Primitives {
								PRIM_BYTE,
								PRIM_SHORT,
								PRIM_INT,
								PRIM_LONG,
								PRIM_FLOAT,
								PRIM_DOUBLE,
								PRIM_BOOLEAN_TRUE,
								PRIM_BOOLEAN_FALSE,
								PRIM_CHAR
	}

	private enum ValuedEnumTest implements ValuedEnum<String> {
																X("x"),
																Y("y"),
																Z("z");

		public final String value;

		private ValuedEnumTest(String value) {
			this.value = value;
		}

		@Override
		public String getEnumValue() {
			return this.value;
		}
	}

	@Test
	@Ignore("TODO: Split into seperate tests, and there are failures, look into")
	public void testAllCombos() {
		Object src;
		Class<?> dst;
		Object castResult, cast2Result;
		boolean success;
		int failureCnt = 0;
		int successCnt = 0;
		failureCnt += testPrimitiveByte(PRIM_BYTE);
		failureCnt += testPrimitiveShort(PRIM_SHORT);
		failureCnt += testPrimitiveInt(PRIM_INT);
		failureCnt += testPrimitiveLong(PRIM_LONG);
		failureCnt += testPrimitiveFloat(PRIM_FLOAT);
		failureCnt += testPrimitiveDouble(PRIM_DOUBLE);
		failureCnt += testPrimitiveBoolean(PRIM_BOOLEAN_TRUE);
		failureCnt += testPrimitiveBoolean(PRIM_BOOLEAN_FALSE);
		failureCnt += testPrimitiveChar(PRIM_CHAR);
		for (int b = 0; b < 2; b++) {
			for (int i = 0; i < SRC_OBJECTS.size(); i++) {
				for (int j = 0; j < DST_CLASSES.size(); j++) {
					src = SRC_OBJECTS.get(i);
					dst = DST_CLASSES.get(j);
					castResult = OH.cast(src, dst, b == 0, false);
					if (dst == Number.class) {
						System.out.println("x");
					}
					cast2Result = OH.cast2(src, dst, b == 0, false);
					success = OH.eq(castResult, cast2Result) || (dst == StringBuilder.class && eqStringBuilder((StringBuilder) castResult, (StringBuilder) cast2Result))
							|| (dst != null && dst.isArray() && compareObjectsAsArrays(castResult, cast2Result));
					if (!success) {
						System.out.println("(" + src.getClass().getSimpleName() + ") " + (isArray(src) ? arrayObjToString(src) : src) + " -> " + dst.getSimpleName());
						System.out.println(success);
					}
					if (success) {
						successCnt++;
					} else {
						System.out.println("FAILURE :: castResult: " + (isArray(castResult) ? arrayObjToString(castResult) : castResult) + ", cast2Result: "
								+ (isArray(cast2Result) ? arrayObjToString(cast2Result) : cast2Result));
						failureCnt++;
					}
				}
			}
		}
		System.out.println("Number of successes : " + successCnt);
		System.out.println("Number of failures : " + failureCnt);
		Assert.assertEquals(0, failureCnt);
	}
	private static int testPrimitiveByte(byte x) {
		Class<?> dst;
		Object castResult, cast2Result;
		boolean success;
		int failureCnt = 0;
		for (int b = 0; b < 2; b++) {
			for (int j = 0; j < DST_CLASSES.size(); j++) {
				dst = DST_CLASSES.get(j);
				castResult = OH.cast(x, dst, b == 0, false);
				cast2Result = OH.cast2(x, dst, b == 0, false);
				success = (dst == boolean.class && castResult.equals(true) && cast2Result == null) || OH.eq(castResult, cast2Result)
						|| (dst == StringBuilder.class && eqStringBuilder((StringBuilder) castResult, (StringBuilder) cast2Result));
				if (!success) {
					System.out.println("(" + byte.class.getSimpleName() + ") " + x + " -> " + dst.getSimpleName());
					System.out.println(success);
				}
				if (success) {
				} else {
					System.out.println("FAILURE :: castResult: " + castResult + ", cast2Result: " + cast2Result);
					failureCnt++;
				}
			}
		}
		return failureCnt;
	}
	private static int testPrimitiveShort(short x) {
		Class<?> dst;
		Object castResult, cast2Result;
		boolean success;
		int failureCnt = 0;
		for (int b = 0; b < 2; b++) {
			for (int j = 0; j < DST_CLASSES.size(); j++) {
				dst = DST_CLASSES.get(j);
				castResult = OH.cast(x, dst, b == 0, false);
				cast2Result = OH.cast2(x, dst, b == 0, false);
				success = (dst == boolean.class && castResult.equals(true) && cast2Result == null) || OH.eq(castResult, cast2Result)
						|| (dst == StringBuilder.class && eqStringBuilder((StringBuilder) castResult, (StringBuilder) cast2Result));
				if (!success) {
					System.out.println("(" + short.class.getSimpleName() + ") " + x + " -> " + dst.getSimpleName());
					System.out.println(success);
				}
				if (success) {
				} else {
					System.out.println("FAILURE :: castResult: " + castResult + ", cast2Result: " + cast2Result);
					failureCnt++;
				}
			}
		}
		return failureCnt;
	}
	private static int testPrimitiveInt(int x) {
		Class<?> dst;
		Object castResult, cast2Result;
		boolean success;
		int failureCnt = 0;
		for (int b = 0; b < 2; b++) {
			for (int j = 0; j < DST_CLASSES.size(); j++) {
				dst = DST_CLASSES.get(j);
				castResult = OH.cast(x, dst, b == 0, false);
				cast2Result = OH.cast2(x, dst, b == 0, false);
				success = (dst == boolean.class && castResult.equals(true) && cast2Result == null) || OH.eq(castResult, cast2Result)
						|| (dst == StringBuilder.class && eqStringBuilder((StringBuilder) castResult, (StringBuilder) cast2Result));
				if (!success) {
					System.out.println("(" + int.class.getSimpleName() + ") " + x + " -> " + dst.getSimpleName());
					System.out.println(success);
				}
				if (success) {
				} else {
					System.out.println("FAILURE :: castResult: " + castResult + ", cast2Result: " + cast2Result);
					failureCnt++;
				}
			}
		}
		return failureCnt;
	}
	private static int testPrimitiveLong(long x) {
		Class<?> dst;
		Object castResult, cast2Result;
		boolean success;
		int failureCnt = 0;
		for (int b = 0; b < 2; b++) {
			for (int j = 0; j < DST_CLASSES.size(); j++) {
				dst = DST_CLASSES.get(j);
				castResult = OH.cast(x, dst, b == 0, false);
				cast2Result = OH.cast2(x, dst, b == 0, false);
				success = (dst == boolean.class && castResult.equals(true) && cast2Result == null) || OH.eq(castResult, cast2Result)
						|| (dst == StringBuilder.class && eqStringBuilder((StringBuilder) castResult, (StringBuilder) cast2Result));
				if (!success) {
					System.out.println("(" + long.class.getSimpleName() + ") " + x + " -> " + dst.getSimpleName());
					System.out.println(success);
				}
				if (success) {
				} else {
					System.out.println("FAILURE :: castResult: " + castResult + ", cast2Result: " + cast2Result);
					failureCnt++;
				}
			}
		}
		return failureCnt;
	}
	private static int testPrimitiveFloat(float x) {
		Class<?> dst;
		Object castResult, cast2Result;
		boolean success;
		int failureCnt = 0;
		for (int b = 0; b < 2; b++) {
			for (int j = 0; j < DST_CLASSES.size(); j++) {
				dst = DST_CLASSES.get(j);
				castResult = OH.cast(x, dst, b == 0, false);
				cast2Result = OH.cast2(x, dst, b == 0, false);
				success = (dst == boolean.class && castResult.equals(true) && cast2Result == null) || OH.eq(castResult, cast2Result)
						|| (dst == StringBuilder.class && eqStringBuilder((StringBuilder) castResult, (StringBuilder) cast2Result));
				if (!success) {
					System.out.println("(" + float.class.getSimpleName() + ") " + x + " -> " + dst.getSimpleName());
					System.out.println(success);
				}
				if (success) {
				} else {
					System.out.println("FAILURE :: castResult: " + castResult + ", cast2Result: " + cast2Result);
					failureCnt++;
				}
			}
		}
		return failureCnt;
	}
	private static int testPrimitiveDouble(double x) {
		Class<?> dst;
		Object castResult, cast2Result;
		boolean success;
		int failureCnt = 0;
		for (int b = 0; b < 2; b++) {
			for (int j = 0; j < DST_CLASSES.size(); j++) {
				dst = DST_CLASSES.get(j);
				castResult = OH.cast(x, dst, b == 0, false);
				cast2Result = OH.cast2(x, dst, b == 0, false);
				success = (dst == boolean.class && castResult.equals(true) && cast2Result == null) || OH.eq(castResult, cast2Result)
						|| (dst == StringBuilder.class && eqStringBuilder((StringBuilder) castResult, (StringBuilder) cast2Result));
				if (!success) {
					System.out.println("(" + double.class.getSimpleName() + ") " + x + " -> " + dst.getSimpleName());
					System.out.println(success);
				}
				if (success) {
				} else {
					System.out.println("FAILURE :: castResult: " + castResult + ", cast2Result: " + cast2Result);
					failureCnt++;
				}
			}
		}
		return failureCnt;
	}
	private static int testPrimitiveBoolean(boolean x) {
		Class<?> dst;
		Object castResult, cast2Result;
		boolean success;
		int failureCnt = 0;
		for (int b = 0; b < 2; b++) {
			for (int j = 0; j < DST_CLASSES.size(); j++) {
				dst = DST_CLASSES.get(j);
				castResult = OH.cast(x, dst, b == 0, false);
				cast2Result = OH.cast2(x, dst, b == 0, false);
				success = OH.eq(castResult, cast2Result) || (dst == StringBuilder.class && eqStringBuilder((StringBuilder) castResult, (StringBuilder) cast2Result));
				if (!success) {
					System.out.println("(" + boolean.class.getSimpleName() + ") " + x + " -> " + dst.getSimpleName());
					System.out.println(success);
				}
				if (success) {
				} else {
					System.out.println("FAILURE :: castResult: " + castResult + ", cast2Result: " + cast2Result);
					failureCnt++;
				}
			}
		}
		return failureCnt;
	}
	private static int testPrimitiveChar(char x) {
		Class<?> dst;
		Object castResult, cast2Result;
		boolean success;
		int failureCnt = 0;
		for (int b = 0; b < 2; b++) {
			for (int j = 0; j < DST_CLASSES.size(); j++) {
				dst = DST_CLASSES.get(j);
				castResult = OH.cast(x, dst, b == 0, false);
				cast2Result = OH.cast2(x, dst, b == 0, false);
				success = OH.eq(castResult, cast2Result) || (dst == StringBuilder.class && eqStringBuilder((StringBuilder) castResult, (StringBuilder) cast2Result));
				if (!success) {
					System.out.println("(" + char.class.getSimpleName() + ") " + x + " -> " + dst.getSimpleName());
					System.out.println(success);
				}
				if (success) {
				} else {
					System.out.println("FAILURE :: castResult: " + castResult + ", cast2Result: " + cast2Result);
					failureCnt++;
				}
			}
		}
		return failureCnt;
	}
	private static boolean eqStringBuilder(StringBuilder sb1, StringBuilder sb2) {
		int length1 = sb1.length();
		if (length1 != sb2.length()) {
			return false;
		}
		for (int i = 0; i < length1; i++) {
			if (sb1.charAt(i) != sb2.charAt(i))
				return false;
		}
		return true;
	}
	private static boolean compareObjectsAsArrays(Object o1, Object o2) {
		if (o1 == null || o2 == null || !o1.getClass().isArray() || !o2.getClass().isArray()) {
			return false;
		}
		int len1 = Array.getLength(o1);
		if (len1 != Array.getLength(o2)) {
			return false;
		}
		// Check that arrays are of the same type
		if (getArrayType(o1) != getArrayType(o2)) {
			return false;
		}
		Object obj1, obj2;
		for (int i = 0; i < len1; i++) {
			try {
				obj1 = Array.get(o1, i);
			} catch (NullPointerException npe) {
				obj1 = null;
			}
			try {
				obj2 = Array.get(o2, i);
			} catch (NullPointerException npe) {
				obj2 = null;
			}
			if (obj1 == null) {
				if (obj2 != null) {
					return false;
				}
			} else {
				if (obj1 instanceof StringBuilder) {
					if (!eqStringBuilder((StringBuilder) obj1, (StringBuilder) obj2))
						return false;
				} else {
					if (!obj1.equals(obj2))
						return false;
				}
			}
		}
		return true;
	}
	private static Class<?> getArrayType(Object o) {
		Object elem;
		for (int i = 0; i < Array.getLength(o); i++) {
			try {
				elem = Array.get(o, i);
			} catch (NullPointerException npe) {
				continue;
			}
			if (elem != null)
				return elem.getClass();
		}
		return Object.class;
	}
	private static String arrayObjToString(Object o) {
		Object elem;
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		int len = Array.getLength(o);
		for (int i = 0; i < len; i++) {
			try {
				elem = Array.get(o, i);
			} catch (NullPointerException npe) {
				elem = null;
			}
			sb.append(elem);
			sb.append(i < len - 1 ? ", " : "]");
		}
		return sb.toString();
	}
	private static boolean isArray(Object o) {
		return o != null && o.getClass().isArray();
	}
}
