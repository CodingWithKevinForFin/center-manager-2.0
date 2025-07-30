/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.concurrent.test;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import com.f1.utils.OH;
import com.f1.utils.concurrent.FastQueue;
import com.f1.utils.structs.Tuple2;

public class FastQueueTest2<T> {
	public static void main(String a[]) throws IOException {
		final PrintWriter f = new PrintWriter(new FileWriter("test.txt"));
		final FastQueue<Tuple2<Integer, Integer>> q = new FastQueue<Tuple2<Integer, Integer>>();
		while (true) {
			for (int threads = 4; threads < 10; threads++)
				for (int loops = 1000; loops < 1000 * 1000 * 10; loops = loops * 10) {
					final int fLoops = loops;
					final int fThreads = threads;
					System.out.println("starting:" + threads + "," + loops);
					for (int i = 0; i < threads; i++) {
						final int t = i;
						new Thread() {
							public void run() {
								try {
									for (int i = 0; i < fLoops; i++) {
										q.put(new Tuple2<Integer, Integer>(t, i));
									}
								} catch (Exception e) {
									e.printStackTrace();
									System.exit(1);
								}

							}

						}.start();
					}

					final AtomicInteger count = new AtomicInteger(0);
					for (int i = 0; i < 4; i++) {
						new Thread() {
							public void run() {
								Map<Integer, Integer> m = new HashMap();
								List<Tuple2> values = new ArrayList<Tuple2>();
								try {
									int weird = 0;
									while (true) {
										Tuple2<Integer, Integer> n = q.getThreadSafe();
										if (n == null) {
											if (count.get() == fLoops * fThreads)
												break;
											else {
												if (weird++ == 10000) {
													System.out.println("values:" + m.values() + " count:" + count.get());
													for (Tuple2 t : values)
														f.println(t);
												}
												continue;
											}
										}
										values.add(n);
										if (m.get(n.getA()) != null && m.get(n.getA()) >= n.getB())
											System.out.println("Error:" + n + " ," + m.get(n.getA()));
										m.put(n.getA(), n.getB());

										if ((count.incrementAndGet() % 100000) == 0)
											System.out.println(count + "  " + m);
									}
									System.out.println("Thread done");

								} catch (Exception e) {
									e.printStackTrace();
									System.exit(1);
								}
							}

						}.start();
					}
					while (count.get() != fLoops * fThreads)
						OH.sleep(100);
					OH.sleep(3000);
					count.set(0);
				}
		}
	}
}
