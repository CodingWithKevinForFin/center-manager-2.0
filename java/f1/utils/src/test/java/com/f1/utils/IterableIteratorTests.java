package com.f1.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import com.f1.utils.structs.ArrayIterator;

public class IterableIteratorTests {

	@Test
	public void test1() {
		List<Integer> t1 = CH.l(1, 2, 3);
		List<Integer> t2 = CH.l(4, 5, 6);
		List<Integer> t3 = CH.l(7, 8, 9);
		Iterator<Integer> i = IterableIterator.create(t1, t2, t3).iterator();
		assertIterator(i, 1, 2, 3, 4, 5, 6, 7, 8, 9);
		assertIterator(IterableIterator.create().iterator());
	}

	@Test
	public void test2() {
		List<Integer> t1 = CH.l(1, 2, 3);
		List<Integer> t2 = CH.l();
		List<Integer> t3 = CH.l(7, 8, 9);
		Iterator<Integer> i = IterableIterator.create(t1, t2, t3).iterator();
		assertIterator(i, 1, 2, 3, 7, 8, 9);
	}

	@Test
	public void test3() {
		List<Integer> t1 = CH.l();
		List<Integer> t2 = CH.l(1, 2, 3);
		List<Integer> t3 = CH.l();
		List<Integer> t4 = CH.l();
		List<Integer> t5 = CH.l();
		List<Integer> t6 = CH.l(7, 8, 9);
		List<Integer> t7 = CH.l(10);
		List<Integer> t8 = CH.l();
		Iterator<Integer> i = IterableIterator.create(t1, t2, t3, t4, t5, t6, t7, t8).iterator();
		assertIterator(i, 1, 2, 3, 7, 8, 9, 10);
	}

	@Test
	public void testArray() {
		ArrayIterator ai = new ArrayIterator(new Integer[] { 1, 2, 3, 4 });
		assertIterator(ai, 1, 2, 3, 4);
	}

	private void assertIterator(Iterator ai, Object... a) {
		for (int i = 0; i < a.length; i++) {
			assertTrue(ai.hasNext());
			assertEquals("at " + i, a[i], ai.next());
		}
		assertFalse(ai.hasNext());

	}

}

