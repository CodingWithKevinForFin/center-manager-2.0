package com.f1.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.f1.utils.structs.Tuple2;

public class MultiTimer implements Timer {

	final private List<Tuple2<Long, Timer>> timers = new ArrayList<Tuple2<Long, Timer>>();
	final private List<Tuple2<Long, Timer>> newTimers = new ArrayList<Tuple2<Long, Timer>>();
	final private List<Timer> removedTimers = new ArrayList<Timer>();

	public MultiTimer(Timer... timers) {
		for (Timer timer : timers)
			addTimer(timer);
	}

	public boolean removeTimer(Timer timer) {
		for (int i = 0; i < newTimers.size(); i++)
			if (newTimers.get(i).getB() == timer) {
				newTimers.remove(i);
				return true;
			}
		for (int i = 0; i < timers.size(); i++)
			if (timers.get(i).getB() == timer) {
				timers.remove(i);
				return true;
			}
		return false;
	}
	public void addTimer(Timer timer) {
		newTimers.add(new Tuple2<Long, Timer>(null, timer));
	}
	public void addTimer(long nextOccurence, Timer timer) {
		timers.add(new Tuple2<Long, Timer>(nextOccurence, timer));
	}

	@Override
	public long calculateNextOccurance(long now) {
		if (!newTimers.isEmpty()) {
			for (Tuple2<Long, Timer> tuple : newTimers) {
				long next = tuple.getB().calculateNextOccurance(now);
				if (next != Timer.DONE) {
					tuple.setA(next);
					timers.add(tuple);
				} else
					removedTimers.add(tuple.getB());
			}
			newTimers.clear();
		}

		long minNext = -1;
		int minIndex = -1;
		for (int i = 0; i < timers.size(); i++) {
			Tuple2<Long, Timer> tuple = timers.get(i);
			long next = tuple.getA();
			if (next < now) {
				next = tuple.getB().calculateNextOccurance(now);
				if (next == Timer.DONE) {
					removedTimers.add(timers.remove(i--).getB());
					continue;
				}
				tuple.setA(next);
			}
			if (minNext == -1 || minNext >= next) {
				minNext = next;
				minIndex = i;
			}
		}
		if (minNext == -1)
			return -1;
		return minNext;
	}
	public Iterable<Timer> popFinishedTimers() {
		ArrayList<Timer> r = new ArrayList<Timer>(removedTimers);
		removedTimers.clear();
		return r;
	}

	public List<Timer> getCurrentTimers(long now) {
		return getCurrentTimer(now, new ArrayList<Timer>(1));
	}

	public List<Timer> getCurrentTimer(long now, List<Timer> sink) {
		for (Tuple2<Long, Timer> tuple : timers)
			if (tuple.getA() == now)
				sink.add(tuple.getB());
		return sink;
	}

	public static void main(String a[]) {
		MultiTimer mt = new MultiTimer();
		TimerWrapper timer = new TimerWrapper(mt, 0, 10001);
		mt.addTimer(new RepeatTimer(100, TimeUnit.MILLISECONDS));
		mt.addTimer(new RepeatTimer(35, TimeUnit.MILLISECONDS));
		for (long now = 0; now != Timer.DONE; now = timer.calculateNextOccurance(now + 1)) {
			System.out.println(now + ": " + mt.getCurrentTimers(now).size());
		}

	}

	public void clear() {
		timers.clear();
		newTimers.clear();
	}
}
