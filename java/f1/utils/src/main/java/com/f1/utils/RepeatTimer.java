package com.f1.utils;

import java.util.concurrent.TimeUnit;

public class RepeatTimer implements Timer {
	final private long frequencyMs;
	private long offsetMs;

	public RepeatTimer(long frequency, TimeUnit units) {
		this(frequency, units, 0);
	}
	public RepeatTimer(long frequency, TimeUnit units, long offset) {
		this.frequencyMs = units.toMillis(frequency);
		this.offsetMs = units.toMillis(offset);
	}
	@Override
	public long calculateNextOccurance(long now) {
		now += frequencyMs - 1;
		return offsetMs + (now / frequencyMs) * frequencyMs;
	}

	public static void main(String ap[]) {
		System.out.println(new RepeatTimer(30, TimeUnit.SECONDS, 30).calculateNextOccurance(20));
		System.out.println(new RepeatTimer(30, TimeUnit.SECONDS, 30).calculateNextOccurance(0));
		System.out.println(new RepeatTimer(1, TimeUnit.SECONDS, 30).calculateNextOccurance(999));
		System.out.println(new RepeatTimer(1, TimeUnit.SECONDS, 30).calculateNextOccurance(1000));
		System.out.println(new RepeatTimer(1, TimeUnit.SECONDS, 30).calculateNextOccurance(1001));
	}

}
