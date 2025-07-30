package com.f1.utils.ids;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import com.f1.utils.OH;
import com.f1.utils.SH;

public class BasicStringIdGenerator implements IdGenerator<String> {
	private final IdGenerator<? extends Number> inner;
	private final int length;
	private final String prefix;

	public BasicStringIdGenerator(String prefix, int length, IdGenerator<? extends Number> inner) {
		this.inner = inner;
		this.prefix = SH.noNull(prefix);
		this.length = length - prefix.length();
	}

	@Override
	public String createNextId() {
		return toTicket(inner.createNextId());
	}

	@Override
	public void createNextIds(int count, Collection<? super String> sink) {
		List<Number> ids = new ArrayList<Number>(count);
		inner.createNextIds(count, ids);
		for (Number v : ids)
			sink.add(toTicket(v));
	}

	private String toTicket(Number l) {
		StringBuilder sb = new StringBuilder(length).append(prefix);
		SH.rightAlign('0', Long.toString(l.longValue(), 30).toUpperCase(), length, true, sb).toString();
		removeVowels(sb, 0, sb.length());
		return sb.toString();
	}

	public static void removeVowels(StringBuilder sb, int start, int end) {
		for (int i = start; i < end; i++)
			sb.setCharAt(i, removeVowel(sb.charAt(i)));
	}

	private static char[] LOWER = ("bcdfghjklmnpqrstvwxz" + (char) ('z' + 1) + (char) ('z' + 2) + (char) ('z' + 3) + (char) ('z' + 4) + (char) ('z' + 5) + (char) ('z' + 6))
			.toCharArray();
	private static char[] UPPER = ("BCDFGHJKLMNPQRSTVWXZ" + (char) ('Z' + 1) + (char) ('Z' + 2) + (char) ('Z' + 3) + (char) ('Z' + 4) + (char) ('Z' + 5) + (char) ('Z' + 6))
			.toCharArray();

	public static char removeVowel(char c) {
		if (OH.isBetween(c, 'A', 'Z'))
			return UPPER[c - 'A'];
		else if (OH.isBetween(c, 'a', 'z'))
			return LOWER[c - 'a'];
		else
			return c;
	}

	public static class Factory implements com.f1.base.Factory<String, BasicStringIdGenerator> {
		private final int length;
		private final String prefix;
		private final com.f1.base.Factory<String, IdGenerator<Long>> inner;

		public Factory(com.f1.base.Factory<String, IdGenerator<Long>> inner, int length, String prefix) {
			this.inner = inner;
			this.length = length;
			this.prefix = prefix;
		}

		@Override
		public BasicStringIdGenerator get(String key) {
			return new BasicStringIdGenerator(prefix, length, this.inner.get(key));
		}

	}
}
