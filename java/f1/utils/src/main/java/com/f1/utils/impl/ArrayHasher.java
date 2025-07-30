package com.f1.utils.impl;

import java.util.Arrays;

import com.f1.utils.AH;
import com.f1.utils.Hasher;

public class ArrayHasher implements Hasher<Object[]> {

	public static final ArrayHasher INSTANCE = new ArrayHasher();
	public static final ByteArrayHasher BYTE_INSTANCE = new ByteArrayHasher();
	public static final ShortArrayHasher SHORT_INSTANCE = new ShortArrayHasher();
	public static final IntArrayHasher INT_INSTANCE = new IntArrayHasher();
	public static final LongArrayHasher LONG_INSTANCE = new LongArrayHasher();
	public static final CharArrayHasher CHAR_INSTANCE = new CharArrayHasher();
	public static final FloatArrayHasher FLOAT_INSTANCE = new FloatArrayHasher();
	public static final DoubleArrayHasher DOUBLE_INSTANCE = new DoubleArrayHasher();
	public static final BooleanArrayHasher BOOLEAN_INSTANCE = new BooleanArrayHasher();

	@Override
	public int hashcode(Object[] o) {
		return Arrays.hashCode(o);
	}

	@Override
	public boolean areEqual(Object[] l, Object[] r) {
		return AH.eq(l, r);
	}

	public static class ByteArrayHasher implements Hasher<byte[]> {

		@Override
		public int hashcode(byte[] o) {
			return Arrays.hashCode(o);
		}

		@Override
		public boolean areEqual(byte[] l, byte[] r) {
			return AH.eq(l, r);
		}

	}

	public static class ShortArrayHasher implements Hasher<short[]> {

		@Override
		public int hashcode(short[] o) {
			return Arrays.hashCode(o);
		}

		@Override
		public boolean areEqual(short[] l, short[] r) {
			return AH.eq(l, r);
		}

	}

	public static class IntArrayHasher implements Hasher<int[]> {

		@Override
		public int hashcode(int[] o) {
			return Arrays.hashCode(o);
		}

		@Override
		public boolean areEqual(int[] l, int[] r) {
			return AH.eq(l, r);
		}

	}

	public static class LongArrayHasher implements Hasher<long[]> {

		@Override
		public int hashcode(long[] o) {
			return Arrays.hashCode(o);
		}

		@Override
		public boolean areEqual(long[] l, long[] r) {
			return AH.eq(l, r);
		}

	}

	public static class DoubleArrayHasher implements Hasher<double[]> {

		@Override
		public int hashcode(double[] o) {
			return Arrays.hashCode(o);
		}

		@Override
		public boolean areEqual(double[] l, double[] r) {
			return AH.eq(l, r);
		}

	}

	public static class FloatArrayHasher implements Hasher<float[]> {

		@Override
		public int hashcode(float[] o) {
			return Arrays.hashCode(o);
		}

		@Override
		public boolean areEqual(float[] l, float[] r) {
			return AH.eq(l, r);
		}

	}

	public static class CharArrayHasher implements Hasher<char[]> {

		@Override
		public int hashcode(char[] o) {
			return Arrays.hashCode(o);
		}

		@Override
		public boolean areEqual(char[] l, char[] r) {
			return AH.eq(l, r);
		}

	}

	public static class BooleanArrayHasher implements Hasher<boolean[]> {

		@Override
		public int hashcode(boolean[] o) {
			return Arrays.hashCode(o);
		}

		@Override
		public boolean areEqual(boolean[] l, boolean[] r) {
			return AH.eq(l, r);
		}

	}

}
