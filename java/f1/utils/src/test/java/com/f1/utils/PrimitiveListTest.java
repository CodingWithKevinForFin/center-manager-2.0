package com.f1.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import junit.framework.Assert;

import org.junit.Test;

public class PrimitiveListTest {

	@Test
	public void test2() {
		List<Long> cl = new LongArrayList();
		ArrayList<Long> a = new ArrayList<Long>();
		Random r = new Random(1234);
		for (int i = 0; i < 100000; i++) {
			try {
				int t = r.nextInt(5);
				switch (t) {
					case 0: {
						if (a.size() == 0)
							continue;
						int index = r.nextInt(cl.size());
						a.remove(index);
						cl.remove(index);
						break;
					}
					case 1: {
						int index = r.nextInt(cl.size() + 1);
						long value = gen(r);
						a.add(index, value);
						cl.add(index, value);
						break;
					}
					case 2: {
						long value = gen(r);
						a.add(value);
						cl.add(value);
						break;
					}
					case 3: {
						int size = r.nextInt(10);
						ArrayList<Long> ta = new ArrayList<Long>();
						for (int j = 0; j < size; j++)
							ta.add(gen(r));
						a.addAll(ta);
						cl.addAll(ta);
						break;
					}
					case 4: {
						if (a.size() == 0)
							continue;
						long j = a.get(r.nextInt(a.size()));
						Assert.assertEquals(a.indexOf(j), a.indexOf(j));
						Assert.assertEquals(a.lastIndexOf(j), a.lastIndexOf(j));
					}
				}
				Assert.assertEquals(a.size(), cl.size());
			} catch (Throwable e) {
				System.out.println(a);
				System.out.println(cl);
				throw new RuntimeException("interval " + i, e);
			}
		}
		for (int j = 0; j < a.size(); j++)
			Assert.assertEquals("at " + j, a.get(j), cl.get(j));
		Iterator<Long> it = a.iterator();
		for (Long val : a) {
			Assert.assertTrue(it.hasNext());
			Assert.assertEquals(val, it.next());
		}
		Assert.assertEquals(a, cl);
		System.out.println(a.size());
	}

	public static void main(String a[]) {
		{
			for (Duration d = new Duration(); d.count() < 10; d.stampStdout(1)) {
				LongArrayList cl = new LongArrayList(10000000);
				for (int i = 0; i < 10000000; i++)
					cl.add(i);
				long t = 0;
				for (int i = 0; i < cl.size(); i++)
					t += cl.getLong(i);
				System.out.println(t);
			}
		}
		{
			for (Duration d = new Duration(); d.count() < 10; d.stampStdout(1)) {
				ArrayList<Long> cl = new ArrayList<Long>(10000000);
				for (int i = 0; i < 10000000; i++)
					cl.add((long) i);
				long t = 0;
				for (int i = 0; i < cl.size(); i++)
					t += cl.get(i);
				System.out.println(t);
			}
		}
	}

	private long gen(Random r) {
		return r.nextLong() % 1000;
	}
}
