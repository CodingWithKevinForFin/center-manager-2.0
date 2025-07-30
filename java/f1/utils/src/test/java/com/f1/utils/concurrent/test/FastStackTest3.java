/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.concurrent.test;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import com.f1.utils.OH;
import com.f1.utils.concurrent.ConcurrentNode;
import com.f1.utils.concurrent.FastStack;

public class FastStackTest3<T> {
	public static void main(String a[]) throws IOException {
		final FastStack<ConcurrentNode> q = new FastStack<ConcurrentNode>();

		while (true) {
			for (int threads = 1; threads < 10; threads++)
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
										q.push(new ConcurrentNode());
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
								try {
									int weird = 0;
									while (true) {
										ConcurrentNode n = q.pop();
										if (n == null) {
											if (count.get() == fLoops * fThreads)
												break;
											else {
												if (weird++ == 10000) {
													System.out.println("weird");
												}
												continue;
											}
										} else
											count.incrementAndGet();

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
