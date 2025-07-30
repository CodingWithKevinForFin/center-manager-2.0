package com.f1.utils;

import static org.junit.Assert.assertEquals;

import java.awt.Color;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

public class ColorHelperTests {

	@Test
	public void testHasAlpha() {
		Assert.assertFalse(ColorHelper.hasAlpha("#ffffff"));
		Assert.assertFalse(ColorHelper.hasAlpha(0xffffffff));
		int t = ColorHelper.colorDodgeRgb(ColorHelper.parseRgb("#fff98a"));
		System.out.println(ColorHelper.toString(t));
	}

	@Test
	public void testString() {
		Random r = new Random(123);
		test(r, true, 3);
		test(r, true, 4);
		test(r, true, 6);
		test(r, true, 8);
		test(r, false, 3);
		test(r, false, 4);
		test(r, false, 6);
		test(r, false, 8);
	}

	private void test(Random r, boolean hasPound, int chars) {
		StringBuilder expected = new StringBuilder();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 100000; i++) {
			expected.setLength(0);
			sb.setLength(0);
			if (hasPound)
				sb.append('#');
			expected.append('#');
			for (int n = 0; n < chars; n++) {
				char c = nextChar(r);
				sb.append(c);
				expected.append(c);
				if (chars < 6) {
					expected.append(c);
				}
			}
			if (expected.length() == 9 && SH.endsWith(expected, "ff")) {
				expected.setLength(7);
			}
			test(i, sb.toString(), expected.toString());
		}

	}
	@Test
	public void testHSL() {
		testHSL(210.005, 100, 3.923, 0, 10, 20);
		testHSL(20.10, 100, 62.549, 255, 128, 64);
		testHSL(300.003, 100, 25.099, 128, 0, 128);
		testHSL(0, 100, 50, 255, 0, 0);
		testHSL(0, 0, 100, 255, 255, 255);
		testHSL(0, 0, 0, 0, 0, 0);
		testHSL(0, 0, 50.197, 128, 128, 128);
	}
	public void testHSL(double h, double s, double l, int r, int g, int b) {
		int ih = (int) (h / 360d * 65535);
		int is = (int) (s / 100d * 65535);
		int il = (int) (l / 100d * 65535);

		int rgb = ColorHelper.toRgb(r, g, b);
		long hsl = ColorHelper.rgb2Hsl(rgb);
		long hsl2 = ColorHelper.toHsl(ih, is, il);
		int rgb2 = ColorHelper.hsl2Rgb(ih, is, il);
		assertEquals(hsl, hsl2);
		assertEquals(rgb, rgb2);
		//		OH.assertEq(ih, ColorHelper.getH(hsl));
		//		OH.assertEq(is, ColorHelper.getS(hsl));
		//		OH.assertEq(il, ColorHelper.getL(hsl));
		//		OH.assertEq(ColorHelper.getR(rgb), ColorHelper.getR(rgb2));
		//		OH.assertEq(ColorHelper.getG(rgb), ColorHelper.getG(rgb2));
		//		OH.assertEq(ColorHelper.getB(rgb), ColorHelper.getB(rgb2));
	}

	private char nextChar(Random r) {
		int n = r.nextInt(16);
		return n < 10 ? (char) (n + '0') : (char) (n - 10 + 'a');
	}

	private void test(int n, String s, String expected) {
		Color c = ColorHelper.parseColor(s);
		String s2 = ColorHelper.toString(c);
		int cn = ColorHelper.parseRgb(s);
		String s3 = ColorHelper.toString(cn);
		Color c2 = ColorHelper.parseColor(s);
		OH.assertEq(c, c2);
		OH.assertEq(s2, expected);
		OH.assertEq(s3, expected);
		if (n % 10000 == 0)
			System.out.println(s + " vs " + expected + " color=" + c2 + "," + c2.getAlpha());
	}

	public static void main(String a[]) {
		//		String s = "#abc";
		String s = "#aabbcc";
		int n = ColorHelper.parseRgb(s);
		System.out.println(ColorHelper.getR(n));
		System.out.println(ColorHelper.getG(n));
		System.out.println(ColorHelper.getB(n));
		System.out.println(ColorHelper.getA(n));

	}
}
