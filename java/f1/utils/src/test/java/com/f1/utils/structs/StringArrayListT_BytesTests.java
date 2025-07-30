package com.f1.utils.structs;

import java.util.ArrayList;
import java.util.Random;

import junit.framework.Assert;

import org.junit.Test;

import com.f1.utils.SH;
import com.f1.utils.StringArrayList_Bytes;

public class StringArrayListT_BytesTests {

	@Test
	public void test1() {

		for (int rand = 0; rand < 17; rand += 17) {
			System.out.println("Rand: " + rand);
			Random r = new Random(rand);
			Tester t = new Tester(r);
			int cnt = 10000;
			for (int n = 0; n < cnt; n++)
				t.add(t.newStringNoNull(20));
			t.verify();
			t.remove(17);
			t.verify();
			t.set(16, t.newStringNoNull(25));
			t.verify();

			t.clear();
			for (int n = 0; n < cnt; n++)
				t.add(t.newString(r.nextInt(20)));
			t.verify();

			for (int n = 0; n < cnt; n++) {
				t.set(r.nextInt(t.size()), t.newString(r.nextInt(20)));
			}
			t.verify();
			for (int n = 0; n < 100; n++) {
				t.remove(r.nextInt(t.size()));
			}
			for (int n = 0; n < cnt; n++) {
				t.add(r.nextInt(t.size()), t.newString(r.nextInt(20)));
			}
			t.verify();
			for (int n = 0; n < cnt; n++)
				t.set(r.nextInt(t.size()), t.newString(r.nextInt(2)));
			t.verify();
			for (int n = 0; n < cnt; n++)
				t.add(r.nextInt(t.size()), t.newString(r.nextInt(100)));
			t.verify();
			while (t.size() > 1)
				t.remove(r.nextInt(t.size()));
			t.verify();
			while (t.size() > 0)
				t.remove(r.nextInt(t.size()));
			t.verify();
			for (int n = 0; n < cnt; n++)
				t.add(t.newString(r.nextInt(20)));
			for (int n = 0; n < cnt / 10; n++) {
				int i = r.nextInt(t.size());
				for (int x = 0; x < 10; x++) {
					t.set(i, t.newString(r.nextInt(100)));
				}
			}
			t.verify();
			for (int x = 0; x < 10; x++) {
				t.set(0, t.newString(r.nextInt(126)));
			}
			t.verify();
			for (int x = 0; x < 10; x++) {
				t.set(t.size() - 1, t.newString(r.nextInt(126)));
			}
			t.verify();
			boolean addMode = true;
			int lowerLimit = 100;
			for (int i = 0; i < 100000; i++) {
				/*				if (i % 100000 == 0) {
									System.out.println(i);
									t.verify();
								}
				*/switch (r.nextInt(2)) {
					case 0:
						if (addMode) {
							t.add(r.nextInt(t.size() + 1), t.newRandomString());
							if (t.size() > 1000) {
								lowerLimit = r.nextInt(10) * 10;
								addMode = false;
							}
						} else {
							t.remove(r.nextInt(t.size()));
							if (t.size() <= lowerLimit)
								addMode = true;
						}
						break;
					case 1:
						if (t.size() > 0)
							t.set(r.nextInt(t.size()), t.newRandomString());
						break;
				}
			}
			t.verify();
			for (int n = 0; n < 20; n++) {
				t.set(r.nextInt(t.size()), t.newString(Byte.MAX_VALUE - 1));
			}
			t.verify();
		}
	}

	public static class Tester {

		final ArrayList<String> a = new ArrayList<String>();
		final StringArrayList_Bytes b = new StringArrayList_Bytes();
		final StringBuilder tmp = new StringBuilder();
		final private Random r;
		public Tester(Random r) {
			this.r = r;
		}
		public String newRandomString() {
			int n = r.nextInt(32);
			if (n == 31)
				return null;
			return newString(n == 30 ? StringArrayList_Bytes.MAX_LENGTH : (n > 20 ? (n * 4) : n));
		}
		public void clear() {
			a.clear();
			b.clear();
		}
		public int size() {
			return a.size();
		}
		public void add(String s) {
			a.add(s);
			b.add(s);
		}
		public void addJustB(String s) {
			b.add(s);
		}
		public void addAndLog(String s) {
			System.out.println("Adding " + a.size() + " to " + s);
			a.add(s);
			b.add(s);
		}
		public void remove(int n) {
			a.remove(n);
			b.remove(n);
		}
		public void setAndLog(int n, String s) {
			System.out.println("Setting " + n + " to " + s);
			a.set(n, s);
			b.set(n, s);
		}
		public void set(int n, String s) {
			a.set(n, s);
			b.set(n, s);
		}
		public void add(int n, String s) {
			a.add(n, s);
			b.add(n, s);
		}

		public void verify() {
			Assert.assertEquals(a.size(), b.size());
			for (int n = 0, l = a.size(); n < l; n++) {
				Assert.assertEquals(a.get(n), b.get(n));
				Assert.assertEquals(a.get(n) == null, b.isNull(n));
				Assert.assertTrue(b.isEqual(n, a.get(n)));
			}
			b.verify();
		}
		public String newString(int size) {
			if (r.nextInt(10) == 0)
				return null;
			SH.clear(tmp);
			while (size-- > 0)
				tmp.append((char) ('a' + r.nextInt(26)));
			return SH.toStringAndClear(tmp);
		}
		public String newStringNoNull(int size) {
			SH.clear(tmp);
			while (size-- > 0)
				tmp.append((char) ('a' + r.nextInt(26)));
			return SH.toStringAndClear(tmp);
		}
	}
}
