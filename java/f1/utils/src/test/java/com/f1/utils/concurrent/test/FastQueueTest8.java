package com.f1.utils.concurrent.test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import com.f1.utils.OH;
import com.f1.utils.concurrent.FastQueue;

public class FastQueueTest8 extends Thread {
	private static final int TOTAL = 1000000;
	private FastQueue<Integer> fq;

	public FastQueueTest8(FastQueue<Integer> fq) {
		this.fq = fq;
	}

	public long tot = 0;
	public long nullCount = 0;
	public static AtomicLong count = new AtomicLong();

	public static void main(String a[]) throws InterruptedException {
		for (int n = 0; n < 100; n++) {
			count.set(0);

			FastQueue<Integer> fq = new FastQueue<Integer>();
			long expected = 0;
			List<FastQueueTest8> l = new ArrayList<FastQueueTest8>();

			for (int i = 0; i < 10; i++) {
				l.add(new FastQueueTest8(fq));
			}
			for (FastQueueTest8 i : l)
				i.start();
			for (int i = 0; i < TOTAL; i++) {
				if (i % 10000 == 0)
					OH.sleep(10);
				fq.put(i);
				expected += i;
			}
			long tot = 0;
			long nullCount = 0;
			for (FastQueueTest8 i : l) {
				i.join();
				tot += i.tot;
				nullCount += i.nullCount;

			}
			System.out.println("Nulls: " + nullCount);
			System.out.println(expected + " vs " + tot);
			OH.assertEq(expected, tot);
		}
	}

	@Override
	public void run() {
		while (count.get() != TOTAL) {

			Integer i = fq.getThreadSafe();
			if (i == null) {
				nullCount++;
				continue;
			}
			tot += i.intValue();
			count.incrementAndGet();
		}
		//		System.out.println(this.getName() + " done");
	}

}
