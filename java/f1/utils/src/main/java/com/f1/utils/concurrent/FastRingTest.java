package com.f1.utils.concurrent;

import com.f1.utils.AH;
import com.f1.utils.CH;
import com.f1.utils.EH;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.SH;

public class FastRingTest<T> {

	private static long doneTime;
	public static void main(String a[]) throws InterruptedException {
		System.out.println(EH.getPid());
		int[] counts = { 1, 5, 2, 50 };
		final Integer[] vals = new Integer[] { 1, 2, 3, 4, 5, 342, 34, 12, 67, 1, 6, 3, 3, 4, 19, 0, 45, 1, 2, 71 };
		final int runCount = 100 * 1000;
		for (final int size : new int[] { 2048, 1, 2, 4, 8, 16, 32, 64, 4096 }) {
			for (final int pCount : counts) {
				for (final int cCount : counts) {
					doneTime = -1;
					System.out.print("############# TESTING " + pCount + " PRODUCERS AND " + cCount + " CONSUMERS ON RING SIZE " + size + " ##############: ");
					int[] vals2 = AH.toArrayInt(CH.l(vals));
					long expected = (long) MH.sum(vals2) * runCount / vals.length;
					for (int count = 0; count < 1; count++) {
						final FastRing<Integer> rb = new FastRing<Integer>(size);
						final Thread[] threads = new Thread[200];
						for (int i = 0; i < pCount; i++) {
							threads[i] = new Thread() {

								long total = 0;
								public void run() {
									int j = runCount / pCount;
									for (int i = 0; i < j; i++) {
										Integer v = vals[i % vals.length];
										total += v.intValue();
										rb.put(v);
									}
								}
								public String toString() {
									return SH.toString(total);
								}
							};
						}
						for (int i = 0; i < cCount; i++) {
							threads[i + 100] = new Thread() {
								long total = 0;
								public void run() {
									for (int i = 0; i < runCount / cCount; i++) {
										Integer v = rb.take();
										total += v.intValue();
									}
									doneTime = System.currentTimeMillis();
								}
								public String toString() {
									return SH.toString(-total);
								}
							};
						}
						for (Thread t : threads)
							if (t != null)
								t.start();
						long start = System.currentTimeMillis();
						boolean done = false;
						while (!done) {
							OH.sleep(10);
							if (rb.getCountRemoved() == runCount) {
								for (Thread t : threads)
									if (t != null)
										t.join();
								done = true;
							}
							if (done) {
								long in = 0;
								long out = 0;
								for (Thread t : threads)
									if (t != null) {
										long n = Long.parseLong(t.toString());
										if (n > 0)
											in += n;
										else
											out -= n;
									}
								long m = 1 + System.currentTimeMillis() - start;
								m = 1 + doneTime - start;
								System.out.println(m + " : " + in + " vs " + out + " pos: " + rb.getCountRemoved() + " vs " + rb.getCountAdded() + "(diff="
										+ (rb.getCountAdded() - rb.getCountRemoved()) + ")   " + (rb.getCountRemoved() / m) + " msg/millis");
								if (in != expected || out != expected)
									throw new RuntimeException("expected: " + expected);
								if (rb.poll() != null)
									throw new RuntimeException("expecting empty ring buffer");
							}
						}
					}
				}
			}
		}

	}
}
