package com.f1.anvil.utils;

import java.util.ArrayList;
import java.util.Random;

import com.f1.ami.center.table.AmiRow;
import com.f1.utils.CH;
import com.f1.utils.OH;

public class AnvilHorizonImpl implements AnvilHorizon {

	AnvilHorizonEventImpl head = new AnvilHorizonEventImpl(this);
	AnvilHorizonEventImpl tail = new AnvilHorizonEventImpl(this);
	AnvilHorizonEventImpl pool;
	long newest = -1;
	private int size = 0;

	public AnvilHorizonImpl() {
		head.setNext(tail);
		tail.setPrior(head);
	}

	@Override
	public long getOldestTime() {
		AnvilHorizonEventImpl next = head.getNext();
		return next == tail ? newest : next.getTime();
	}

	@Override
	public long getNewestTime() {
		return newest;
	}

	@Override
	public AnvilHorizonEvent addTime(long time, AmiRow row) {
		if (time < newest)
			return null;
		size++;
		newest = time;
		AnvilHorizonEventImpl r;
		if (pool == null) {
			r = new AnvilHorizonEventImpl(this);
		} else {
			r = pool;
			pool = r.getNext();
		}
		r.reset(row, time, tail);
		return r;
	}
	public void returnToPool(AnvilHorizonEventImpl event) {
		size--;
		event.setNext(this.pool);
		event.setPrior(null);
		this.pool = event;

	}

	public static void main(String args[]) {
		AnvilHorizon t = new AnvilHorizonImpl();
		ArrayList<Long> t2 = new ArrayList<Long>();
		ArrayList<AnvilHorizonEvent> t3 = new ArrayList<AnvilHorizonEvent>();
		Random r = new Random(100);
		for (int j = 1; j < 100; j++) {
			for (int i = 0; i < 1000; i++) {
				long n = i + j * 1000;
				t2.add(n);
				t3.add(t.addTime(n, null));
			}
			System.out.println("starting");

			for (int i = 0; i < 1000; i++) {
				int n = r.nextInt(t2.size());
				AnvilHorizonEvent a = t3.remove(n);
				Long b = t2.remove(n);
				OH.assertEq(a.getTime(), b.longValue());
				a.remove();
				if (t2.isEmpty()) {
					System.out.println(t.getOldestTime());
					System.out.println(t.getNewestTime());
					continue;
				}
				Long c = t2.get(CH.minIndex(t2));
				Long d = t.getOldestTime();
				if (c.longValue() != d.longValue())
					System.out.println(t2);
				OH.assertEq(c, d);
			}
		}

	}

	public int getSize() {
		return size;
	}

}