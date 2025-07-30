package com.f1.utils.structs;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.f1.utils.OH;
import com.f1.utils.structs.CompactLongKeyMap.KeyGetter;

public class CompactLongKeyTest {

	Map<Long, Integer> m = new HashMap<Long, Integer>();
	CompactLongKeyMap<Integer> cm = new CompactLongKeyMap<Integer>("test", new KeyGetter<Integer>() {
		@Override
		public long getKey(Integer object) {
			return object;
		}
	}, 8);

	@Test
	public void test1Fast() {
		long orig = System.currentTimeMillis();
		for (int i = 1; i < 200; i = i * 4) {
			long start = System.currentTimeMillis();
			test1(i, i * 10);
			long end = System.currentTimeMillis();
			System.out.println(i + " Time: " + (end - start) + "    " + (end - orig));
		}

	}
	@Test
	@Ignore("This test takes too long, its disabled unless we need to run it manually")
	public void test1Slow() {
		long orig = System.currentTimeMillis();
		for (int i = 1; i < 10000; i = i * 4) {
			long start = System.currentTimeMillis();
			test1(i, i * 10);
			long end = System.currentTimeMillis();
			System.out.println(i + " Time: " + (end - start) + "    " + (end - orig));
		}

	}
	public void test1(int seed, int n) {

		Random r = new Random(seed);
		for (int z = 1; z < 5; z++) {
			for (int j = 0; j < 100; j++) {
				int l = 1 + r.nextInt(4 * z);
				int k = 1 + r.nextInt(4 * z);
				l *= l * l;
				k *= k * k;
				int q = 1 + r.nextInt(n);
				int s = 1 + r.nextInt(n);
				for (int i = 0; i < l; i++) {
					int val = r.nextInt(s);
					put(m, cm, val);
					if (i % 10 == 0)
						testEquals(m, cm);
				}
				testEquals(m, cm);
				for (int i = 0; i < k; i++) {
					int val = r.nextInt(q);
					remove(m, cm, val);
					if (i % 10 == 0)
						testEquals(m, cm);
				}
				testEquals(m, cm);
				for (int i = 0; i < k; i++) {
					if (r.nextBoolean()) {
						int val = r.nextInt(q);
						remove(m, cm, val);
					} else {
						int val = r.nextInt(q);
						put(m, cm, val);
					}
					if (i % 10 == 0)
						testEquals(m, cm);
				}
				for (int i = 0; i < k; i++) {
					if (r.nextBoolean()) {
						int cnt = r.nextInt(q);
						int val = r.nextInt(q);
						while (cnt-- > 0)
							remove(m, cm, val++);
					} else {
						int cnt = r.nextInt(q);
						int val = r.nextInt(q);
						while (cnt-- > 0)
							put(m, cm, val++);
					}
					if (i % 10 == 0)
						testEquals(m, cm);
				}
				testEquals(m, cm);
			}
			m.clear();
			cm.clear();
		}
	}

	public <T> void put(Map<Long, T> m, CompactLongKeyMap<T> cm, T value) {
		cm.debug();
		long key = cm.getKey(value);
		T v1 = m.put(key, value);
		T v2 = cm.put(value);
		assertEquals(-1, v1, v2);
	}
	public <T> void remove(Map<Long, T> m, CompactLongKeyMap<T> cm, T value) {
		cm.debug();
		long key = cm.getKey(value);
		T v1 = m.remove(key);
		T v2 = cm.remove(key);
		assertEquals(-1, v1, v2);
	}

	public <T> void testEquals(Map<Long, T> m, CompactLongKeyMap<T> cm) {
		assertEquals(-1L, (long) m.size(), cm.size());

		for (Entry<Long, T> e : m.entrySet())
			assertEquals(e.getKey(), e.getValue(), cm.get(e.getKey()));

		long count = 0;
		for (T i : cm) {
			long key = cm.getKey(i);
			assertEquals(key, m.get(key), i);
			assertEquals(key, m.get(key), cm.get(key));
			count++;
		}
		assertEquals(-1, (long) m.size(), count);
	}

	public <T> void assertEquals(long t, T i, T j) {
		if (OH.ne(i, j)) {
			Assert.assertEquals(OH.toString(t), i, j);
		}
	}
	@Test
	public void test2() {
		cm.clear();
		m.clear();
		Random r = new Random(123);
		for (int j = 0; j < 1000; j++)
			for (int i = 5; i < 10000; i++) {
				put(m, cm, r.nextInt(i));
				remove(m, cm, r.nextInt(i));
			}
	}
}
