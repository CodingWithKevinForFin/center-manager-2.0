package com.f1.utils;

import java.util.Random;

import junit.framework.Assert;

import org.junit.Test;

public class VidParserTests {

	@Test
	public void test() {
		System.out.println(VidParser.toLong("SF.ST.IDANSW"));
		System.out.println(VidParser.toLong("SF.ST.IDANSA"));
		for (int len = 2; len <= 12; len++)
			for (int i = 0; i < 37; i++)
				assertSame(SH.repeat(c(i), len));
		System.out.println(VidParser.fromLong(-1));
		Random r = new Random(123);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 1000000; i++) {
			for (int j = 0; j < 2 + 2 * r.nextInt(6); j++) {
				sb.append(c(r.nextInt(37)));
			}
			assertSame(sb);
			sb.setLength(0);
		}

	}
	private char c(int i) {
		if (i < 10)
			return (char) ('0' + i);
		if (i == 36)
			return '.';
		return (char) ('A' + (i - 10));
	}
	public void assertSame(CharSequence text) {
		long id = VidParser.toLong(text);
		String text2 = VidParser.fromLong(id);
		Assert.assertEquals(text.toString(), text2);
	}

	@Test
	public void test3() {
		long id = VidParser.toLong("AB");
	}

	@Test(expected = Exception.class)
	public void test4() {
		long id2 = VidParser.toLong("1234567890123");
	}
}
