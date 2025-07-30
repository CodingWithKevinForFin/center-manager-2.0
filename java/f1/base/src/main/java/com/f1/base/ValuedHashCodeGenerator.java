package com.f1.base;

import java.util.Random;

public class ValuedHashCodeGenerator {

	public static final int RAND_DEPTH = 16;//65536
	private static final int[] RAND = new int[1 << RAND_DEPTH];
	static {
		Random r = new Random(1);
		for (int i = 0; i < RAND.length; i++)
			RAND[i] = r.nextInt();
	}

	static public int rand(long i) {
		return RAND[(int) (i & ((1 << ValuedHashCodeGenerator.RAND_DEPTH) - 1))];
	}

	private static int i = 1;

	public static int next(Valued v) {
		return ValuedHashCodeGenerator.rand(i++) + ValuedHashCodeGenerator.rand((i++ >> ValuedHashCodeGenerator.RAND_DEPTH)) + (i++);
	}

}
