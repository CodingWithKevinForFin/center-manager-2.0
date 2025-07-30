package com.f1.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class BitMaskDescriptionTests {

	@Test
	public void test1() {
		BitMaskDescription bm = new BitMaskDescription("TEST", ',', 32);
		bm.define(1, "ONE");
		bm.define(2, "TWO");
		bm.define(4, "FOUR");
		bm.define(8, "EIGHT");
		assertEquals("ONE", bm.toString(1));
		assertEquals("TWO", bm.toString(2));
		assertEquals("ONE,TWO", bm.toString(3));
		assertEquals("ONE,EIGHT", bm.toString(9));
		assertEquals("ONE,TWO,EIGHT", bm.toString(11));
		assertEquals("ONE,0x10", bm.toString(17));
		System.out.println(bm.getDescriptions());
	}

	@Test(expected = IllegalArgumentException.class)
	public void test2() {
		BitMaskDescription bm = new BitMaskDescription("TEST", ',', 32);
		bm.define(3, "ONE");
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void test3() {
		BitMaskDescription bm = new BitMaskDescription("TEST", ',', 2);
		bm.define(4, "ONE");
	}
}
