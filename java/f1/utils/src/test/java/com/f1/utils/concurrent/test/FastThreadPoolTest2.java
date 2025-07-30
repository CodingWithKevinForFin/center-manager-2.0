/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.concurrent.test;

import java.util.concurrent.atomic.AtomicInteger;

import com.f1.utils.OH;
import com.f1.utils.concurrent.FastThreadPool;

public class FastThreadPoolTest2 {

	final static public AtomicInteger count = new AtomicInteger(0);

	public static void main2(String a[]) {
		while (true) {
			count.set(0);
			FastThreadPool tp = new FastThreadPool(2, "name");
			tp.start();
			Runnable r = new Runnable() {

				@Override
				public void run() {
					count.incrementAndGet();
				}
			};

			for (int i = 0; i < 10000000; i++)
				tp.execute(r);

			while (true) {
				OH.sleep(1000);
				System.out.println(count);
				if (count.get() == 10000000)
					break;
			}
		}
	}
}
