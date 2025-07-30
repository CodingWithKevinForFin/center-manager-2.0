package com.f1.utils.structs;

import static org.junit.Assert.*;
import java.util.BitSet;
import org.junit.Test;

public class ArrayBitSetTests {
	static boolean F = false, T = true;

	@Test
	public void testNew1() {
		test();
	}

	@Test
	public void testNew2() {
		test(F);
	}

	@Test
	public void testNew3() {
		test(T);
	}

	@Test
	public void testNew4() {
		test(T, T, T, T);
	}

	@Test
	public void testNew5() {
		test(F, F, F, F);
	}

	@Test
	public void testNew6() {
		test(T, F, T, F);
	}

	@Test
	public void testNew7() {
		test(F, T, F, T, F);
	}

	@Test
	public void testNew8() {
		test(T, F, T, F, T, F, T);
	}

	private void test(boolean... a) {
		ArrayBitSet fbs = new ArrayBitSet(a);
		for (int i = 0; i < a.length; i++)
			assertEquals(a[i], fbs.get(i));
		BitSet bs = newBitSet(a);
		assertEquals(bs.length(), fbs.length());
	}

	private BitSet newBitSet(boolean[] a) {
		BitSet r = new BitSet(a.length);
		for (int i = 0; i < a.length; i++)
			if (a[i])
				r.set(i);
		return r;
	}

}
