package com.f1.utils.impl;

import java.util.BitSet;

import com.f1.base.ToStringable;
import com.f1.utils.CharReader;

public class BasicCharMatcher implements CharMatcher, ToStringable {

	private final boolean includeEof;
	private final BitSet bits = new BitSet();

	public BasicCharMatcher(CharSequence chars, boolean includeEOF) {
		this.includeEof = includeEOF;
		final int len = chars.length();
		for (int i = 0; i < len;) {
			char c = chars.charAt(i++);
			if (c == '\\') {
				if (i < len)
					c = chars.charAt(i++);
				else
					throw new RuntimeException("Trailing escape: " + chars);
			}
			if (i < len && chars.charAt(i) == '-') {
				if (++i < len) {
					char d = chars.charAt(i++);
					if (d == '\\') {
						if (i < len)
							d = chars.charAt(i++);
						else
							throw new RuntimeException("Trailing escape: " + chars);
					}
					if (c < d)
						while (c <= d)
							set(c++);
					else
						while (d <= c)
							set(d++);
				} else
					throw new RuntimeException("Trailing escape: " + chars);
			} else
				set(c);
		}
	}

	private void set(int c) {
		if (bits.get(c))
			throw new RuntimeException("duplicate char: " + (char) c);
		bits.set(c);
	}

	@Override
	public boolean matches(int c) {
		return c == CharReader.EOF ? includeEof : bits.get(c);
	}

	public static void main(String a[]) {
		BasicCharMatcher t = new BasicCharMatcher("a-eA-E\\-\\\\", false);
		System.out.println(t);
		System.out.println(t.matches('e'));
		System.out.println(t.matches('f'));
		System.out.println(t.matches('A'));
		System.out.println(t.matches('E'));
		System.out.println(t.matches('F'));
		System.out.println(t.matches('-'));
		System.out.println(t.matches('\\'));
	}

	public String toString() {
		return toString(new StringBuilder()).toString();
	}

	@Override
	public StringBuilder toString(StringBuilder sink) {
		for (int i = this.bits.nextSetBit(0); i != -1; i = this.bits.nextSetBit(i + 1)) {
			sink.append((char) i);
		}
		return sink;
	}

}
