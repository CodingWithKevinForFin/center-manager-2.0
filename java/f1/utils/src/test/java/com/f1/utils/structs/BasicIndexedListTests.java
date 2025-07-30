package com.f1.utils.structs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Test;

public class BasicIndexedListTests {

	@Test
	public void testAddKVInt() {
		BasicIndexedList list = new BasicIndexedList<Integer, Integer>();
		assertFalse(list.getHasChanged());
		list.add(1, 10, 0);
		list.add(2, 20, 1);
		list.add(3, 30, 1);
		assertTrue(list.getHasChanged());
	}

	@Test(expected = RuntimeException.class)
	public void testAddKVIntFail() {
		BasicIndexedList list = new BasicIndexedList<Integer, Integer>();
		list.add(4, 40, 10);

	}

	@Test
	public void testAddKV() {
		BasicIndexedList list = new BasicIndexedList<Integer, Integer>();
		assertFalse(list.getHasChanged());
		Random rand = new Random();
		for (int i = 1; i <= 10000; i++) {
			list.add(i, rand.nextInt(1000));
		}
		assertTrue(list.getHasChanged());
	}

	@Test
	public void testRemove() {
		BasicIndexedList list = new BasicIndexedList<Integer, Integer>();
		Random rand = new Random();
		for (int i = 1; i <= 10000; i++) {
			list.add(i, rand.nextInt(10));
		}
		list.resetHasChanged();
		assertFalse(list.getHasChanged());
		String results = "[";
		for (int i = 1; i <= 10000; i += 2) {
			list.remove(i);
			results += "[" + (i + 1) + "," + list.get(i + 1) + "],";
		}
		results = results.substring(0, results.length() - 1);
		results += "]";
		assertTrue(list.getHasChanged());
		assertEquals(results, list.toString());
	}

	@Test(expected = RuntimeException.class)
	public void testRemoveFail() {
		BasicIndexedList list = new BasicIndexedList<Integer, Integer>();
		list.remove(1);
	}

	@Test
	public void testGetAt() {
		BasicIndexedList list = new BasicIndexedList<Integer, Integer>();
		for (int i = 0; i < 10000; i++) {
			list.add(i, i * 10);
		}
		for (int i = 0; i < 10000; i++) {
			assertEquals(i * 10, list.getAt(i));
		}
	}
	@Test(expected = IndexOutOfBoundsException.class)
	public void testGetAtFail() {
		BasicIndexedList list = new BasicIndexedList<Integer, Integer>();
		list.getAt(10);
	}

	@Test
	public void testGet() {
		BasicIndexedList list = new BasicIndexedList<Integer, Integer>();
		for (int i = 0; i < 100000; i++) {
			list.add(i, i * 10);
		}
		for (int i = 0; i < 100000; i++) {
			assertEquals(i * 10, list.getAt(i));
		}
	}

	@Test(expected = RuntimeException.class)
	public void testGetFail() {
		BasicIndexedList list = new BasicIndexedList<Integer, Integer>();
		list.get(1);
	}

	@Test
	public void testGetSize() {
		BasicIndexedList list = new BasicIndexedList<Integer, Integer>();
		assertEquals(0, list.getSize());
		Random rand = new Random();
		int limit = rand.nextInt(1000000);
		int adjustedLimit = limit;
		int num;
		for (int i = 0; i < limit; i++) {
			list.resetHasChanged();
			num = rand.nextInt(1000000);
			if (!list.containsKey(num))
				list.add(num, rand.nextInt(1000));
			else
				adjustedLimit--;
		}
		assertEquals(adjustedLimit, list.getSize());
	}

	@Test
	public void testGetPosition() {
		BasicIndexedList list = new BasicIndexedList<Integer, Integer>();
		for (int i = 0; i < 100000; i++) {
			list.add(i + 1, i * 10);
			assertEquals(i, list.getPosition(i + 1));
		}
	}

	@Test(expected = RuntimeException.class)
	public void testGetPositionFail() {
		BasicIndexedList list = new BasicIndexedList<Integer, Integer>();
		list.getPosition(3);
	}

	@Test
	public void testGetHasChanged() {
		BasicIndexedList list = new BasicIndexedList<Integer, Integer>();
		assertFalse(list.getHasChanged());
		list.add(1, 10, 0);
		assertTrue(list.getHasChanged());
	}

	@Test
	public void testResetHasChanged() {
		BasicIndexedList list = new BasicIndexedList<Integer, Integer>();
		list.add(1, 10, 0);
		assertTrue(list.getHasChanged());
		list.resetHasChanged();
		assertFalse(list.getHasChanged());
	}

	@Test
	public void testRemoveAt() {
		BasicIndexedList list = new BasicIndexedList<Integer, Integer>();
		list.add(1, 10);
		list.add(2, 20);
		list.add(3, 30);
		assertEquals(0, list.getPosition(1));
	}

	@Test(expected = RuntimeException.class)
	public void testRemoveAtFail() {
		BasicIndexedList list = new BasicIndexedList<Integer, Integer>();
		list.getPosition(1);
	}

	@Test
	public void testContainsKey() {
		BasicIndexedList list = new BasicIndexedList<Integer, Integer>();
		assertFalse(list.containsKey(1));
		list.add(1, 10);
		list.add(2, 20);
		list.add(3, 30);
		assertTrue(list.containsKey(1));
		assertFalse(list.containsKey(100));
	}

	@Test
	public void testToString() {
		BasicIndexedList list = new BasicIndexedList<Integer, Integer>();
		Random rand = new Random();
		String results = "[";
		for (int i = 0; i < 10000; i++) {
			list.add(i, rand.nextInt(10));
			results += "[" + (i) + "," + list.get(i) + "],";
		}
		results = results.substring(0, results.length() - 1);
		results += "]";
		assertEquals(results, list.toString());
	}

	@Test
	public void testToStringStringBuilder() {
		BasicIndexedList list = new BasicIndexedList<Integer, Integer>();
		Random rand = new Random();
		String results = "[";
		for (int i = 0; i < 10000; i++) {
			list.add(i, rand.nextInt(10));
			results += "[" + (i) + "," + list.get(i) + "],";
		}
		results = results.substring(0, results.length() - 1);
		results += "]";
		assertEquals(results, list.toString(new StringBuilder()).toString());
	}

}
