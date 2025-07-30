/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import com.f1.base.IterableAndSize;

/**
 * Array Helper
 */
public class AH {

	static public <T> T[] insert(T[] source, int index, T n) {
		int length = source.length;
		if (index == length)
			return append(source, n);
		OH.assertBetween(index, 0, length);
		T[] r = newInstance(source, length + 1);
		System.arraycopy(source, 0, r, 0, index);
		r[index] = n;
		System.arraycopy(source, index, r, index + 1, length - index);
		return r;
	}

	@SuppressWarnings("unchecked")
	static public <T> T[] newInstance(T[] type, int length) {
		return (T[]) Array.newInstance((Class<?>) type.getClass().getComponentType(), length);
	}

	static public <T> T[] insertArray(T[] source, int index, T[] values) {
		int length = source.length;
		OH.assertBetween(index, 0, length);
		T[] r = (T[]) newInstance(source, length + values.length);
		System.arraycopy(source, 0, r, 0, index);
		System.arraycopy(values, 0, r, index, values.length);
		System.arraycopy(source, index, r, index + values.length, length - index);
		return r;
	}

	@SuppressWarnings("unchecked")
	static public <T> T[] remove(T[] source, int index) {
		final int length = source.length;
		OH.assertBetween(index, 0, length);
		if (index >= length)
			throw new IndexOutOfBoundsException(SH.toString(index) + ">" + length);
		final Class<?> componentType = source.getClass().getComponentType();
		final T[] r;
		if (componentType == Object.class) {
			r = (T[]) newObjects(length - 1);
		} else {
			r = (T[]) Array.newInstance(source.getClass().getComponentType(), length - 1);
		}
		System.arraycopy(source, 0, r, 0, index);
		System.arraycopy(source, index + 1, r, index, length - index - 1);
		return r;
	}

	//if value is not found, returns original array
	static public <T> T[] remove(T[] source, T value) {
		int index = indexOf(value, source);
		if (index == -1)
			return source;
		final int length = source.length;
		final Class<?> componentType = source.getClass().getComponentType();
		final T[] r;
		if (componentType == Object.class) {
			r = (T[]) newObjects(length - 1);
		} else {
			r = (T[]) Array.newInstance(source.getClass().getComponentType(), length - 1);
		}
		System.arraycopy(source, 0, r, 0, index);
		System.arraycopy(source, index + 1, r, index, length - index - 1);
		return r;
	}

	static public int[] insert(int[] source, int index, int n) {
		int length = source.length;
		if (index == length)
			return append(source, n);
		OH.assertBetween(index, 0, length);
		int[] r = new int[length + 1];
		System.arraycopy(source, 0, r, 0, index);
		r[index] = n;
		System.arraycopy(source, index, r, index + 1, length - index);
		return r;
	}

	static public int[] insertArray(int[] source, int index, int[] values) {
		int length = source.length;
		OH.assertBetween(index, 0, length);
		int[] r = new int[length + values.length];
		System.arraycopy(source, 0, r, 0, index);
		System.arraycopy(values, 0, r, index, values.length);
		System.arraycopy(source, index, r, index + values.length, length - index);
		return r;
	}

	static public int[] remove(int[] source, int index) {
		int length = source.length;
		OH.assertBetween(index, 0, length);
		if (length == 1)
			return OH.EMPTY_INT_ARRAY;
		int[] r = new int[length - 1];
		System.arraycopy(source, 0, r, 0, index);
		System.arraycopy(source, index + 1, r, index, length - index - 1);
		return r;
	}

	static public boolean[] insert(boolean[] source, int index, boolean n) {
		int length = source.length;
		if (index == length)
			return append(source, n);
		OH.assertBetween(index, 0, length);
		boolean[] r = new boolean[length + 1];
		System.arraycopy(source, 0, r, 0, index);
		r[index] = n;
		System.arraycopy(source, index, r, index + 1, length - index);
		return r;
	}

	static public boolean[] insertArray(boolean[] source, int index, boolean[] values) {
		int length = source.length;
		OH.assertBetween(index, 0, length);
		boolean[] r = new boolean[length + values.length];
		System.arraycopy(source, 0, r, 0, index);
		System.arraycopy(values, 0, r, index, values.length);
		System.arraycopy(source, index, r, index + values.length, length - index);
		return r;
	}

	static public boolean[] remove(boolean[] source, int index) {
		int length = source.length;
		OH.assertBetween(index, 0, length);
		if (length == 1)
			return OH.EMPTY_BOOLEAN_ARRAY;
		boolean[] r = new boolean[length - 1];
		System.arraycopy(source, 0, r, 0, index);
		System.arraycopy(source, index + 1, r, index, length - index - 1);
		return r;
	}

	static public double[] insert(double[] source, int index, double n) {
		int length = source.length;
		if (index == length)
			return append(source, n);
		OH.assertBetween(index, 0, length);
		double[] r = new double[length + 1];
		System.arraycopy(source, 0, r, 0, index);
		r[index] = n;
		System.arraycopy(source, index, r, index + 1, length - index);
		return r;
	}

	static public double[] insertArray(double[] source, int index, double[] values) {
		int length = source.length;
		OH.assertBetween(index, 0, length);
		double[] r = new double[length + values.length];
		System.arraycopy(source, 0, r, 0, index);
		System.arraycopy(values, 0, r, index, values.length);
		System.arraycopy(source, index, r, index + values.length, length - index);
		return r;
	}

	static public double[] remove(double[] source, int index) {
		int length = source.length;
		OH.assertBetween(index, 0, length);
		if (length == 1)
			return OH.EMPTY_DOUBLE_ARRAY;
		double[] r = new double[length - 1];
		System.arraycopy(source, 0, r, 0, index);
		System.arraycopy(source, index + 1, r, index, length - index - 1);
		return r;
	}

	static public float[] insert(float[] source, int index, float n) {
		int length = source.length;
		if (index == length)
			return append(source, n);
		OH.assertBetween(index, 0, length);
		float[] r = new float[length + 1];
		System.arraycopy(source, 0, r, 0, index);
		r[index] = n;
		System.arraycopy(source, index, r, index + 1, length - index);
		return r;
	}

	static public float[] insertArray(float[] source, int index, float[] values) {
		int length = source.length;
		OH.assertBetween(index, 0, length);
		float[] r = new float[length + values.length];
		System.arraycopy(source, 0, r, 0, index);
		System.arraycopy(values, 0, r, index, values.length);
		System.arraycopy(source, index, r, index + values.length, length - index);
		return r;
	}

	static public float[] remove(float[] source, int index) {
		int length = source.length;
		OH.assertBetween(index, 0, length);
		if (length == 1)
			return OH.EMPTY_FLOAT_ARRAY;
		float[] r = new float[length - 1];
		System.arraycopy(source, 0, r, 0, index);
		System.arraycopy(source, index + 1, r, index, length - index - 1);
		return r;
	}

	static public long[] insert(long[] source, int index, long n) {
		int length = source.length;
		if (index == length)
			return append(source, n);
		OH.assertBetween(index, 0, length);
		long[] r = new long[length + 1];
		System.arraycopy(source, 0, r, 0, index);
		r[index] = n;
		System.arraycopy(source, index, r, index + 1, length - index);
		return r;
	}

	static public long[] insertArray(long[] source, int index, long[] values) {
		int length = source.length;
		OH.assertBetween(index, 0, length);
		long[] r = new long[length + values.length];
		System.arraycopy(source, 0, r, 0, index);
		System.arraycopy(values, 0, r, index, values.length);
		System.arraycopy(source, index, r, index + values.length, length - index);
		return r;
	}

	static public long[] remove(long[] source, int index) {
		int length = source.length;
		OH.assertBetween(index, 0, length);
		if (length == 1)
			return OH.EMPTY_LONG_ARRAY;
		long[] r = new long[length - 1];
		System.arraycopy(source, 0, r, 0, index);
		System.arraycopy(source, index + 1, r, index, length - index - 1);
		return r;
	}

	static public short[] insert(short[] source, int index, short n) {
		int length = source.length;
		if (index == length)
			return append(source, n);
		OH.assertBetween(index, 0, length);
		short[] r = new short[length + 1];
		System.arraycopy(source, 0, r, 0, index);
		r[index] = n;
		System.arraycopy(source, index, r, index + 1, length - index);
		return r;
	}

	static public short[] insertArray(short[] source, int index, short[] values) {
		int length = source.length;
		OH.assertBetween(index, 0, length);
		short[] r = new short[length + values.length];
		System.arraycopy(source, 0, r, 0, index);
		System.arraycopy(values, 0, r, index, values.length);
		System.arraycopy(source, index, r, index + values.length, length - index);
		return r;
	}

	static public short[] remove(short[] source, int index) {
		int length = source.length;
		OH.assertBetween(index, 0, length);
		if (length == 1)
			return OH.EMPTY_SHORT_ARRAY;
		short[] r = new short[length - 1];
		System.arraycopy(source, 0, r, 0, index);
		System.arraycopy(source, index + 1, r, index, length - index - 1);
		return r;
	}

	static public byte[] insert(byte[] source, int index, byte n) {
		int length = source.length;
		if (index == length)
			return append(source, n);
		OH.assertBetween(index, 0, length);
		byte[] r = new byte[length + 1];
		System.arraycopy(source, 0, r, 0, index);
		r[index] = n;
		System.arraycopy(source, index, r, index + 1, length - index);
		return r;
	}

	static public byte[] insertArray(byte[] source, int index, byte[] values) {
		int length = source.length;
		OH.assertBetween(index, 0, length);
		byte[] r = new byte[length + values.length];
		System.arraycopy(source, 0, r, 0, index);
		System.arraycopy(values, 0, r, index, values.length);
		System.arraycopy(source, index, r, index + values.length, length - index);
		return r;
	}

	static public byte[] remove(byte[] source, int index) {
		int length = source.length;
		OH.assertBetween(index, 0, length);
		if (length == 1)
			return OH.EMPTY_BYTE_ARRAY;
		byte[] r = new byte[length - 1];
		System.arraycopy(source, 0, r, 0, index);
		System.arraycopy(source, index + 1, r, index, length - index - 1);
		return r;
	}

	static public char[] insert(char[] source, int index, char n) {
		int length = source.length;
		if (index == length)
			return append(source, n);
		OH.assertBetween(index, 0, length);
		char[] r = new char[length + 1];
		System.arraycopy(source, 0, r, 0, index);
		r[index] = n;
		System.arraycopy(source, index, r, index + 1, length - index);
		return r;
	}

	static public char[] insertArray(char[] source, int index, char[] values) {
		int length = source.length;
		OH.assertBetween(index, 0, length);
		char[] r = new char[length + values.length];
		System.arraycopy(source, 0, r, 0, index);
		System.arraycopy(values, 0, r, index, values.length);
		System.arraycopy(source, index, r, index + values.length, length - index);
		return r;
	}

	static public char[] remove(char[] source, int index) {
		int length = source.length;
		OH.assertBetween(index, 0, length);
		if (length == 1)
			return OH.EMPTY_CHAR_ARRAY;
		char[] r = new char[length - 1];
		System.arraycopy(source, 0, r, 0, index);
		System.arraycopy(source, index + 1, r, index, length - index - 1);
		return r;
	}

	public static <T> T[] toArray(IterableAndSize<T> col, Class<T> rtype) {
		if (rtype.isPrimitive())
			rtype = (Class<T>) OH.getBoxed(rtype);
		T r[] = (T[]) Array.newInstance(rtype, col.size());
		int j = 0;
		for (T v : col)
			r[j++] = v;
		return r;
	}
	public static <T> T[] toArray(Collection<T> col, Class<T> rtype) {
		if (rtype.isPrimitive())
			rtype = (Class<T>) OH.getBoxed(rtype);
		T r[] = (T[]) Array.newInstance(rtype, col.size());
		int j = 0;
		for (T v : col)
			r[j++] = v;
		return r;
	}
	public static int[] toArrayInt(Collection<? extends Number> col) {
		int r[] = new int[col.size()];
		int j = 0;
		for (Number v : col)
			r[j++] = v.intValue();
		return r;
	}

	public static boolean[] toArrayBoolean(Collection<Boolean> col) {
		boolean r[] = new boolean[col.size()];
		int j = 0;
		for (boolean v : col)
			r[j++] = v;
		return r;
	}

	public static long[] toArrayLong(Collection<? extends Number> col) {
		long r[] = new long[col.size()];
		int j = 0;
		for (Number v : col)
			r[j++] = v.longValue();
		return r;
	}

	public static double[] toArrayDouble(Collection<? extends Number> col) {
		double r[] = new double[col.size()];
		int j = 0;
		for (Number v : col)
			r[j++] = v == null ? Double.NaN : v.doubleValue();
		return r;
	}

	public static float[] toArrayFloat(Collection<? extends Number> col) {
		float r[] = new float[col.size()];
		int j = 0;
		for (Number v : col)
			r[j++] = v == null ? Float.NaN : v.floatValue();
		return r;
	}

	public static short[] toArrayShort(Collection<? extends Number> col) {
		short r[] = new short[col.size()];
		int j = 0;
		for (Number v : col)
			r[j++] = v.shortValue();
		return r;
	}

	public static byte[] toArrayByte(Collection<? extends Number> col) {
		byte r[] = new byte[col.size()];
		int j = 0;
		for (Number v : col)
			r[j++] = v.byteValue();
		return r;
	}

	public static char[] toArrayChar(Collection<Character> col) {
		char r[] = new char[col.size()];
		int j = 0;
		for (char v : col)
			r[j++] = v;
		return r;
	}

	public static int indexOf(char c, char[] a) {
		for (int i = 0; i < a.length; i++)
			if (a[i] == c)
				return i;
		return -1;
	}
	public static int indexOf(char c, char[] a, int start) {
		for (int i = start; i < a.length; i++)
			if (a[i] == c)
				return i;
		return -1;
	}

	public static int indexOf(byte c, byte[] a) {
		for (int i = 0; i < a.length; i++)
			if (a[i] == c)
				return i;
		return -1;
	}

	public static int indexOf(byte c, byte[] a, int start) {
		for (int i = start; i < a.length; i++)
			if (a[i] == c)
				return i;
		return -1;
	}
	public static int indexOf(byte c, byte[] a, int start, int arrayLength) {
		for (int i = start; i < arrayLength; i++)
			if (a[i] == c)
				return i;
		return -1;
	}

	public static int indexOf(short c, short[] a) {
		for (int i = 0; i < a.length; i++)
			if (a[i] == c)
				return i;
		return -1;
	}

	public static int indexOf(int c, int[] a) {
		for (int i = 0; i < a.length; i++)
			if (a[i] == c)
				return i;
		return -1;
	}

	public static int indexOf(long c, long[] a) {
		for (int i = 0; i < a.length; i++)
			if (a[i] == c)
				return i;
		return -1;
	}

	public static int indexOf(float c, float[] a) {
		for (int i = 0; i < a.length; i++)
			if (a[i] == c)
				return i;
		return -1;
	}

	public static int indexOf(double c, double[] a) {
		for (int i = 0; i < a.length; i++)
			if (a[i] == c)
				return i;
		return -1;
	}

	public static int indexOf(boolean c, boolean[] a) {
		for (int i = 0; i < a.length; i++)
			if (a[i] == c)
				return i;
		return -1;
	}

	public static int indexOf(Object c, Object[] a) {
		for (int i = 0; i < a.length; i++)
			if (OH.eq(a[i], c))
				return i;
		return -1;
	}

	public static int indexOfByIdentity(Object c, Object[] a) {
		for (int i = 0; i < a.length; i++)
			if (a[i] == c)
				return i;
		return -1;
	}
	public static int indexOf(Object c, Object[] a, int start) {
		for (int i = start; i < a.length; i++)
			if (OH.eq(a[i], c))
				return i;
		return -1;
	}

	public static int indexOfByIdentity(Object c, Object[] a, int start) {
		for (int i = start; i < a.length; i++)
			if (a[i] == c)
				return i;
		return -1;
	}

	public static Character[] box(char[] a) {
		Character[] r = new Character[a.length];
		for (int i = 0; i < a.length; i++)
			r[i] = a[i];
		return r;
	}

	public static Boolean[] box(boolean[] a) {
		Boolean[] r = new Boolean[a.length];
		for (int i = 0; i < a.length; i++)
			r[i] = a[i];
		return r;
	}

	public static Byte[] box(byte[] a) {
		Byte[] r = new Byte[a.length];
		for (int i = 0; i < a.length; i++)
			r[i] = a[i];
		return r;
	}

	public static Short[] box(short[] a) {
		Short[] r = new Short[a.length];
		for (int i = 0; i < a.length; i++)
			r[i] = a[i];
		return r;
	}

	public static Integer[] box(int[] a) {
		Integer[] r = new Integer[a.length];
		for (int i = 0; i < a.length; i++)
			r[i] = a[i];
		return r;
	}

	public static Long[] box(long[] a) {
		Long[] r = new Long[a.length];
		for (int i = 0; i < a.length; i++)
			r[i] = a[i];
		return r;
	}

	public static Float[] box(float[] a) {
		Float[] r = new Float[a.length];
		for (int i = 0; i < a.length; i++)
			r[i] = a[i];
		return r;
	}

	public static Double[] box(double[] a) {
		Double[] r = new Double[a.length];
		for (int i = 0; i < a.length; i++)
			r[i] = a[i];
		return r;
	}

	public static char min(char[] a) {
		char r = a[0];
		for (int i = 1; i < a.length; i++)
			if (a[i] < r)
				r = a[i];
		return r;
	}

	public static char max(char[] a) {
		char r = a[0];
		for (int i = 1; i < a.length; i++)
			if (a[i] > r)
				r = a[i];
		return r;
	}

	public static byte min(byte[] a) {
		byte r = a[0];
		for (int i = 1; i < a.length; i++)
			if (a[i] < r)
				r = a[i];
		return r;
	}

	public static byte max(byte[] a) {
		byte r = a[0];
		for (int i = 1; i < a.length; i++)
			if (a[i] > r)
				r = a[i];
		return r;
	}

	public static short min(short[] a) {
		short r = a[0];
		for (int i = 1; i < a.length; i++)
			if (a[i] < r)
				r = a[i];
		return r;
	}

	public static short max(short[] a) {
		short r = a[0];
		for (int i = 1; i < a.length; i++)
			if (a[i] > r)
				r = a[i];
		return r;
	}

	public static int min(int[] a) {
		int r = a[0];
		for (int i = 1; i < a.length; i++)
			if (a[i] < r)
				r = a[i];
		return r;
	}

	public static int max(int[] a) {
		int r = a[0];
		for (int i = 1; i < a.length; i++)
			if (a[i] > r)
				r = a[i];
		return r;
	}

	public static long min(long[] a) {
		long r = a[0];
		for (int i = 1; i < a.length; i++)
			if (a[i] < r)
				r = a[i];
		return r;
	}

	public static long max(long[] a) {
		long r = a[0];
		for (int i = 1; i < a.length; i++)
			if (a[i] > r)
				r = a[i];
		return r;
	}

	public static float min(float[] a) {
		float r = a[0];
		for (int i = 1; i < a.length; i++)
			if (a[i] < r)
				r = a[i];
		return r;
	}

	public static float max(float[] a) {
		float r = a[0];
		for (int i = 1; i < a.length; i++)
			if (a[i] > r)
				r = a[i];
		return r;
	}

	public static double min(double[] a) {
		double r = a[0];
		for (int i = 1; i < a.length; i++)
			if (a[i] < r)
				r = a[i];
		return r;
	}

	public static double max(double[] a) {
		double r = a[0];
		for (int i = 1; i < a.length; i++)
			if (a[i] > r)
				r = a[i];
		return r;
	}

	public static boolean isEmpty(Object[] array) {
		return array == null || array.length == 0;
	}
	public static boolean isEmpty(byte[] array) {
		return array == null || array.length == 0;
	}
	public static boolean isEmpty(long[] array) {
		return array == null || array.length == 0;
	}
	public static boolean isEmpty(int[] array) {
		return array == null || array.length == 0;
	}
	public static boolean isEmpty(float[] array) {
		return array == null || array.length == 0;
	}
	public static boolean isEmpty(double[] array) {
		return array == null || array.length == 0;
	}
	public static boolean isEmpty(short[] array) {
		return array == null || array.length == 0;
	}
	public static boolean isEmpty(char[] array) {
		return array == null || array.length == 0;
	}
	public static boolean isEmpty(boolean[] array) {
		return array == null || array.length == 0;
	}

	public static boolean isntEmpty(Object[] array) {
		return !isEmpty(array);
	}
	public static boolean isntEmpty(byte[] array) {
		return !isEmpty(array);
	}
	public static boolean isntEmpty(long[] array) {
		return !isEmpty(array);
	}
	public static boolean isntEmpty(int[] array) {
		return !isEmpty(array);
	}
	public static boolean isntEmpty(float[] array) {
		return !isEmpty(array);
	}
	public static boolean isntEmpty(double[] array) {
		return !isEmpty(array);
	}
	public static boolean isntEmpty(short[] array) {
		return !isEmpty(array);
	}
	public static boolean isntEmpty(char[] array) {
		return !isEmpty(array);
	}
	public static boolean isntEmpty(boolean[] array) {
		return !isEmpty(array);
	}

	public static <T> T last(T[] array) {
		return array == null || array.length == 0 ? null : array[array.length - 1];
	}
	public static long last(long[] array, long dflt) {
		return array == null || array.length == 0 ? dflt : array[array.length - 1];
	}
	public static int last(int[] array, int dflt) {
		return array == null || array.length == 0 ? dflt : array[array.length - 1];
	}
	public static byte last(byte[] array, byte dflt) {
		return array == null || array.length == 0 ? dflt : array[array.length - 1];
	}

	private static int getLength(Object src) {
		return src == null ? -1 : Array.getLength(src);
	}

	public static char[] fill(char[] a, int start, int end, char value) {
		while (start < end)
			a[start++] = value;
		return a;
	}

	public static char[] fill(char[] a, char value) {
		return fill(a, 0, a.length, value);
	}

	public static byte[] fill(byte[] a, int start, int end, byte value) {
		while (start < end)
			a[start++] = value;
		return a;
	}

	public static byte[] fill(byte[] sink, byte[] value) {
		return fill(sink, 0, sink.length, value);
	}

	public static byte[] fill(byte[] sink, int start, int end, byte[] value) {
		if (value.length == 1)
			return fill(sink, start, end, value[0]);
		if (value.length == 0)
			throw new IllegalArgumentException("empty array for value");
		int blockSize = value.length;
		final int limit = end - blockSize + 1;
		while (start < limit) {
			System.arraycopy(value, 0, sink, start, blockSize);
			start += blockSize;
		}
		if (start < end)
			System.arraycopy(value, 0, sink, start, end - start);
		return sink;
	}

	public static byte[] fill(byte[] a, byte value) {
		return fill(a, 0, a.length, value);
	}

	public static short[] fill(short[] a, int start, int end, short value) {
		while (start < end)
			a[start++] = value;
		return a;
	}

	public static short[] fill(short[] a, short value) {
		return fill(a, 0, a.length, value);
	}

	public static int[] fill(int[] a, int start, int end, int value) {
		while (start < end)
			a[start++] = value;
		return a;
	}

	public static int[] fill(int[] a, int value) {
		return fill(a, 0, a.length, value);
	}

	public static long[] fill(long[] a, int start, int end, long value) {
		while (start < end)
			a[start++] = value;
		return a;
	}

	public static long[] fill(long[] a, long value) {
		return fill(a, 0, a.length, value);
	}

	public static float[] fill(float[] a, int start, int end, float value) {
		while (start < end)
			a[start++] = value;
		return a;
	}

	public static float[] fill(float[] a, float value) {
		return fill(a, 0, a.length, value);
	}

	public static double[] fill(double[] a, int start, int end, double value) {
		while (start < end)
			a[start++] = value;
		return a;
	}

	public static double[] fill(double[] a, double value) {
		return fill(a, 0, a.length, value);
	}

	public static boolean[] fill(boolean[] a, int start, int end, boolean value) {
		while (start < end)
			a[start++] = value;
		return a;
	}

	public static boolean[] fill(boolean[] a, boolean value) {
		return fill(a, 0, a.length, value);
	}

	public static <T> T[] fill(T[] a, int start, int end, T value) {
		while (start < end)
			a[start++] = value;
		return a;
	}

	public static <T> T[] fill(T[] a, T value) {
		return fill(a, 0, a.length, value);
	}

	public static <T> T[] a(final T... values) {
		return values;
	}

	public static boolean[] booleans(final boolean... values) {
		return values;
	}

	public static char[] chars(final char... values) {
		return values;
	}

	public static byte[] bytes(final byte... values) {
		return values;
	}

	public static short[] shorts(final short... values) {
		return values;
	}

	public static int[] ints(final int... values) {
		return values;
	}

	public static long[] longs(final long... values) {
		return values;
	}

	public static float[] floats(final float... values) {
		return values;
	}

	public static double[] doubles(final double... values) {
		return values;
	}

	public static <T> T getOr(T[] a, int index, T deflt) {
		return a == null || index < 0 || index >= a.length ? deflt : a[index];
	}

	public static byte[] cat(byte[]... arrays) {
		final int len = arrays.length;
		int size = 0, i = 0;
		while (i < len)
			size += arrays[i++].length;
		final byte[] r = new byte[size];
		while (--i > 0)
			System.arraycopy(arrays[i], 0, r, size -= arrays[i].length, arrays[i].length);
		return r;
	}

	public static boolean[] cat(boolean[]... arrays) {
		int size = 0, i = 0, len = arrays.length;
		while (i < len)
			size += arrays[i++].length;
		final boolean[] r = new boolean[size];
		while (i-- > 0)
			System.arraycopy(arrays[i], 0, r, size -= (len = arrays[i].length), len);
		return r;
	}

	public static char[] cat(char[]... arrays) {
		int size = 0, i = 0, len = arrays.length;
		while (i < len)
			size += arrays[i++].length;
		final char[] r = new char[size];
		while (i-- > 0)
			System.arraycopy(arrays[i], 0, r, size -= (len = arrays[i].length), len);
		return r;
	}

	public static short[] cat(short[]... arrays) {
		int size = 0, i = 0, len = arrays.length;
		while (i < len)
			size += arrays[i++].length;
		final short[] r = new short[size];
		while (i-- > 0)
			System.arraycopy(arrays[i], 0, r, size -= (len = arrays[i].length), len);
		return r;
	}

	public static int[] cat(int[]... arrays) {
		int size = 0, i = 0, len = arrays.length;
		while (i < len)
			size += arrays[i++].length;
		final int[] r = new int[size];
		while (i-- > 0)
			System.arraycopy(arrays[i], 0, r, size -= (len = arrays[i].length), len);
		return r;
	}

	public static long[] cat(long[]... arrays) {
		int size = 0, i = 0, len = arrays.length;
		while (i < len)
			size += arrays[i++].length;
		final long[] r = new long[size];
		while (i-- > 0)
			System.arraycopy(arrays[i], 0, r, size -= (len = arrays[i].length), len);
		return r;
	}

	public static float[] cat(float[]... arrays) {
		int size = 0, i = 0, len = arrays.length;
		while (i < len)
			size += arrays[i++].length;
		final float[] r = new float[size];
		while (i-- > 0)
			System.arraycopy(arrays[i], 0, r, size -= (len = arrays[i].length), len);
		return r;
	}

	public static double[] cat(double[]... arrays) {
		int size = 0, i = 0, len = arrays.length;
		while (i < len)
			size += arrays[i++].length;
		final double[] r = new double[size];
		while (i-- > 0)
			System.arraycopy(arrays[i], 0, r, size -= (len = arrays[i].length), len);
		return r;
	}

	public static <T> T[] cat(T[]... arrays) {
		int size = 0, i = 0, len = arrays.length;
		while (i < len)
			size += arrays[i++].length;
		final T[] r = newInstance(arrays[0], size);
		while (i-- > 0)
			System.arraycopy(arrays[i], 0, r, size -= (len = arrays[i].length), len);
		return r;
	}

	public static int indexOfSorted(int value, int[] array) {
		switch (array.length) {
			case 3:
				if (array[2] == value)
					return 2;
			case 2:
				if (array[1] == value)
					return 1;
			case 1:
				if (array[0] == value)
					return 0;
			case 0:
				return -1;
		}
		int min = 0, max = array.length;
		while (min < max) {
			int pivot = (min + max) >>> 1;
			int c = OH.compare(array[pivot], value);
			if (c == 0)
				return pivot;
			else if (c < 0)
				min = pivot + 1;
			else
				max = pivot - 1;
		}
		return min < array.length && array[min] == value ? min : -1;
	}
	public static <T extends Comparable> int indexOfSorted(T value, T[] array) {
		return indexOfSorted(value, array, array.length);
	}
	public static <T extends Comparable> int indexOfSorted(T value, T[] array, int arraylength) {
		switch (arraylength) {
			case 3:
				if (OH.compare(array[2], value) == 0)
					return 2;
			case 2:
				if (OH.compare(array[1], value) == 0)
					return 1;
			case 1:
				if (OH.compare(array[0], value) == 0)
					return 0;
			case 0:
				return -1;
		}
		int min = 0, max = arraylength;
		while (min < max) {
			int pivot = (min + max) >>> 1;
			int c = OH.compare(array[pivot], value);
			if (c == 0)
				return pivot;
			else if (c < 0)
				min = pivot + 1;
			else
				max = pivot - 1;
		}
		return min < arraylength && OH.compare(array[min], value) == 0 ? min : -1;
	}
	public static <T> int indexOfSorted(T value, T[] array, Comparator<T> cm) {
		int arraylength = array.length;
		switch (arraylength) {
			case 3:
				if (cm.compare(array[2], value) == 0)
					return 2;
			case 2:
				if (cm.compare(array[1], value) == 0)
					return 1;
			case 1:
				if (cm.compare(array[0], value) == 0)
					return 0;
			case 0:
				return -1;
		}
		int min = 0, max = arraylength;
		while (min < max) {
			int pivot = (min + max) >>> 1;
			int c = cm.compare(array[pivot], value);
			if (c == 0)
				return pivot;
			else if (c < 0)
				min = pivot + 1;
			else
				max = pivot - 1;
		}
		return min < arraylength && cm.compare(array[min], value) == 0 ? min : -1;
	}
	public static <T extends Comparable> int indexOfSortedLessThan(T value, T[] array) {
		return indexOfSortedLessThan(value, array, array.length);
	}
	public static <T extends Comparable> int indexOfSortedLessThan(T value, T[] array, int arraylength) {
		switch (arraylength) {
			case 3:
				if (OH.compare(array[2], value) < 0)
					return 2;
			case 2:
				if (OH.compare(array[1], value) < 0)
					return 1;
			case 1:
				if (OH.compare(array[0], value) < 0)
					return 0;
			case 0:
				return -1;
		}
		int min = 0, max = arraylength;
		while (max >= 0) {
			int pivot = (min + max) >>> 1;
			int c = OH.compare(array[pivot], value);
			if (c == 0)
				return pivot - 1;
			else if (c < 0) {
				if (pivot + 1 == arraylength || OH.compare(array[pivot + 1], value) > 0)
					return pivot;
				min = pivot + 1;
			} else
				max = pivot - 1;
		}
		return -1;
	}
	public static <T> int indexOfSortedLessThan(T value, T[] array, Comparator<T> comp) {
		int arraylength = array.length;
		switch (arraylength) {
			case 3:
				if (comp.compare(array[2], value) < 0)
					return 2;
			case 2:
				if (comp.compare(array[1], value) < 0)
					return 1;
			case 1:
				if (comp.compare(array[0], value) < 0)
					return 0;
			case 0:
				return -1;
		}
		int min = 0, max = arraylength;
		while (max >= 0) {
			int pivot = (min + max) >>> 1;
			int c = comp.compare(array[pivot], value);
			if (c == 0)
				return pivot - 1;
			else if (c < 0) {
				if (pivot + 1 == arraylength || comp.compare(array[pivot + 1], value) > 0)
					return pivot;
				min = pivot + 1;
			} else
				max = pivot - 1;
		}
		return -1;
	}
	public static <T extends Comparable> int indexOfSortedLessThanEqualTo(T value, T[] array) {
		return indexOfSortedLessThanEqualTo(value, array, array.length);
	}
	public static <T extends Comparable> int indexOfSortedLessThanEqualTo(T value, T[] array, int arraylength) {
		switch (arraylength) {
			case 3:
				if (OH.compare(array[2], value) <= 0)
					return 2;
			case 2:
				if (OH.compare(array[1], value) <= 0)
					return 1;
			case 1:
				if (OH.compare(array[0], value) <= 0)
					return 0;
			case 0:
				return -1;
		}
		int min = 0, max = arraylength;
		while (max >= 0) {
			int pivot = (min + max) >>> 1;
			int c = OH.compare(array[pivot], value);
			if (c == 0)
				return pivot;
			else if (c < 0) {
				if (pivot + 1 == arraylength || OH.compare(array[pivot + 1], value) > 0)
					return pivot;
				min = pivot + 1;
			} else
				max = pivot - 1;
		}
		return -1;
	}
	public static <T> int indexOfSortedLessThanEqualTo(T value, T[] array, Comparator<T> comp) {
		int arraylength = array.length;
		switch (arraylength) {
			case 3:
				if (comp.compare(array[2], value) <= 0)
					return 2;
			case 2:
				if (comp.compare(array[1], value) <= 0)
					return 1;
			case 1:
				if (comp.compare(array[0], value) <= 0)
					return 0;
			case 0:
				return -1;
		}
		int min = 0, max = arraylength;
		while (max >= 0) {
			int pivot = (min + max) >>> 1;
			int c = comp.compare(array[pivot], value);
			if (c == 0)
				return pivot;
			else if (c < 0) {
				if (pivot + 1 == arraylength || comp.compare(array[pivot + 1], value) > 0)
					return pivot;
				min = pivot + 1;
			} else
				max = pivot - 1;
		}
		return -1;
	}
	public static <T extends Comparable> int indexOfSortedGreaterThan(T value, T[] array) {
		return indexOfSortedGreaterThan(value, array, array.length);
	}
	public static <T extends Comparable> int indexOfSortedGreaterThanEqualTo(T value, T[] array) {
		return indexOfSortedGreaterThanEqualTo(value, array, array.length);
	}
	public static <T extends Comparable> int indexOfSortedGreaterThanEqualTo(T value, T[] array, int arraylength) {
		switch (arraylength) {
			case 3:
				if (OH.compare(array[0], value) >= 0)
					return 0;
				if (OH.compare(array[1], value) >= 0)
					return 1;
				if (OH.compare(array[2], value) >= 0)
					return 2;
				return -1;
			case 2:
				if (OH.compare(array[0], value) >= 0)
					return 0;
				if (OH.compare(array[1], value) >= 0)
					return 1;
				return -1;
			case 1:
				if (OH.compare(array[0], value) >= 0)
					return 0;
			case 0:
				return -1;
		}
		int min = 0, max = arraylength;
		while (min < arraylength) {
			int pivot = (min + max) >>> 1;
			int c = OH.compare(array[pivot], value);
			if (c == 0)
				return pivot;
			else if (c < 0) {
				min = pivot + 1;
			} else {
				if (pivot == 0 || OH.compare(array[pivot - 1], value) < 0)
					return pivot;
				max = pivot - 1;
			}
		}
		return -1;
	}
	public static <T> int indexOfSortedGreaterThanEqualTo(T value, T[] array, Comparator<T> comp) {
		int arraylength = array.length;
		switch (arraylength) {
			case 3:
				if (comp.compare(array[0], value) >= 0)
					return 0;
				if (comp.compare(array[1], value) >= 0)
					return 1;
				if (comp.compare(array[2], value) >= 0)
					return 2;
				return -1;
			case 2:
				if (comp.compare(array[0], value) >= 0)
					return 0;
				if (comp.compare(array[1], value) >= 0)
					return 1;
				return -1;
			case 1:
				if (comp.compare(array[0], value) >= 0)
					return 0;
			case 0:
				return -1;
		}
		int min = 0, max = arraylength;
		while (min < arraylength) {
			int pivot = (min + max) >>> 1;
			int c = comp.compare(array[pivot], value);
			if (c == 0)
				return pivot;
			else if (c < 0) {
				min = pivot + 1;
			} else {
				if (pivot == 0 || comp.compare(array[pivot - 1], value) < 0)
					return pivot;
				max = pivot - 1;
			}
		}
		return -1;
	}
	public static <T extends Comparable> int indexOfSortedGreaterThan(T value, T[] array, int arraylength) {
		switch (arraylength) {
			case 3:
				if (OH.compare(array[0], value) > 0)
					return 0;
				if (OH.compare(array[1], value) > 0)
					return 1;
				if (OH.compare(array[2], value) > 0)
					return 2;
				return -1;
			case 2:
				if (OH.compare(array[0], value) > 0)
					return 0;
				if (OH.compare(array[1], value) > 0)
					return 1;
				return -1;
			case 1:
				if (OH.compare(array[0], value) > 0)
					return 0;
			case 0:
				return -1;
		}
		int min = 0, max = arraylength;
		while (min < arraylength) {
			int pivot = (min + max) >>> 1;
			int c = OH.compare(array[pivot], value);
			if (c == 0) {
				return (++pivot == arraylength) ? -1 : pivot;
			} else if (c < 0) {
				min = pivot + 1;
			} else {
				if (pivot == 0 || OH.compare(array[pivot - 1], value) < 0)
					return pivot;
				max = pivot - 1;
			}
		}
		return -1;
	}
	public static <T> int indexOfSortedGreaterThan(T value, T[] array, Comparator<T> comp) {
		int arraylength = array.length;
		switch (arraylength) {
			case 3:
				if (comp.compare(array[0], value) > 0)
					return 0;
				if (comp.compare(array[1], value) > 0)
					return 1;
				if (comp.compare(array[2], value) > 0)
					return 2;
				return -1;
			case 2:
				if (comp.compare(array[0], value) > 0)
					return 0;
				if (comp.compare(array[1], value) > 0)
					return 1;
				return -1;
			case 1:
				if (comp.compare(array[0], value) > 0)
					return 0;
			case 0:
				return -1;
		}
		int min = 0, max = arraylength;
		while (min < arraylength) {
			int pivot = (min + max) >>> 1;
			int c = comp.compare(array[pivot], value);
			if (c == 0) {
				return (++pivot == arraylength) ? -1 : pivot;
			} else if (c < 0) {
				min = pivot + 1;
			} else {
				if (pivot == 0 || comp.compare(array[pivot - 1], value) < 0)
					return pivot;
				max = pivot - 1;
			}
		}
		return -1;
	}
	public static int indexOfSorted(long value, long[] array) {
		return indexOfSorted(value, array, array.length);
	}
	public static int indexOfSorted(long value, long[] array, int arraylength) {
		switch (arraylength) {
			case 3:
				if (array[2] == value)
					return 2;
			case 2:
				if (array[1] == value)
					return 1;
			case 1:
				if (array[0] == value)
					return 0;
			case 0:
				return -1;
		}
		int min = 0, max = arraylength;
		while (min < max) {
			int pivot = (min + max) >>> 1;
			int c = OH.compare(array[pivot], value);
			if (c == 0)
				return pivot;
			else if (c < 0)
				min = pivot + 1;
			else
				max = pivot - 1;
		}
		return min < arraylength && array[min] == value ? min : -1;
	}
	public static int indexOfSorted(short value, short[] array) {
		return indexOfSorted(value, array, array.length);
	}
	public static int indexOfSorted(short value, short[] array, int arrayLength) {
		switch (arrayLength) {
			case 3:
				if (array[2] == value)
					return 2;
			case 2:
				if (array[1] == value)
					return 1;
			case 1:
				if (array[0] == value)
					return 0;
			case 0:
				return -1;
		}
		int min = 0, max = arrayLength;
		while (min < max) {
			int pivot = (min + max) >>> 1;
			int c = OH.compare(array[pivot], value);
			if (c == 0)
				return pivot;
			else if (c < 0)
				min = pivot + 1;
			else
				max = pivot - 1;
		}
		return min < arrayLength && array[min] == value ? min : -1;
	}
	public static int indexOfSortedLessThanEqualTo(int value, int[] array) {
		return indexOfSortedLessThanEqualTo(value, array, array.length);
	}
	public static int indexOfSortedLessThanEqualTo(int value, int[] array, int arraylength) {
		switch (arraylength) {
			case 3:
				if (array[2] <= value)
					return 2;
			case 2:
				if (array[1] <= value)
					return 1;
			case 1:
				if (array[0] <= value)
					return 0;
			case 0:
				return -1;
		}
		int min = 0, max = arraylength;
		while (max >= 0) {
			int pivot = (min + max) >>> 1;
			int c = OH.compare(array[pivot], value);
			if (c == 0)
				return pivot;
			else if (c < 0) {
				if (pivot + 1 == arraylength || array[pivot + 1] > value)
					return pivot;
				min = pivot + 1;
			} else
				max = pivot - 1;
		}
		return -1;
	}
	public static int indexOfSortedGreaterThanEqualTo(int value, int[] array) {
		return indexOfSortedGreaterThanEqualTo(value, array, array.length);
	}
	public static int indexOfSortedGreaterThanEqualTo(int value, int[] array, int arraylength) {
		switch (arraylength) {
			case 3:
				if (array[0] >= value)
					return 0;
				if (array[1] >= value)
					return 1;
				if (array[2] >= value)
					return 2;
				return -1;
			case 2:
				if (array[0] >= value)
					return 0;
				if (array[1] >= value)
					return 1;
				return -1;
			case 1:
				if (array[0] >= value)
					return 0;
			case 0:
				return -1;
		}
		int min = 0, max = arraylength;
		while (min < arraylength) {
			int pivot = (min + max) >>> 1;
			int c = OH.compare(array[pivot], value);
			if (c == 0)
				return pivot;
			else if (c < 0) {
				min = pivot + 1;
			} else {
				if (pivot == 0 || array[pivot - 1] < value)
					return pivot;
				max = pivot - 1;
			}
		}
		return -1;
	}
	public static int indexOfSortedLessThanEqualTo(long value, long[] array) {
		return indexOfSortedLessThanEqualTo(value, array, array.length);
	}
	public static int indexOfSortedLessThanEqualTo(long value, long[] array, int arraylength) {
		switch (arraylength) {
			case 3:
				if (array[2] <= value)
					return 2;
			case 2:
				if (array[1] <= value)
					return 1;
			case 1:
				if (array[0] <= value)
					return 0;
			case 0:
				return -1;
		}
		int min = 0, max = arraylength;
		while (max >= 0) {
			int pivot = (min + max) >>> 1;
			int c = OH.compare(array[pivot], value);
			if (c == 0)
				return pivot;
			else if (c < 0) {
				if (pivot + 1 == arraylength || array[pivot + 1] > value)
					return pivot;
				min = pivot + 1;
			} else
				max = pivot - 1;
		}
		return -1;
	}
	public static int indexOfSortedGreaterThanEqualTo(long value, long[] array) {
		return indexOfSortedGreaterThanEqualTo(value, array, array.length);
	}
	public static int indexOfSortedGreaterThanEqualTo(long value, long[] array, int arraylength) {
		switch (arraylength) {
			case 3:
				if (array[0] >= value)
					return 0;
				if (array[1] >= value)
					return 1;
				if (array[2] >= value)
					return 2;
				return -1;
			case 2:
				if (array[0] >= value)
					return 0;
				if (array[1] >= value)
					return 1;
				return -1;
			case 1:
				if (array[0] >= value)
					return 0;
			case 0:
				return -1;
		}
		int min = 0, max = arraylength;
		while (min < arraylength) {
			int pivot = (min + max) >>> 1;
			int c = OH.compare(array[pivot], value);
			if (c == 0)
				return pivot;
			else if (c < 0) {
				min = pivot + 1;
			} else {
				if (pivot == 0 || array[pivot - 1] < value)
					return pivot;
				max = pivot - 1;
			}
		}
		return -1;
	}
	public static int indexOfSortedGreaterThanEqualTo(short value, short[] array) {
		return indexOfSortedGreaterThanEqualTo(value, array, array.length);
	}
	public static int indexOfSortedGreaterThanEqualTo(short value, short[] array, int arrayLength) {
		switch (arrayLength) {
			case 3:
				if (array[0] >= value)
					return 0;
				if (array[1] >= value)
					return 1;
				if (array[2] >= value)
					return 2;
				return -1;
			case 2:
				if (array[0] >= value)
					return 0;
				if (array[1] >= value)
					return 1;
				return -1;
			case 1:
				if (array[0] >= value)
					return 0;
			case 0:
				return -1;
		}
		int min = 0, max = arrayLength;
		while (min < arrayLength) {
			int pivot = (min + max) >>> 1;
			int c = OH.compare(array[pivot], value);
			if (c == 0)
				return pivot;
			else if (c < 0) {
				min = pivot + 1;
			} else {
				if (pivot == 0 || array[pivot - 1] < value)
					return pivot;
				max = pivot - 1;
			}
		}
		return -1;
	}
	public static int indexOfSortedLessThanEqualTo(short value, short[] array) {
		switch (array.length) {
			case 3:
				if (array[2] <= value)
					return 2;
			case 2:
				if (array[1] <= value)
					return 1;
			case 1:
				if (array[0] <= value)
					return 0;
			case 0:
				return -1;
		}
		int min = 0, max = array.length;
		while (max >= 0) {
			int pivot = (min + max) >>> 1;
			int c = OH.compare(array[pivot], value);
			if (c == 0)
				return pivot;
			else if (c < 0) {
				if (pivot + 1 == array.length || array[pivot + 1] > value)
					return pivot;
				min = pivot + 1;
			} else
				max = pivot - 1;
		}
		return -1;
	}

	public static <T> T[] sort(T[] a) {
		T[] r = a.clone();
		Arrays.sort(r);
		return r;
	}
	public static <T> T[] sort(Collection<T> a) {
		T[] r = (T[]) a.toArray();
		Arrays.sort(r);
		return r;
	}

	public static int[] sort(int[] a) {
		int[] r = a.clone();
		Arrays.sort(r);
		return r;
	}

	public static byte[] sort(byte[] a) {
		byte[] r = a.clone();
		Arrays.sort(r);
		return r;
	}

	public static short[] sort(short[] a) {
		short[] r = a.clone();
		Arrays.sort(r);
		return r;
	}

	public static long[] sort(long[] a) {
		long[] r = a.clone();
		Arrays.sort(r);
		return r;
	}

	public static double[] sort(double[] a) {
		double[] r = a.clone();
		Arrays.sort(r);
		return r;
	}

	public static float[] sort(float[] a) {
		float[] r = a.clone();
		Arrays.sort(r);
		return r;
	}

	public static char[] sort(char[] a) {
		char[] r = a.clone();
		Arrays.sort(r);
		return r;
	}

	// false first, true second
	public static boolean[] sort(boolean[] a) {
		boolean[] r = new boolean[a.length];
		int start = 0;
		for (int i = 0; i < a.length; i++)
			if (!a[i])
				start++;
		for (int i = start; i < a.length; i++)
			a[i] = true;
		return r;
	}

	public static int indexOf(byte[] data, byte[] find, int start) {
		return indexOf(data, find, start, data.length);
	}

	public static int indexOf(byte[] data, byte[] find, int start, int end) {
		if (find.length == 0)
			return start;
		byte c = find[0];
		OUTER: for (final int e = end - find.length + 1; start < e; start++) {
			if (data[start] == c) {
				for (int j = 1; j < find.length; j++)
					if (data[start + j] != find[j])
						continue OUTER;
				return start;
			}
		}
		return -1;
	}

	public static <T> T max(T[] array, Comparator<? super T> comparator) {
		if (isEmpty(array))
			return null;
		T r = array[0];
		for (int i = 1; i < array.length; i++)
			if (comparator.compare(array[i], r) > 0)
				r = array[i];
		return r;
	}

	public static <V, T extends Comparable<V>> T max(T[] array) {
		return max(array, 0, array.length);
	}
	public static <V, T extends Comparable<V>> T max(T[] array, int start, int end) {
		if (isEmpty(array))
			return null;
		T r = array[start];
		while (++start < end)
			if (OH.compare(array[start], r) > 0)
				r = array[start];
		return r;
	}

	public static <T> T min(T[] array, Comparator<? super T> comparator) {
		if (isEmpty(array))
			return null;
		T r = array[0];
		for (int i = 1; i < array.length; i++)
			if (comparator.compare(array[i], r) < 0)
				r = array[i];
		return r;
	}
	public static <T> int minPos(T[] array, Comparator<? super T> comparator) {
		if (isEmpty(array))
			return -1;
		T r = array[0];
		int rr = 0;
		for (int i = 1; i < array.length; i++)
			if (comparator.compare(array[i], r) < 0)
				r = array[rr = i];
		return rr;
	}
	public static <T> int maxPos(T[] array, Comparator<? super T> comparator) {
		if (isEmpty(array))
			return -1;
		T r = array[0];
		int rr = 0;
		for (int i = 1; i < array.length; i++)
			if (comparator.compare(array[i], r) > 0)
				r = array[rr = i];
		return rr;
	}

	public static <V, T extends Comparable<V>> T min(T[] array) {
		return min(array, 0, array.length);
	}
	public static <V, T extends Comparable<V>> T min(T[] array, int start, int end) {
		if (isEmpty(array))
			return null;
		T r = array[start];
		while (++start < end)
			if (OH.compare(array[start], r) < 0)
				r = array[start];
		return r;
	}

	public static int length(Object[] data) {
		return data == null ? -1 : data.length;
	}

	public static int length(boolean[] data) {
		return data == null ? -1 : data.length;
	}

	public static int length(double[] data) {
		return data == null ? -1 : data.length;
	}

	public static int length(float[] data) {
		return data == null ? -1 : data.length;
	}

	public static int length(long[] data) {
		return data == null ? -1 : data.length;
	}

	public static int length(int[] data) {
		return data == null ? -1 : data.length;
	}

	public static int length(short[] data) {
		return data == null ? -1 : data.length;
	}

	public static int length(char[] data) {
		return data == null ? -1 : data.length;
	}

	public static int length(byte[] data) {
		return data == null ? -1 : data.length;
	}
	public static Integer lengthOrNull(byte[] data) {
		return data == null ? null : data.length;
	}

	// will ALWAYS create a new array
	public static long[] subarrayNoThrow(long[] data, int start, int length) {
		if (data == null)
			return null;
		if (start < 0)
			start = 0;
		if (start >= data.length)
			return OH.EMPTY_LONG_ARRAY;
		if (start + length > data.length)
			length = data.length - start;
		if (start == 0) {
			if (data.length == length)
				return data.clone();
			else
				return Arrays.copyOf(data, length);
		}
		long[] r = new long[length];
		System.arraycopy(data, start, r, 0, length);
		return r;
	}
	// will ALWAYS create a new array
	public static int[] subarrayNoThrow(int[] data, int start, int length) {
		if (data == null)
			return null;
		if (start < 0)
			start = 0;
		if (start >= data.length)
			return OH.EMPTY_INT_ARRAY;
		if (start + length > data.length)
			length = data.length - start;
		if (start == 0) {
			if (data.length == length)
				return data.clone();
			else
				return Arrays.copyOf(data, length);
		}
		int[] r = new int[length];
		System.arraycopy(data, start, r, 0, length);
		return r;
	}

	// will ALWAYS create a new array
	public static long[] subarray(long[] data, int start, int length) {
		if (length + start > data.length)
			throw new IndexOutOfBoundsException(length + " + " + start + " > " + data.length);
		if (start == 0) {
			if (data.length == length)
				return data.clone();
			else
				return Arrays.copyOf(data, length);
		}
		long[] r = new long[length];
		System.arraycopy(data, start, r, 0, length);
		return r;
	}
	// will ALWAYS create a new array
	public static int[] subarray(int[] data, int start, int length) {
		if (length + start > data.length)
			throw new IndexOutOfBoundsException(length + " + " + start + " > " + data.length);
		if (start == 0) {
			if (data.length == length)
				return data.clone();
			else
				return Arrays.copyOf(data, length);
		}
		int[] r = new int[length];
		System.arraycopy(data, start, r, 0, length);
		return r;
	}
	// will ALWAYS create a new array
	public static byte[] subarray(byte[] data, int start, int length) {
		if (length + start > data.length)
			throw new IndexOutOfBoundsException(length + " + " + start + " > " + data.length);
		if (start == 0) {
			if (data.length == length)
				return data.clone();
			else
				return Arrays.copyOf(data, length);
		}
		byte[] r = new byte[length];
		System.arraycopy(data, start, r, 0, length);
		return r;
	}
	// will ALWAYS create a new array
	public static char[] subarray(char[] data, int start, int length) {
		if (length + start > data.length)
			throw new IndexOutOfBoundsException(length + " + " + start + " > " + data.length);
		if (start == 0) {
			if (data.length == length)
				return data.clone();
			else
				return Arrays.copyOf(data, length);
		}
		char[] r = new char[length];
		System.arraycopy(data, start, r, 0, length);
		return r;
	}
	// will ALWAYS create a new array
	public static <T> T[] subarray(T[] data, int start, int length) {
		if (length + start > data.length)
			throw new IndexOutOfBoundsException(length + " + " + start + " > " + data.length);
		if (start == 0) {
			if (data.length == length)
				return data.clone();
			else
				return Arrays.copyOf(data, length);
		}
		T[] r = newInstance(data, length);
		System.arraycopy(data, start, r, 0, length);
		return r;
	}
	public static byte[] subarrayNoThrow(byte[] data, int start, int length) {
		if (data == null)
			return null;
		if (start < 0)
			start = 0;
		if (start >= data.length)
			return OH.EMPTY_BYTE_ARRAY;
		if (length + start > data.length)
			length = data.length - start;
		if (start == 0) {
			if (data.length == length)
				return data.clone();
			else
				return Arrays.copyOf(data, length);
		}
		byte[] r = new byte[length];
		System.arraycopy(data, start, r, 0, length);
		return r;
	}

	public static Object cloneArray(Object array) {

		//TODO: uhhh.. why aren't I just calling .clone() ?
		final int len = Array.getLength(array);
		Object r = Array.newInstance(array.getClass().getComponentType(), len);
		System.arraycopy(array, 0, r, 0, len);
		return r;
	}

	static public <T> T[] append(T[] source, T n) {
		final int len = source.length;
		final T[] r = Arrays.copyOf(source, len + 1);
		r[len] = n;
		return r;
	}
	static public <T> T[] appendArray(T[] source, T[] n) {
		final int len = source.length;
		final T[] r = Arrays.copyOf(source, len + n.length);
		System.arraycopy(n, 0, r, len, n.length);
		return r;
	}
	static public int[] appendArray(int[] source, int[] n) {
		final int len = source.length;
		final int[] r = Arrays.copyOf(source, len + n.length);
		System.arraycopy(n, 0, r, len, n.length);
		return r;
	}
	static public long[] appendArray(long[] source, long[] n) {
		final int len = source.length;
		final long[] r = Arrays.copyOf(source, len + n.length);
		System.arraycopy(n, 0, r, len, n.length);
		return r;
	}
	static public short[] appendArray(short[] source, short[] n) {
		final int len = source.length;
		final short[] r = Arrays.copyOf(source, len + n.length);
		System.arraycopy(n, 0, r, len, n.length);
		return r;
	}
	static public double[] appendArray(double[] source, double[] n) {
		final int len = source.length;
		final double[] r = Arrays.copyOf(source, len + n.length);
		System.arraycopy(n, 0, r, len, n.length);
		return r;
	}
	static public float[] appendArray(float[] source, float[] n) {
		final int len = source.length;
		final float[] r = Arrays.copyOf(source, len + n.length);
		System.arraycopy(n, 0, r, len, n.length);
		return r;
	}
	static public boolean[] appendArray(boolean[] source, boolean[] n) {
		final int len = source.length;
		final boolean[] r = Arrays.copyOf(source, len + n.length);
		System.arraycopy(n, 0, r, len, n.length);
		return r;
	}
	static public char[] appendArray(char[] source, char[] n) {
		final int len = source.length;
		final char[] r = Arrays.copyOf(source, len + n.length);
		System.arraycopy(n, 0, r, len, n.length);
		return r;
	}
	static public byte[] appendArray(byte[] source, byte[] n) {
		final int len = source.length;
		final byte[] r = Arrays.copyOf(source, len + n.length);
		System.arraycopy(n, 0, r, len, n.length);
		return r;
	}

	static public <T> byte[] append(byte[] source, byte n) {
		final byte[] r = Arrays.copyOf(source, source.length + 1);
		r[source.length] = n;
		return r;
	}
	static public <T> short[] append(short[] source, short n) {
		final short[] r = Arrays.copyOf(source, source.length + 1);
		r[source.length] = n;
		return r;
	}
	static public <T> long[] append(long[] source, long n) {
		final long[] r = Arrays.copyOf(source, source.length + 1);
		r[source.length] = n;
		return r;
	}
	static public <T> int[] append(int[] source, int n) {
		final int[] r = Arrays.copyOf(source, source.length + 1);
		r[source.length] = n;
		return r;
	}
	static public <T> double[] append(double[] source, double n) {
		final double[] r = Arrays.copyOf(source, source.length + 1);
		r[source.length] = n;
		return r;
	}
	static public <T> float[] append(float[] source, float n) {
		final float[] r = Arrays.copyOf(source, source.length + 1);
		r[source.length] = n;
		return r;
	}
	static public <T> boolean[] append(boolean[] source, boolean n) {
		final boolean[] r = Arrays.copyOf(source, source.length + 1);
		r[source.length] = n;
		return r;
	}
	static public <T> char[] append(char[] source, char n) {
		final char[] r = Arrays.copyOf(source, source.length + 1);
		r[source.length] = n;
		return r;
	}

	public static long[] i(long[] i) {
		return i == null ? OH.EMPTY_LONG_ARRAY : i;
	}
	public static int[] i(int[] i) {
		return i == null ? OH.EMPTY_INT_ARRAY : i;
	}
	public static double[] i(double[] i) {
		return i == null ? OH.EMPTY_DOUBLE_ARRAY : i;
	}
	public static float[] i(float[] i) {
		return i == null ? OH.EMPTY_FLOAT_ARRAY : i;
	}
	public static short[] i(short[] i) {
		return i == null ? OH.EMPTY_SHORT_ARRAY : i;
	}
	public static char[] i(char[] i) {
		return i == null ? OH.EMPTY_CHAR_ARRAY : i;
	}
	public static byte[] i(byte[] i) {
		return i == null ? OH.EMPTY_BYTE_ARRAY : i;
	}
	public static boolean[] i(boolean[] i) {
		return i == null ? OH.EMPTY_BOOLEAN_ARRAY : i;
	}

	public static int[] newInts(int length) {
		return length == 0 ? OH.EMPTY_INT_ARRAY : new int[length];
	}
	public static long[] newLongs(int length) {
		return length == 0 ? OH.EMPTY_LONG_ARRAY : new long[length];
	}
	public static Object[] newObjects(int length) {
		return length == 0 ? OH.EMPTY_OBJECT_ARRAY : new Object[length];
	}
	public static double[] newDoubles(int length) {
		return length == 0 ? OH.EMPTY_DOUBLE_ARRAY : new double[length];
	}
	public static float[] newFloats(int length) {
		return length == 0 ? OH.EMPTY_FLOAT_ARRAY : new float[length];
	}
	public static short[] newShorts(int length) {
		return length == 0 ? OH.EMPTY_SHORT_ARRAY : new short[length];
	}
	public static byte[] newBytes(int length) {
		return length == 0 ? OH.EMPTY_BYTE_ARRAY : new byte[length];
	}
	public static char[] newChars(int length) {
		return length == 0 ? OH.EMPTY_CHAR_ARRAY : new char[length];
	}
	public static boolean[] newBooleans(int length) {
		return length == 0 ? OH.EMPTY_BOOLEAN_ARRAY : new boolean[length];
	}

	public static boolean startsWith(byte[] data, byte[] find, int start) {
		if (find.length + start > data.length)
			return false;
		for (int i = 0; i < find.length; i++)
			if (data[i + start] != find[i])
				return false;
		return true;
	}

	//chars
	public static byte[] castToBytes(char[] src) {
		final byte[] r = newBytes(src.length);
		for (int i = 0; i < src.length; i++)
			r[i] = (byte) src[i];
		return r;
	}
	public static short[] castToShorts(char[] src) {
		final short[] r = newShorts(src.length);
		for (int i = 0; i < src.length; i++)
			r[i] = (short) src[i];
		return r;
	}
	public static int[] castToInts(char[] src) {
		final int[] r = newInts(src.length);
		for (int i = 0; i < src.length; i++)
			r[i] = (int) src[i];
		return r;
	}
	public static long[] castToLongs(char[] src) {
		final long[] r = newLongs(src.length);
		for (int i = 0; i < src.length; i++)
			r[i] = (long) src[i];
		return r;
	}
	public static double[] castToDoubles(char[] src) {
		final double[] r = newDoubles(src.length);
		for (int i = 0; i < src.length; i++)
			r[i] = (double) src[i];
		return r;
	}
	public static float[] castToFloats(char[] src) {
		final float[] r = newFloats(src.length);
		for (int i = 0; i < src.length; i++)
			r[i] = (float) src[i];
		return r;
	}

	//bytes
	public static short[] castToShorts(byte[] src) {
		final short[] r = newShorts(src.length);
		for (int i = 0; i < src.length; i++)
			r[i] = (short) src[i];
		return r;
	}
	public static char[] castToChars(byte[] src) {
		final char[] r = newChars(src.length);
		for (int i = 0; i < src.length; i++)
			r[i] = (char) src[i];
		return r;
	}
	public static int[] castToInts(byte[] src) {
		final int[] r = newInts(src.length);
		for (int i = 0; i < src.length; i++)
			r[i] = (int) src[i];
		return r;
	}
	public static long[] castToLongs(byte[] src) {
		final long[] r = newLongs(src.length);
		for (int i = 0; i < src.length; i++)
			r[i] = (long) src[i];
		return r;
	}
	public static double[] castToDoubles(byte[] src) {
		final double[] r = newDoubles(src.length);
		for (int i = 0; i < src.length; i++)
			r[i] = (double) src[i];
		return r;
	}
	public static float[] castToFloats(byte[] src) {
		final float[] r = newFloats(src.length);
		for (int i = 0; i < src.length; i++)
			r[i] = (float) src[i];
		return r;
	}

	//short
	public static byte[] castToBytes(short[] src) {
		final byte[] r = newBytes(src.length);
		for (int i = 0; i < src.length; i++)
			r[i] = (byte) src[i];
		return r;
	}
	public static char[] castToChars(short[] src) {
		final char[] r = newChars(src.length);
		for (int i = 0; i < src.length; i++)
			r[i] = (char) src[i];
		return r;
	}
	public static int[] castToInts(short[] src) {
		final int[] r = newInts(src.length);
		for (int i = 0; i < src.length; i++)
			r[i] = (int) src[i];
		return r;
	}
	public static long[] castToLongs(short[] src) {
		final long[] r = newLongs(src.length);
		for (int i = 0; i < src.length; i++)
			r[i] = (long) src[i];
		return r;
	}
	public static double[] castToDoubles(short[] src) {
		final double[] r = newDoubles(src.length);
		for (int i = 0; i < src.length; i++)
			r[i] = (double) src[i];
		return r;
	}
	public static float[] castToFloats(short[] src) {
		final float[] r = newFloats(src.length);
		for (int i = 0; i < src.length; i++)
			r[i] = (float) src[i];
		return r;
	}

	//ints
	public static byte[] castToBytes(int[] src) {
		final byte[] r = newBytes(src.length);
		for (int i = 0; i < src.length; i++)
			r[i] = (byte) src[i];
		return r;
	}
	public static short[] castToShorts(int[] src) {
		final short[] r = newShorts(src.length);
		for (int i = 0; i < src.length; i++)
			r[i] = (short) src[i];
		return r;
	}
	public static char[] castToChars(int[] src) {
		final char[] r = newChars(src.length);
		for (int i = 0; i < src.length; i++)
			r[i] = (char) src[i];
		return r;
	}
	public static long[] castToLongs(int[] src) {
		final long[] r = newLongs(src.length);
		for (int i = 0; i < src.length; i++)
			r[i] = (long) src[i];
		return r;
	}
	public static double[] castToDoubles(int[] src) {
		final double[] r = newDoubles(src.length);
		for (int i = 0; i < src.length; i++)
			r[i] = (double) src[i];
		return r;
	}
	public static float[] castToFloats(int[] src) {
		final float[] r = newFloats(src.length);
		for (int i = 0; i < src.length; i++)
			r[i] = (float) src[i];
		return r;
	}

	//longs
	public static byte[] castToBytes(long[] src) {
		final byte[] r = newBytes(src.length);
		for (int i = 0; i < src.length; i++)
			r[i] = (byte) src[i];
		return r;
	}
	public static short[] castToShorts(long[] src) {
		final short[] r = newShorts(src.length);
		for (int i = 0; i < src.length; i++)
			r[i] = (short) src[i];
		return r;
	}
	public static char[] castToChars(long[] src) {
		final char[] r = newChars(src.length);
		for (int i = 0; i < src.length; i++)
			r[i] = (char) src[i];
		return r;
	}
	public static int[] castToInts(long[] src) {
		final int[] r = newInts(src.length);
		for (int i = 0; i < src.length; i++)
			r[i] = (int) src[i];
		return r;
	}
	public static double[] castToDoubles(long[] src) {
		final double[] r = newDoubles(src.length);
		for (int i = 0; i < src.length; i++)
			r[i] = (double) src[i];
		return r;
	}
	public static float[] castToFloats(long[] src) {
		final float[] r = newFloats(src.length);
		for (int i = 0; i < src.length; i++)
			r[i] = (float) src[i];
		return r;
	}

	//doubles
	public static byte[] castToBytes(double[] src) {
		final byte[] r = newBytes(src.length);
		for (int i = 0; i < src.length; i++)
			r[i] = (byte) src[i];
		return r;
	}
	public static short[] castToShorts(double[] src) {
		final short[] r = newShorts(src.length);
		for (int i = 0; i < src.length; i++)
			r[i] = (short) src[i];
		return r;
	}
	public static char[] castToChars(double[] src) {
		final char[] r = newChars(src.length);
		for (int i = 0; i < src.length; i++)
			r[i] = (char) src[i];
		return r;
	}
	public static int[] castToInts(double[] src) {
		final int[] r = newInts(src.length);
		for (int i = 0; i < src.length; i++)
			r[i] = (int) src[i];
		return r;
	}
	public static long[] castToLongs(double[] src) {
		final long[] r = newLongs(src.length);
		for (int i = 0; i < src.length; i++)
			r[i] = (long) src[i];
		return r;
	}
	public static float[] castToFloats(double[] src) {
		final float[] r = newFloats(src.length);
		for (int i = 0; i < src.length; i++)
			r[i] = (float) src[i];
		return r;
	}

	//floats
	public static byte[] castToBytes(float[] src) {
		final byte[] r = newBytes(src.length);
		for (int i = 0; i < src.length; i++)
			r[i] = (byte) src[i];
		return r;
	}
	public static short[] castToShorts(float[] src) {
		final short[] r = newShorts(src.length);
		for (int i = 0; i < src.length; i++)
			r[i] = (short) src[i];
		return r;
	}
	public static char[] castToChars(float[] src) {
		final char[] r = newChars(src.length);
		for (int i = 0; i < src.length; i++)
			r[i] = (char) src[i];
		return r;
	}
	public static int[] castToInts(float[] src) {
		final int[] r = newInts(src.length);
		for (int i = 0; i < src.length; i++)
			r[i] = (int) src[i];
		return r;
	}
	public static long[] castToLongs(float[] src) {
		final long[] r = newLongs(src.length);
		for (int i = 0; i < src.length; i++)
			r[i] = (long) src[i];
		return r;
	}
	public static double[] castToDoubles(float[] src) {
		final double[] r = newDoubles(src.length);
		for (int i = 0; i < src.length; i++)
			r[i] = (double) src[i];
		return r;
	}

	public static <T> T[] noEmpty(T[] a, T[] ifEmpty) {
		return isEmpty(a) ? ifEmpty : a;
	}
	public static int[] noEmpty(int[] a, int[] ifEmpty) {
		return isEmpty(a) ? ifEmpty : a;
	}
	public static byte[] noEmpty(byte[] a, byte[] ifEmpty) {
		return isEmpty(a) ? ifEmpty : a;
	}
	public static long[] noEmpty(long[] a, long[] ifEmpty) {
		return isEmpty(a) ? ifEmpty : a;
	}
	public static double[] noEmpty(double[] a, double[] ifEmpty) {
		return isEmpty(a) ? ifEmpty : a;
	}
	public static float[] noEmpty(float[] a, float[] ifEmpty) {
		return isEmpty(a) ? ifEmpty : a;
	}
	public static short[] noEmpty(short[] a, short[] ifEmpty) {
		return isEmpty(a) ? ifEmpty : a;
	}
	public static char[] noEmpty(char[] a, char[] ifEmpty) {
		return isEmpty(a) ? ifEmpty : a;
	}
	public static boolean[] noEmpty(boolean[] a, boolean[] ifEmpty) {
		return isEmpty(a) ? ifEmpty : a;
	}

	public static boolean isSorted(int[] data) {
		return isSorted(data, 0, data.length);
	}
	public static boolean isSorted(int[] data, int start, int end) {
		for (int i = start + 1; i < end; i++)
			if (data[i - 1] > data[i])
				return false;
		return true;
	}
	public static boolean isSorted(long[] data) {
		return isSorted(data, 0, data.length);
	}
	public static boolean isSorted(long[] data, int start, int end) {
		for (int i = start + 1; i < end; i++)
			if (data[i - 1] > data[i])
				return false;
		return true;
	}
	public static boolean isSorted(byte[] data) {
		return isSorted(data, 0, data.length);
	}
	public static boolean isSorted(byte[] data, int start, int end) {
		for (int i = start + 1; i < end; i++)
			if (data[i - 1] > data[i])
				return false;
		return true;
	}
	public static boolean isSorted(short[] data) {
		return isSorted(data, 0, data.length);
	}
	public static boolean isSorted(short[] data, int start, int end) {
		for (int i = start + 1; i < end; i++)
			if (data[i - 1] > data[i])
				return false;
		return true;
	}
	public static boolean isSorted(Comparable[] data, int start, int end, boolean nullsLower) {
		if (end - start > 1)
			for (int i = start + 1; i < end; i++)
				if (OH.compare(data[i - 1], data[i], nullsLower) > 0)
					return false;
		return true;
	}

	public static boolean isSorted(double[] data) {
		return isSorted(data, 0, data.length);
	}
	public static boolean isSorted(double[] data, int start, int end) {
		for (int i = start + 1; i < end; i++)
			if (data[i - 1] > data[i])
				return false;
		return true;
	}

	public static boolean isSorted(float[] data) {
		return isSorted(data, 0, data.length);
	}
	public static boolean isSorted(float[] data, int start, int end) {
		for (int i = start + 1; i < end; i++)
			if (data[i - 1] > data[i])
				return false;
		return true;
	}

	public static boolean isSorted(char[] data) {
		return isSorted(data, 0, data.length);
	}
	public static boolean isSorted(char[] data, int start, int end) {
		for (int i = start + 1; i < end; i++)
			if (data[i - 1] > data[i])
				return false;
		return true;
	}

	public static char[] toSequence(char first, char last) {
		OH.assertLe(first, last);
		int len = last - first + 1;
		char[] r = new char[len];
		for (int i = 0; i < len; i++)
			r[i] = first++;
		return r;
	}

	public static char[] union(char[]... arrays) {
		int len = 0;
		for (char[] t : arrays)
			len += t.length;
		char[] r = new char[len];
		int pos = 0;
		for (char[] t : arrays) {
			System.arraycopy(t, 0, r, pos, t.length);
			pos += t.length;
		}
		return r;
	}

	public static void swap(byte[] a, int i, int j) {
		byte t = a[i];
		a[i] = a[j];
		a[j] = t;
	}
	public static void swap(short[] a, int i, int j) {
		short t = a[i];
		a[i] = a[j];
		a[j] = t;
	}
	public static void swap(int[] a, int i, int j) {
		int t = a[i];
		a[i] = a[j];
		a[j] = t;
	}
	public static void swap(long[] a, int i, int j) {
		long t = a[i];
		a[i] = a[j];
		a[j] = t;
	}
	public static void swap(float[] a, int i, int j) {
		float t = a[i];
		a[i] = a[j];
		a[j] = t;
	}
	public static void swap(double[] a, int i, int j) {
		double t = a[i];
		a[i] = a[j];
		a[j] = t;
	}
	public static void swap(char[] a, int i, int j) {
		char t = a[i];
		a[i] = a[j];
		a[j] = t;
	}
	public static void swap(boolean[] a, int i, int j) {
		boolean t = a[i];
		a[i] = a[j];
		a[j] = t;
	}
	public static void swap(Object[] a, int i, int j) {
		Object t = a[i];
		a[i] = a[j];
		a[j] = t;
	}

	//	public static void arraycopy(byte[] src, int srcPos, byte[] dest, int destPos, int len) {
	//		try {
	//			if (len < 10 && src != dest) {
	//				switch (len) {
	//					case 10:
	//						dest[destPos + 9] = src[srcPos + 9];
	//					case 9:
	//						dest[destPos + 8] = src[srcPos + 8];
	//					case 8:
	//						dest[destPos + 7] = src[srcPos + 7];
	//					case 7:
	//						dest[destPos + 6] = src[srcPos + 6];
	//					case 6:
	//						dest[destPos + 5] = src[srcPos + 5];
	//					case 5:
	//						dest[destPos + 4] = src[srcPos + 4];
	//					case 4:
	//						dest[destPos + 3] = src[srcPos + 3];
	//					case 3:
	//						dest[destPos + 2] = src[srcPos + 2];
	//					case 2:
	//						dest[destPos + 1] = src[srcPos + 1];
	//					case 1:
	//						dest[destPos + 0] = src[srcPos + 0];
	//				}
	//				//				if (src == dest) {
	//				//					if (srcPos > destPos)
	//				//						while (len-- > 0)
	//				//							dest[destPos++] = src[srcPos++];
	//				//					else
	//				//						while (len-- > 0)
	//				//							dest[destPos + len] = src[srcPos + len];
	//				//				} else
	//				//					while (len-- > 0)
	//				//						dest[destPos++] = src[srcPos++];
	//
	//			} else
	//				System.arraycopy(src, srcPos, dest, destPos, len);
	//		} catch (NullPointerException e) {
	//			if (src == null && dest == null)
	//				throw new NullPointerException("src and dest");
	//			if (src == null)
	//				throw new NullPointerException("src");
	//			if (dest == null)
	//				throw new NullPointerException("dest");
	//			throw e;
	//		} catch (IndexOutOfBoundsException e) {
	//			throw new IndexOutOfBoundsException("src.length=" + getLength(src) + ",srcPos=" + srcPos + ",dest.length=" + getLength(dest) + ",destPos=" + destPos + ",length=" + len);
	//		}
	//	}
	public static void arraycopy(Object src, int srcPos, Object dest, int destPos, int length) {
		if (src == null && dest == null)
			throw new NullPointerException("src and dest");
		if (src == null)
			throw new NullPointerException("src");
		if (dest == null)
			throw new NullPointerException("dest");
		try {
			System.arraycopy(src, srcPos, dest, destPos, length);
		} catch (IndexOutOfBoundsException e) {
			throw new IndexOutOfBoundsException(
					"src.length=" + getLength(src) + ",srcPos=" + srcPos + ",dest.length=" + getLength(dest) + ",destPos=" + destPos + ",length=" + length);
		}
	}

	static public <T extends Collection<Long>> T fill(T sink, long... values) {
		for (long v : values)
			sink.add(v);
		return sink;
	}
	static public <T extends Collection<Integer>> T fill(T sink, int... values) {
		for (int v : values)
			sink.add(v);
		return sink;
	}
	static public <T extends Collection<Short>> T fill(T sink, short... values) {
		for (short v : values)
			sink.add(v);
		return sink;
	}
	static public <T extends Collection<Byte>> T fill(T sink, byte... values) {
		for (byte v : values)
			sink.add(v);
		return sink;
	}
	static public <T extends Collection<Character>> T fill(T sink, char... values) {
		for (char v : values)
			sink.add(v);
		return sink;
	}
	static public <T extends Collection<Float>> T fill(T sink, float... values) {
		for (float v : values)
			sink.add(v);
		return sink;
	}
	static public <T extends Collection<Double>> T fill(T sink, double... values) {
		for (double v : values)
			sink.add(v);
		return sink;
	}
	static public <T extends Collection<Boolean>> T fill(T sink, boolean... values) {
		for (boolean v : values)
			sink.add(v);
		return sink;
	}

	public static <T> boolean endsWith(T[] array, T[] endsWith) {
		int pos = array.length - endsWith.length;
		return pos >= 0 && startsWith(array, endsWith, pos);
	}
	public static <T> boolean startsWith(T[] parts, T[] startsWith) {
		return startsWith(parts, startsWith, 0);
	}
	public static <T> boolean startsWith(T[] parts, T[] startsWith, int pos) {
		if (pos + startsWith.length > parts.length)
			return false;
		for (int i = 0; i < startsWith.length; i++)
			if (OH.ne(parts[i + pos], startsWith[i]))
				return false;
		return true;
	}

	//	public static void main(String[] a) {
	//		List<Long> col = CH.l(5L, 6L, 7L);
	//		Long[] ar = toArray(col, long.class);
	//		System.out.println(ar[2]);
	//	}

	public static byte[] copyOf(byte[] data, int start, int length) {
		final byte[] r = newBytes(length);
		arraycopy(data, start, r, 0, length);
		return r;
	}
	public static char[] copyOf(char[] data, int start, int length) {
		final char[] r = newChars(length);
		arraycopy(data, start, r, 0, length);
		return r;
	}
	public static double[] copyOf(double[] data, int start, int length) {
		final double[] r = newDoubles(length);
		arraycopy(data, start, r, 0, length);
		return r;
	}
	public static float[] copyOf(float[] data, int start, int length) {
		final float[] r = newFloats(length);
		arraycopy(data, start, r, 0, length);
		return r;
	}
	public static long[] copyOf(long[] data, int start, int length) {
		final long[] r = newLongs(length);
		arraycopy(data, start, r, 0, length);
		return r;
	}
	public static int[] copyOf(int[] data, int start, int length) {
		final int[] r = newInts(length);
		arraycopy(data, start, r, 0, length);
		return r;
	}
	public static short[] copyOf(short[] data, int start, int length) {
		final short[] r = newShorts(length);
		arraycopy(data, start, r, 0, length);
		return r;
	}
	public static boolean[] copyOf(boolean[] data, int start, int length) {
		final boolean[] r = newBooleans(length);
		arraycopy(data, start, r, 0, length);
		return r;
	}
	public static <T> T[] copy(T[] src, int srcPos, T[] dest, int destPos, int length) {
		arraycopy(src, srcPos, dest, destPos, length);
		return dest;
	}
	public static <T> void copy(List<T> src, int srcPos, T[] dest, int destPos, int length) {
		while (length-- > 0)
			dest[destPos + length] = src.get(srcPos + length);
	}

	public static void removeInplace(Object[] data, int position, int removeCount) {
		if (removeCount == 0)
			return;
		System.arraycopy(data, position + removeCount, data, position, data.length - position - removeCount);
		for (int i = 0; i < removeCount; i++)
			data[data.length - i - 1] = null;
	}

	public static <T> void assertNotNull(T[] data) {
		if (data == null)
			throw new NullPointerException("array");
		for (int i = 0; i < data.length; i++)
			if (data[i] == null)
				throw new NullPointerException("element " + i);
	}

	public static boolean eq(byte[] a1, int start1, byte[] a2, int start2, int length) {
		int end = start1 + length;
		if (end > a1.length || start2 + length > a2.length)
			return false;
		while (start1 != end)
			if (a1[start1++] != a2[start2++])
				return false;
		return true;
	}

	public static boolean eq(Object[] l, Object[] r) {
		if (l == r)
			return true;
		if (l == null || r == null)
			return false;
		final int len = l.length;
		if (r.length != len)
			return false;
		for (int i = 0; i < len; i++)
			if (!OH.eq(l[i], r[i]))
				return false;
		return true;
	}
	public static boolean eq(int[] l, int[] r) {
		if (l == r)
			return true;
		if (l == null || r == null)
			return false;
		final int len = l.length;
		if (r.length != len)
			return false;
		for (int i = 0; i < r.length; i++)
			if (l[i] != r[i])
				return false;
		return true;
	}
	public static boolean eq(long[] l, long[] r) {
		if (l == r)
			return true;
		if (l == null || r == null)
			return false;
		final int len = l.length;
		if (r.length != len)
			return false;
		for (int i = 0; i < r.length; i++)
			if (l[i] != r[i])
				return false;
		return true;
	}
	public static boolean eq(short[] l, short[] r) {
		if (l == r)
			return true;
		if (l == null || r == null)
			return false;
		final int len = l.length;
		if (r.length != len)
			return false;
		for (int i = 0; i < r.length; i++)
			if (l[i] != r[i])
				return false;
		return true;
	}
	public static boolean eq(float[] l, float[] r) {
		if (l == r)
			return true;
		if (l == null || r == null)
			return false;
		final int len = l.length;
		if (r.length != len)
			return false;
		for (int i = 0; i < r.length; i++)
			if (l[i] != r[i])
				return false;
		return true;
	}
	public static boolean eq(double[] l, double[] r) {
		if (l == r)
			return true;
		if (l == null || r == null)
			return false;
		final int len = l.length;
		if (r.length != len)
			return false;
		for (int i = 0; i < r.length; i++)
			if (l[i] != r[i])
				return false;
		return true;
	}
	public static boolean eq(char[] l, char[] r) {
		if (l == r)
			return true;
		if (l == null || r == null)
			return false;
		final int len = l.length;
		if (r.length != len)
			return false;
		for (int i = 0; i < r.length; i++)
			if (l[i] != r[i])
				return false;
		return true;
	}
	public static boolean eq(byte[] l, byte[] r) {
		if (l == r)
			return true;
		if (l == null || r == null)
			return false;
		final int len = l.length;
		if (r.length != len)
			return false;
		for (int i = 0; i < r.length; i++)
			if (l[i] != r[i])
				return false;
		return true;
	}
	public static boolean eq(boolean[] l, boolean[] r) {
		if (l == r)
			return true;
		if (l == null || r == null)
			return false;
		final int len = l.length;
		if (r.length != len)
			return false;
		for (int i = 0; i < r.length; i++)
			if (l[i] != r[i])
				return false;
		return true;
	}

	public static <T> boolean contains(T o, T[] nodes) {
		return contains(o, nodes, 0, nodes.length);
	}

	public static <T> boolean contains(T o, T[] nodes, int start, int end) {
		for (int i = start; i < end; i++)
			if (OH.eq(nodes[i], o))
				return true;
		return false;
	}

	public static <T> int indexOf(T o, T[] nodes, int start, int end) {
		for (int i = start; i < end; i++)
			if (OH.eq(nodes[i], o))
				return i;
		return -1;
	}

	public static <T> int lastIndexOf(T o, T[] nodes, int start, int end) {
		for (int i = end - 1; i >= start; i--)
			if (OH.eq(nodes[i], o))
				return i;
		return -1;
	}

	static public void reverse(Object o[]) {
		reverse(o, 0, o.length);
	}
	static public void reverse(Object o[], int start, int end) {
		for (int s = start, e = end - 1; s < e;) {
			Object tmp = o[s];
			o[s++] = o[e];
			o[e--] = tmp;
		}
	}

	public static <T> T[] castTo(Object[] values, Class<T> type) {
		if (values == null)
			return null;
		final T[] r = (T[]) Array.newInstance(type, values.length);
		for (int i = 0; i < values.length; i++)
			r[i] = (T) values[i];
		return r;
	}
	public static void shuffle(Object[] arr, Random rnd) {
		for (int i = arr.length; i > 1; i--)
			swap(arr, i - 1, rnd.nextInt(i));
	}

	static public <T> T[] insertNulls(T[] source, int position, int count) {
		return copyOf(source, source.length + count, position);
	}
	static public <T> T[] copyOf(T[] source, int newLength, int whereToStartPadding) {
		int len = source.length;
		if (whereToStartPadding == source.length || newLength <= len)
			return Arrays.copyOf(source, newLength);
		T[] r = (T[]) Array.newInstance(source.getClass().getComponentType(), newLength);
		if (whereToStartPadding == 0)
			System.arraycopy(source, 0, r, newLength - len, len);
		else {
			System.arraycopy(source, 0, r, 0, whereToStartPadding);
			System.arraycopy(source, whereToStartPadding, r, whereToStartPadding + newLength - len, len - whereToStartPadding);
		}
		return r;
	}
	static public <T> T[] appendList(T[] source, List<T> n) {
		return insertList(source, source.length, n);
	}
	static public <T> T[] insertList(T[] source, int index, List<T> n) {
		int len = source.length;
		final int len2 = CH.size(n);
		T[] r = copyOf(source, len + len2, index);
		copy(n, 0, r, index, len2);
		return r;
	}

	static public <T> T[] concat(T[]... values) {
		int size = 0;
		for (T[] v : values)
			if (v != null)
				size += v.length;
		T[] r = (T[]) Array.newInstance(values.getClass().getComponentType().getComponentType(), size);
		int pos = 0;
		for (T[] v : values)
			if (v != null) {
				System.arraycopy(v, 0, r, pos, v.length);
				pos += v.length;
			}
		return r;
	}
	static public long[] concat(long[]... values) {
		int size = 0;
		for (long[] v : values)
			if (v != null)
				size += v.length;
		if (size == 0)
			return OH.EMPTY_LONG_ARRAY;
		long[] r = new long[size];
		int pos = 0;
		for (long[] v : values)
			if (v != null) {
				System.arraycopy(v, 0, r, pos, v.length);
				pos += v.length;
			}
		return r;
	}
	static public int[] concat(int[]... values) {
		int size = 0;
		for (int[] v : values)
			if (v != null)
				size += v.length;
		if (size == 0)
			return OH.EMPTY_INT_ARRAY;
		int[] r = new int[size];
		int pos = 0;
		for (int[] v : values)
			if (v != null) {
				System.arraycopy(v, 0, r, pos, v.length);
				pos += v.length;
			}
		return r;
	}
	static public short[] concat(short[]... values) {
		int size = 0;
		for (short[] v : values)
			if (v != null)
				size += v.length;
		if (size == 0)
			return OH.EMPTY_SHORT_ARRAY;
		short[] r = new short[size];
		int pos = 0;
		for (short[] v : values)
			if (v != null) {
				System.arraycopy(v, 0, r, pos, v.length);
				pos += v.length;
			}
		return r;
	}
	static public double[] concat(double[]... values) {
		int size = 0;
		for (double[] v : values)
			if (v != null)
				size += v.length;
		if (size == 0)
			return OH.EMPTY_DOUBLE_ARRAY;
		double[] r = new double[size];
		int pos = 0;
		for (double[] v : values)
			if (v != null) {
				System.arraycopy(v, 0, r, pos, v.length);
				pos += v.length;
			}
		return r;
	}
	static public float[] concat(float[]... values) {
		int size = 0;
		for (float[] v : values)
			if (v != null)
				size += v.length;
		if (size == 0)
			return OH.EMPTY_FLOAT_ARRAY;
		float[] r = new float[size];
		int pos = 0;
		for (float[] v : values)
			if (v != null) {
				System.arraycopy(v, 0, r, pos, v.length);
				pos += v.length;
			}
		return r;
	}
	static public byte[] concat(byte[]... values) {
		int size = 0;
		for (byte[] v : values)
			if (v != null)
				size += v.length;
		if (size == 0)
			return OH.EMPTY_BYTE_ARRAY;
		byte[] r = new byte[size];
		int pos = 0;
		for (byte[] v : values)
			if (v != null) {
				System.arraycopy(v, 0, r, pos, v.length);
				pos += v.length;
			}
		return r;
	}
	static public char[] concat(char[]... values) {
		int size = 0;
		for (char[] v : values)
			if (v != null)
				size += v.length;
		if (size == 0)
			return OH.EMPTY_CHAR_ARRAY;
		char[] r = new char[size];
		int pos = 0;
		for (char[] v : values)
			if (v != null) {
				System.arraycopy(v, 0, r, pos, v.length);
				pos += v.length;
			}
		return r;
	}
	static public boolean[] concat(boolean[]... values) {
		int size = 0;
		for (boolean[] v : values)
			if (v != null)
				size += v.length;
		if (size == 0)
			return OH.EMPTY_BOOLEAN_ARRAY;
		boolean[] r = new boolean[size];
		int pos = 0;
		for (boolean[] v : values)
			if (v != null) {
				System.arraycopy(v, 0, r, pos, v.length);
				pos += v.length;
			}
		return r;
	}

	static public void reverse(byte o[]) {
		reverse(o, 0, o.length);
	}
	static public void reverse(byte o[], int start, int end) {
		for (int s = start, e = end - 1; s < e;) {
			byte tmp = o[s];
			o[s++] = o[e];
			o[e--] = tmp;
		}
	}
	static public void reverse(boolean o[]) {
		reverse(o, 0, o.length);
	}
	static public void reverse(boolean o[], int start, int end) {
		for (int s = start, e = end - 1; s < e;) {
			boolean tmp = o[s];
			o[s++] = o[e];
			o[e--] = tmp;
		}
	}
	static public void reverse(float o[]) {
		reverse(o, 0, o.length);
	}
	static public void reverse(float o[], int start, int end) {
		for (int s = start, e = end - 1; s < e;) {
			float tmp = o[s];
			o[s++] = o[e];
			o[e--] = tmp;
		}
	}
	static public void reverse(double o[]) {
		reverse(o, 0, o.length);
	}
	static public void reverse(double o[], int start, int end) {
		for (int s = start, e = end - 1; s < e;) {
			double tmp = o[s];
			o[s++] = o[e];
			o[e--] = tmp;
		}
	}
	static public void reverse(long o[]) {
		reverse(o, 0, o.length);
	}
	static public void reverse(long o[], int start, int end) {
		for (int s = start, e = end - 1; s < e;) {
			long tmp = o[s];
			o[s++] = o[e];
			o[e--] = tmp;
		}
	}
	static public void reverse(int o[]) {
		reverse(o, 0, o.length);
	}
	static public void reverse(int o[], int start, int end) {
		for (int s = start, e = end - 1; s < e;) {
			int tmp = o[s];
			o[s++] = o[e];
			o[e--] = tmp;
		}
	}
	static public void reverse(short o[]) {
		reverse(o, 0, o.length);
	}
	static public void reverse(short o[], int start, int end) {
		for (int s = start, e = end - 1; s < e;) {
			short tmp = o[s];
			o[s++] = o[e];
			o[e--] = tmp;
		}
	}
	static public void reverse(char o[]) {
		reverse(o, 0, o.length);
	}
	static public void reverse(char o[], int start, int end) {
		for (int s = start, e = end - 1; s < e;) {
			char tmp = o[s];
			o[s++] = o[e];
			o[e--] = tmp;
		}
	}

	public static <T> T[] enssureCapacity(T[] array, int length, int growBy) {
		return length <= array.length ? array : Arrays.copyOf(array, Math.max(array.length, length) * growBy);
	}
	public static boolean[] enssureCapacity(boolean[] array, int length, int growBy) {
		return length <= array.length ? array : Arrays.copyOf(array, Math.max(array.length, length) * growBy);
	}
	public static char[] enssureCapacity(char[] array, int length, int growBy) {
		return length <= array.length ? array : Arrays.copyOf(array, Math.max(array.length, length) * growBy);
	}
	public static float[] enssureCapacity(float[] array, int length, int growBy) {
		return length <= array.length ? array : Arrays.copyOf(array, Math.max(array.length, length) * growBy);
	}
	public static double[] enssureCapacity(double[] array, int length, int growBy) {
		return length <= array.length ? array : Arrays.copyOf(array, Math.max(array.length, length) * growBy);
	}
	public static byte[] enssureCapacity(byte[] array, int length, int growBy) {
		return length <= array.length ? array : Arrays.copyOf(array, Math.max(array.length, length) * growBy);
	}
	public static short[] enssureCapacity(short[] array, int length, int growBy) {
		return length <= array.length ? array : Arrays.copyOf(array, Math.max(array.length, length) * growBy);
	}
	public static int[] enssureCapacity(int[] array, int length, int growBy) {
		return length <= array.length ? array : Arrays.copyOf(array, Math.max(array.length, length) * growBy);
	}
	public static long[] enssureCapacity(long[] array, int length, int growBy) {
		return length <= array.length ? array : Arrays.copyOf(array, Math.max(array.length, length) * growBy);
	}

	public static void assertStartAndLength(int arrayLength, int off, int len) {
		if (off < 0 || len < 0 || off + len > arrayLength)
			throw new IndexOutOfBoundsException("invalid offset (" + off + ") and length(" + len + ") for given array length: " + arrayLength);

	}

	//may return same map
	public static <T> T[] removeAll(T[] values, T value) {
		int cnt = 0;
		for (T t : values)
			if (OH.eq(t, value))
				cnt++;
		if (cnt == 0)
			return values;
		T[] r = newInstance(values, values.length - cnt);
		cnt = 0;
		if (r.length > 0)
			for (T t : values)
				if (OH.ne(t, value))
					r[cnt++] = t;
		return r;
	}

	public static boolean[] insertBooleans(boolean[] bytes, int start, int count) {
		int len = bytes.length;
		if (start > len)
			throw new IndexOutOfBoundsException("start: " + start + " > " + len);
		else if (count == 0)
			return bytes.clone();
		else if (start == len)
			return Arrays.copyOf(bytes, len + count);
		final boolean[] r = new boolean[len + count];
		if (start == 0)
			System.arraycopy(bytes, 0, r, count, len);
		else {
			System.arraycopy(bytes, 0, r, 0, start);
			System.arraycopy(bytes, start, r, start + count, len - start);
		}
		return r;
	}
	public static char[] insertChars(char[] bytes, int start, int count) {
		int len = bytes.length;
		if (start > len)
			throw new IndexOutOfBoundsException("start: " + start + " > " + len);
		else if (count == 0)
			return bytes.clone();
		else if (start == len)
			return Arrays.copyOf(bytes, len + count);
		final char[] r = new char[len + count];
		if (start == 0)
			System.arraycopy(bytes, 0, r, count, len);
		else {
			System.arraycopy(bytes, 0, r, 0, start);
			System.arraycopy(bytes, start, r, start + count, len - start);
		}
		return r;
	}
	public static float[] insertFloats(float[] bytes, int start, int count) {
		int len = bytes.length;
		if (start > len)
			throw new IndexOutOfBoundsException("start: " + start + " > " + len);
		else if (count == 0)
			return bytes.clone();
		else if (start == len)
			return Arrays.copyOf(bytes, len + count);
		final float[] r = new float[len + count];
		if (start == 0)
			System.arraycopy(bytes, 0, r, count, len);
		else {
			System.arraycopy(bytes, 0, r, 0, start);
			System.arraycopy(bytes, start, r, start + count, len - start);
		}
		return r;
	}
	public static double[] insertDoubles(double[] bytes, int start, int count) {
		int len = bytes.length;
		if (start > len)
			throw new IndexOutOfBoundsException("start: " + start + " > " + len);
		else if (count == 0)
			return bytes.clone();
		else if (start == len)
			return Arrays.copyOf(bytes, len + count);
		final double[] r = new double[len + count];
		if (start == 0)
			System.arraycopy(bytes, 0, r, count, len);
		else {
			System.arraycopy(bytes, 0, r, 0, start);
			System.arraycopy(bytes, start, r, start + count, len - start);
		}
		return r;
	}
	public static byte[] insertBytes(byte[] bytes, int start, int count) {
		int len = bytes.length;
		if (start > len)
			throw new IndexOutOfBoundsException("start: " + start + " > " + len);
		else if (count == 0)
			return bytes.clone();
		else if (start == len)
			return Arrays.copyOf(bytes, len + count);
		final byte[] r = new byte[len + count];
		if (start == 0)
			System.arraycopy(bytes, 0, r, count, len);
		else {
			System.arraycopy(bytes, 0, r, 0, start);
			System.arraycopy(bytes, start, r, start + count, len - start);
		}
		return r;
	}
	public static short[] insertShorts(short[] bytes, int start, int count) {
		int len = bytes.length;
		if (start > len)
			throw new IndexOutOfBoundsException("start: " + start + " > " + len);
		else if (count == 0)
			return bytes.clone();
		else if (start == len)
			return Arrays.copyOf(bytes, len + count);
		final short[] r = new short[len + count];
		if (start == 0)
			System.arraycopy(bytes, 0, r, count, len);
		else {
			System.arraycopy(bytes, 0, r, 0, start);
			System.arraycopy(bytes, start, r, start + count, len - start);
		}
		return r;
	}
	public static int[] insertInts(int[] bytes, int start, int count) {
		int len = bytes.length;
		if (start > len)
			throw new IndexOutOfBoundsException("start: " + start + " > " + len);
		else if (count == 0)
			return bytes.clone();
		else if (start == len)
			return Arrays.copyOf(bytes, len + count);
		final int[] r = new int[len + count];
		if (start == 0)
			System.arraycopy(bytes, 0, r, count, len);
		else {
			System.arraycopy(bytes, 0, r, 0, start);
			System.arraycopy(bytes, start, r, start + count, len - start);
		}
		return r;
	}
	public static long[] insertLongs(long[] bytes, int start, int count) {
		int len = bytes.length;
		if (start > len)
			throw new IndexOutOfBoundsException("start: " + start + " > " + len);
		else if (count == 0)
			return bytes.clone();
		else if (start == len)
			return Arrays.copyOf(bytes, len + count);
		final long[] r = new long[len + count];
		if (start == 0)
			System.arraycopy(bytes, 0, r, count, len);
		else {
			System.arraycopy(bytes, 0, r, 0, start);
			System.arraycopy(bytes, start, r, start + count, len - start);
		}
		return r;
	}

	public static Object arraycopy(Object values, int start, int length) {
		Object values2 = Array.newInstance(values.getClass().getComponentType(), length);
		System.arraycopy(values, start, values2, 0, length);
		return values2;
	}
}
