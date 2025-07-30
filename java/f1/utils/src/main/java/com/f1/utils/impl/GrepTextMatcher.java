package com.f1.utils.impl;

import java.util.Arrays;

import com.f1.utils.AH;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.TextMatcher;

public class GrepTextMatcher implements TextMatcher {

	final private Part parts;//points to the first 'part' of the expression
	final private int ignoreLast;
	final private String text;

	private GrepTextMatcher(Part parts, int ignoreLast, String text) {
		this.parts = parts;
		this.ignoreLast = ignoreLast;
		this.text = text;
		//		this(text, true, false, forceCaseSensetive);
	}

	public static TextMatcher valueOf(String text, boolean forceCaseSensetive) {
		return valueOf(text, true, false, forceCaseSensetive);

	}
	public static TextMatcher valueOf(String text, boolean mustStartEnd, boolean periodIsWild, boolean forceCaseSensetive) {
		//		this.text = text;
		if (text.length() == 0) {
			//			ignoreLast = 0;
			//			parts = SkipPart.EMPTY;
			return SimpleTextMatcher.EMPTY;
		}
		boolean caseSensitive;
		char chars[];
		if (text.charAt(0) == '~') {
			if (text.length() == 1) {
				//				ignoreLast = 0;
				//				parts = SkipPart.EMPTY;
				return SimpleTextMatcher.EMPTY;
			}
			caseSensitive = true;
			chars = new char[text.length() - 1];
			text.getChars(1, text.length(), chars, 0);
		} else {
			chars = text.toCharArray();
			caseSensitive = forceCaseSensetive;
		}
		int ignoreLast = 0;
		Part parts = null;
		boolean mustStart = false;
		boolean mustEnd = false;
		int end = chars.length - 1;
		int i = 0;
		if (end > 0) {//is there more then one char?
			if (chars[i] == '^') {//is this a 'hard beginning', a.k.a. starts w/ hat(^)
				i++;
				if (chars[i] == '*')
					i++;
				else
					mustStart = true;
			} else if (mustStartEnd) {
				if (chars[i] == '*')
					i++;
				else
					mustStart = true;
			}
			if (chars[end] == '$' && !isEscape(chars, end - 1)) {
				mustEnd = true;
				end--;
			} else if (mustStartEnd) {
				if (chars[end] == '*' && !isEscape(chars, end - 1))
					end--;
				else
					mustEnd = true;
			}
			//work backwards looking for consecutive dots(.) which can be ignored except if the expression has a 'hard stop', in which case the dots simply dictate length.
			//Also, if a star(*) is encountered then it's no longer a hard-stop, and once again dots(.) can be ignored
			//Note, ignoring is effectively 'pulling' the end toward the beginning
			ignoreLast = 0;
			w: while (end >= 0) {
				switch (chars[end]) {
					case '.':
						if (periodIsWild) {
							if (isEscape(chars, end - 1))
								break w;
							ignoreLast++;
							end--;
							continue;
						} else {
							break w;
						}
					case '*':
						if (isEscape(chars, end - 1))
							break w;
						mustEnd = false;
						end--;
						continue;
					default:
						break w;
				}
			}
		} else {//single char, so the cases are self-evident
			switch (chars[0]) {
				case '^'://all text has a start
				case '$'://all text has a finish
					if (mustStartEnd) {
						//						parts = new StringPart(chars, 0, 0, caseSensitive, true, true);
						//						ignoreLast = 0;
						return SimpleTextMatcher.EMPTY;
					} else {
						//						parts = null;
						//						ignoreLast = 0;
						//						return;
						return ConstTextMatcher.TRUE;
					}
				case '*'://all text has zero or more chars
					//					parts = null;
					//					ignoreLast = 0;
					//					return;
					return ConstTextMatcher.TRUE;
				case '.':// any single character.
					if (periodIsWild)
						parts = new SkipPart(1, true);
					else
						parts = new StringPart(chars, 0, 1, caseSensitive, mustStart, mustEnd);
					ignoreLast = 0;
					return new GrepTextMatcher(parts, ignoreLast, text);
				default://trivial case where we are looking for a single char
					mustEnd = mustStart = mustStartEnd;
					parts = new StringPart(chars, 0, 1, caseSensitive, mustStart, mustEnd);
					ignoreLast = 0;
					return new GrepTextMatcher(parts, ignoreLast, text);
			}
		}
		Part last = null;// the last part built.
		int start = i;
		int dotsCount = 0;
		//traverse through all the chars from start to finish
		while (i <= end) {
			boolean hasStar = false, hasBracket = false;
			//are we at a 'special char?'
			switch (chars[i]) {
				case '*':
					hasStar = true;
					break;
				case '.':
					if (periodIsWild) {
						dotsCount = 1;
						break;
					} else {
						i++;
						continue;
					}
				case '[':
					hasBracket = true;
					break;
				case '\\'://handle escaped char. Basically, skip next char and continue evaluation
					i += 2;
					continue;
				default://boring char.. continue evaluation
					i++;
					continue;
			}
			int next = i + 1;
			//If a star(*) or dot(.) is found then conflate any following dots and stars.
			if (!hasBracket) {
				loop: for (int j = i + 1; j <= end; j++) {
					switch (chars[j]) {
						case '.':
							if (periodIsWild) {
								dotsCount++;
								next++;
								continue;
							} else
								break loop;
						case '*':
							hasStar = true;
							next++;
							continue;
						case '[':
							hasBracket = true;
							next++;
							break loop;
						default:
							break loop;
					}
				}
			}
			//If the position progressed then build a simple string part capturing that chunk of text.
			if (i > start) {
				Part p = new StringPart(chars, start, i, caseSensitive, mustStart, mustEnd && i == end);
				if (last == null)
					last = parts = p;//first part
				else
					last.setNext(last = p);//append to parts
				mustStart = !hasStar;
			}
			//there were dots(.) found and we are not at the end, so create a skip part.
			if (dotsCount > 0 && next <= end) {
				Part p = new SkipPart(dotsCount, mustStart);
				mustStart = !hasStar;
				dotsCount = 0;
				if (last == null)
					last = parts = p;
				else
					last.setNext(last = p);
			}
			i = start = next;
			//we are at a bracket([)
			if (hasBracket) {
				int j = i;
				w: while (j < end) {
					switch (chars[j]) {
						case '\\':
							j += 2;
							continue;
						case ']':
							break w;
						default:
							j++;
					}
				}
				if (chars[j] != ']')//that 
					throw new RuntimeException("missing: " + ']');

				Part p = new OrCharPart(chars, i, j, caseSensitive, mustStart, mustEnd && j == end);
				mustStart = !hasStar;
				if (last == null)
					last = parts = p;
				else
					last.setNext(last = p);
				i = start = j + 1;
			}
		}
		if (i > start) {// some simple text remains at the end, tack it on.
			Part p = new StringPart(chars, start, i, caseSensitive, mustStart, mustEnd);
			if (last == null)//this is the first part, so handle trivial case of just text
				last = parts = p;
			else
				last.setNext(last = p); //there were prior parts so append this remaining part to the end
		} else if (parts == null) {//there's no 'remaining' text, nor prior parts.D
			if (mustStart && mustEnd) {
				parts = SkipPart.EMPTY;//basically ^$
			} else {
				parts = Simple.TRUE; //Always True
			}
		}
		parts.calcRemainingLength();
		if (parts.next == null && parts instanceof StringPart) {
			StringPart p = (StringPart) parts;
			if (MH.allBits(p.flags, StringPart.MUST_END | StringPart.MUST_START))
				return new SimpleTextMatcher(p.chars, !MH.anyBits(p.flags, StringPart.CASE_SENSITIVE));
		}
		return new GrepTextMatcher(parts, ignoreLast, text);
	}

	//is the char at supplied index 'escaped' meaning its prefixed w/ a NON-ESCAPED backslash (\). 

	private static boolean isEscape(char[] chars, int i) {
		int cnt = 0;
		while (i >= 0 && chars[i] == '\\') {
			cnt++;
			i--;
		}
		return (cnt & 1) == 1;
	}

	@Override
	public boolean matches(String input) {
		if (input == null)
			return false;
		int length = input.length() - ignoreLast;
		int i = 0;
		int lastI = -1;
		Part lastPart = null;
		for (Part p = parts; p != null;) {
			if (length - i < p.getRemainingLength())
				return false;
			if (!p.getMustStart()) {
				lastI = i;
				lastPart = p;
			}
			if (p.getMustEnd() && p.getNext() != null) {
				i = p.consume(input, i, length - p.getNext().getRemainingLength());
			} else {
				i = p.consume(input, i, length);
			}
			if (i > length)
				return false;
			if (i == -1) {
				if (lastPart != null && lastPart != p) {
					p = lastPart;
					i = ++lastI;
				} else
					return false;
			} else
				p = p.getNext();
		}
		return true;// !mustEnd || i == length;
	}

	@Override
	public boolean matches(CharSequence input) {
		int length = input.length() - ignoreLast;
		int i = 0;
		int lastI = -1;
		Part lastPart = null;
		for (Part p = parts; p != null;) {
			if (length - i < p.getRemainingLength())
				return false;
			if (!p.getMustStart()) {
				lastI = i;
				lastPart = p;
			}
			if (p.getMustEnd() && p.getNext() != null) {
				i = p.consume(input, i, length - p.getNext().getRemainingLength());
			} else {
				i = p.consume(input, i, length);
			}
			if (i > length)
				return false;
			if (i == -1) {
				if (lastPart != null && lastPart != p) {
					p = lastPart;
					i = ++lastI;
				} else
					return false;
			} else
				p = p.getNext();
		}
		return true;// !mustEnd || i == length;
	}

	private static abstract class Part {
		static final byte CASE_SENSITIVE = 1, MUST_START = 2, MUST_END = 4;
		private Part next;
		private int remainingLength;
		protected byte flags;

		public int consume(String text, int start, int textLength) {
			return consume((CharSequence) text, start, textLength);
		}

		abstract public int consume(CharSequence text, int start, int textLength);

		public void setNext(Part part) {
			this.next = part;
		}

		public void setMustStart() {
			flags |= MUST_START;
		}

		public void setMustEnd() {
			flags |= MUST_END;
		}

		public Part getNext() {
			return next;
		}

		public boolean getMustEnd() {
			return (flags & MUST_END) != 0;
		}

		public boolean getMustStart() {
			return (flags & MUST_START) != 0;
		}

		abstract int getPartLength();
		int getRemainingLength() {
			return remainingLength;
		}

		private void calcRemainingLength() {
			if (next != null) {
				next.calcRemainingLength();
				this.remainingLength = getPartLength() + next.getRemainingLength();
				if (next.getMustEnd() && next.getMustStart())
					setMustEnd();
			} else
				this.remainingLength = getPartLength();
		}

		protected boolean equalsPart(Part other) {
			return OH.eq(next, other.next) && OH.eq(remainingLength, other.remainingLength) && OH.eq(flags, other.flags);
		}

	}

	/**
	 * 
	 * if mustStart=true: abc
	 * 
	 * 
	 * 
	 * if mustStart=false: *abc
	 * 
	 */

	private static class StringPart extends Part {
		final private String chars;
		final private int charsLength;

		private StringPart(String chars, boolean caseSensitive, boolean mustStart, boolean mustEnd) {
			this.chars = chars;
			this.charsLength = chars.length();
			this.flags = (byte) ((mustStart ? MUST_START : 0) | (mustEnd ? MUST_END : 0) | (caseSensitive ? CASE_SENSITIVE : 0));
		}

		public StringPart(char[] chars, int start, int end, boolean caseSensitive, boolean mustStart, boolean mustEnd) {
			this(new String(stripChars(chars, start, end)), caseSensitive, mustStart, mustEnd);
		}

		@Override
		public int consume(String text, int start, int textLength) {
			switch (flags) {
				case MUST_END | MUST_START | CASE_SENSITIVE: {
					return start + charsLength == textLength && (text.startsWith(chars, start)) ? textLength : -1;
				}
				case MUST_END | MUST_START: {
					return start + charsLength == textLength && SH.startsWithIgnoreCase(text, chars, start) ? textLength : -1;
				}
				case MUST_END | CASE_SENSITIVE: {
					int s = textLength - charsLength;
					if (s < start)
						return -1;
					int i = text.indexOf(chars, s);
					return i == -1 ? -1 : i + charsLength;
				}
				case MUST_END: {
					int s = textLength - charsLength;
					if (s < start)
						return -1;
					int i = SH.indexOfIgnoreCase(text, chars, s);
					return i == -1 ? -1 : i + charsLength;
				}
				case MUST_START | CASE_SENSITIVE:
					return (text.startsWith(chars, start)) ? start + charsLength : -1;
				case MUST_START:
					return SH.startsWithIgnoreCase(text, chars, start) ? start + charsLength : -1;
				case CASE_SENSITIVE: {
					int i = text.indexOf(chars, start);
					return i == -1 ? -1 : i + chars.length();
				}
				case 0: {
					int i = SH.indexOfIgnoreCase(text, chars, start);
					return i == -1 ? -1 : i + chars.length();
				}
				default:
					throw new RuntimeException("flag unknown: " + flags);
			}
		}

		@Override
		public int consume(CharSequence text, int start, int textLength) {
			switch (flags) {
				case MUST_END | MUST_START | CASE_SENSITIVE: {
					return start + charsLength == textLength && (SH.startsWith(text, chars, start)) ? textLength : -1;
				}
				case MUST_END | MUST_START: {
					return start + charsLength == textLength && SH.startsWithIgnoreCase(text, chars, start) ? textLength : -1;
				}
				case MUST_END | CASE_SENSITIVE: {
					int s = textLength - charsLength;
					if (s < start)
						return -1;
					int i = SH.indexOf(text, chars, s);
					return i == -1 ? -1 : i + charsLength;
				}
				case MUST_END: {
					int s = textLength - charsLength;
					if (s < start)
						return -1;
					int i = SH.indexOfIgnoreCase(text, chars, s);
					return i == -1 ? -1 : i + charsLength;
				}
				case MUST_START | CASE_SENSITIVE:
					return (SH.startsWith(text, chars, start)) ? start + charsLength : -1;
				case MUST_START:
					return SH.startsWithIgnoreCase(text, chars, start) ? start + charsLength : -1;
				case CASE_SENSITIVE: {
					int i = SH.indexOf(text, chars, start);
					return i == -1 ? -1 : i + chars.length();
				}
				case 0: {
					int i = SH.indexOfIgnoreCase(text, chars, start);
					return i == -1 ? -1 : i + chars.length();
				}
				default:
					throw new RuntimeException("flag unknown: " + flags);
			}
		}

		@Override
		int getPartLength() {
			return charsLength;
		}
		@Override
		public boolean equals(Object obj) {
			if (obj == null || obj.getClass() != StringPart.class)
				return false;
			StringPart other = (StringPart) obj;
			return equalsPart(other) && OH.eq(chars, other.chars);
		}

	}

	public static class Simple extends Part {
		public final static Simple TRUE = new Simple();

		@Override
		public int consume(CharSequence text, int start, int textLength) {
			return start;
		}

		@Override
		int getPartLength() {
			return 0;
		}
		@Override
		public boolean equals(Object obj) {
			if (obj == null || obj.getClass() != Simple.class)
				return false;
			return equalsPart((Part) obj);
		}

	}

	/**
	 * 
	 * [abc]
	 * 
	 */
	private static class OrCharPart extends Part {
		final private char[] chars;

		public OrCharPart(char[] chars, int start, int end, boolean caseSensitive, boolean mustStart, boolean mustEnd) {
			chars = stripChars(chars, start, end);
			this.chars = caseSensitive ? chars : toArray(chars, 0, chars.length, caseSensitive);
			Arrays.sort(this.chars);
			this.flags = (byte) ((mustStart ? MUST_START : 0) | (mustEnd ? MUST_END : 0) | (caseSensitive ? CASE_SENSITIVE : 0));
		}

		@Override
		public int consume(CharSequence text, int start, int textLength) {
			switch (flags & (MUST_START | MUST_END)) {
				case MUST_START | MUST_END:
					return (start + 1 == textLength && in(text.charAt(start))) ? textLength : -1;
				case MUST_END:
					return (in(text.charAt(textLength - 1))) ? textLength : -1;
				case MUST_START:
					return (in(text.charAt(start++))) ? start : -1;
			}

			while (start < textLength)
				if (in(text.charAt(start++)))
					return start;
			return -1;

		}

		private final boolean in(char c1) {
			for (int i = 0; i < chars.length; i++) {
				int c = c1 - chars[i];
				if (c == 0)
					return true;
				else if (c < 0)
					break;
			}
			return false;

		}

		@Override
		int getPartLength() {
			return 1;
		}
		@Override
		public boolean equals(Object obj) {
			if (obj == null || obj.getClass() != OrCharPart.class)
				return false;
			OrCharPart other = (OrCharPart) obj;
			return equalsPart(other) && AH.eq(chars, other.chars);
		}

	}

	public static class SkipPart extends Part {
		public static final Part EMPTY = new SkipPart(0, true, true);
		private final int count;

		public SkipPart(int count, boolean mustStart) {
			this.count = count;
			if (mustStart)
				setMustStart();
		}

		public SkipPart(int count, boolean mustStart, boolean mustEnd) {
			this.count = count;
			if (mustStart)
				setMustStart();
			if (mustEnd)
				setMustEnd();
		}

		@Override

		public int consume(CharSequence text, int start, int textLength) {
			int r = start + count;
			if (MH.areAllBitsSet(flags, MUST_START | MUST_END)) {
				return r == textLength ? r : -1;
			}
			return (r > textLength) ? -1 : r;

		}

		@Override

		int getPartLength() {

			return count;

		}
		@Override
		public boolean equals(Object obj) {
			if (obj == null || obj.getClass() != OrCharPart.class)
				return false;
			SkipPart other = (SkipPart) obj;
			return equalsPart(other) && OH.eq(count, other.count);
		}

	}

	// If case sensitive is false then it will convert [abc] to [aAbBcC]

	private static char[] toArray(char[] chars, int start, int end, boolean caseSensitive) {
		if (caseSensitive) {
			if (start == 0 && end == chars.length)
				return chars;
			return Arrays.copyOfRange(chars, start, end);
		}
		int i = chars.length;
		for (int j = start; j < end; j++) {
			final char c = chars[j];
			if (Character.toUpperCase(c) != Character.toLowerCase(c))
				i++;
		}
		final char[] r = new char[i];
		i = 0;
		for (int j = start; j < end; j++) {
			final char c = chars[j];
			final char c1 = Character.toUpperCase(c), c2 = Character.toLowerCase(c);
			r[i++] = c1;
			if (c1 != c2)
				r[i++] = c2;
		}
		return r;
	}

	private static char[] stripChars(char[] chars, int start, int end) {
		int size = 0;
		for (int i = start; i < end; i++) {
			if (chars[i] == '\\')
				i++;
			size++;
		}
		if (size == chars.length)
			return chars;
		final char[] c = new char[size];
		for (int i = start, j = 0; i < end; i++) {
			final char c2 = chars[i];
			c[j++] = c2 == '\\' ? SH.toSpecialIfSpecial(chars[++i]) : c2;
		}
		return c;
	}

	@Override
	public String toString() {
		return text;
	}

	@Override
	public StringBuilder toString(StringBuilder sink) {
		return sink.append(text);
	}
	@Override
	public boolean equals(Object obj) {
		if (obj == null || obj.getClass() != GrepTextMatcher.class)
			return false;
		GrepTextMatcher other = (GrepTextMatcher) obj;
		return OH.eq(ignoreLast, other.ignoreLast) && OH.eq(text, other.text) && OH.eq(parts, other.parts);
	}

}
