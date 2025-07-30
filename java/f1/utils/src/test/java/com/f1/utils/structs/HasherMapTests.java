package com.f1.utils.structs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Random;

import org.junit.Test;

import com.f1.utils.SH;
import com.f1.utils.concurrent.HasherMap;
import com.f1.utils.concurrent.LinkedHasherMap;
import com.f1.utils.impl.BasicHasher;

public class HasherMapTests {

	@Test
	public void testClone() {

		HasherMap<String, String> t = new HasherMap<String, String>(BasicHasher.INSTANCE, 32, .8f);
		Random r = new Random(123);
		for (int i = 0; i < 100; i++) {
			String s = SH.toString(r.nextInt(1000000), 32);
			t.put(s, s);
		}
		HasherMap<String, String> t2 = t.clone();

		Iterator<String> i1 = t.iterator();
		Iterator<String> i2 = t2.iterator();
		while (i1.hasNext()) {
			assertTrue(i2.hasNext());
			assertEquals(i1.next(), i2.next());
		}
		assertFalse(i2.hasNext());

	}

	@Test
	public void testLinkedHasherMap() {
		LinkedHashMap<Integer, Integer> t1 = new LinkedHashMap();
		LinkedHasherMap<Integer, Integer> t2 = new LinkedHasherMap();
		Random r = new Random(123);
		for (int n = 0; n < 20; n++) {
			for (int i = 0; i < 1000; i++) {
				int k = r.nextInt(100);
				int v = r.nextInt(10000);
				t1.put(k, v);
				t2.put(k, v);
			}
			Iterator<Integer> i1 = t1.values().iterator();
			Iterator<Integer> i2 = t2.values().iterator();
			while (i1.hasNext()) {
				assertTrue(i2.hasNext());
				assertEquals(i1.next(), i2.next());
			}
			assertFalse(i2.hasNext());
			assertEquals(t1.size(), t2.size());

			for (int i = 0; i < 50; i++) {
				int k = r.nextInt(100);
				Integer v = t1.remove(k);
				System.out.println("Removed " + k + "=" + v);
				assertEquals(v, t2.remove(k));
				assertEquals(t1.size(), t2.size());
			}
			System.out.println();

			i1 = t1.values().iterator();
			i2 = t2.values().iterator();
			while (i1.hasNext()) {
				assertTrue(i2.hasNext());
				assertEquals(i1.next(), i2.next());
			}
			assertFalse(i2.hasNext());
			assertEquals(t1.size(), t2.size());
		}
	}
}
