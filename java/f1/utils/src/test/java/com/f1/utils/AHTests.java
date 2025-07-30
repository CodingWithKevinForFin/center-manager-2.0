package com.f1.utils;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import org.junit.Test;

public class AHTests {

	@Test
	public void testMinInt() {
		assertEquals(-1, AH.min(new int[] { 1, 2, 3, 4, -1, 2 }));
		assertEquals(4, AH.min(new int[] { 4 }));
	}

	@Test
	public void testMaxInt() {
		assertEquals(4, AH.max(new int[] { 1, 2, 3, 4, -1, 2 }));
		assertEquals(4, AH.max(new int[] { 4 }));
	}

	@Test
	public void testMinDouble() {
		assertEquals(-1d, AH.min(new double[] { 1, 2, 3, 4, -1, 2 }), .0001d);
		assertEquals(4d, AH.min(new double[] { 4 }), .0001d);
	}

	@Test
	public void testMaxDouble() {
		assertEquals(4d, AH.max(new double[] { 1, 2, 3, 4, -1, 2 }), .0001d);
		assertEquals(4d, AH.max(new double[] { 4 }), .0001d);
	}

	@Test
	public void testA() {
		String[] a = AH.a("0", "1", "2");
		assertEquals("0", a[0]);
		assertEquals("1", a[1]);
		assertEquals("2", a[2]);
	}

	@Test
	public void testB() {
		int c = 0;
		String values[] = new String[] { "a", "b", "c" };
		Duration d = new Duration("reflec");
		for (int j = 0; j < 10; j++) {
			for (int i = 0; i < 100000; i++) {
				String[] a = AH.a("0", "1", values[i % 3]);
				c += a.length;
			}
			d.stampMsStdout();
		}
		d = new Duration("arrays");
		for (int j = 0; j < 10; j++) {
			for (int i = 0; i < 100000; i++) {
				String[] a = new String[] { "0", "1", values[1 % 3] };
				c += a.length;
			}
			d.stampMsStdout();
		}
	}

	@Test
	public void testCat() {
		assertArrayEquals(AH.longs(1, 2, 3, 4, 5, 6), AH.cat(AH.longs(1, 2), AH.longs(3, 4), AH.longs(5, 6)));
		assertArrayEquals(AH.longs(1, 2, 3), AH.cat(AH.longs(1), AH.longs(2), AH.longs(3)));
		assertArrayEquals(AH.longs(2, 3), AH.cat(AH.longs(), AH.longs(2), AH.longs(3)));
		assertArrayEquals(AH.longs(2), AH.cat(AH.longs(), AH.longs(2), AH.longs()));
		assertArrayEquals(AH.longs(), AH.cat(AH.longs(), AH.longs(), AH.longs()));
		assertArrayEquals(AH.longs(), AH.cat(AH.longs()));
	}

	@Test
	public void testIndexOfSorted() {
		Random r = new Random(123);
		for (int k = 0; k < 10; k++) {
			for (int i = 0; i < 10; i++) {
				int a[] = new int[i];
				for (int j = 0; j < a.length; j++)
					a[j] = r.nextInt();
				Arrays.sort(a);
				testIndexOfSorted(a);
			}
		}
	}
	public void testIndexOfSorted(int array[]) {
		Arrays.sort(array);
		ArrayList<Integer> l = new ArrayList<Integer>(array.length);
		for (int i : array)
			l.add(i);
		String s = l.toString();
		for (int j = 0; j < array.length; j++) {
			for (int i = -1; i < 2; i++) {
				int k = array[j] + i;
				assertEquals(s + " <== " + k, l.indexOf(k), AH.indexOfSorted(k, array));
				System.out.println(s + " <== " + k);
			}
		}
	}

	@Test
	public void testIndexOfSorted2() {
		long a[] = new long[] { 1, 5, 6, 8, 13 };
		assertEquals(-1, AH.indexOfSortedLessThanEqualTo(3, OH.EMPTY_LONG_ARRAY));
		assertEquals(0, AH.indexOfSortedLessThanEqualTo(3, AH.longs(3)));
		assertEquals(0, AH.indexOfSortedLessThanEqualTo(4, AH.longs(3)));
		assertEquals(-1, AH.indexOfSortedLessThanEqualTo(2, AH.longs(3)));
		assertEquals(-1, AH.indexOfSortedLessThanEqualTo(0, AH.longs(1, 3, 5)));
		assertEquals(0, AH.indexOfSortedLessThanEqualTo(1, AH.longs(1, 3, 5)));
		assertEquals(0, AH.indexOfSortedLessThanEqualTo(2, AH.longs(1, 3, 5)));
		assertEquals(1, AH.indexOfSortedLessThanEqualTo(3, AH.longs(1, 3, 5)));
		assertEquals(1, AH.indexOfSortedLessThanEqualTo(4, AH.longs(1, 3, 5)));
		assertEquals(2, AH.indexOfSortedLessThanEqualTo(5, AH.longs(1, 3, 5)));
		assertEquals(2, AH.indexOfSortedLessThanEqualTo(6, AH.longs(1, 3, 5)));
		assertEquals(2, AH.indexOfSortedLessThanEqualTo(7, AH.longs(1, 3, 5)));

		assertEquals(-1, AH.indexOfSortedLessThanEqualTo(0, AH.longs(1, 3, 5, 7, 11)));
		assertEquals(0, AH.indexOfSortedLessThanEqualTo(1, AH.longs(1, 3, 5, 7, 11)));
		assertEquals(0, AH.indexOfSortedLessThanEqualTo(2, AH.longs(1, 3, 5, 7, 11)));
		assertEquals(1, AH.indexOfSortedLessThanEqualTo(3, AH.longs(1, 3, 5, 7, 11)));
		assertEquals(1, AH.indexOfSortedLessThanEqualTo(4, AH.longs(1, 3, 5, 7, 11)));
		assertEquals(2, AH.indexOfSortedLessThanEqualTo(5, AH.longs(1, 3, 5, 7, 11)));
		assertEquals(2, AH.indexOfSortedLessThanEqualTo(6, AH.longs(1, 3, 5, 7, 11)));
		assertEquals(3, AH.indexOfSortedLessThanEqualTo(7, AH.longs(1, 3, 5, 7, 11)));
		assertEquals(3, AH.indexOfSortedLessThanEqualTo(8, AH.longs(1, 3, 5, 7, 11)));
		assertEquals(3, AH.indexOfSortedLessThanEqualTo(9, AH.longs(1, 3, 5, 7, 11)));
		assertEquals(3, AH.indexOfSortedLessThanEqualTo(10, AH.longs(1, 3, 5, 7, 11)));
		assertEquals(4, AH.indexOfSortedLessThanEqualTo(11, AH.longs(1, 3, 5, 7, 11)));
		assertEquals(4, AH.indexOfSortedLessThanEqualTo(12, AH.longs(1, 3, 5, 7, 11)));
		Random r = new Random(123);
		LongArrayList ial = new LongArrayList();
		for (int i = 0; i < 100; i++) {
			ial.add(r.nextLong());
		}
		long[] longs = AH.sort(ial.toLongArray());
		for (int i = 0; i < 1000000; i++) {
			long val;
			if (i < longs.length)
				val = longs[i];
			else if (i < longs.length * 2)
				val = longs[i - longs.length] + 1;
			else if (i < longs.length * 3)
				val = longs[i - longs.length - longs.length] + -1;
			else
				val = r.nextLong();
			int j = AH.indexOfSortedLessThanEqualTo(val, longs);
			if (j < 0)
				assertEquals(true, longs[0] > val);
			else if (j == longs.length - 1)
				assertEquals(true, longs[longs.length - 1] <= val);
			else
				assertEquals(true, val == longs[j] || OH.isBetween(val, longs[j], longs[j + 1] - 1));
		}

	}
	@Test
	public void testIndexOfSortedGt() {
		long a[] = new long[] { 1, 5, 6, 8, 13 };
		assertEquals(-1, AH.indexOfSortedGreaterThanEqualTo(3, OH.EMPTY_LONG_ARRAY));
		assertEquals(0, AH.indexOfSortedGreaterThanEqualTo(3, AH.longs(3)));
		assertEquals(-1, AH.indexOfSortedGreaterThanEqualTo(4, AH.longs(3)));
		assertEquals(0, AH.indexOfSortedGreaterThanEqualTo(2, AH.longs(3)));
		assertEquals(0, AH.indexOfSortedGreaterThanEqualTo(0, AH.longs(1, 3, 5)));
		assertEquals(0, AH.indexOfSortedGreaterThanEqualTo(1, AH.longs(1, 3, 5)));
		assertEquals(1, AH.indexOfSortedGreaterThanEqualTo(2, AH.longs(1, 3, 5)));
		assertEquals(1, AH.indexOfSortedGreaterThanEqualTo(3, AH.longs(1, 3, 5)));
		assertEquals(2, AH.indexOfSortedGreaterThanEqualTo(4, AH.longs(1, 3, 5)));
		assertEquals(2, AH.indexOfSortedGreaterThanEqualTo(5, AH.longs(1, 3, 5)));
		assertEquals(-1, AH.indexOfSortedGreaterThanEqualTo(6, AH.longs(1, 3, 5)));
		assertEquals(-1, AH.indexOfSortedGreaterThanEqualTo(7, AH.longs(1, 3, 5)));

		assertEquals(0, AH.indexOfSortedGreaterThanEqualTo(0, AH.longs(1, 3, 5, 7, 11)));
		assertEquals(0, AH.indexOfSortedGreaterThanEqualTo(1, AH.longs(1, 3, 5, 7, 11)));
		assertEquals(1, AH.indexOfSortedGreaterThanEqualTo(2, AH.longs(1, 3, 5, 7, 11)));
		assertEquals(1, AH.indexOfSortedGreaterThanEqualTo(3, AH.longs(1, 3, 5, 7, 11)));
		assertEquals(2, AH.indexOfSortedGreaterThanEqualTo(4, AH.longs(1, 3, 5, 7, 11)));
		assertEquals(2, AH.indexOfSortedGreaterThanEqualTo(5, AH.longs(1, 3, 5, 7, 11)));
		assertEquals(3, AH.indexOfSortedGreaterThanEqualTo(6, AH.longs(1, 3, 5, 7, 11)));
		assertEquals(3, AH.indexOfSortedGreaterThanEqualTo(7, AH.longs(1, 3, 5, 7, 11)));
		assertEquals(4, AH.indexOfSortedGreaterThanEqualTo(8, AH.longs(1, 3, 5, 7, 11)));
		assertEquals(4, AH.indexOfSortedGreaterThanEqualTo(9, AH.longs(1, 3, 5, 7, 11)));
		assertEquals(4, AH.indexOfSortedGreaterThanEqualTo(10, AH.longs(1, 3, 5, 7, 11)));
		assertEquals(4, AH.indexOfSortedGreaterThanEqualTo(11, AH.longs(1, 3, 5, 7, 11)));
		assertEquals(-1, AH.indexOfSortedGreaterThanEqualTo(12, AH.longs(1, 3, 5, 7, 11)));
		Random r = new Random(123);
		LongArrayList ial = new LongArrayList();
		for (int i = 0; i < 100; i++) {
			ial.add(r.nextLong());
		}
		long[] longs = AH.sort(ial.toLongArray());
		for (int i = 0; i < 1000000; i++) {
			long val;
			if (i < longs.length)
				val = longs[i];
			else if (i < longs.length * 2)
				val = longs[i - longs.length] + 1;
			else if (i < longs.length * 3)
				val = longs[i - longs.length - longs.length] + -1;
			else
				val = r.nextLong();
			int j = AH.indexOfSortedGreaterThanEqualTo(val, longs);
			if (j < 0)
				assertEquals(true, longs[longs.length - 1] < val);
			else if (j == 0)
				assertEquals(true, longs[0] >= val);
			else
				assertEquals(true, val == longs[j] || OH.isBetween(val, longs[j - 1] + 1, longs[j]));
		}

	}
	@Test
	public void testIndexOfBytes() {
		testIndexOfBytes("1234567", "1234");
		testIndexOfBytes("1234567", "234");
		testIndexOfBytes("1234567", "34");
		testIndexOfBytes("1234567", "4");
		testIndexOfBytes("12345671234567", "1234");
		testIndexOfBytes("12345671234567", "234");
		testIndexOfBytes("12345671234567", "34");
		testIndexOfBytes("12345671234567", "4");
	}

	public void testIndexOfBytes(String out, String find) {
		for (int start = 0; start < out.length(); start++) {
			System.out.println(out + ".indexOf(" + find + "," + start + "):" + AH.indexOf(out.getBytes(), find.getBytes(), start));
			assertEquals(out + ".indexOf(" + find + "," + start + ")", out.indexOf(find, start), AH.indexOf(out.getBytes(), find.getBytes(), start));
		}
	}

	@Test
	public void testIndexOf() {
		assertEquals(0, AH.indexOf("abdf".getBytes(), "a".getBytes(), 0));
		assertEquals(3, AH.indexOf("fdsa".getBytes(), "a".getBytes(), 0));
		assertEquals(3, AH.indexOf("fdsab".getBytes(), "a".getBytes(), 3));
		assertEquals(-1, AH.indexOf("fdsab".getBytes(), "a".getBytes(), 4));
	}

	@Test
	public void testFill() {
		for (int i = 0; i < 20; i++) {
			byte[] sink = new byte[i];
			AH.fill(sink, new byte[] { 1, 2, 3, 4, 5 });
			System.out.println(SH.join(',', sink));
			AH.fill(sink, new byte[] { 1 });
			System.out.println(SH.join(',', sink));
		}
	}

	@Test
	public void testSw() {
		assertTrue(AH.startsWith(AH.a("A", "B", "C"), AH.a("A")));
		assertTrue(AH.startsWith(AH.a("A", "B", "C"), AH.a("A", "B")));
		assertTrue(AH.startsWith(AH.a("A", "B", "C"), AH.a("A", "B", "C")));
		assertFalse(AH.startsWith(AH.a("A", "B", "C"), AH.a("A", "B", "C", "D")));
		assertTrue(AH.endsWith(AH.a("A", "B", "C"), AH.a("C")));
		assertTrue(AH.endsWith(AH.a("A", "B", "C"), AH.a("B", "C")));
		assertTrue(AH.endsWith(AH.a("A", "B", "C"), AH.a("A", "B", "C")));
		assertFalse(AH.endsWith(AH.a("A", "B", "C"), AH.a("A", "B", "C", "D")));
		assertFalse(AH.endsWith(AH.a("A", "B", "C"), AH.a("!", "A", "B", "C")));
		assertTrue(AH.startsWith(AH.a("A", "B", "C"), AH.a("B", "C"), 1));
		assertTrue(AH.startsWith(AH.a("A", "B", "C"), AH.a("C"), 2));
		assertFalse(AH.startsWith(AH.a("A", "B", "C"), AH.a("C", "D"), 2));
		assertFalse(AH.startsWith(AH.a("A", "B", "C"), AH.a("C"), 3));
		assertFalse(AH.startsWith(AH.a("A"), AH.a("A"), 3));
		assertTrue(AH.startsWith(AH.a("A"), AH.a("A")));
		assertTrue(AH.startsWith(AH.a("A"), AH.a()));
		assertTrue(AH.startsWith(AH.a(), AH.a()));
		assertFalse(AH.startsWith(AH.a(), AH.a("A")));
	}

	@Test
	public void testIndexOfShort() {
		short[] shorts = new short[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
		assertEquals(-1, AH.indexOfSorted((short) 10, shorts, 10));
	}
	@Test
	public void testRemoveInplace() {
		Object[] test = AH.a(1, 2, 3, 4, 5, 6, 7, 8);
		AH.removeInplace(test, 2, 1);
		assertArrayEquals(AH.a(1, 2, 4, 5, 6, 7, 8, null), test);

		test = AH.a(1, 2, 3, 4, 5, 6, 7, 8);
		AH.removeInplace(test, 2, 2);
		assertArrayEquals(AH.a(1, 2, 5, 6, 7, 8, null, null), test);

		test = AH.a(1, 2, 3, 4, 5, 6, 7, 8);
		AH.removeInplace(test, 0, 8);
		assertArrayEquals(AH.a(null, null, null, null, null, null, null, null), test);

		test = AH.a(1, 2, 3, 4, 5, 6, 7, 8);
		AH.removeInplace(test, 0, 8);
		assertArrayEquals(AH.a(null, null, null, null, null, null, null, null), test);
	}
	@Test
	public void testInsertNulls() {
		String[] a = AH.a("1", "2", "3", "4");
		assertArrayEquals(AH.a("1", "2", "3", "4"), AH.insertNulls(a, 0, 0));
		assertArrayEquals(AH.a(null, "1", "2", "3", "4"), AH.insertNulls(a, 0, 1));
		assertArrayEquals(AH.a(null, null, "1", "2", "3", "4"), AH.insertNulls(a, 0, 2));
		assertArrayEquals(AH.a("1", "2", "3", "4", null), AH.insertNulls(a, 4, 1));
		assertArrayEquals(AH.a("1", "2", "3", "4", null, null), AH.insertNulls(a, 4, 2));
		assertArrayEquals(AH.a("1", "2", null, null, "3", "4"), AH.insertNulls(a, 2, 2));
		assertArrayEquals(AH.a("1", "2", null, "3", "4"), AH.insertNulls(a, 2, 1));
	}

	@Test
	public void testConcat() {
		assertArrayEquals(AH.longs(), AH.concat(AH.longs(), AH.longs(), AH.longs()));
		assertArrayEquals(AH.longs(1), AH.concat(AH.longs(1), AH.longs(), AH.longs()));
		assertArrayEquals(AH.longs(1), AH.concat(AH.longs(), AH.longs(1), AH.longs()));
		assertArrayEquals(AH.longs(1), AH.concat(AH.longs(), AH.longs(), AH.longs(1)));
		assertArrayEquals(AH.longs(1, 2, 3), AH.concat(AH.longs(1), AH.longs(2), AH.longs(3)));
		assertArrayEquals(AH.longs(1, 2, 3, 4, 5, 6, 7, 8, 9), AH.concat(AH.longs(1, 2), AH.longs(3, 4, 5), AH.longs(6, 7, 8, 9)));
		assertArrayEquals(AH.longs(1, 2, 3, 4, 5, 6, 7, 8, 9), AH.concat(null, AH.longs(1, 2), null, AH.longs(3, 4, 5), AH.longs(6, 7, 8, 9)));
		assertArrayEquals(new String[] { "1", "9" }, AH.concat(new String[] { "1" }, new String[] { "9" }));
	}

	@Test
	public void testRemoveAll() {
		assertArrayEquals(new String[] {}, AH.removeAll(new String[] { "a" }, "a"));
		assertArrayEquals(new String[] { "b", "c" }, AH.removeAll(new String[] { "a", "b", "c", "a" }, "a"));
		assertArrayEquals(new String[] { "a", "c", "a" }, AH.removeAll(new String[] { "a", "b", "c", "a" }, "b"));
		assertArrayEquals(new String[] { "a", "b", "c", "a" }, AH.removeAll(new String[] { "a", "b", "c", "a" }, "q"));
	}
	@Test
	public void testInsertBytes() {
		byte data[] = new byte[] { 1, 2, 3, 4, 5 };
		assertArrayEquals(new byte[] { 1, 2, 3, 4, 5 }, AH.insertBytes(data, 0, 0));
		assertArrayEquals(new byte[] { 0, 1, 2, 3, 4, 5 }, AH.insertBytes(data, 0, 1));
		assertArrayEquals(new byte[] { 1, 0, 2, 3, 4, 5 }, AH.insertBytes(data, 1, 1));
		assertArrayEquals(new byte[] { 1, 2, 0, 3, 4, 5 }, AH.insertBytes(data, 2, 1));
		assertArrayEquals(new byte[] { 1, 2, 3, 0, 4, 5 }, AH.insertBytes(data, 3, 1));
		assertArrayEquals(new byte[] { 1, 2, 3, 4, 0, 5 }, AH.insertBytes(data, 4, 1));
		assertArrayEquals(new byte[] { 1, 2, 3, 4, 5, 0 }, AH.insertBytes(data, 5, 1));
		assertArrayEquals(new byte[] { 0, 0, 1, 2, 3, 4, 5 }, AH.insertBytes(data, 0, 2));
		assertArrayEquals(new byte[] { 1, 0, 0, 2, 3, 4, 5 }, AH.insertBytes(data, 1, 2));
		assertArrayEquals(new byte[] { 1, 2, 0, 0, 3, 4, 5 }, AH.insertBytes(data, 2, 2));
		assertArrayEquals(new byte[] { 1, 2, 3, 0, 0, 4, 5 }, AH.insertBytes(data, 3, 2));
		assertArrayEquals(new byte[] { 1, 2, 3, 4, 0, 0, 5 }, AH.insertBytes(data, 4, 2));
		assertArrayEquals(new byte[] { 1, 2, 3, 4, 5, 0, 0 }, AH.insertBytes(data, 5, 2));
	}
}
