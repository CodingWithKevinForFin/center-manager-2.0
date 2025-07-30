package com.f1.utils.impl;

import com.f1.utils.CharReader;
import com.f1.utils.SH;
import com.f1.utils.TextMatcher;

public class TextMatcherFactory {

	public static final String EVERYTHING = "*";
	public static final String NOTHING = "";
	private static final int[] SLASH_EOF = new int[] { '/', CharReader.EOF };
	private static final CharMatcher SPECIAL_CHARS = new BasicCharMatcher("|&)?:", true);
	private static final CharMatcher SINGLEQUOTE_EOF = new BasicCharMatcher("'", true);
	public final static TextMatcherFactory PARTIAL_CASE_SENSETIVE = new TextMatcherFactory(false, true, true);
	public final static TextMatcherFactory PARTIAL_CASE_INSENSETIVE = new TextMatcherFactory(false, true, false);
	public final static TextMatcherFactory FULL_CASE_SENSETIVE = new TextMatcherFactory(true, true, true);
	public final static TextMatcherFactory FULL_CASE_INSENSETIVE = new TextMatcherFactory(true, true, false);
	public final static TextMatcherFactory DEFAULT_FILE_PATTERN = new TextMatcherFactory(true, false, false);
	public final static TextMatcherFactory DEFAULT = PARTIAL_CASE_INSENSETIVE;
	final private boolean mustStartEnd;
	final private boolean periodIsWild;
	final private boolean isCaseSensetive;

	public static TextMatcherFactory getFactory(boolean isCaseSensetive, boolean isFullMatch) {
		if (isFullMatch)
			return isCaseSensetive ? FULL_CASE_SENSETIVE : FULL_CASE_INSENSETIVE;
		else
			return isCaseSensetive ? PARTIAL_CASE_SENSETIVE : PARTIAL_CASE_INSENSETIVE;
	}

	public TextMatcherFactory(boolean mustStartEnd, boolean periodIsWild, boolean isCaseSensetive) {
		this.isCaseSensetive = isCaseSensetive;
		this.mustStartEnd = mustStartEnd;
		this.periodIsWild = periodIsWild;
	}

	/*
	
	* 
	
	*/
	public TextMatcher toMatcher(String text) {
		return this.toMatcher(text, true);
	}

	public TextMatcher toMatcher(String text, boolean threadSafe) {
		try {
			if (text == null)
				return ConstTextMatcher.FALSE;
			else if (text.length() == 0)
				return SimpleTextMatcher.EMPTY;
			else if ("*".equals(text))
				return ConstTextMatcher.TRUE;
			StringCharReader reader = new StringCharReader(text);
			reader.setToStringIncludesLocation(true);
			StringBuilder sb = new StringBuilder();
			TextMatcher r = toGrepMatcher(reader, sb, threadSafe, false, false);
			if (reader.peakOrEof() != CharReader.EOF) {
				reader.readUntil(CharReader.EOF, SH.clear(sb));
				throw new RuntimeException("Trailing text in expression: " + sb);
			}
			return r;
		} catch (Exception e) {
			throw new RuntimeException("Error parsing matcher: " + text, e);
		}

	}
	public TextMatcher toMatcherNoThrow(CharReader text, StringBuilder sb) {
		try {
			return toGrepMatcher(text, sb, true, false, false);
		} catch (Exception e) {
			return ConstTextMatcher.FALSE;
		}
	}

	public TextMatcher toMatcher(CharReader text, StringBuilder sb) {
		return toGrepMatcher(text, sb, true, false, false);
	}

	private TextMatcher toGrepMatcher(CharReader text, StringBuilder sb, boolean threadSafe, boolean stopOnCloseParanthesis, boolean stopOnColon) {
		TextMatcher r;
		switch (text.peakOrEof()) {
			case CharReader.EOF:
				return ConstTextMatcher.FALSE;
			case '/':
				r = toPatterMatcher(text, sb, threadSafe);
				break;
			case '\'':
				r = toSimpleTextMatcher(text, sb);
				break;
			case '!':
				text.expect('!');
				r = new ConditionalMatcher.Not(toGrepMatcher(text, sb, threadSafe, stopOnCloseParanthesis, stopOnColon));
				break;
			case '?': {
				text.expect('?');
				r = NULL_MATCHER;
				break;
			}
			case '(': {
				text.expect('(');
				r = toGrepMatcher(text, sb, threadSafe, true, stopOnColon);
				text.expect(')');
				break;
			}
			default: {
				SH.clear(sb);
				outer: for (;;) {
					text.readUntilAny(SPECIAL_CHARS, true, '\\', sb);
					switch (text.peakOrEof()) {
						case ':':
							if (stopOnColon)
								break outer;
							break;
						case ')':
							if (stopOnCloseParanthesis)
								break outer;
							break;
						default:
							break outer;
					}
					sb.append(text.readChar());
				}
				r = GrepTextMatcher.valueOf(sb.toString(), mustStartEnd, periodIsWild, this.isCaseSensetive);
			}
		}
		int c = text.peakOrEof();
		switch (c) {
			case CharReader.EOF:
			case ':':
			case ')':
				return r;
			case '?':
				text.expect('?');
				TextMatcher t = toGrepMatcher(text, sb, threadSafe, stopOnCloseParanthesis, true);
				text.expect(':');
				TextMatcher f = toGrepMatcher(text, sb, threadSafe, stopOnCloseParanthesis, stopOnColon);
				return new ConditionalMatcher.If(r, t, f);
			case '|':
				text.expect('|');
				return toOr(r, toGrepMatcher(text, sb, threadSafe, stopOnCloseParanthesis, stopOnColon));
			case '&':
				text.expect('&');
				return new ConditionalMatcher.And(r, toGrepMatcher(text, sb, threadSafe, stopOnCloseParanthesis, stopOnColon));
			default:
				throw new RuntimeException("Trailing text");
		}
	}

	private TextMatcher toSimpleTextMatcher(CharReader text, StringBuilder sb) {
		text.expect('\'');
		text.readUntilAnySkipEscaped(SINGLEQUOTE_EOF, '\\', SH.clear(sb));
		text.expectNoThrow('\'');
		return new SimpleTextMatcher(sb.toString(), !this.isCaseSensetive);
	}

	private TextMatcher toPatterMatcher(CharReader text, StringBuilder sb, boolean threadSafe) {
		text.expect('/');
		int c = text.readUntilAny(SLASH_EOF, '\\', SH.clear(sb));
		String regex = sb.toString();
		String options;
		if (c == '/') {
			text.expect('/');
			text.readUntilAny(SLASH_EOF, '\\', SH.clear(sb));
			options = sb.toString();
			text.expectNoThrow('/');
		} else
			options = "";
		return new PatternTextMatcher(regex, options, threadSafe);

	}

	public String stringToExpression(String text) {
		text = "'" + text + "'";
		return text;
	}

	private static final TextMatcher NULL_MATCHER = new TextMatcher() {

		@Override
		public StringBuilder toString(StringBuilder sink) {
			return sink.append("<null>");
		}

		@Override
		public boolean matches(String input) {
			return input == null;
		}

		@Override
		public boolean matches(CharSequence input) {
			return input == null;
		}
		@Override
		public boolean equals(Object obj) {
			return obj == NULL_MATCHER;
		}
	};

	static public String escapeToPattern(String text, boolean periodsAreWild) {
		if (text == null)
			return "?";
		if (text.length() == 0)
			return "''";
		for (int i = text.length() - 1; i >= 0; i--) {
			switch (text.charAt(i)) {
				case ' ':
					if (i != 0 && i != text.length() - 1)
						continue;
				case '.':
					if (!periodsAreWild)
						continue;
				case '*':
				case '^':
				case '$':
				case '[':
				case ']':
				case '(':
				case ')':
				case '~':
				case '|':
				case '&':
				case '?':
				case '!':
				case ':':
				case '\'':
				case '\\':
				case '/':
				case '>':
				case '<':
					return SH.quote('\'', text, new StringBuilder(text.length() + 4)).toString();
				case '-':
					if (i != 0)
						return SH.quote('\'', text, new StringBuilder(text.length() + 4)).toString();
			}
		}
		return text;
	}

	public static String unescapeFromPattern(String s) {
		if (s == null)
			return null;
		if (s.startsWith("'") && s.endsWith("'")) {
			StringBuilder sb = new StringBuilder();
			for (int i = 1; i < s.length() - 1; i++) {
				char c = s.charAt(i);
				if (c == '\'')
					c = s.charAt(i++);
				sb.append(c);
			}
			return sb.toString();
		}
		return s;
	}
	public TextMatcher toOr(TextMatcher l, TextMatcher r) {
		if (l instanceof TextMatcherOr) {
			TextMatcherOr l2 = (TextMatcherOr) l;
			l2.addClause(r);
			return l2;
		} else if (r instanceof TextMatcherOr) {
			TextMatcherOr r2 = (TextMatcherOr) r;
			r2.addClause(l);
			return r2;
		} else {
			TextMatcherOr ret = new TextMatcherOr();
			ret.addClause(l);
			ret.addClause(r);
			return ret;
		}
	}

}
