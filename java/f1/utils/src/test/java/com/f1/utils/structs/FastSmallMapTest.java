package com.f1.utils.structs;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.junit.Test;

import com.f1.utils.CH;

public class FastSmallMapTest {

	@Test
	public void test1() {
		Map m1 = new HashMap();
		Map m2 = new FastSmallMap();
		put(m1, m2, "test", 123);
		put(m1, m2, "test", 456);
		put(m1, m2, "test2", 321);
		remove(m1, m2, "test2");
		put(m1, m2, "test2", 1);
		get(m1, m2, "test2");
		m1.clear();
		m2.clear();
		assertEq(m1, m2);
		assertEq(m2, m1);
		Random rand = new Random(123);
		for (int n = 0; n < 3; n++) {
			for (int i = 0; i < 20; i++)
				if (rand.nextInt(3) == 0)
					put(m1, m2, rand.nextInt(5), rand.nextInt());
				else
					remove(m1, m2, rand.nextInt(5));
			for (int i = 0; i < 200; i++)
				if (rand.nextInt(3) == 0)
					put(m1, m2, rand.nextInt(25), rand.nextInt());
				else
					remove(m1, m2, rand.nextInt(25));
			for (int i = 0; i < 1000; i++)
				if (rand.nextInt(5) == 0)
					put(m1, m2, rand.nextInt(25), rand.nextInt());
				else
					remove(m1, m2, rand.nextInt(25));
			//			dump(m1, m2);
		}
		m1.clear();
		m2.clear();
		for (int n = 2; n < 10; n++) {
			int sizes[] = new int[n];
			for (int i = 0; i < n * 100; i++) {
				if (rand.nextInt(2) == 0)
					put(m1, m2, rand.nextInt(n - 1), rand.nextInt());
				else
					remove(m1, m2, rand.nextInt(n - 1));
				sizes[m1.size()]++;
			}
			//			System.out.println(SH.join(',', sizes));
		}

	}

	private void dump(Map m1, Map m2) {
		System.out.println(m1);
		System.out.println(m2);
	}

	private void put(Map m1, Map m2, Object key, Object value) {
		Object o1 = m1.put(key, value);
		Object o2 = m2.put(key, value);
		assertEq(m1, m2);
		assertEq(m2, m1);
		assertEquals(o1, o2);
	}
	private void remove(Map m1, Map m2, Object key) {
		Object o1 = m1.remove(key);
		Object o2 = m2.remove(key);
		assertEq(m1, m2);
		assertEq(m2, m1);
		assertEquals(o1, o2);
	}
	private void get(Map m1, Map m2, Object key) {
		Object o1 = m1.get(key);
		Object o2 = m2.get(key);
		assertEq(m1, m2);
		assertEq(m2, m1);
		assertEquals(o1, o2);
	}

	@Test
	public void testEq() {

		Map m1 = new FastSmallMap();
		Map m2 = new FastSmallMap();
		for (int i = 0; i < 10; i++) {
			assertEquals(m1.equals(m2), true);
			assertEquals(m2.equals(m1), true);
			m1.put(i, i * 2);
			assertEquals(m1.equals(m2), false);
			assertEquals(m2.equals(m1), false);
			m2.put(i, i * 2);
		}
		for (int i = 0; i < 10; i++) {
			assertEquals(m1.equals(m2), true);
			assertEquals(m2.equals(m1), true);
			m1.remove(i);
			assertEquals(m1.equals(m2), false);
			assertEquals(m2.equals(m1), false);
			m2.remove(i);
		}
		assertEquals(m1.equals(m2), true);
		m1.put(15, null);
		m2.put(16, null);
		assertEquals(m1.equals(m2), false);
		m1.put(16, null);
		m2.put(15, null);
		assertEquals(m1.equals(m2), true);
	}
	private void assertEq(Map<Object, Object> m1, Map<Object, Object> m2) {
		assertEquals(m1.size(), m2.size());
		for (Entry<Object, Object> e : m1.entrySet())
			assertEquals(m2.get(e.getKey()), e.getValue());
		assertEquals(m1.keySet(), m2.keySet());
		assertEquals(CH.sort((Collection<Comparable>) (Collection) m1.values()), CH.sort((Collection<Comparable>) (Collection) m2.values()));
	}

}
