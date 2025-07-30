/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.concurrent.test;

import java.util.HashMap;
import java.util.Map;

import com.f1.utils.OH;
import com.f1.utils.concurrent.FastQueue;
import com.f1.utils.structs.Tuple2;

public class FastQueueTest4<T> {
	public static void main2(String a[]) {
		while (true) {
			final FastQueue<Tuple2<Integer, Integer>> q = new FastQueue<Tuple2<Integer, Integer>>();
			for (int threads = 1; threads < 10; threads++)
				for (int loops = 1000; loops < 1000 * 1000 * 10; loops = loops * 10) {
					final int fLoops = loops;
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
					int count = 0;
					Map<Integer, Integer> m = new HashMap();
					while (true) {
						Tuple2<Integer, Integer> n = q.get();
						if (n == null)
							continue;
						if (m.get(n.getA()) == null) {
							if (n.getB() != 0)
								System.out.println("Error:" + n);
						} else if (m.get(n.getA()) + 1 != n.getB())
							System.out.println("Error:" + n);
						m.put(n.getA(), n.getB());
						count++;

						if ((count % 100000) == 0)
							System.out.println(count + "  " + m);
						if (count == loops * threads)
							break;
					}
					OH.sleep(100);
				}
		}
	}
}
