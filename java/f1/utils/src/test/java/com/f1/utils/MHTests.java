package com.f1.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Random;

import org.junit.Ignore;
import org.junit.Test;

import com.f1.utils.impl.PerfTest;

public class MHTests {

	@Test
	public void testAll() {
		assertTrue(MH.areAllBitsSet(1 | 2 | 4, 1 | 2 | 4));
		assertTrue(MH.areAllBitsSet(1 | 2 | 4 | 8, 1 | 2 | 4));
		assertFalse(MH.areAllBitsSet(1 | 2, 1 | 2 | 4));
		assertTrue(MH.areAllBitsSet(1 | 2, 0));
		assertFalse(MH.areAllBitsSet(1 | 2, 8));
		assertTrue(MH.areAllBitsSet(0, 0));
		assertTrue(MH.areAllBitsSet(1, 0));
		assertFalse(MH.areAllBitsSet(0, 1));
		assertTrue(MH.areAllBitsSet(1, 1));
	}

	@Test
	public void testAny() {
		assertTrue(MH.areAnyBitsSet(1 | 2 | 4, 1 | 2 | 4));
		assertTrue(MH.areAnyBitsSet(1 | 2 | 4 | 8, 1 | 2 | 4));
		assertTrue(MH.areAnyBitsSet(1 | 2, 1 | 2 | 4));
		assertFalse(MH.areAnyBitsSet(0, 0));
		assertFalse(MH.areAnyBitsSet(1, 0));
		assertFalse(MH.areAnyBitsSet(0, 1));
		assertTrue(MH.areAnyBitsSet(1, 1));
	}

	@Test
	public void testFirstBit() {
		for (int i = 0; i < 64; i++)
			assertEquals(i, MH.indexOfBitSet(1L << i, 0));
		assertEquals(0, MH.indexOfBitSet(0x90000001, 0));
		assertEquals(-1, MH.indexOfBitSet(0, 0));
		assertEquals(0, MH.indexOfBitSet(0x00000001, 0));
		assertEquals(1, MH.indexOfBitSet(0x00000002, 0));
		assertEquals(2, MH.indexOfBitSet(0x00000004, 0));
		assertEquals(3, MH.indexOfBitSet(0x00000008, 0));
		assertEquals(4, MH.indexOfBitSet(0x00000010, 0));
		assertEquals(5, MH.indexOfBitSet(0x00000020, 0));
		assertEquals(6, MH.indexOfBitSet(0x00000040, 0));
		assertEquals(7, MH.indexOfBitSet(0x00000080, 0));
		assertEquals(8, MH.indexOfBitSet(0x00000100, 0));
		assertEquals(9, MH.indexOfBitSet(0x00000200, 0));
		assertEquals(10, MH.indexOfBitSet(0x00000400, 0));
		assertEquals(11, MH.indexOfBitSet(0x00000800, 0));
		assertEquals(12, MH.indexOfBitSet(0x00001000, 0));
		assertEquals(13, MH.indexOfBitSet(0x00002000, 0));
		assertEquals(14, MH.indexOfBitSet(0x00004000, 0));
		assertEquals(15, MH.indexOfBitSet(0x00008000, 0));
		assertEquals(16, MH.indexOfBitSet(0x00010000, 0));
		assertEquals(17, MH.indexOfBitSet(0x00020000, 0));
		assertEquals(18, MH.indexOfBitSet(0x00040000, 0));
		assertEquals(19, MH.indexOfBitSet(0x00080000, 0));
		assertEquals(20, MH.indexOfBitSet(0x00100000, 0));
		assertEquals(21, MH.indexOfBitSet(0x00200000, 0));
		assertEquals(22, MH.indexOfBitSet(0x00400000, 0));
		assertEquals(23, MH.indexOfBitSet(0x00800000, 0));
		assertEquals(24, MH.indexOfBitSet(0x01000000, 0));
		assertEquals(25, MH.indexOfBitSet(0x02000000, 0));
		assertEquals(26, MH.indexOfBitSet(0x04000000, 0));
		assertEquals(27, MH.indexOfBitSet(0x08000000, 0));
		assertEquals(28, MH.indexOfBitSet(0x10000000, 0));
		assertEquals(29, MH.indexOfBitSet(0x20000000, 0));
		assertEquals(30, MH.indexOfBitSet(1 << 30, 0));
		assertEquals(31, MH.indexOfBitSet(0x80000000, 0));
		assertEquals(8, MH.indexOfBitSet(0x80032300, 0));
		assertEquals(9, MH.indexOfBitSet(0x80144200, 0));
		assertEquals(-1, MH.indexOfBitSet(0x00000001, 4));
		assertEquals(-1, MH.indexOfBitSet(0x00000002, 4));
		assertEquals(-1, MH.indexOfBitSet(0x00000004, 4));
		assertEquals(-1, MH.indexOfBitSet(0x00000008, 4));
		assertEquals(4, MH.indexOfBitSet(0x00000010, 4));
		assertEquals(5, MH.indexOfBitSet(0x00000020, 4));
		assertEquals(6, MH.indexOfBitSet(0x00000040, 4));
		assertEquals(7, MH.indexOfBitSet(0x00000080, 4));
		assertEquals(8, MH.indexOfBitSet(0x00000100, 4));
		assertEquals(9, MH.indexOfBitSet(0x00000200, 4));
		assertEquals(10, MH.indexOfBitSet(0x00000400, 4));
	}

	@Test
	public void testLastBit() {
		assertEquals(0, MH.indexOfBitSet(0x90000001, 0));
		textIndexOfLastBitSet(0, (0x00000001));
		textIndexOfLastBitSet(1, (0x00000002));
		textIndexOfLastBitSet(2, (0x00000004));
		textIndexOfLastBitSet(3, (0x00000008));
		textIndexOfLastBitSet(4, (0x00000010));
		textIndexOfLastBitSet(5, (0x00000020));
		textIndexOfLastBitSet(6, (0x00000040));
		textIndexOfLastBitSet(7, (0x00000080));
		textIndexOfLastBitSet(8, (0x00000100));
		textIndexOfLastBitSet(9, (0x00000200));
		textIndexOfLastBitSet(10, (0x00000400));
		textIndexOfLastBitSet(11, (0x00000800));
		textIndexOfLastBitSet(12, (0x00001000));
		textIndexOfLastBitSet(13, (0x00002000));
		textIndexOfLastBitSet(14, (0x00004000));
		textIndexOfLastBitSet(15, (0x00008000));
		textIndexOfLastBitSet(16, (0x00010000));
		textIndexOfLastBitSet(17, (0x00020000));
		textIndexOfLastBitSet(18, (0x00040000));
		textIndexOfLastBitSet(19, (0x00080000));
		textIndexOfLastBitSet(20, (0x00100000));
		textIndexOfLastBitSet(21, (0x00200000));
		textIndexOfLastBitSet(22, (0x00400000));
		textIndexOfLastBitSet(23, (0x00800000));
		textIndexOfLastBitSet(24, (0x01000000));
		textIndexOfLastBitSet(25, (0x02000000));
		textIndexOfLastBitSet(26, (0x04000000));
		textIndexOfLastBitSet(27, (0x08000000));
		textIndexOfLastBitSet(28, (0x10000000));
		textIndexOfLastBitSet(29, (0x20000000));
		assertEquals(30, MH.indexOfBitSet(0x40000000, 0));
		assertEquals(31, MH.indexOfBitSet(0x80000000, 0));
	}

	public void textIndexOfLastBitSet(int expected, int actual) {
		assertEquals(expected, MH.indexOfLastBitSet(actual));
		assertEquals(expected, MH.indexOfLastBitSet(actual | (1 << 31)));
		assertEquals(expected, MH.indexOfLastBitSet(actual | (1 << 31) | (1 << 29)));
		assertEquals(expected, MH.indexOfLastBitSet(actual | (1 << 31) | (1 << 29) | (1 << (expected + 1))));
	}

	@Test
	public void testIndexOfLastBitSet() {
		assertEquals(0, MH.indexOfLastBitSet(1));
		assertEquals(1, MH.indexOfLastBitSet(2));
		assertEquals(2, MH.indexOfLastBitSet(4));
		assertEquals(3, MH.indexOfLastBitSet(8));
		assertEquals(4, MH.indexOfLastBitSet(16));
		assertEquals(5, MH.indexOfLastBitSet(32));
		assertEquals(6, MH.indexOfLastBitSet(64));
		assertEquals(7, MH.indexOfLastBitSet(128));
		assertEquals(8, MH.indexOfLastBitSet(256));
		assertEquals(9, MH.indexOfLastBitSet(512));
		assertEquals(0, MH.indexOfLastBitSet(1));
		assertEquals(0, MH.indexOfLastBitSet(3));
		assertEquals(0, MH.indexOfLastBitSet(5));
		assertEquals(1, MH.indexOfLastBitSet(6));
		assertEquals(2, MH.indexOfLastBitSet(20));
		assertEquals(6, MH.indexOfLastBitSet(512 + 64));
		assertEquals(1, MH.indexOfLastBitSet(66));
		assertEquals(0, MH.indexOfLastBitSet(129));
		assertEquals(0, MH.indexOfLastBitSet(257));
		assertEquals(0, MH.indexOfLastBitSet(513));
		assertEquals(10, MH.indexOfLastBitSet(1024));
		assertEquals(3, MH.indexOfLastBitSet(408));
	}

	@Test
	public void testFirstBitStart() {
		assertEquals(-1, MH.indexOfBitSet(0x00000040, 8));
		assertEquals(-1, MH.indexOfBitSet(0x00000080, 8));
		assertEquals(8, MH.indexOfBitSet(0x00000100, 8));
		assertEquals(9, MH.indexOfBitSet(0x00000200, 8));
		assertEquals(10, MH.indexOfBitSet(0x00000400, 8));
		assertEquals(-1, MH.indexOfBitSet(0x00000400, 31));
		assertEquals(31, MH.indexOfBitSet(0x80000000, 31));
	}

	@Test
	public void testCycle() {
		assertEquals(0, MH.mod(-3, 3));
		assertEquals(1, MH.mod(-2, 3));
		assertEquals(2, MH.mod(-1, 3));
		assertEquals(0, MH.mod(0, 3));
		assertEquals(1, MH.mod(1, 3));
		assertEquals(2, MH.mod(2, 3));
		assertEquals(0, MH.mod(3, 3));
		assertEquals(1, MH.mod(4, 3));
		assertEquals(2, MH.mod(5, 3));
	}

	@Test
	public void testBits() {
		assertEquals(0, MH.clearBits(8, 8));
		assertEquals(1 + 2 + 4, MH.clearBits(1 + 2 + 4 + 8, 8));
		assertEquals(1 + 4 + 16 + 32, MH.clearBits(1 + 2 + 4 + 8 + 16 + 32, 2 + 8));
		assertEquals(1 + 4 + 16 + 32, MH.clearBits(1 + 2 + 4 + 8 + 16 + 32, 2 + 8 + 64));
		assertEquals(0, MH.clearBits(0, 2 + 8 + 64));
	}

	@Test
	public void testRound() {
		assertEquals(20.002, MH.round(20.0028, BigDecimal.ROUND_FLOOR, 3), .000000001);
		assertEquals(20.003, MH.round(20.0028, BigDecimal.ROUND_CEILING, 3), .000000001);
		assertEquals(-20.002, MH.round(-20.0028, BigDecimal.ROUND_DOWN, 3), .000000001);
		assertEquals(-20.003, MH.round(-20.0028, BigDecimal.ROUND_UP, 3), .000000001);
		assertEquals(-20.003, MH.round(-20.0028, BigDecimal.ROUND_FLOOR, 3), .000000001);
		assertEquals(-20.002, MH.round(-20.0028, BigDecimal.ROUND_CEILING, 3), .000000001);
		assertEquals(-20.0028, MH.round(-20.0028, BigDecimal.ROUND_CEILING, 4), .000000001);
	}

	@Test
	public void testRound2() {
		assertFalse(4.5 == MH.round((double) 4.57, MH.ROUND_DOWN, 0));
		assertFalse(5.0 == MH.round((double) 4.57, MH.ROUND_DOWN, 0));
		assertFalse(4.0 == MH.round((double) 4.57, MH.ROUND_UP, 0));
		assertFalse(4.0 == MH.round((double) 4.57, MH.ROUND_HALF_EVEN, 0));
		assertTrue(4.0 == MH.round((double) 4.57, MH.ROUND_DOWN, 0));

		assertEquals(4.0, MH.round((double) 4.57, MH.ROUND_DOWN, 0), 0);
		assertEquals(5.0, MH.round((double) 4.57, MH.ROUND_UP, 0), 0);
		assertEquals(5.0, MH.round((double) 4.57, MH.ROUND_HALF_EVEN, 0), 0);

		assertFalse(4.6 == MH.round((double) 4.57, MH.ROUND_DOWN, 1));
		assertEquals(4.5, MH.round((double) 4.57, MH.ROUND_DOWN, 1), 0);
		assertEquals(4.6, MH.round((double) 4.57, MH.ROUND_UP, 1), 0);
		assertEquals(4.6, MH.round((double) 4.57, MH.ROUND_HALF_EVEN, 1), 0);

		assertEquals(4.57, MH.round((double) 4.57, MH.ROUND_DOWN, 2), 0);
		assertEquals(4.57, MH.round((double) 4.57, MH.ROUND_UP, 2), 0);
		assertEquals(4.57, MH.round((double) 4.57, MH.ROUND_HALF_EVEN, 2), 0);

		assertEquals(4.57, MH.round((double) 4.57, MH.ROUND_DOWN, 3), 0);
		assertEquals(4.57, MH.round((double) 4.57, MH.ROUND_DOWN, 3), 0);

		double num = Math.PI * 54321 + 0.1; //170654.55453565117
		assertEquals(170654.6, MH.round((double) num, MH.ROUND_HALF_EVEN, 1), 0);
		assertEquals(170655, MH.round((double) num, MH.ROUND_HALF_EVEN, 0), 0);
		assertEquals(170650, MH.round((double) num, MH.ROUND_HALF_EVEN, -1), 0);
		assertEquals(170700, MH.round((double) num, MH.ROUND_HALF_EVEN, -2), 0);
		assertEquals(171000, MH.round((double) num, MH.ROUND_HALF_EVEN, -3), 0);
		assertEquals(170000, MH.round((double) num, MH.ROUND_HALF_EVEN, -4), 0);
		assertEquals(200000, MH.round((double) num, MH.ROUND_HALF_EVEN, -5), 0);
		assertEquals(0, MH.round((double) num, MH.ROUND_HALF_EVEN, -6), 0);
		assertEquals(0, MH.round((double) num, MH.ROUND_HALF_EVEN, -7), 0);

		assertEquals(170654.5, MH.round((double) num, MH.ROUND_DOWN, 1), 0);
		assertEquals(170654, MH.round((double) num, MH.ROUND_DOWN, 0), 0);
		assertEquals(170650, MH.round((double) num, MH.ROUND_DOWN, -1), 0);
		assertEquals(170600, MH.round((double) num, MH.ROUND_DOWN, -2), 0);
		assertEquals(170000, MH.round((double) num, MH.ROUND_DOWN, -3), 0);
		assertEquals(170000, MH.round((double) num, MH.ROUND_DOWN, -4), 0);
		assertEquals(100000, MH.round((double) num, MH.ROUND_DOWN, -5), 0);
		assertEquals(0, MH.round((double) num, MH.ROUND_DOWN, -6), 0);
		assertEquals(0, MH.round((double) num, MH.ROUND_DOWN, -7), 0);

		assertEquals(170654.6, MH.round((double) num, MH.ROUND_UP, 1), 0);
		assertEquals(170655, MH.round((double) num, MH.ROUND_UP, 0), 0);
		assertEquals(170660, MH.round((double) num, MH.ROUND_UP, -1), 0);
		assertEquals(170700, MH.round((double) num, MH.ROUND_UP, -2), 0);
		assertEquals(171000, MH.round((double) num, MH.ROUND_UP, -3), 0);
		assertEquals(180000, MH.round((double) num, MH.ROUND_UP, -4), 0);
		assertEquals(200000, MH.round((double) num, MH.ROUND_UP, -5), 0);
		assertEquals(1000000, MH.round((double) num, MH.ROUND_UP, -6), 0);
		assertEquals(10000000, MH.round((double) num, MH.ROUND_UP, -7), 0);

		double num2 = Math.PI * 123456789L; //3.878509413581852E8
		double num3 = Math.PI * 123456789L * 987654321L * 12; // Less than max long  4.5967518976359506E18
		double num4 = Math.PI * 123456789L * 987654321L * 123; // Larger than max long 4.711670695076849E19
		double num5 = Math.PI * 123456789L * 987654321L * 123456L; // 4.729138352287866E22
		assertEquals(3.878509413581852E8, num2, 0);
		assertEquals(4.5967518976359506E18, num3, 0);
		assertEquals(4.711670695076849E19, num4, 0);
		assertEquals(4.729138352287866E22, num5, 0);

		assertEquals(3.878509414E8, MH.round((double) num2, MH.ROUND_HALF_EVEN, 1), 0);
		assertEquals(3.878509414E8, MH.round((double) num2, MH.ROUND_UP, 1), 0);
		assertEquals(3.878509413E8, MH.round((double) num2, MH.ROUND_DOWN, 1), 0);
		assertEquals(3.87850941E8, MH.round((double) num2, MH.ROUND_HALF_EVEN, 0), 0);
		assertEquals(3.87850942E8, MH.round((double) num2, MH.ROUND_UP, 0), 0);
		assertEquals(3.87850941E8, MH.round((double) num2, MH.ROUND_DOWN, 0), 0);
		assertEquals(3.879E8, MH.round((double) num2, MH.ROUND_HALF_EVEN, -5), 0);
		assertEquals(3.879E8, MH.round((double) num2, MH.ROUND_UP, -5), 0);
		assertEquals(3.878E8, MH.round((double) num2, MH.ROUND_DOWN, -5), 0);

		assertEquals(4.5967518976359506E18, MH.round((double) num3, MH.ROUND_HALF_EVEN, 2), 0);
		assertEquals(4.5967518976359506E18, MH.round((double) num3, MH.ROUND_UP, 2), 0);
		assertEquals(4.5967518976359506E18, MH.round((double) num3, MH.ROUND_DOWN, 2), 0);
		assertEquals(4.5967518976359506E18, MH.round((double) num3, MH.ROUND_HALF_EVEN, 0), 0);
		assertEquals(4.5967518976359506E18, MH.round((double) num3, MH.ROUND_UP, 0), 0);
		assertEquals(4.5967518976359506E18, MH.round((double) num3, MH.ROUND_DOWN, 0), 0);
		assertEquals(4.596751897635951E18, MH.round((double) num3, MH.ROUND_HALF_EVEN, -3), 0);
		assertEquals(4.596751897635951E18, MH.round((double) num3, MH.ROUND_UP, -3), 0);
		assertEquals(4.596751897635950E18, MH.round((double) num3, MH.ROUND_DOWN, -3), 0);
		assertEquals(4.596751898E18, MH.round((double) num3, MH.ROUND_HALF_EVEN, -9), 0);
		assertEquals(4.596751898E18, MH.round((double) num3, MH.ROUND_UP, -9), 0);
		assertEquals(4.596751897E18, MH.round((double) num3, MH.ROUND_DOWN, -9), 0);

		assertEquals(4.711670695076849E19, MH.round((double) num4, MH.ROUND_HALF_EVEN, 2), 0);
		assertEquals(4.711670695076849E19, MH.round((double) num4, MH.ROUND_UP, 2), 0);
		assertEquals(4.711670695076849E19, MH.round((double) num4, MH.ROUND_DOWN, 2), 0);
		assertEquals(4.711670695076849E19, MH.round((double) num4, MH.ROUND_HALF_EVEN, 0), 0);
		assertEquals(4.711670695076849E19, MH.round((double) num4, MH.ROUND_UP, 0), 0);
		assertEquals(4.711670695076849E19, MH.round((double) num4, MH.ROUND_DOWN, 0), 0);
		assertEquals(4.711670695076849E19, MH.round((double) num4, MH.ROUND_HALF_EVEN, -4), 0);
		assertEquals(4.71167069507685E19, MH.round((double) num4, MH.ROUND_UP, -4), 0);
		assertEquals(4.711670695076849E19, MH.round((double) num4, MH.ROUND_DOWN, -4), 0);
		assertEquals(4.71167069507685E19, MH.round((double) num4, MH.ROUND_HALF_EVEN, -5), 0);
		assertEquals(4.71167069507685E19, MH.round((double) num4, MH.ROUND_UP, -5), 0);
		assertEquals(4.71167069507684E19, MH.round((double) num4, MH.ROUND_DOWN, -5), 0);
		assertEquals(4.7116706950768E19, MH.round((double) num4, MH.ROUND_HALF_EVEN, -6), 0);
		assertEquals(4.7116706950769E19, MH.round((double) num4, MH.ROUND_UP, -6), 0);
		assertEquals(4.7116706950768E19, MH.round((double) num4, MH.ROUND_DOWN, -6), 0);
		assertEquals(4.7116706951E19, MH.round((double) num4, MH.ROUND_HALF_EVEN, -9), 0);
		assertEquals(4.7116706951E19, MH.round((double) num4, MH.ROUND_UP, -9), 0);
		assertEquals(4.7116706950E19, MH.round((double) num4, MH.ROUND_DOWN, -9), 0);

		assertEquals(4.729138352287866E22, MH.round((double) num5, MH.ROUND_HALF_EVEN, 17), 0);
		assertEquals(4.729138352287866E22, MH.round((double) num5, MH.ROUND_UP, 17), 0);
		assertEquals(4.729138352287866E22, MH.round((double) num5, MH.ROUND_DOWN, 17), 0);
		assertEquals(4.729138352287866E22, MH.round((double) num5, MH.ROUND_HALF_EVEN, 2), 0);
		assertEquals(4.729138352287866E22, MH.round((double) num5, MH.ROUND_UP, 2), 0);
		assertEquals(4.729138352287866E22, MH.round((double) num5, MH.ROUND_DOWN, 2), 0);
		assertEquals(4.729138352287866E22, MH.round((double) num5, MH.ROUND_HALF_EVEN, 0), 0);
		assertEquals(4.729138352287866E22, MH.round((double) num5, MH.ROUND_UP, 0), 0);
		assertEquals(4.729138352287866E22, MH.round((double) num5, MH.ROUND_DOWN, 0), 0);
		assertEquals(4.72914E22, MH.round((double) num5, MH.ROUND_HALF_EVEN, -17), 0);
		assertEquals(4.72914E22, MH.round((double) num5, MH.ROUND_UP, -17), 0);
		assertEquals(4.72913E22, MH.round((double) num5, MH.ROUND_DOWN, -17), 0);
		// Expects IndexOutOfBounds Not supported
		//		assertEquals(4.72914E22, MH.round((double) num5, MH.ROUND_HALF_EVEN, -21), 0);
		//		assertEquals(4.72914E22, MH.round((double) num5, MH.ROUND_UP, -21), 0);
		//		assertEquals(4.72913E22, MH.round((double) num5, MH.ROUND_DOWN, -21), 0);
	}

	@Test
	public void testAvg() {
		testAvg(0, 0);
		testAvg(20, -20);
		testAvg(-20, 20);
		testAvg(120, 80);
		testAvg(80, 120);
		testAvg(-120, -80);
		testAvg(-80, -120);
		testAvg(Long.MIN_VALUE, Long.MAX_VALUE);
		testAvg(Long.MAX_VALUE, Long.MIN_VALUE);
		testAvg(Long.MIN_VALUE, Long.MAX_VALUE - 10);
		testAvg(Long.MAX_VALUE - 10, Long.MIN_VALUE);
		testAvg(Long.MIN_VALUE, Long.MAX_VALUE - 10);
		testAvg(Long.MAX_VALUE, Long.MAX_VALUE);
		testAvg(Long.MAX_VALUE - 10, Long.MAX_VALUE);
		testAvg(Long.MAX_VALUE, 0);
		testAvg(0, Long.MAX_VALUE);
		testAvg(Long.MIN_VALUE, 0);
		testAvg(1, Long.MIN_VALUE);
		testAvg(2, Long.MIN_VALUE);
		testAvg(3, Long.MIN_VALUE);
		testAvg(0, Long.MIN_VALUE);
		testAvg(Long.MIN_VALUE, 0);
		Random r = new Random(123);
		for (int i = 0; i < 1000; i++) {
			testAvg(nextLong(r), nextLong(r));
		}
	}

	private long nextLong(Random r) {
		int i = r.nextInt(100);
		if (i < 10)
			return Long.MIN_VALUE;
		if (i >= 90)
			return Long.MAX_VALUE;
		return r.nextLong();
	}

	public void testAvg(long l1, long l2) {
		long i = BigInteger.valueOf(l1).add(BigInteger.valueOf(l2)).divide(BigInteger.valueOf(2)).longValue();
		long j = MH.avg(l1, l2);
		assertTrue(l1 + "," + l2 + "  " + i + " != " + j, i == j || i == j - 1 || i == j + 1);
	}

	@Test
	public void testRoundBy() {
		int i = -100;
		int base = -100;
		do {
			do {
				assertEquals("" + i, base, MH.roundBy(i, 10, MH.ROUND_CEILING));
				assertEquals("" + i, base, MH.roundBy(i, 10, MH.ROUND_DOWN));
				assertEquals("" + i, base, MH.roundBy(i, 10, MH.ROUND_FLOOR));
				assertEquals("" + i, base, MH.roundBy(i, 10, MH.ROUND_HALF_EVEN));
				assertEquals("" + i, base, MH.roundBy(i, 10, MH.ROUND_UP));
			} while (++i < base + 1);
			do {
				assertEquals("" + i, base + 10, MH.roundBy(i, 10, MH.ROUND_CEILING));
				assertEquals("" + i, base + 10, MH.roundBy(i, 10, MH.ROUND_DOWN));
				assertEquals("" + i, base, MH.roundBy(i, 10, MH.ROUND_FLOOR));
				assertEquals("" + i, base, MH.roundBy(i, 10, MH.ROUND_HALF_EVEN));
				assertEquals("" + i, base, MH.roundBy(i, 10, MH.ROUND_UP));
			} while (++i < base + 6);
			do {
				assertEquals("" + i, base + 10, MH.roundBy(i, 10, MH.ROUND_CEILING));
				assertEquals("" + i, base + 10, MH.roundBy(i, 10, MH.ROUND_DOWN));
				assertEquals("" + i, base, MH.roundBy(i, 10, MH.ROUND_FLOOR));
				assertEquals("" + i, base + 10, MH.roundBy(i, 10, MH.ROUND_HALF_EVEN));
				assertEquals("" + i, base, MH.roundBy(i, 10, MH.ROUND_UP));
			} while (++i < base + 10);
			base += 10;
		} while (i < 0);
		do {
			do {
				assertEquals("" + i, base, MH.roundBy(i, 10, MH.ROUND_CEILING));
				assertEquals("" + i, base, MH.roundBy(i, 10, MH.ROUND_DOWN));
				assertEquals("" + i, base, MH.roundBy(i, 10, MH.ROUND_FLOOR));
				assertEquals("" + i, base, MH.roundBy(i, 10, MH.ROUND_HALF_EVEN));
				assertEquals("" + i, base, MH.roundBy(i, 10, MH.ROUND_UP));
			} while (++i < base + 1);
			do {
				assertEquals("" + i, base + 10, MH.roundBy(i, 10, MH.ROUND_CEILING));
				assertEquals("" + i, base, MH.roundBy(i, 10, MH.ROUND_DOWN));
				assertEquals("" + i, base, MH.roundBy(i, 10, MH.ROUND_FLOOR));
				assertEquals("" + i, base, MH.roundBy(i, 10, MH.ROUND_HALF_EVEN));
				assertEquals("" + i, base + 10, MH.roundBy(i, 10, MH.ROUND_UP));
			} while (++i < base + 5);
			do {
				assertEquals("" + i, base + 10, MH.roundBy(i, 10, MH.ROUND_CEILING));
				assertEquals("" + i, base, MH.roundBy(i, 10, MH.ROUND_DOWN));
				assertEquals("" + i, base, MH.roundBy(i, 10, MH.ROUND_FLOOR));
				assertEquals("" + i, base + 10, MH.roundBy(i, 10, MH.ROUND_HALF_EVEN));
				assertEquals("" + i, base + 10, MH.roundBy(i, 10, MH.ROUND_UP));
			} while (++i < base + 10);
			base += 10;
		} while (i < 100);
	}

	@Test
	public void testComm() {
		long left = 1 | 4;
		long right = 2 | 4;
		assertEquals(0L, MH.commBits(left, right, false, false, false));
		assertEquals(1L, MH.commBits(left, right, true, false, false));
		assertEquals(2L, MH.commBits(left, right, false, true, false));
		assertEquals(3L, MH.commBits(left, right, true, true, false));
		assertEquals(4L, MH.commBits(left, right, false, false, true));
		assertEquals(5L, MH.commBits(left, right, true, false, true));
		assertEquals(6L, MH.commBits(left, right, false, true, true));
		assertEquals(7L, MH.commBits(left, right, true, true, true));
	}

	@Test
	public void testDigitsCount() {
		assertEquals(1, MH.getDigitsCount(0, 10));
		assertEquals(1, MH.getDigitsCount(9, 10));
		assertEquals(2, MH.getDigitsCount(10, 10));
		assertEquals(2, MH.getDigitsCount(10, 10));
		assertEquals(1, MH.getDigitsCount(10, 16));
		assertEquals(1, MH.getDigitsCount(15, 16));
		assertEquals(2, MH.getDigitsCount(16, 16));
		assertEquals(2, MH.getDigitsCount(255, 16));
		assertEquals(3, MH.getDigitsCount(256, 16));
		assertEquals(4, MH.getDigitsCount(-256, 16));
		assertEquals(7, MH.getDigitsCount(70, 2));
		assertEquals(31, MH.getDigitsCount(Integer.MAX_VALUE, 2));
		assertEquals(32, MH.getDigitsCount(((long) Integer.MAX_VALUE + 1), 2));
		Random r = new Random(123);
		for (int i = 0; i < 10000; i++) {
			int v = r.nextInt(1 + i * 100);
			int base = r.nextInt(32) + 2;
			assertEquals(v + ":" + base, SH.toString(v, base).length(), MH.getDigitsCount(v, base));
		}
	}

	@Test
	public void testIndexOfOnlyBit() {
		for (int i = 0; i < 64; i++)
			assertEquals("" + i, i, MH.indexOfOnlyBitSet(1L << i));
	}

	@Test
	public void testUnsignedInt() {
		assertEquals((long) Integer.MAX_VALUE, MH.toUnsignedInt(Integer.MAX_VALUE));
		assertEquals(0L, MH.toUnsignedInt(0));
		assertEquals(10L, MH.toUnsignedInt(10));
		assertEquals((long) Integer.MAX_VALUE + 1L, MH.toUnsignedInt(Integer.MIN_VALUE));
		assertEquals((long) Integer.MAX_VALUE - (long) Integer.MIN_VALUE, MH.toUnsignedInt(-1));
		long tests[] = new long[] { 0, Integer.MAX_VALUE, MH.MAX_UNSIGNED_INT, 2000 };
		for (long i : tests)
			assertEquals(i, MH.toUnsignedInt(MH.fromUnsignedInt(i)));
	}

	@Test
	public void testBitsDepth1() {
		byte[] buf = new byte[20];
		System.out.println(Integer.toBinaryString(256 + 64));
		System.out.println(Integer.toBinaryString(-(256 + 64)));
		ByteHelper.writeInt3(1000, buf, 0);
		ByteHelper.writeInt3(-1000, buf, 10);
		int value = -1;
		int pos = 0;
		for (int i = 0; i < MH.MAX_VALUE3; i++) {
			ByteHelper.writeInt3((int) i, buf, pos);
			int value2 = ByteHelper.readInt3(buf, pos);
			assertEquals(i, value2);
		}
		for (int i = MH.MIN_VALUE3; i < 0; i++) {
			ByteHelper.writeInt3((int) i, buf, pos);
			int value2 = ByteHelper.readInt3(buf, pos);
			assertEquals(i, value2);
		}
	}

	@Test
	public void testBitsDepth() {
		assertEquals(MH.MAX_VALUE8 >> 32, (long) Integer.MAX_VALUE);
		assertEquals(1, MH.getByteDepth(0));
		assertEquals(1, MH.getByteDepth(Byte.MAX_VALUE));
		assertEquals(1, MH.getByteDepth(Byte.MIN_VALUE));
		assertEquals(2, MH.getByteDepth(Short.MAX_VALUE));
		assertEquals(2, MH.getByteDepth(Short.MIN_VALUE));
		assertEquals(4, MH.getByteDepth(Integer.MAX_VALUE));
		assertEquals(4, MH.getByteDepth(Integer.MIN_VALUE));
		assertEquals(8, MH.getByteDepth(Long.MAX_VALUE));
		assertEquals(8, MH.getByteDepth(Long.MIN_VALUE));
		assertEquals(2, MH.getByteDepth(Byte.MAX_VALUE + 1));
		assertEquals(2, MH.getByteDepth(Byte.MIN_VALUE - 1));
		assertEquals(3, MH.getByteDepth(Short.MAX_VALUE + 1));
		assertEquals(3, MH.getByteDepth(Short.MIN_VALUE - 1));
		assertEquals(5, MH.getByteDepth(Integer.MAX_VALUE + 1L));
		assertEquals(5, MH.getByteDepth(Integer.MIN_VALUE - 1L));
		assertEquals(7, MH.getByteDepth(Long.MAX_VALUE / 256L));
		assertEquals(7, MH.getByteDepth(Long.MIN_VALUE / 256L));
		assertEquals(6, MH.getByteDepth(Long.MAX_VALUE / 256L / 256L));
		assertEquals(6, MH.getByteDepth(Long.MIN_VALUE / 256L / 256L));
		assertEquals(5, MH.getByteDepth(Integer.MAX_VALUE * 256L));
		assertEquals(5, MH.getByteDepth(Integer.MIN_VALUE * 256L));
		assertEquals(6, MH.getByteDepth(Integer.MAX_VALUE * 512L));
		Random r = new Random(1);
		byte[] buf = new byte[20];
		int[] counts = new int[9];
		for (int i = 0; i < 1000 * 1000 * 10; i++) {
			long value = r.nextLong();
			value >>= r.nextInt(63);
			if (r.nextBoolean())
				value *= -1;
			int pos = r.nextInt(12);
			int depth = MH.getByteDepth(value);
			long value2;
			counts[depth]++;
			switch (depth) {
				case 1:
					ByteHelper.writeByte((byte) value, buf, pos);
					value2 = ByteHelper.readByte(buf, pos);
					break;
				case 2:
					ByteHelper.writeShort((short) value, buf, pos);
					value2 = ByteHelper.readShort(buf, pos);
					break;
				case 3:
					ByteHelper.writeInt3((int) value, buf, pos);
					value2 = ByteHelper.readInt3(buf, pos);
					break;
				case 4:
					ByteHelper.writeInt((int) value, buf, pos);
					value2 = ByteHelper.readInt(buf, pos);
					break;
				case 5:
					ByteHelper.writeLong5(value, buf, pos);
					value2 = ByteHelper.readLong5(buf, pos);
					break;
				case 6:
					ByteHelper.writeLong6(value, buf, pos);
					value2 = ByteHelper.readLong6(buf, pos);
					break;
				case 7:
					ByteHelper.writeLong7(value, buf, pos);
					value2 = ByteHelper.readLong7(buf, pos);
					break;
				case 8:
					ByteHelper.writeLong(value, buf, pos);
					value2 = ByteHelper.readLong(buf, pos);
					break;
				default:
					throw new RuntimeException("" + depth);
			}
			assertEquals(value, value2);
		}
		System.out.println(Arrays.toString(counts));
	}

	@Test
	public void testAddNoOverflow() {
		assertEquals(0L, MH.addNoOverflow(0, 0));
		assertEquals(-1L, MH.addNoOverflow(Long.MAX_VALUE, Long.MIN_VALUE));
		assertEquals(Long.MAX_VALUE, MH.addNoOverflow(Long.MAX_VALUE, Long.MAX_VALUE));
		assertEquals(-1L, MH.addNoOverflow(Long.MIN_VALUE, Long.MAX_VALUE));
		assertEquals(Long.MIN_VALUE, MH.addNoOverflow(Long.MIN_VALUE, Long.MIN_VALUE));
		assertEquals(Long.MAX_VALUE, MH.addNoOverflow(Long.MAX_VALUE, 100));
		assertEquals(Long.MAX_VALUE - 100, MH.addNoOverflow(Long.MAX_VALUE, -100));
		assertEquals(Long.MIN_VALUE + 100, MH.addNoOverflow(Long.MIN_VALUE, 100));
		assertEquals(Long.MIN_VALUE, MH.addNoOverflow(Long.MIN_VALUE, -100));
	}

	@Test
	public void testGetUpperPower2() {
		assertFalse(MH.isPowerOfTwo(0));
		assertTrue(MH.isPowerOfTwo(1));
		assertTrue(MH.isPowerOfTwo(2));
		assertFalse(MH.isPowerOfTwo(3));
		assertTrue(MH.isPowerOfTwo(4));
		assertFalse(MH.isPowerOfTwo(5));
		assertFalse(MH.isPowerOfTwo(6));
		assertFalse(MH.isPowerOfTwo(7));
		assertTrue(MH.isPowerOfTwo(8));
		assertFalse(MH.isPowerOfTwo(9));
		for (int i = 4; i < 63; i++) {
			long n = 1L << i;
			assertTrue(MH.isPowerOfTwo(n));
			assertFalse(MH.isPowerOfTwo(n + 1));
			assertFalse(MH.isPowerOfTwo(n - 1));
			assertEquals(n, MH.getPowerOfTwoUpper(n - 2));
			assertEquals(n, MH.getPowerOfTwoUpper(n - 3));
			assertEquals(n, MH.getPowerOfTwoUpper(n - 1));
			assertEquals(n, MH.getPowerOfTwoUpper((n + (n >> 1)) / 2));
			assertEquals(n, MH.getPowerOfTwoUpper(n));
			assertEquals(n << 1, MH.getPowerOfTwoUpper(n + 1));
			assertEquals(n << 1, MH.getPowerOfTwoUpper(n + 2));
			if (i != 62)
				assertEquals(n << 1, MH.getPowerOfTwoUpper((n + (n << 1)) / 2));
		}
	}

	@Test
	public void testPctDiff() {
		assertEquals(0, MH.pctDiff(10, 10), .0000001);
		assertEquals(0, MH.pctDiff(-10, -10), .0000001);
		assertEquals(0, MH.pctChange(10, 10), .0000001);
		assertEquals(0, MH.pctChange(0, 0), .0000001);
		assertEquals(0, MH.pctChange(-10, -10), .0000001);
		assertEquals(.66666666667, MH.pctDiff(10, 5), .00001);
		assertEquals(.66666666667, MH.pctDiff(5, 10), .00001);
		assertEquals(.0004999999, MH.pctDiff(100.05, 100.10), .00001);
	}
	@Test
	@Ignore("TODO: MH.pctDiff(0,0) is returning NaN instead of 0, is that correct, we believe 0 is correct")
	public void testPctDiff2() {
		assertEquals(0, MH.pctDiff(0, 0), .0000001);
		assertEquals(.0004999999, MH.pctDiff(-100.05, -100.10), .00001);
	}

	@Test
	public void testMagnitude() {
		assertEquals(-3, MH.getMagnitude(.001000));
		assertEquals(-3, MH.getMagnitude(.009999));
		assertEquals(-2, MH.getMagnitude(.01000));
		assertEquals(-2, MH.getMagnitude(.09999));
		assertEquals(-1, MH.getMagnitude(.1000));
		assertEquals(-1, MH.getMagnitude(.9999));
		assertEquals(1, MH.getMagnitude(1));
		assertEquals(1, MH.getMagnitude(1.0000001));
		assertEquals(1, MH.getMagnitude(9.999999));
		assertEquals(2, MH.getMagnitude(10));
		assertEquals(2, MH.getMagnitude(10.000001));
		assertEquals(2, MH.getMagnitude(99.999999));
		assertEquals(3, MH.getMagnitude(100));
		assertEquals(3, MH.getMagnitude(999.9999));
		assertEquals(4, MH.getMagnitude(1000));
	}

	@Test
	public void testAbs() {
		assertEquals(1f, MH.abs(-1f), .0000000000000001);
		assertEquals(100000f, MH.abs(-100000f), .0000000000000001);
		System.out.println(Double.isInfinite(Double.NEGATIVE_INFINITY));
		System.out.println(Double.NEGATIVE_INFINITY == Double.NEGATIVE_INFINITY);
		System.out.println(Double.NEGATIVE_INFINITY < 0);
	}

	@Test
	public void testRemainder() {
		assertEquals(Double.POSITIVE_INFINITY, MH.remainder(0, 0), .0001);
		System.out.println(5 % 10);
		assertEquals(5, MH.remainder(5, 10), .000001);
		assertEquals(3, MH.remainder(13, 10), .000001);
		assertEquals(0, MH.remainder(24, 6), .000001);
		assertEquals(.5, MH.remainder(24.5, 6), .000001);
	}

	@Test
	public void testStdDev() {
		assertEquals(0, MH.stdev(AH.doubles(1, 1, 1, 1)), .00000001);
		assertEquals(3.6, MH.stdev(AH.doubles(1, 1, 1, 1, 10)), .00000001);
	}

	long[] interesting = new long[] { Long.MIN_VALUE, Long.MAX_VALUE, 0, 1, -1, Long.MIN_VALUE + 1, Long.MAX_VALUE - 1, 0x8000000000000000L };

	@Test
	public void testBitHelper() {
		Random r = new Random(123);
		for (int s = 0; s < 64; s++) {
			for (int e = 63; e >= s; e--) {
				boolean isPositive = false;
				for (long i = Long.MIN_VALUE;; i += r.nextInt(100000000) * (long) r.nextInt(100000000)) {
					if (i > 0)
						isPositive = true;
					else if (isPositive)
						break;
					OH.assertEq(firstBitSet(i, s, e), BitHelper.indexOfFirstBitBetween(i, s, e));
				}
				for (long i : interesting) {
					OH.assertEq(firstBitSet(i, s, e), BitHelper.indexOfFirstBitBetween(i, s, e));
					OH.assertEq(firstBitSet(i, 0, 64), BitHelper.indexOfFirstBit(i));
					OH.assertEq(firstBitSet(i, s, 64), BitHelper.indexOfFirstBitAfter(i, s));
					OH.assertEq(firstBitSet(i, 0, e), BitHelper.indexOfFirstBitBefore(i, e));
				}
			}
		}
	}

	private static int firstBitSet(long n, int start, int end) {
		for (int i = start; i < end; i++)
			if (((1L << i) & n) != 0)
				return i;
		return -1;
	}

	@Test
	public void testMedianSmallBasic() {
		Random r = new Random(123);
		DoubleArrayList d = new DoubleArrayList();
		testMedianHelper(d);
		d.add(r.nextInt());
		testMedianHelper(d);
		d.add(r.nextInt());
		testMedianHelper(d);
		d.add(r.nextInt());
		testMedianHelper(d);
		d.add(r.nextInt());
		testMedianHelper(d);
		d.add(r.nextInt());
		testMedianHelper(d);
		d.add(r.nextInt());
		testMedianHelper(d);
		d.add(r.nextInt());
		testMedianHelper(d);
		d.add(r.nextInt());
		testMedianHelper(d);
		d.add(r.nextInt());
		testMedianHelper(d);
		d.add(r.nextInt());
		testMedianHelper(d);
	}
	@Test
	public void testMedianSmallDouble() {
		Random r = new Random(123);
		for (int i = 0; i < 100; i++) {
			DoubleArrayList d = new DoubleArrayList();
			for (int n = 0, l = r.nextInt(1000); n < l; n++)
				d.add(r.nextGaussian() * 10000);
			testMedianHelper(d);
		}
	}
	@Test
	public void testMedianSmallInt() {
		Random r = new Random(123);
		for (int i = 0; i < 100; i++) {
			DoubleArrayList d = new DoubleArrayList();
			for (int n = 0, l = r.nextInt(1000); n < l; n++)
				d.add(r.nextInt(1000));
			testMedianHelper(d);
		}
	}
	@Test
	public void testMedianBig() {
		Random r = new Random(123);
		DoubleArrayList big = new DoubleArrayList();
		for (int n = 0, l = 1000000; n < l; n++)
			big.add(r.nextGaussian() * 100);
		testMedianHelper(big);
		big.add(r.nextGaussian() * 100);
		testMedianHelper(big);
	}
	@Test
	public void testMedianPerformanceUnsorted() {
		Random r = new Random(123);

		double t2 = 0;
		int count = 100000;
		DoubleArrayList test1 = new DoubleArrayList(count);

		for (int i = 0; i < count; i++) {
			int num = MH.rand(r, 0, count);
			test1.add(num);
		}

		PerfTest pt = new PerfTest(3);
		for (Object i : pt.test("copy")) {
			DoubleArrayList numbers = new DoubleArrayList(test1);
			int size = numbers.size();
			Double median = 0d;
			Double median2 = 0d;
			numbers.isSorted();
			median = numbers.get(size / 2);
			if (size % 2 == 0)
				median2 = numbers.get(size / 2 - 1);
			t2 += median + median2;

		}
		for (Object i : pt.test("sort")) {
			DoubleArrayList numbers = new DoubleArrayList(test1);
			t2 += MH.medianUsingSort(numbers);

		}
		for (Object i : pt.test("select")) {
			DoubleArrayList numbers = new DoubleArrayList(test1);
			t2 += MH.median(numbers);

		}

		System.out.println("Median performance unsorted array");
		pt.printStats();
		System.out.println(t2);
	}
	@Test
	public void testMedianPerformanceSorted() {
		Random r = new Random(123);

		double t2 = 0;
		int count = 100000;
		DoubleArrayList test1 = new DoubleArrayList(count);

		for (int i = 0; i < count; i++) {
			int num = MH.rand(r, 0, count);
			test1.add(num);
		}

		//Sorted Array
		test1.sort();
		PerfTest pt2 = new PerfTest(3);
		for (Object i : pt2.test("copy")) {
			DoubleArrayList numbers = new DoubleArrayList(test1);
			int size = numbers.size();
			Double median = 0d;
			Double median2 = 0d;
			numbers.isSorted();
			median = numbers.get(size / 2);
			if (size % 2 == 0)
				median2 = numbers.get(size / 2 - 1);
			t2 += median + median2;

		}
		for (Object i : pt2.test("sort")) {
			DoubleArrayList numbers = new DoubleArrayList(test1);
			t2 += MH.medianUsingSort(numbers);

		}
		for (Object i : pt2.test("select")) {
			DoubleArrayList numbers = new DoubleArrayList(test1);
			t2 += MH.median(numbers);

		}

		System.out.println("Median performance sorted array");
		pt2.printStats();
		System.out.println(t2);
	}
	public void testMedianHelper(DoubleArrayList d) {
		OH.assertEq(MH.median(new DoubleArrayList(d)), MH.medianUsingSort(new DoubleArrayList(d)));
	}
}
