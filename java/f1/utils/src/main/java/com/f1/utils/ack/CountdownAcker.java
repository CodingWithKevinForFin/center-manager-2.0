package com.f1.utils.ack;

import java.util.ArrayList;
import java.util.List;
import com.f1.base.Ackable;
import com.f1.base.Acker;
import com.f1.utils.OH;
import com.f1.utils.structs.Tuple2;

public class CountdownAcker implements Acker {

	final private List<Tuple2<Ackable, Object>> acks;
	private int size;

	public CountdownAcker(int size) {
		this.size = OH.assertGt(size, 0);
		acks = new ArrayList<Tuple2<Ackable, Object>>(size);
	}

	@Override
	public void ack(Ackable ackable_, Object optionalResult_) {
		synchronized (acks) {
			if (acks.size() == size)
				throw new IndexOutOfBoundsException("already received " + size + " ack(s)");
			acks.add(new Tuple2<Ackable, Object>(ackable_, optionalResult_));
			if (acks.size() == size)
				acks.notify();
		}
	}

	public List<Tuple2<Ackable, Object>> getAcks() {
		synchronized (acks) {
			return new ArrayList<Tuple2<Ackable, Object>>(acks);
		}
	}

	public int getAckedCount() {
		synchronized (acks) {
			return acks.size();
		}
	}

	public int getUnackedCount() {
		synchronized (acks) {
			return size - acks.size();
		}
	}

	/**
	 * @param timeoutMs
	 *            must be positive number of milliseconds (not zero)
	 * @return true if all acks received, false if timeout
	 */
	public boolean waitForAcks(long timeoutMs) {
		OH.assertGt(timeoutMs, 0);
		synchronized (acks) {
			if (size == acks.size())
				return true;
			if (!OH.wait(acks, timeoutMs))
				return false;
			return size == acks.size();
		}
	}

}

