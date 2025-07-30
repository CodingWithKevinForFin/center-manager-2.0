package com.f1.utils;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Test;

import com.f1.utils.assist.RootAssister;

public class RHTests {

	@Test
	public void testToLegible() {
		assertEquals("com.f1.utils.RHTests", RH.toLegibleString(RHTests.class));
		assertEquals("StringBuilder", RH.toLegibleString(StringBuilder.class));
		assertEquals("Integer[]", RH.toLegibleString(Integer[].class));
		assertEquals("int[]", RH.toLegibleString(int[].class));
		assertEquals("java.util.Map.Entry", RH.toLegibleString(Map.Entry.class));
		assertEquals("java.util.Map.Entry[][]", RH.toLegibleString(Map.Entry[][].class));
		assertEquals("java.util.Map.Entry[][]", RH.toLegibleString(Map.Entry[][].class));
	}

	@Test
	public void testInvoke() {
		RH.invokeConstructor(BasicPointer.class.getName(), "hello");
		RH.invokeConstructor(BasicPointer.class.getName(), (String) null);
	}

	@Test
	public void testArray() {

		assertArrayEquals(new String[2][0], new String[2][0]);

		String[][][][][][] a = (String[][][][][][]) RH.newArray("java.lang.String", 4, 2, 0, 0, null, null);
		System.out.println(RootAssister.INSTANCE.toLegibleString(a, 10000));
	}

	String s;

}
