/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.concurrent.test;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import com.f1.utils.IOH;
import com.f1.utils.OH;
import com.f1.utils.concurrent.ConcurrentNode;
import com.f1.utils.concurrent.SynchronizedStack;

public class FastStackTest2 {
	static final AtomicInteger count = new AtomicInteger();
	static final AtomicLong loops = new AtomicLong();

	public static void main(String a[]) {
		final SynchronizedStack f = new SynchronizedStack();
		f.push(new ConcurrentNode(count.incrementAndGet()));
		f.push(new ConcurrentNode(count.incrementAndGet()));
		f.push(new ConcurrentNode(count.incrementAndGet()));
		f.push(new ConcurrentNode(count.incrementAndGet()));
		f.push(new ConcurrentNode(count.incrementAndGet()));
		f.push(new ConcurrentNode(count.incrementAndGet()));
		f.push(new ConcurrentNode(count.incrementAndGet()));
		f.push(new ConcurrentNode(count.incrementAndGet()));
		f.push(new ConcurrentNode(count.incrementAndGet()));
		f.push(new ConcurrentNode(count.incrementAndGet()));
		Runnable r;
		r = new Runnable() {

			@Override
			public void run() {
				StringBuilder sb = new StringBuilder();
				ConcurrentNode fn = new ConcurrentNode(count.incrementAndGet());
				f.push(fn);
				try {
					for (int i = 0; i < 1000 * 1000 * 100; i++) {
						loops.incrementAndGet();
						fn = f.pop();
						if (fn == null)
							throw new NullPointerException(Thread.currentThread().getName());
						f.push(fn);
					}
				} finally {

					try {
						IOH.writeText(new File("FILE-" + Thread.currentThread().getName()), sb.toString());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

			}
		};
		for (int i = 0; i < 10; i++)
			new Thread(r, "T" + i).start();
		while (true) {
			System.out.println(loops);
			OH.sleep(1000);
		}
	}
}
