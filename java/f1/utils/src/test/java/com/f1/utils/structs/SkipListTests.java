package com.f1.utils.structs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import com.f1.utils.CircularList;
import com.f1.utils.Duration;
import com.f1.utils.OH;

public class SkipListTests {

	@Test
	public void testSparce() {

		SparseList<Integer> sl = new SparseList<Integer>(0);
		for (int i = 0; i < 1000000; i++)
			sl.add(i);

		System.out.println("Starting");
		Random r = new Random(1234);
		for (int n = 0; n < 10; n++) {
			long start = System.currentTimeMillis();
			for (int i = 0; i < 1000000; i++)
				sl.get(r.nextInt(sl.size()));
			long end = System.currentTimeMillis();
			System.out.println(end - start);
		}

		Iterator<Integer> i = sl.iterator();
		int pos = 0;
		while (i.hasNext()) {
			Assert.assertEquals(sl.get(pos), i.next());
			pos++;
		}
		Assert.assertEquals(pos, sl.size());

	}
	@Test
	public void testSkipListSplitBlock() {
		// This test is based on the fact the list will split at 
		ArrayList<SkipListDataEntry<Integer>> arrList = new ArrayList<SkipListDataEntry<Integer>>(1000);
		SkipList<SkipListDataEntry<Integer>> skipList = new SkipList<SkipListDataEntry<Integer>>(1000);
		int split = skipList.blockSize / 2 + 1; // And This will split if you insert at 501

		int i = 0;
		for (; i < 3; i++) {
			SkipListDataEntry<Integer> e = new SkipListDataEntry<Integer>(i);
			arrList.add(e);
			skipList.add(e);
		}
		for (; i < 700; i++) {
			SkipListDataEntry<Integer> e = new SkipListDataEntry<Integer>(i);
			arrList.add(1, e);
			skipList.add(1, e);
		}
		for (; i < 1000; i++) {
			SkipListDataEntry<Integer> e = new SkipListDataEntry<Integer>(i);
			arrList.add(300, e);
			skipList.add(300, e);
		}

		SkipListDataEntry<Integer> e2 = new SkipListDataEntry<Integer>(i++);
		arrList.add(split, e2);
		skipList.add(split, e2);

		SkipListDataEntry<Integer> e3 = new SkipListDataEntry<Integer>(i++);
		arrList.add(split - 1, e3);
		skipList.add(split - 1, e3);

		Assert.assertEquals("Checking if node at " + i + ", " + " is the same", arrList.get(split + 1), skipList.get(split + 1));
		Assert.assertEquals("Checking if node at " + i + ", " + " is the same", arrList.get(split), skipList.get(split));
	}

	@Test
	public void testSort() {
		Random r = new Random(123645);

		for (int n = 0; n < 20; n++) {
			List<SkipListDataEntry<Integer>> others = new ArrayList<SkipListDataEntry<Integer>>(1000);
			SkipList<SkipListDataEntry<Integer>> sl = new SkipList<SkipListDataEntry<Integer>>(1000);
			for (int i = 0; i < 10000; i++) {
				SkipListDataEntry<Integer> e = new SkipListDataEntry<Integer>(r.nextInt());
				others.add(e);
				sl.add(e);
			}
			Collections.sort(sl, new Comparator<SkipListDataEntry<Integer>>() {
				@Override
				public int compare(SkipListDataEntry<Integer> o1, SkipListDataEntry<Integer> o2) {
					return OH.compare(o1.getData(), o2.getData());
				}
			});
			for (int i = 0; i < 10000; i++) {
				try {
					int rn = r.nextInt(sl.size);
					SkipListDataEntry<Integer> entry = others.get(rn);
					int loc = entry.getLocation();
					if (loc > 0) {
						OH.assertLe(sl.get(loc - 1).getData(), entry.getData());
					}
					if (loc < sl.size - 1) {
						OH.assertGe(sl.get(loc + 1).getData(), entry.getData());
					}
				} catch (Exception e) {
					throw new RuntimeException("For " + n + ":" + i, e);
				}
			}
		}
	}
	@Test
	public void test1() {
		SkipList<SkipListDataEntry<Integer>> sl = new SkipList<SkipListDataEntry<Integer>>(1000);
		for (int i = 0; i < 100; i++)
			sl.add(i, new SkipListDataEntry<Integer>(i));
		System.out.println(sl);
		System.out.println();
		sl.remove(1);

		for (int i = 0; i < 90; i++) {
			System.out.println(i);
			sl.remove(i / 10);
		}
		System.out.println(sl);
		System.out.println();
	}

	public void test2() {
		for (Duration d = new Duration("skiplist"); d.count() < 10; d.stampMsStdout()) {
			SkipList<SkipListDataEntry<Integer>> sl = new SkipList<SkipListDataEntry<Integer>>(1000 * 1000 * 10);
			for (int i = 0; i < 1000 * 1000 * 10; i++) {
				sl.add(i, new SkipListDataEntry<Integer>(i));
			}
		}
		for (Duration d = new Duration("arrylist"); d.count() < 10; d.stampMsStdout()) {
			ArrayList<Integer> sl = new ArrayList<Integer>(1000000);
			for (int i = 0; i < 1000 * 1000 * 10; i++) {
				sl.add(i, i);
			}
		}
	}

	@Test
	public void testCirc() {
		Random r = new Random(1234);
		for (int p = 1; p < 2; p += 1) {
			CircularList<SkipListDataEntry<Integer>> cl = new CircularList<SkipListDataEntry<Integer>>(p * p * 100);
			ArrayList<SkipListDataEntry<Integer>> a = new ArrayList<SkipListDataEntry<Integer>>(0);
			toSize(cl, a, 100000, r);
			toSize(cl, a, 1000, r);
			toSize(cl, a, 100000, r);
			toSize(cl, a, 10000, r);
			testSample(cl, a, p * 100, r);
			toSize(cl, a, 0, r);
			toSize(cl, a, 100000, r);
			toSize(cl, a, 10000, r);
			testSample(cl, a, p * 100, r);
			toSize(cl, a, 1000, r);
			testSample(cl, a, p * 100, r);
			toSize(cl, a, 1000, r);
			toSize(cl, a, 100, r);
			testSample(cl, a, p, r);
			toSize(cl, a, 0, r);

			for (int i = 0; i < 10; i++)
				toSizeCont(cl, a, i * 10000, r);
			testSample(cl, a, p * 100, r);
			for (int i = 10; i >= 10; i--)
				toSizeCont(cl, a, i * 10000, r);
			testSample(cl, a, p * 100, r);
		}
	}
	@Test
	public void test4() {
		Random r = new Random(1234);
		for (int p = 1; p < 5; p += 1) {
			SkipList<SkipListDataEntry<Integer>> cl = new SkipList<SkipListDataEntry<Integer>>(p * p * 100);
			ArrayList<SkipListDataEntry<Integer>> a = new ArrayList<SkipListDataEntry<Integer>>(0);
			toSize(cl, a, 100000, r);
			toSize(cl, a, 1000, r);
			toSize(cl, a, 100000, r);
			toSize(cl, a, 10000, r);
			testSample(cl, a, p * 100, r);
			toSize(cl, a, 0, r);
			toSize(cl, a, 100000, r);
			toSize(cl, a, 10000, r);
			testSample(cl, a, p * 100, r);
			toSize(cl, a, 1000, r);
			testSample(cl, a, p * 100, r);
			toSize(cl, a, 1000, r);
			toSize(cl, a, 100, r);
			testSample(cl, a, p, r);
			toSize(cl, a, 0, r);

			for (int i = 0; i < 10; i++)
				toSizeCont(cl, a, i * 10000, r);
			testSample(cl, a, p * 100, r);
			for (int i = 10; i >= 10; i--)
				toSizeCont(cl, a, i * 10000, r);
			testSample(cl, a, p * 100, r);
		}
	}
	private void testSample(List<SkipListDataEntry<Integer>> sl, List<SkipListDataEntry<Integer>> a, int i, Random r) {
		Assert.assertEquals(sl.size(), a.size());
		if (sl.size() == 0)
			return;
		int n = i;
		if (sl instanceof SkipList)
			while (n-- > 0) {
				int index = r.nextInt(sl.size());
				SkipListDataEntry<Integer> val = a.get(index);
				SkipListDataEntry<Integer> val2 = sl.get(val.getLocation());
				Assert.assertEquals(val.getData(), val2.getData());
			}
		while (i-- > 0) {
			int index = r.nextInt(sl.size());
			SkipListDataEntry<Integer> val = a.get(index);
			SkipListDataEntry<Integer> val2 = sl.get(index);
			Assert.assertEquals(val.getData(), val2.getData());
		}

	}

	private void toSize(List<SkipListDataEntry<Integer>> cl, List<SkipListDataEntry<Integer>> a, int i, Random r) {
		System.out.println("At " + (cl.size()) + " to " + i);
		while (cl.size() < i) {
			int index = r.nextInt(cl.size() + 1);
			int value = r.nextInt();
			SkipListDataEntry<Integer> v = new SkipListDataEntry<Integer>(value);
			a.add(index, v);
			cl.add(index, v);
		}
		while (cl.size() > i) {
			int index = r.nextInt(cl.size());
			SkipListDataEntry<Integer> val = a.remove(index);
			if (cl.get(index).getData() == 2087757280)
				System.out.println("here we go!");
			SkipListDataEntry<Integer> val2 = cl.remove(index);
			if (val2.getLocation() == 0)
				System.out.println("Problems!");
			OH.assertEq(val2.getLocation(), -1);
			Assert.assertEquals(val.getData(), val2.getData());
		}
	}
	private void toSizeCont(List<SkipListDataEntry<Integer>> cl, List<SkipListDataEntry<Integer>> a, int i, Random r) {
		int index = r.nextInt(cl.size() + 1);
		System.out.println("Continuous At " + (cl.size()) + " to " + i + " (starting at " + index + ")");
		while (cl.size() < i) {
			int value = r.nextInt();
			SkipListDataEntry<Integer> v = new SkipListDataEntry<Integer>(value);
			a.add(index, v);
			cl.add(index, v);
		}
		while (cl.size() > i) {
			if (index >= cl.size())
				index = cl.size() - 1;
			SkipListDataEntry<Integer> val = a.remove(index);
			SkipListDataEntry<Integer> val2 = cl.remove(index);
			Assert.assertEquals(val.getData(), val2.getData());
		}
	}

	@Test
	public void test3() {
		Random r = new Random(1234);
		for (int p = 1; p < 2; p += 1) {
			SkipList<SkipListDataEntry<Integer>> cl = new SkipList<SkipListDataEntry<Integer>>(p * p * 100);
			ArrayList<SkipListDataEntry<Integer>> a = new ArrayList<SkipListDataEntry<Integer>>(0);
			for (int q = 1; q < 16; q += 1) {
				for (int k = 2; k < 5; k++) {
					System.out.println(p + "," + q + "," + k + ": " + cl.size());
					for (int i = 0; i < 5000; i++) {
						try {
							int c = r.nextInt(15);
							if (c < q) {
								if (cl.size() > 0) {
									int index = r.nextInt(cl.size());
									if (c == 0)
										index = 0;
									else if (c == 1)
										index = cl.size() - 1;
									SkipListDataEntry<Integer> t1 = a.remove(index);
									cl.assertCorrect();
									SkipListDataEntry<Integer> t2 = cl.remove(index);
									cl.assertCorrect();
									if (OH.ne(t1.getData(), t2.getData()))
										Assert.assertEquals(t1.getData(), t2.getData());
								}
							} else {
								int index = r.nextInt(cl.size() + 1);
								if (c < 7)
									index = 0;
								else if (c < 9)
									index = cl.size();
								int value = r.nextInt();
								a.add(index, new SkipListDataEntry<Integer>(value));
								cl.assertCorrect();
								if (q == 1 && k == 2 && i == 960)
									System.out.println("asdf");
								cl.add(index, new SkipListDataEntry<Integer>(value));
								cl.assertCorrect();
							}
							Assert.assertEquals(a.size(), cl.size());
							if (i % 1000 == 0)
								assertEquals("" + k + ", " + q, a, cl);
						} catch (Throwable e) {
							throw new RuntimeException("at " + q + " , " + k + ", " + i, e);
						}
					}
					assertEquals("" + k, a, cl);
				}
			}
		}
	}

	private void assertEquals(String text, ArrayList<SkipListDataEntry<Integer>> a, SkipList<SkipListDataEntry<Integer>> b) {
		Assert.assertEquals(a.size(), b.size());
		for (int i = 0; i < a.size(); i++) {
			Assert.assertEquals("at " + i + ", " + text, a.get(i).getData(), b.get(i).getData());
		}
	}

	@Test
	public void testLocations() {
		SkipList<SkipListDataEntry<Integer>> sl = new SkipList<SkipListDataEntry<Integer>>(10000);
		for (int i = 0; i < 100000; i++) {
			sl.add(new SkipListDataEntry<Integer>(i));
		}
		Random r = new Random(321);
		assertLocations(r, sl);

		for (int i = 0; i < 10000; i++) {
			sl.add(0, new SkipListDataEntry<Integer>(i));
		}
		assertLocations(r, sl);
		sl.add(44879, new SkipListDataEntry<Integer>(0));
		int loca = sl.get(44880).getLocation();
		Assert.assertEquals(44880, loca);
		assertLocations(r, sl);

		for (int i = 0; i < 10000; i++) {
			int loc = r.nextInt(sl.size());
			System.out.println(loc);
			sl.add(loc, new SkipListDataEntry<Integer>(i));
		}
		assertLocations(r, sl);
	}
	private void assertLocations(Random r, SkipList<SkipListDataEntry<Integer>> a) {
		for (int i = 0; i < a.size(); i++) {
			if (i != a.get(i).getLocation())
				System.out.println(i);
			Assert.assertEquals(i, a.get(i).getLocation());
		}
	}

}
