package com.f1.utils;

import java.util.ArrayList;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

public class CircularListTests {

	@Test
	public void test() {
		CircularList<String> cl = new CircularList<String>(4);
		CH.l(cl, "a", "b", "c", "d");
		Assert.assertEquals("a", cl.get(0));
		Assert.assertEquals("b", cl.get(1));
		Assert.assertEquals("c", cl.get(2));
		Assert.assertEquals("d", cl.get(3));
		cl.remove(0);
		Assert.assertEquals("b", cl.get(0));
		Assert.assertEquals("c", cl.get(1));
		Assert.assertEquals("d", cl.get(2));
	}

	@Test
	public void test2() {
		CircularList<Integer> cl = new CircularList<Integer>();
		ArrayList<Integer> a = new ArrayList<Integer>();
		Random r = new Random(1234);
		int cnt = 0;
		for (int i = 0; i < 1000000; i++) {
			if (cnt++ == 65503) {
				System.out.println("hear");
			}
			if (i % 10000 == 0)
				System.out.println(cl.size() + " == " + a.size());
			try {
				if (r.nextInt(10000) == 0) {
					while (a.size() > 0) {
						Integer v1 = a.remove(0);
						Integer v2 = cl.remove(0);
						Assert.assertEquals(v1, v2);
					}
					for (int j = 0; j < 1000; j++) {
						int n = r.nextInt(100);
						for (int k = 0; k < n; k++) {
							a.add(j);
							cl.add(j);
						}
						for (int k = 0; k < n; k++) {
							Integer v1 = a.remove(0);
							Integer v2 = cl.remove(0);
							Assert.assertEquals(v1, v2);
						}
					}
				} else if (r.nextInt(100000) == 0) {
					a.clear();
					cl.clear();
				}
				if (r.nextInt(3) == 0 && a.size() > 0) {
					int index = r.nextInt(cl.size());
					Integer v1 = a.remove(index);
					Integer v2 = cl.remove(index);
					Assert.assertEquals(v1, v2);
				} else {
					int index = r.nextInt(cl.size() + 1);
					int value = r.nextInt();
					a.add(index, value);
					cl.add(index, value);
				}
				if (i % 1000 == 0) {
					int index = r.nextInt(cl.size());
					Assert.assertEquals(cl.indexOf(a.get(index)), index);
					Assert.assertEquals(a.size(), cl.size());
				}
			} catch (Throwable e) {
				throw new RuntimeException("at " + i, e);
			}
		}
		Assert.assertEquals(a, cl);
	}
}
