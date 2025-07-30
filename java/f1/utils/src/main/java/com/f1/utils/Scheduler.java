package com.f1.utils;

import java.util.PriorityQueue;
import java.util.logging.Logger;

import com.f1.base.Clearable;
import com.f1.base.Generator;
import com.f1.base.ToStringable;
import com.f1.utils.concurrent.ObjectPoolForClearable;

public class Scheduler<T> implements Generator<Scheduler.AmiScheduleEvent<T>> {
	private static final Logger log = LH.get();
	final private PriorityQueue<AmiScheduleEvent<T>> queue = new PriorityQueue<Scheduler.AmiScheduleEvent<T>>();
	final private ObjectPoolForClearable<AmiScheduleEvent<T>> pool = new ObjectPoolForClearable<AmiScheduleEvent<T>>(this, 1024);
	private long sequence = 0;

	@Override
	public AmiScheduleEvent<T> nw() {
		return new AmiScheduleEvent<T>();
	}
	public long addEvent(long timeMs, T event) {
		return addEvent(timeMs, 0, event);
	}
	public long addEvent(long timeMs, int priority, T event) {
		long r = sequence++;
		final AmiScheduleEvent<T> evt = pool.nw();
		evt.reset(timeMs, priority, r, event);
		queue.offer(evt);
		return r;
	}
	public long getNextTime() {
		final AmiScheduleEvent<T> next = queue.peek();
		return (next == null) ? Long.MAX_VALUE : next.time;
	}
	public boolean isEmpty() {
		return queue.isEmpty();
	}
	public int size() {
		return queue.size();
	}

	/**
	 * @param now
	 *            cutoff time.
	 * @return null if there now events before the supplied cutoff time. Otherwise the oldest event (event with the lowest time)
	 */
	public T next(long now) {
		if (getNextTime() > now)
			return null;
		final AmiScheduleEvent<T> obj = queue.poll();
		final T r = obj.object;
		pool.recycle(obj);
		return r;

	}

	public static class AmiScheduleEvent<T> implements Comparable<AmiScheduleEvent<T>>, ToStringable, Clearable {

		private long time;
		private long sequence;
		private T object;
		private int priority;

		protected void reset(long time, int priority, long sequence, T runnable) {
			this.time = time;
			this.priority = priority;
			this.sequence = sequence;
			this.object = runnable;
		}

		@Override
		public int compareTo(AmiScheduleEvent<T> o) {
			if (time > o.time)
				return 1;
			else if (time < o.time)
				return -1;
			if (priority > o.priority)
				return 1;
			else if (priority < o.priority)
				return -1;
			else
				return (sequence > o.sequence) ? 1 : -1;
		}

		public String toString() {
			return toString(new StringBuilder()).toString();
		}

		@Override
		public StringBuilder toString(StringBuilder sink) {
			return sink.append("AmiScheduledEvent[").append(sequence).append("]: ").append(time).append(" => ").append(object);
		}

		@Override
		public void clear() {
			this.object = null;
			sequence = time = 0;
		}

	}

	////////////////// TESTING
	public static void main(String[] a) {
		Scheduler<Long> s = new Scheduler<Long>();

		addEvent(s, 100);
		addEvent(s, 200);
		addEvent(s, 150);
		runTo(s, 125);
		runTo(s, 175);

		addEvent(s, 100);
		addEvent(s, 108);
		addEvent(s, 110);
		addEvent(s, 111);
		addEvent(s, 112);
		addEvent(s, 107);
		addEvent(s, 84);
		addEvent(s, 109);
		addEvent(s, 117);
		addEvent(s, 118);
		addEvent(s, 113);
		runTo(s, 111);
		addEvent(s, 107);
		addEvent(s, 114);
		runTo(s, 118);
		addEvent(s, 120);
		addEvent(s, 121);
		addEvent(s, 124);
		addEvent(s, 126);
		addEvent(s, 126);
		addEvent(s, 126);
		runTo(s, 130);
		addEvent(s, 126);
		addEvent(s, 129);
		addEvent(s, 150);
		addEvent(s, 128);
		addEvent(s, 127);
		addEvent(s, 129);
		addEvent(s, 129);
		runTo(s, 130);
	}

	private static void runTo(Scheduler<Long> s, long now) {
		System.out.print("  (");
		for (;;) {
			Long time = s.next(now);
			if (time == null)
				break;
			System.out.print(" " + time);
		}
		System.out.println(" )  <<== To " + now);

	}

	private static void addEvent(Scheduler<Long> s, long n) {
		System.out.println("Adding   " + n);
		s.addEvent(n, n);
	}
}
