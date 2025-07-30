package com.f1.utils.impl;

import com.f1.utils.AH;
import com.f1.utils.CharArray;
import com.f1.utils.CharReader;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.structs.Tuple2;

public class StringCharReader implements CharReader, CharSequence {
	public static final CharMatcher ALPHA = new BasicCharMatcher("A-Za-z", false);
	public static final CharMatcher NEWLINE = new BasicCharMatcher("\n\r", false);
	public static final CharMatcher ALPHA_NUM = new BasicCharMatcher("A-Za-z0-9", false);
	public static final CharMatcher ALPHA_NUM_UNDERBAR = new BasicCharMatcher("A-Za-z0-9_", false);
	public static final CharMatcher WHITE_SPACE = new BasicCharMatcher("\n\r \t", false);
	public static final CharMatcher WHITE_SPACE_COMMA = new BasicCharMatcher("\n\r \t,", false);

	private char[] inner;
	int location;
	int markLocation;

	private boolean caseInsensitive = false;
	private boolean toStringIncludesLocation;
	private String origText;
	private boolean strictEscape = false;

	public StringCharReader(CharSequence inner) {
		reset(SH.toCharArray(inner));
	}
	public StringCharReader(CharSequence inner, boolean caseInsensitive) {
		reset(SH.toCharArray(inner));
		setCaseInsensitive(caseInsensitive);
	}
	public StringCharReader(CharSequence inner, int start, int length) {
		reset(SH.toCharArray(inner, start, length));
	}

	public StringCharReader(final char[] inner) {
		reset(inner);
	}

	public StringCharReader() {
		this(OH.EMPTY_CHAR_ARRAY);
	}

	@Override
	public char readChar() {
		return inner[location++];
	}

	@Override
	public int readCharOrEof() {
		if (location == inner.length)
			return EOF;
		return inner[location++];
	}

	@Override
	public int readChars(final char[] out) {
		return readChars(out, 0, out.length);
	}

	@Override
	public int readChars(final char[] out, final int offset, int length) {
		if (length + location > inner.length)
			length = inner.length - location;
		System.arraycopy(inner, location, out, offset, length);
		location += length;
		return length;
	}

	@Override
	public char peak() {
		return inner[location];
	}

	@Override
	public int peakOrEof() {
		if (location == inner.length)
			return EOF;
		return inner[location];
	}

	@Override
	public int expectAny(final int c[]) {
		int r = peakOrEof();
		for (int c2 : c)
			if (eq(r, c2)) {
				if (r != EOF)
					location++;
				return r;
			}
		throw newExpressionParserException("Expecting " + toString(c) + " but found: " + peak());
	}

	private String toString(int[] chars) {
		StringBuilder sb = new StringBuilder("[");
		boolean first = true;
		for (int c : chars) {
			if (first)
				first = false;
			else
				sb.append(',');
			if (c == EOF)
				sb.append("EOF");
			else
				sb.append((char) c);
		}
		sb.append(']');
		return sb.toString();
	}

	@Override
	public void expectSequence(final char[] c) {
		for (int i = 0; i < c.length; i++)
			if (!eq(inner[location + i], c[i]))
				throw newExpressionParserException("Expecting '" + new String(c) + "' but found: " + new String(inner, location, i + 1) + "...");
		location += c.length;
	}
	@Override
	public boolean expectSequenceNoThrow(final char[] c) {
		for (int i = 0; i < c.length; i++)
			if (!eq(inner[location + i], c[i]))
				return false;
		location += c.length;
		return true;
	}

	@Override
	public int getCountRead() {
		return location;
	}
	public void setCountRead(int location) {
		OH.assertBetween(location, 0, inner.length);
		this.location = location;
	}

	@Override
	public int readUntilAny(int expecting[], StringBuilder sink) {
		int count = 0;
		int r;
		for (;;) {
			int i = location + count;
			if (i == inner.length && (r = AH.indexOf(EOF, expecting)) != -1)
				break;
			if ((r = indexOf(inner[i], expecting)) != -1)
				break;
			count++;
		}
		if (sink != null)
			sink.append(inner, location, count);
		location += count;
		return expecting[r];
	}
	@Override
	public int readWhileAny(int expecting[], StringBuilder sink) {
		int count = 0;
		int r;
		for (;;) {
			int i = location + count;
			if (i == inner.length && (r = AH.indexOf(EOF, expecting)) == -1)
				break;
			if ((r = indexOf(inner[i], expecting)) == -1)
				break;
			count++;
		}
		if (sink != null)
			sink.append(inner, location, count);
		location += count;
		return count;
	}

	@Override
	public int readWhileAny(CharMatcher expecting, StringBuilder sink) {
		int count = 0;
		int r;
		for (;;) {
			int i = location + count;
			if (i == inner.length || !get(expecting, inner[i]))
				break;
			count++;
		}
		if (sink != null)
			sink.append(inner, location, count);
		location += count;
		return count;
	}
	@Override
	public int readWhileAny(CharSequence expecting, StringBuilder sink) {
		int count = 0;
		int r;
		for (;;) {
			int i = location + count;
			if (i == inner.length || indexOf(inner[i], expecting) != -1)
				break;
			count++;
		}
		if (sink != null)
			sink.append(inner, location, count);
		location += count;
		return count;
	}

	@Override
	public int readUntilAny(int expecting[], char escape, StringBuilder sink) {
		int count = 0;
		int r;
		for (;;) {
			int i = location + count;
			if (i == inner.length && (r = AH.indexOf(EOF, expecting)) != -1)
				break;
			char c = inner[i];
			if (c == escape)
				count++;
			else if ((r = indexOf(c, expecting)) != -1)
				break;
			count++;
		}
		if (sink != null)
			sink.append(inner, location, count);
		location += count;
		return expecting[r];
	}

	@Override
	public int readUntilAny(CharMatcher expecting, boolean includeEOF, char escape, StringBuilder sink) {
		int count = 0;
		int r;
		for (;;) {
			int i = location + count;
			if (i == inner.length && includeEOF) {
				if (sink != null)
					sink.append(inner, location, count);
				location += count;
				return EOF;
			}
			char c = inner[i];
			if (c == escape)
				count++;
			else if (get(expecting, c)) {
				if (sink != null)
					sink.append(inner, location, count);
				location += count;
				return c;
			}
			count++;
		}
	}
	@Override
	public int readUntilAnySkipEscaped(CharMatcher expecting, char escape, StringBuilder sink) {
		OH.assertEq(escape, '\\');
		int sinkSize = sink == null ? -1 : sink.length();
		int i = location, start = location;
		for (;; i++) {
			if (i == inner.length) {
				if (expecting.matches(EOF))
					break;
				if (sink != null)
					sink.setLength(sinkSize);
				location = i;
				throw newExpressionParserException("EOF");
			}
			final char c = inner[i];
			if (get(expecting, c))
				break;
			if (c == escape) {
				if (sink != null)
					sink.append(inner, start, i - start);
				char a = inner[++i];
				if (get(expecting, a)) {
					sink.append(a);
				} else if (a == SH.CHAR_UNICODE) {
					sink.append((char) SH.parseInt(this, i + 1, i + 5, 16));
					i += 4;
				} else if (a == c) {
					sink.append(a);
				} else {
					char a2 = SH.toSpecial(a);
					if (a2 == SH.CHAR_NOT_SPECIAL) {
						if (strictEscape) {
							location = i;
							throw newExpressionParserException("Invalid escaped char");
						}
						sink.append(a);
					} else
						sink.append(a2);
				}
				start = i + 1;
			}
		}
		if (sink != null)
			sink.append(inner, start, i - start);
		final int diff = i - location;
		location = i;
		return diff;
	}

	@Override
	public int readUntil(int c, StringBuilder sink) {
		if (c == EOF) {
			int r = inner.length - location;
			if (sink != null)
				sink.append(inner, location, r);
			location = inner.length;
			return r;
		}
		int count = 0, remaining = inner.length - location;
		while (count < remaining) {
			if (eq(inner[location + count], c))
				break;
			count++;
		}
		if (sink != null)
			sink.append(inner, location, count);
		location += count;
		return count;
	}

	@Override
	public int readUntil(int c, char escape, StringBuilder sink) {
		if (c == EOF) {
			int r = inner.length - location;
			if (sink != null)
				sink.append(inner, location, r);
			location = inner.length;
			return r;
		}
		int count = 0, remaining = inner.length - location;
		while (count < remaining) {
			char c2 = inner[location + count];
			if (eq(c2, c))
				break;
			else if (c2 == escape) {
				count += 2;
				if (count > remaining)
					count--;
			} else
				count++;
		}
		if (sink != null)
			sink.append(inner, location, count);
		location += count;
		return count;
	}

	@Override
	public int readUntilSkipEscaped(int c, char escape, StringBuilder sink) {
		if (sink == null)
			return readUntilSkipEscaped(c, escape);
		OH.assertEq(escape, '\\');
		if (c == EOF) {
			while (location < inner.length) {
				char c2 = inner[location++];
				if (c2 != '\\')
					sink.append(c2);
			}
		} else {
			while (location < inner.length) {
				char c2 = inner[location];
				if (eq(c2, c))
					return c2;
				else if (c2 == escape) {
					char a = inner[++location];
					if (a == SH.CHAR_UNICODE) {
						sink.append((char) SH.parseInt(this, ++location, location += 4, 16));
					} else if (a == c) {
						sink.append(a);
						location++;
					} else {
						char a2 = SH.toSpecial(a);
						if (a2 == SH.CHAR_NOT_SPECIAL) {
							if (strictEscape)
								throw newExpressionParserException("Invalid escaped char");
							sink.append(a);
						} else
							sink.append(a2);
						location++;
					}
				} else {
					sink.append(c2);
					location++;
				}
			}
		}
		return EOF;
	}
	public int readUntilSkipEscaped(int c, char escape) {
		OH.assertEq(escape, '\\');
		if (c == EOF) {
			location = inner.length;
		} else {
			while (location < inner.length) {
				char c2 = inner[location];
				if (eq(c2, c))
					return c2;
				else if (c2 == escape) {
					char a = inner[++location];
					if (a == SH.CHAR_UNICODE) {
						location += 4;
					} else if (a == c) {
						location++;
					} else {
						char a2 = SH.toSpecial(a);
						if (a2 == SH.CHAR_NOT_SPECIAL) {
							if (strictEscape)
								throw newExpressionParserException("Invalid escaped char");
						}
						location++;
					}
				} else {
					location++;
				}
			}
		}
		return EOF;
	}

	@Override
	public char expect(int c) {
		if (location >= inner.length)
			throw newExpressionParserException("Expecting " + (char) c + " but found End-of-Line");
		if (eq(inner[location], c)) {
			return inner[location++];
		} else
			throw newExpressionParserException("Expecting " + (char) c + " but found: " + peak());
	}

	@Override
	public boolean expectNoThrow(int c) {
		if (location < inner.length && eq(inner[location], c)) {
			location++;
			return true;
		} else
			return false;
	}

	@Override
	public int skip(char c) {
		int start = location;
		while (start < inner.length && eq(inner[start], c))
			start++;
		return location = start;
	}

	@Override
	public int skipAny(int[] chars) {
		int start = location;
		while (start < inner.length && indexOf(inner[start], chars) != -1)
			start++;
		return location = start;
	}

	@Override
	public int skip(CharMatcher chars) {
		int start = location;
		while (location < inner.length && get(chars, inner[location]))
			location++;
		return location - start;
	}

	@Override
	public void pushBack(char c) {
		inner[--location] = c;
	}

	public String toStringWithLocation() {
		StringBuilder sb = new StringBuilder(inner.length * 2 + 10);
		String s = new String(inner);
		Tuple2<Integer, Integer> lp = SH.getLinePosition(s, this.location);
		String[] lines = SH.splitLines(s);
		for (int i = 0; i < lines.length; i++) {
			sb.append(lines[i]).append(SH.NEWLINE);
			if (lp.getA() == i)
				SH.repeat(' ', lp.getB(), sb).append("^").append(SH.NEWLINE);
		}
		return sb.toString();
	}
	public String toStringWithoutLocation() {
		return this.origText;
	}
	@Override
	public String toString() {
		if (toStringIncludesLocation)
			return toStringWithLocation();
		else
			return toStringWithoutLocation();
	}

	@Override
	public boolean peakSequence(char[] string) {
		int start = location;
		if (start + string.length > inner.length)
			return false;
		for (int i = 0; i < string.length; i++, start++)
			if (!eq(string[i], inner[start]))
				return false;
		return true;
	}

	@Override
	public boolean peakSequence(CharSequence string) {
		int start = location;
		int len = string.length();
		if (start + len > inner.length)
			return false;
		for (int i = 0; i < len; i++, start++)
			if (!eq(string.charAt(i), inner[start]))
				return false;
		return true;
	}
	@Override
	public boolean read(CharSequence string, CharMatcher followedBy) {
		int start = location;
		int len = string.length();
		if (start + len > inner.length)
			return false;
		for (int i = 0; i < len; i++, start++)
			if (!eq(string.charAt(i), inner[start]))
				return false;
		if (start == inner.length) {
			if (!followedBy.matches(EOF))
				return false;
		} else if (!followedBy.matches(inner[start]))
			return false;
		location += len;
		return true;
	}
	@Override
	public boolean read(char[] string, CharMatcher followedBy) {
		int start = location;
		int len = string.length;
		if (start + len > inner.length)
			return false;
		for (int i = 0; i < len; i++, start++)
			if (!eq(string[i], inner[start]))
				return false;
		if (start == inner.length) {
			if (!followedBy.matches(EOF))
				return false;
		} else if (!followedBy.matches(inner[start]))
			return false;
		location += len;
		return true;
	}

	public void reset(char[] inner) {
		this.inner = inner;
		this.origText = new String(inner);
		this.location = 0;
		this.markLocation = 0;
	}
	public void reset(CharSequence inner) {
		this.inner = SH.toCharArray(inner);
		this.origText = inner.toString();
		this.location = 0;
		this.markLocation = 0;
	}

	@Override
	public void mark() {
		this.markLocation = location;
	}

	@Override
	public void returnToMark() {

		this.location = markLocation;
	}

	@Override
	public int readUntilAny(CharMatcher expecting, boolean includeEOF, StringBuilder sink) {
		int count = 0;
		int r;
		for (;;) {
			int i = location + count;
			if (i > inner.length)
				throw new RuntimeException();
			if (i == inner.length && includeEOF) {
				if (sink != null)
					sink.append(inner, location, count);
				location += count;
				return EOF;
			}
			char c = inner[i];
			if (get(expecting, c)) {
				if (sink != null)
					sink.append(inner, location, count);
				location += count;
				return c;
			}
			count++;
		}
	}
	@Override
	public int readUntilAny(CharSequence chars, boolean includeEOF, StringBuilder sink) {
		int count = 0;
		int r;
		for (;;) {
			int i = location + count;
			if (i == inner.length && includeEOF) {
				if (sink != null)
					sink.append(inner, location, count);
				location += count;
				return EOF;
			}
			char c = inner[i];
			if (indexOf(c, chars) != -1) {
				if (sink != null)
					sink.append(inner, location, count);
				location += count;
				return c;
			}
			count++;
		}
	}

	public char[] getInner(int start, int end) {
		char[] r = new char[end - start];
		System.arraycopy(inner, start, r, 0, end - start);
		return r;
	}

	@Override
	public int peak(char[] sink_) {
		int i = 0;
		for (int j = location; i < sink_.length && location < inner.length; i++, j++)
			sink_[i] = inner[j];
		return i;
	}

	@Override
	public int readChars(int i, StringBuilder sb) {
		int r = inner.length - location;
		if (r > i)
			r = i;
		sb.append(inner, location, r);
		location += r;
		return r;
	}

	@Override
	public int skipChars(int i) {
		int r = inner.length - location;
		if (r > i)
			r = i;
		location += r;
		return r;
	}

	@Override
	public void reset(String string) {
		reset(string.toCharArray());
	}

	@Override
	public void expectSequence(CharSequence text) {
		int len = text.length();
		for (int i = 0; i < len; i++)
			if (!eq(inner[location + i], text.charAt(i)))
				throw newExpressionParserException("Expecting '" + text + "' but found: " + new String(inner, location, i + 1) + "...");
		location += len;
	}

	@Override
	public boolean expectSequenceNoThrow(String text) {
		int len = text.length();
		if (location + len > this.inner.length)
			return false;
		for (int i = 0; i < len; i++)
			if (!eq(inner[location + i], text.charAt(i)))
				return false;
		location += len;
		return true;
	}

	@Override
	public ExpressionParserException newExpressionParserException(String text) {
		return new ExpressionParserException(location, text);
	}
	@Override
	public ExpressionParserException newExpressionParserException(String text, Exception e) {
		return new ExpressionParserException(location, text, e);
	}
	@Override
	public int readUntilSequence(char[] sequence, StringBuilder sink) {
		if (sequence.length == 0)
			return 0;
		final int start = location;
		char first = sequence[0];

		int loc = location, last = inner.length - sequence.length + 1;
		outer: while (loc < last) {
			if (eq(inner[loc], first)) {
				for (int i = 1; i < sequence.length; i++)
					if (!eq(inner[loc + i], sequence[i])) {
						loc++;
						continue outer;
					}
				int r = loc - location;
				if (sink != null)
					sink.append(inner, location, r);
				location = loc;
				return r;
			} else
				loc++;
		}
		location = start;
		return -1;
	}

	@Override
	public int readUntilSequence(CharSequence sequence, StringBuilder sink) {
		int len = sequence.length();
		if (len == 0)
			return 0;
		final int start = location;
		char first = sequence.charAt(0);

		int loc = location, last = inner.length - len + 1;
		outer: while (loc < last) {
			if (eq(inner[loc], first)) {
				for (int i = 1; i < len; i++)
					if (!eq(inner[loc + i], sequence.charAt(i))) {
						loc++;
						continue outer;
					}
				int r = loc - location;
				if (sink != null)
					sink.append(inner, location, r);
				location = loc;
				return r;
			} else
				loc++;
		}
		location = start;
		return -1;
	}

	/**
	 * Reads until a sequence of string is found:<br>
	 * 1. If string is not found, returns the current cursor location. Cursor is not advanced. <br>
	 * 2. If string is found, returns the new cursor location. Cursor is advanced. <br>
	 * 
	 * @param sequence
	 *            A sequence of string to be read
	 * 
	 * @param sink
	 * 
	 */
	@Override
	public int readUntilSequenceAndSkip(String sequence, StringBuilder sink) {
		int len = sequence.length();
		if (len == 0)
			return 0;
		final int start = location;
		char first = sequence.charAt(0);

		int loc = location, last = inner.length - len + 1;
		outer: while (loc < last) {
			if (eq(inner[loc], first)) {
				for (int i = 1; i < len; i++)
					if (!eq(inner[loc + i], sequence.charAt(i))) {
						loc++;
						continue outer;
					}
				int r = loc - location;
				if (sink != null)
					sink.append(inner, location, r);
				location = loc + len;
				return r;
			} else
				loc++;
		}
		location = start;
		return -1;
	}
	@Override
	public int readUntilSequenceAndSkip(char[] sequence, StringBuilder sink) {
		int len = sequence.length;
		if (len == 0)
			return 0;
		final int start = location;
		char first = sequence[0];

		int loc = location, last = inner.length - len + 1;
		outer: while (loc < last) {
			if (eq(inner[loc], first)) {
				for (int i = 1; i < len; i++)
					if (!eq(inner[loc + i], sequence[i])) {
						loc++;
						continue outer;
					}
				int r = loc - location;
				if (sink != null)
					sink.append(inner, location, r);
				location = loc + len;
				return r;
			} else
				loc++;
		}
		location = start;
		return -1;
	}

	static public int[] toInts(String text) {
		final int len = text.length();
		final int[] r = new int[len];
		for (int i = 0; i < len; i++)
			r[i] = text.charAt(i);
		return r;
	}
	static public int[] toIntsAndEof(String text) {
		final int len = text.length();
		final int[] r = new int[len + 1];
		for (int i = 0; i < len; i++)
			r[i] = text.charAt(i);
		r[len] = EOF;
		return r;
	}
	@Override
	public boolean isEof() {
		return location == inner.length;
	}
	public int getAvailable() {
		return inner.length - location;
	}
	public String getAsText() {
		return new String(this.inner);
	}
	@Override
	public int length() {
		return this.inner.length;
	}
	@Override
	public char charAt(int index) {
		return this.inner[index];
	}
	@Override
	public CharSequence subSequence(int start, int end) {
		if (start == 0 && end == this.inner.length)
			return this;
		return new CharArray(this.inner, start, end);
	}
	@Override
	public boolean getCaseInsensitive() {
		return caseInsensitive;
	}

	@Override
	public void setCaseInsensitive(boolean ignoreCase) {
		this.caseInsensitive = ignoreCase;
	}

	//equality helpers based on case
	private boolean eq(char a, char b) {
		return a == b || (caseInsensitive && (a = Character.toUpperCase(a)) == b || a == Character.toUpperCase(b));
	}
	//equality helpers based on case
	private int indexOf(int c, int a[]) {
		if (caseInsensitive) {
			for (int i = 0; i < a.length; i++)
				if (eq(a[i], c))
					return i;
			return -1;
		} else
			return AH.indexOf(c, a);
	}
	private int indexOf(int c, CharSequence a) {
		for (int i = 0, l = a.length(); i < l; i++)
			if (eq(a.charAt(i), c))
				return i;
		return -1;
	}
	//equality helpers based on case
	private boolean get(CharMatcher expecting, char c) {
		return expecting.matches(c) || (caseInsensitive && expecting.matches(Character.isUpperCase(c) ? Character.toLowerCase(c) : Character.toUpperCase(c)));
	}
	//equality helpers based on case
	private boolean eq(int a, int b) {
		return a == b || (caseInsensitive && (a = Character.toUpperCase(a)) == b || a == Character.toUpperCase(b));
	}
	public StringCharReader setToStringIncludesLocation(boolean b) {
		this.toStringIncludesLocation = b;
		return this;
	}
	public boolean getToStringIncludesLocation() {
		return toStringIncludesLocation;
	}
	@Override
	public String substring(int start, int end) {
		return new String(this.inner, start, end - start);
	}
	@Override
	public String getText() {
		return this.origText;
	}
	public boolean isStrictEscape() {
		return strictEscape;
	}
	public void setStrictEscape(boolean strictEscape) {
		this.strictEscape = strictEscape;
	}
}
