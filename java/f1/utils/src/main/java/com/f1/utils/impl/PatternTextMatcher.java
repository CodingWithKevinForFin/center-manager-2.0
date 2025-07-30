package com.f1.utils.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.TextMatcher;

public class PatternTextMatcher implements TextMatcher {
	public static final char OPTION_CANON_EQ = 'q';
	public static final char OPTION_FULL_LINE = 'f';
	public static final char OPTION_CASE_INSENSITIVE = 'i';
	public static final char OPTION_COMMENTS = 'c';
	public static final char OPTION_DOTALL = 'd';
	public static final char OPTION_LITERAL = 'l';
	public static final char OPTION_MULTILINE = 'm';
	public static final char OPTION_UNICODE_CASE = 'u';
	public static final char OPTION_UNIX_LINES = 'x';
	public static final char OPTION_NOT_MATCH = 'v';
	public static final char OPTION_NO_THROW = 'n';

	public static final int UNIX_LINES = Pattern.UNIX_LINES;
	public static final int CASE_INSENSITIVE = Pattern.CASE_INSENSITIVE;
	public static final int COMMENTS = Pattern.COMMENTS;
	public static final int MULTILINE = Pattern.MULTILINE;
	public static final int LITERAL = Pattern.LITERAL;
	public static final int DOTALL = Pattern.DOTALL;
	public static final int UNICODE_CASE = Pattern.UNICODE_CASE;
	public static final int CANON_EQ = Pattern.CANON_EQ;
	public static final int NOT_MATCH = 0x800;//if set, returns false if matches, true if doesn't match
	public static final int FULL_LINE = 0x1000;//if set then must be a full match, otherwise regex must just exist in string
	public static final int NO_THROW = 0x2000;//if pattern is bad, don't throw an exception, bad exceptions always return false, unless NOT_MATCH is set

	private final Pattern pattern;
	private final String regex;
	private final int options;
	private final Matcher matcher;

	public PatternTextMatcher(String regex, int options, boolean threadSafe) {
		this.regex = regex;
		this.options = options;
		Pattern p;
		try {
			p = Pattern.compile(regex, MH.clearBits(options, NOT_MATCH | FULL_LINE | NO_THROW));
		} catch (RuntimeException e) {
			if (!MH.anyBits(options, NO_THROW))
				throw e;
			p = null;
		}
		this.pattern = p;
		this.matcher = threadSafe || this.pattern == null ? null : this.pattern.matcher("");
	}

	public PatternTextMatcher(String regex, String optionsText, boolean threadSafe) {
		this(regex, parseOptions(optionsText), threadSafe);
	}

	@Override
	public boolean matches(String input) {
		return matches((CharSequence) input);
	}
	@Override
	public boolean matches(CharSequence input) {
		if (input == null)
			return MH.anyBits(options, NOT_MATCH);
		Matcher matcher;
		if (this.matcher != null) {
			this.matcher.reset(input);
			matcher = this.matcher;
		} else {
			if (pattern == null)
				return MH.anyBits(options, NOT_MATCH);
			matcher = pattern.matcher(input);
		}
		switch (options & (NOT_MATCH | FULL_LINE)) {
			case 0:
				return matcher.find();
			case NOT_MATCH:
				return !matcher.find();
			case FULL_LINE:
				return matcher.matches();
			case NOT_MATCH | FULL_LINE:
				return !matcher.matches();
			default:
				throw new IllegalStateException(SH.toString(options));
		}
	}

	public static int parseOptions(String options) {
		int r = OPTION_DOTALL;
		for (int i = 0, l = options.length(); i < l; i++) {
			final char c = Character.toLowerCase(options.charAt(i));
			switch (c) {
				case OPTION_CANON_EQ:
					r |= CANON_EQ;
					break;
				case OPTION_CASE_INSENSITIVE:
					r |= CASE_INSENSITIVE;
					break;
				case OPTION_COMMENTS:
					r |= COMMENTS;
					break;
				case OPTION_DOTALL:
					r ^= DOTALL;
					break;
				case OPTION_LITERAL:
					r |= LITERAL;
					break;
				case OPTION_MULTILINE:
					r |= MULTILINE;
					break;
				case OPTION_UNICODE_CASE:
					r |= UNICODE_CASE;
					break;
				case OPTION_UNIX_LINES:
					r |= UNIX_LINES;
					break;
				case OPTION_NOT_MATCH:
					r |= NOT_MATCH;
					break;
				case OPTION_FULL_LINE:
					r |= FULL_LINE;
					break;
				case OPTION_NO_THROW:
					r |= NO_THROW;
					break;
				default:
					throw new RuntimeException("invalid pattern option: " + c);
			}
		}
		return r;
	}

	public static StringBuilder toOptions(int flags, StringBuilder sink) {
		if (MH.areAnyBitsSet(flags, CANON_EQ))
			sink.append(OPTION_CANON_EQ);
		if (MH.areAnyBitsSet(flags, CASE_INSENSITIVE))
			sink.append(OPTION_CASE_INSENSITIVE);
		if (MH.areAnyBitsSet(flags, COMMENTS))
			sink.append(OPTION_COMMENTS);
		if (MH.areAnyBitsSet(flags, DOTALL))
			sink.append(OPTION_DOTALL);
		if (MH.areAnyBitsSet(flags, LITERAL))
			sink.append(OPTION_LITERAL);
		if (MH.areAnyBitsSet(flags, MULTILINE))
			sink.append(OPTION_MULTILINE);
		if (MH.areAnyBitsSet(flags, UNICODE_CASE))
			sink.append(OPTION_UNICODE_CASE);
		if (MH.areAnyBitsSet(flags, UNIX_LINES))
			sink.append(OPTION_UNIX_LINES);
		if (MH.areAnyBitsSet(flags, NOT_MATCH))
			sink.append(OPTION_NOT_MATCH);
		if (MH.areAnyBitsSet(flags, FULL_LINE))
			sink.append(OPTION_FULL_LINE);
		if (MH.areAnyBitsSet(flags, NO_THROW))
			sink.append(OPTION_NO_THROW);
		return sink;
	}

	@Override
	public StringBuilder toString(StringBuilder sink) {
		sink.append('/').append(this.pattern).append('/');
		toOptions(this.options, sink);
		return sink.append('/');
	}
	public String getRegex() {
		return this.regex;
	}
	public Pattern getPattern() {
		return this.pattern;
	}

	@Override
	public String toString() {
		return toString(new StringBuilder()).toString();
	}
	@Override
	public boolean equals(Object obj) {
		if (obj == null || obj.getClass() != PatternTextMatcher.class)
			return false;
		PatternTextMatcher other = (PatternTextMatcher) obj;
		return OH.eq(options, other.options) && OH.eq(regex, other.regex) && OH.eq(pattern, other.pattern) && OH.eq(matcher, other.matcher);
	}
}
