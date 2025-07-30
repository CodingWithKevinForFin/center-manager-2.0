/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils;

public class Duration {

	private long start;
	private long prior;
	private long cnt;
	private String text;
	private StringBuilder sb = new StringBuilder();

	public Duration() {
		this("");
	}

	public Duration(String text) {
		reset(text);
	}

	public String stamp(int incCount) {
		return stamp(incCount, "");
	}

	public String stampMills(int incCount) {
		return stampMills(incCount, "");
	}

	public String stampMicros(int incCount) {
		return stampMicros(incCount, "");
	}

	public void stampStdout() {
		System.out.println(stamp(0));
	}

	public void stampStdout(int incCount) {
		System.out.println(stamp(incCount));
	}

	public void stampMillsStdout(int incCount) {
		System.out.println(stampMills(incCount));
	}

	public void stampMicrosStdout(int incCount) {
		System.out.println(stampMicros(incCount));
	}

	public void stampMsStdout() {
		System.out.println(stampMills(1));
	}

	public String stamp(int incCount, Object append) {
		cnt += incCount;
		long now = System.nanoTime();
		sb.setLength(text.length() + 1);
		toStringSec(now - start, sb);
		if (cnt > 1) {
			sb.append(", ");
			if (incCount == 1) {
				toStringSec((now - prior) / incCount, sb).append(", ");
				toStringSec((now - start) / cnt, sb).append(" sec (total,immediate,avg for " + cnt + ")");
			} else {
				toStringSec((now - start) / cnt, sb).append(" sec (total,avg for " + cnt + ")");
			}
		} else
			sb.append(" sec (total)");
		prior = now;
		return sb.toString();
	}

	public String stampMills(int incCount, Object append) {
		cnt += incCount;
		long now = System.nanoTime();
		sb.setLength(text.length() + 1);
		toStringMillis(now - start, sb);
		if (cnt > 1) {
			sb.append(", ");
			if (incCount == 1) {
				toStringMillis((now - prior) / incCount, sb).append(", ");
				toStringMillis((now - start) / cnt, sb).append(" millis (total,immediate,avg for " + cnt + ")");
			} else {
				toStringMillis((now - start) / cnt, sb).append(" millis (total,avg for " + cnt + ")");
			}
		} else
			sb.append(" millis (total)");
		prior = now;
		return sb.toString();
	}

	public String stampMicros(int incCount, Object append) {
		cnt += incCount;
		long now = System.nanoTime();
		sb.setLength(text.length() + 1);
		toStringMicros(now - start, sb);
		if (cnt > 1) {
			sb.append(", ");
			if (incCount == 1) {
				toStringMicros((now - prior) / incCount, sb).append(", ");
				toStringMicros((now - start) / cnt, sb).append(" micros (total,immediate,avg for " + cnt + ")");
			} else {
				toStringMicros((now - start) / cnt, sb).append(" micros (total,avg for " + cnt + ")");
			}
		} else
			sb.append(" micros (total)");
		prior = now;
		return sb.toString();
	}

	private static StringBuilder toStringSec(long time, StringBuilder sb) {
		sb.append(time / 1000000000L).append('.');
		return SH.rightAlign('0', SH.toString((time / 1000) % 1000000), 6, true, sb);
	}

	private static StringBuilder toStringMillis(long time, StringBuilder sb) {
		sb.append(time / 1000000L).append('.');
		return SH.rightAlign('0', SH.toString(time % 1000000), 6, true, sb);
	}

	private static StringBuilder toStringMicros(long time, StringBuilder sb) {
		sb.append(time / 1000L).append('.');
		return SH.rightAlign('0', SH.toString(time % 1000), 6, true, sb);
	}

	public void reset() {
		reset(text);
	}

	public void reset(String text) {
		this.text = text;
		sb.setLength(0);
		sb.append(text).append(' ');
		prior = start = System.nanoTime();
		cnt = 0;
	}

	public void incrementCount(int i) {
		cnt += i;
	}

	public void incrementCount() {
		cnt++;
	}

	public long count() {
		return cnt;
	}

}
