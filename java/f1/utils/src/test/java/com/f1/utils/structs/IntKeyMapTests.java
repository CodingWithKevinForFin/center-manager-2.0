package com.f1.utils.structs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.junit.Test;

import com.f1.utils.SH;
import com.f1.utils.structs.IntKeyMap.Node;

public class IntKeyMapTests {

	@Test
	public void testIntKeyMapConstructor() {
		IntKeyMap map = new IntKeyMap<Integer>();
	}

	@Test
	public void testIntKeyMapInt() {
		IntKeyMap map1 = new IntKeyMap<Integer>(10);
		IntKeyMap map2 = new IntKeyMap<Integer>(0);
	}

	@Test
	public void testPut() {
		IntKeyMap map = new IntKeyMap<Integer>();
		for (int i = 0; i < 100000; i++) {
			map.put(i, i * 10);
		}
	}

	@Test
	public void testRemove() {
		IntKeyMap map = new IntKeyMap<Integer>();
		for (int i = 0; i < 100000; i++) {
			map.put(i, i * 10);
		}
		for (int i = 0; i < 100000; i++) {
			if (i % 3 == 0)
				assertEquals(i * 10, map.remove(i));
		}
		map.remove(-1);
	}
	@Test
	public void testRemove2() {
		IntKeyMap<Integer> map = new IntKeyMap<Integer>();
		Map<Integer, Integer> map2 = new HashMap<Integer, Integer>();
		Random r = new Random(123);
		for (int i = 0; i < 100000; i++) {
			boolean add = r.nextInt(3) == 0;
			int key = r.nextInt(25);
			int value = r.nextInt();
			if (add) {
				map2.put(key, value);
				map.put(key, value);
			} else {
				assertEquals(map.remove(key), map2.remove(key));
			}
		}

		Map<Integer, Integer> map3 = new HashMap<Integer, Integer>();
		map.fill(map3);
		assertEquals(map2, map3);

	}
	@Test
	public void testRemove3() {
		IntKeyMap<Integer> map = new IntKeyMap<Integer>();
		Random r = new Random(123);
		for (int i = 0; i < 500; i++) {
			int key = r.nextInt(250);
			int value = r.nextInt();
			map.put(key, value);
		}
		r = new Random(123);
		for (int i = 0; i < 500; i++) {
			int key = r.nextInt(250);
			int value = r.nextInt();
			map.remove(key);
		}

		assertEquals(0, map.size());
	}
	@Test
	public void testGetNode() {
		IntKeyMap map = new IntKeyMap<Integer>();
		for (int i = 0; i < 100000; i++) {
			map.put(i, i * 10);
			Node node = new Node<Integer>(i, i * 10, null);
			assertEquals(node.getValue(), map.getNode(i).getValue());
		}

	}

	@Test(expected = NullPointerException.class)
	public void testGetNodeFail() {
		IntKeyMap map = new IntKeyMap<Integer>();
		for (int i = 0; i < 100000; i++) {
			map.getNode(i).getValue();
		}
	}

	@Test
	public void testGet() {
		IntKeyMap map = new IntKeyMap<Integer>();
		for (int i = 0; i < 100000; i++) {
			map.put(i, i * 10);
			Node node = new Node<Integer>(i, i * 10, null);
			assertEquals(node.getValue(), map.get(i));
		}
		assertEquals(null, map.get(-1));
	}

	@Test
	public void testGetNodeOrCreate() {
		IntKeyMap map = new IntKeyMap<Integer>();
		for (int i = 0; i < 100000; i++) {
			map.put(i, i * 10);
			Node node = new Node<Integer>(i, i * 10, null);
			assertEquals(node.getValue(), map.getNodeOrCreate(i).getValue());
		}
		assertEquals(null, map.getNodeOrCreate(-1).getValue());
	}

	@Test
	public void testNode() {
		Node node = new Node<Integer>(1, 10, null);
		Node node2 = new Node<Integer>(1, 10, new Node<Integer>(2, 20, null));
	}

	@Test
	public void testGetValue() {
		for (int i = 0; i < 100000; i++) {
			Node node = new Node<Integer>(i, i * 10, null);
			assertEquals(new Integer(i * 10), node.getValue());
		}
	}

	@Test
	public void testGetKey() {
		for (int i = 0; i < 100000; i++) {
			Node node = new Node<Integer>(i, i * 10, null);
			assertEquals(i, node.getIntKey());
		}
	}

	@Test
	public void testSetValue() {
		for (int i = 0; i < 100000; i++) {
			Node node = new Node<Integer>(i, i, null);
			node.setValue(i * 10);
			assertEquals(new Integer(i * 10), node.getValue());
		}
	}

	@Test
	public void testToString() {
		String compare;
		for (int i = 0; i < 100000; i++) {
			Node node = new Node<Integer>(i, i * 10, null);
			compare = i + "=" + i * 10;
			assertEquals(compare, node.toString(new StringBuilder()).toString());
		}

	}

	@Test
	public void testHasNext() {
		IntKeyMap map = new IntKeyMap<Integer>();
		assertFalse(map.iterator().hasNext());
		map.put(1, 10);
		map.put(1, 100);
		map.put(2, 20);
		assertTrue(map.iterator().hasNext());
	}

	@Test(expected = NullPointerException.class)
	public void testNextFail() {
		IntKeyMap map = new IntKeyMap<Integer>();
		assertEquals(null, map.iterator().next());
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testIteratorRemove() {
		IntKeyMap map = new IntKeyMap<Integer>();
		map.iterator().remove();
	}

	@Test
	public void testKeyHasNext() {
		IntKeyMap map = new IntKeyMap<Integer>();
		assertFalse(map.keyIterator().hasNext());
		map.put(1, 10);
		map.put(1, 100);
		map.put(2, 20);
		assertTrue(map.keyIterator().hasNext());
	}

	@Test(expected = NullPointerException.class)
	public void testKeyNextFail() {
		IntKeyMap map = new IntKeyMap<Integer>();
		assertEquals(null, map.keyIterator().next());
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testKeyIteratorRemove() {
		IntKeyMap map = new IntKeyMap<Integer>();
		map.keyIterator().remove();
	}

	@Test
	public void testSize() {
		IntKeyMap map = new IntKeyMap<Integer>();
		assertEquals(0, map.size());
		int limit = 100000;
		for (int i = 0; i < limit; i++) {
			map.put(i, i * 10);
		}
		assertEquals(limit, map.size());
	}

	@Test
	public void testIntKeyMap() {
		IntKeyMap<Object> m = new IntKeyMap<Object>();
		HashMap<Integer, Object> h = new HashMap<Integer, Object>();
		assertSame(h, m);
		Random r = new Random(123);
		for (int k = 1; k < 3; k++) {
			for (int j = 1; j < 10; j++) {
				for (int i = 0; i < 1000; i++) {
					int n = r.nextInt(10000);
					h.put(n, n);
					m.put(n, n);
				}
				assertSame(h, m);
				for (int i = 0; i < 1000 * j; i++) {
					int n = r.nextInt(10000);
					assertEquals(h.remove(n), m.remove(n));
				}
				assertSame(h, m);
			}
			assertSame(h, m);
			for (int i : h.keySet())
				m.remove(i);
			h.clear();
			assertSame(h, m);
		}
	}
	@Test
	public void testLongKeyMap() {
		LongKeyMap<Object> m = new LongKeyMap<Object>();
		HashMap<Long, Object> h = new HashMap<Long, Object>();
		assertSame(h, m);
		Random r = new Random(123);
		for (int k = 1; k < 3; k++) {
			for (int j = 1; j < 10; j++) {
				for (int i = 0; i < 1000; i++) {
					long n = r.nextInt(10000);
					h.put(n, n);
					m.put(n, n);
				}
				assertSame(h, m);
				for (int i = 0; i < 1000 * j; i++) {
					long n = r.nextInt(10000);
					assertEquals(h.remove(n), m.remove(n));
				}
				assertSame(h, m);
			}
			assertSame(h, m);
			for (long i : h.keySet())
				m.remove(i);
			h.clear();
			assertSame(h, m);
		}
	}
	@Test
	public void testLongKeyMapBucketSize() {
		LongKeyMap<Object> m = new LongKeyMap<Object>();
		m.put(32273, null);
		m.put(-31100, null);
		m.put(32113, null);
		m.put(3217, null);
		m.put(3219, null);
		m.put(3219, null);
		m.put(32117, null);
		m.put(32122, null);
		System.out.println(SH.join(",", m.getBucketSizes()));
		System.out.println(LongKeyMap.diagnose(m));
	}

	private void assertSame(HashMap<Integer, Object> h, IntKeyMap<Object> m) {
		assertEquals(h.size(), m.size());
		for (Integer i : h.keySet())
			assertEquals(h.get(i), m.get(i));
		for (Integer i : m.keys())
			assertEquals(m.get(i), h.get(i));
		System.out.println(m.size());
		System.out.println(h.size());
	}
	private void assertSame(HashMap<Long, Object> h, LongKeyMap<Object> m) {
		assertEquals(h.size(), m.size());
		for (Long i : h.keySet())
			assertEquals(h.get(i), m.get(i));
		for (Long i : m.keys())
			assertEquals(m.get(i), h.get(i));
		System.out.println(m.size());
		System.out.println(h.size());
	}

}
