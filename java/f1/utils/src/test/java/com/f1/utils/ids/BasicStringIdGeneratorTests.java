package com.f1.utils.ids;

import static org.junit.Assert.*;
import org.junit.Test;
import com.f1.utils.SH;

public class BasicStringIdGeneratorTests {

	@Test
	public void testRemoveVowelsLower() {
		StringBuilder sb = new StringBuilder(SH.ALPHA_LOWER);
		BasicStringIdGenerator.removeVowels(sb, 0, sb.length());
		assertEquals("bcdfghjklmnpqrstvwxz" + (char) ('z' + 1) + (char) ('z' + 2) + (char) ('z' + 3) + (char) ('z' + 4) + (char) ('z' + 5) + (char) ('z' + 6),
				sb.toString());
		System.out.println(sb);
	}

	@Test
	public void testRemoveVowelsUpper() {
		StringBuilder sb = new StringBuilder(SH.ALPHA_UPPER);
		BasicStringIdGenerator.removeVowels(sb, 0, sb.length());
		assertEquals("BCDFGHJKLMNPQRSTVWXZ" + (char) ('Z' + 1) + (char) ('Z' + 2) + (char) ('Z' + 3) + (char) ('Z' + 4) + (char) ('Z' + 5) + (char) ('Z' + 6),
				sb.toString());
		System.out.println(sb);
	}
}

