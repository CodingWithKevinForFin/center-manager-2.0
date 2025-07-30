package com.f1.utils.concurrent.test;

import java.util.ArrayList;
import java.util.List;

import com.f1.utils.OH;
import com.f1.utils.concurrent.FastQueue;

public class FastQueueTest6 extends Thread {
	private FastQueue<Integer> fq;
	private long tot = 0;
	private int count = 0;

	public FastQueueTest6(FastQueue<Integer> fq) {
		this.fq = fq;
	}

	public static void main(String a[]) throws InterruptedException {

		for (int j = 9; j < 10; j++) {
			for (int n = 0; n < 40; n++) {
				long start = System.currentTimeMillis();
				FastQueue<Integer> fq = new FastQueue<Integer>();
				long expected = 0;
				for (int i = 0; i < 200000 * j; i++) {
					fq.put(i);
					expected += i;
				}
				List<FastQueueTest6> l = new ArrayList<FastQueueTest6>();
				for (int i = 0; i < n; i++) {
					l.add(new FastQueueTest6(fq));
				}
				for (FastQueueTest6 i : l)
					i.start();
				for (int i = 0; i < 2000000 * j; i++) {
					fq.put(i);
					expected += i;
				}
				for (FastQueueTest6 i : l)
					i.join();
				long tot = 0;
				for (FastQueueTest6 i : l) {
					//					System.out.println(i.getName() + ": " + i.count + ", " + i.tot);
					tot += i.tot;
				}
				long extraTot = 0;
				long extraCount = 0;
				for (;;) {
					Integer i = fq.getThreadSafe();
					if (i == null)
						break;
					extraTot += i;
					extraCount++;
				}
				tot += extraTot;
				//				System.out.println(Thread.currentThread().getName() + ": " + extraCount + ", " + extraTot);
				//				System.out.println(expected + " vs " + tot);
				OH.assertEq(expected, tot);
				long end = System.currentTimeMillis();
				System.out.println("################ " + j + " x " + n + " ######################: " + (end - start));
			}
		}

	}

	@Override
	public void run() {
		for (;;) {
			Integer i = fq.getThreadSafe();
			if (i == null)
				break;
			tot += i;
			count++;
		}
		//		System.out.println(this.getName() + " done");
	}

}
