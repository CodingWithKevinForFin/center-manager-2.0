/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import com.f1.base.Complex;
import com.f1.base.Legible;
import com.f1.base.StringBuildable;
import com.f1.base.ToStringable;
import com.f1.utils.casters.Caster_Boolean;
import com.f1.utils.casters.Caster_Byte;
import com.f1.utils.casters.Caster_Double;
import com.f1.utils.casters.Caster_Float;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.casters.Caster_Long;
import com.f1.utils.casters.Caster_Short;
import com.f1.utils.impl.CharMatcher;
import com.f1.utils.impl.ConstTextMatcher;
import com.f1.utils.impl.TextMatcherFactory;
import com.f1.utils.structs.Tuple2;
import com.f1.utils.structs.Tuple3;

/**
 * String Helper
 */
public class SH {
	public static final char CHAR_UPSIDEDOWN_QUESTIONMARK = 0x00bf;
	public static final String POS_INFINITY = "infinity";
	public static final String NEG_INFINITY = "-infinity";
	public static final String NAN = "NaN";
	public static final String EMPTY_STRING = "";

	public static final Charset CHARSET_UTF = Charset.forName("UTF-8");
	public static final Charset CHARSET_UTF16 = Charset.forName("UTF-16");
	public static final Charset CHARSET_USASCII = Charset.forName("US-ASCII");
	public static final int MAX_STRING_CONST_LENGTH = 10000;
	public static final String MAX_INT_DEC = Integer.toString(Integer.MAX_VALUE);
	public static final String MIN_INT_DEC = Integer.toString(Integer.MIN_VALUE);
	public static final String MAX_INT_OCT = Integer.toString(Integer.MAX_VALUE, 8);
	public static final String MIN_INT_OCT = Integer.toString(Integer.MIN_VALUE, 8);
	public static final String MAX_INT_HEX = Integer.toString(Integer.MAX_VALUE, 16);
	public static final String MIN_INT_HEX = Integer.toString(Integer.MIN_VALUE, 16);
	public static final String ALPHA_LOWER = "abcdefghijklmnopqrstuvwxyz";
	public static final String ALPHA_UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	public static final String NUMBERS = "0123456789";
	public static final String NEWLINE = System.getProperty("line.separator");
	public static final int NEWLINE_LENGTH = NEWLINE.length();
	public static final char CHAR_QUOTE = '"';
	public static final char CHAR_SINGLE_QUOTE = '\'';
	public static final char CHAR_TAB = '\t';
	public static final char CHAR_NEWLINE = '\n';
	public static final char CHAR_BACKSPACE = '\b';
	public static final char CHAR_FORMFEED = '\f';
	public static final char CHAR_RETURN = '\r';
	public static final char CHAR_BACKSLASH = '\\';
	public static final char CHAR_FORWARDSLASH = '/';

	public static final byte BYTE_TAB = '\t';
	public static final byte BYTE_CR = '\r';
	public static final byte BYTE_NEWLINE = '\n';
	public static final byte BYTE_TAB_KEY = 9;
	public static final byte BYTE_ESC_KEY = 27;
	public static final byte BYTE_LEFT_KEY = 37;
	public static final byte BYTE_UP_KEY = 38;
	public static final byte BYTE_RIGHT_KEY = 39;
	public static final byte BYTE_DOWN_KEY = 40;
	public static final byte BYTE_ENTER_KEY = 13;

	/** Null */
	public final static char CHAR_NUL = (char) 0;

	/** Start of heading */
	public final static char CHAR_SOH = (char) 1;

	/** start of text */
	public final static char CHAR_STX = (char) 2;

	/** End of Text */
	public final static char CHAR_ETX = (char) 3;

	/** End of Transmission */
	public final static char CHAR_EOT = (char) 4;

	/** Enquiry */
	public final static char CHAR_ENQ = (char) 5;

	/** acknowledge */
	public final static char CHAR_ACK = (char) 6;

	/** Bell */
	public final static char CHAR_BEL = (char) 7;

	/** Backspace */
	public final static char CHAR_BS = (char) 8;

	/** Horizontal Tab */
	public final static char CHAR_HT = (char) 9;

	/** Line Feed */
	public final static char CHAR_LF = CHAR_NEWLINE;

	/** Vertical Tab */
	public final static char CHAR_VT = (char) 11;

	/** Form Feed */
	public final static char CHAR_FF = (char) 12;

	/** Carriage Return */
	public final static char CHAR_CR = CHAR_RETURN;

	/** shift out */
	public final static char CHAR_SO = (char) 14;

	/** shift in */
	public final static char CHAR_SI = (char) 15;

	/** data link escape */
	public final static char CHAR_DLE = (char) 16;

	/** Device Control 1 */
	public final static char CHAR_DC1 = (char) 17;

	/** Device Control 2 */
	public final static char CHAR_DC2 = (char) 18;

	/** Device Control 3 */
	public final static char CHAR_DC3 = (char) 19;

	/** Device Control 4 */
	public final static char CHAR_DC4 = (char) 20;

	/** escape */
	public static final char CHAR_ESC = (char) 27;

	/** Delete */
	public static final char CHAR_DEL = (char) 127;

	public static final char CHAR_UNICODE = 'u';
	public static final char CHAR_NOT_SPECIAL = 0;

	public static final char UNICODE_LEFT_SINGLE_QUOTATION = '\u2018';
	public static final char UNICODE_RIGHT_SINGLE_QUOTATION = '\u2019';
	public static final char UNICODE_LEFT_DOUBLE_QUOTATION = '\u201C';
	public static final char UNICODE_RIGHT_DOUBLE_QUOTATION = '\u201D';

	public final static byte TELNET_CODE_ESC = (byte) 27;
	public static final byte TELNET_CODE_DEL = (byte) 127;
	private static final String PREFIX_HEX = "0x";
	public static final NavigableSet<Character> ALPHA_LOWER_SET = CH.s(new TreeSet<Character>(), AH.box(ALPHA_LOWER.toCharArray()));
	public static final NavigableSet<Character> ALPHA_UPPER_SET = CH.s(new TreeSet<Character>(), AH.box(ALPHA_UPPER.toCharArray()));
	public static final NavigableSet<Character> NUMERIC_SET = CH.s(new TreeSet<Character>(), AH.box(NUMBERS.toCharArray()));
	public static final NavigableSet<Character> ALPHA_NUMERIC_SET;
	static {
		ALPHA_NUMERIC_SET = new TreeSet<Character>();
		ALPHA_NUMERIC_SET.addAll(ALPHA_LOWER_SET);
		ALPHA_NUMERIC_SET.addAll(ALPHA_UPPER_SET);
		ALPHA_NUMERIC_SET.addAll(NUMERIC_SET);
	}

	/**
	 * joins the subst array into a delimited string ordered by the elements of the array. the {@link Object#toString()} is used to convert the objects to a string An empty array
	 * will result in an empty string. the number of delims will be one less than the number of elements in the array
	 * 
	 * @param delim
	 *            delimeter used to join
	 * @param start
	 *            start index (inclusive)
	 * @param end
	 *            end index (exclusive)
	 * @param tokens
	 *            tokens to take subset from
	 * @param r
	 *            sink
	 * @return sink for convenience
	 */
	public static StringBuilder joinSub(char delim, int start, int end, Object tokens[], StringBuilder r) {
		if (end == start)
			return r;
		s(tokens[start], r);
		for (int i = start + 1; i < end; i++)
			s(tokens[i], r.append(delim));
		return r;
	}

	/**
	 * joins the subst array into a delimited string ordered by the elements of the array. An empty array will result in an empty string. the number of delims will be one less than
	 * the number of elements in the array
	 * 
	 * @param delim
	 *            delimeter used to join
	 * @param start
	 *            start index (inclusive)
	 * @param end
	 *            end index (exclusive)
	 * @param tokens
	 *            tokens to take subset from
	 * @param r
	 *            sink
	 * @return sink for convenience
	 */
	public static StringBuilder joinSub(char delim, int start, int end, String tokens[], StringBuilder r) {
		if (end == start)
			return r;
		r.append(tokens[start]);
		for (int i = start + 1; i < end; i++)
			r.append(delim).append(tokens[i]);
		return r;
	}

	/**
	 * joins the array into a delimited string ordered by the elements of the array. the {@link Object#toString()} is used to convert the objects to a string. An empty array will
	 * result in an empty string. the number of delims will be one less than the number of elements in the array
	 * 
	 * @param delim
	 *            the delim between each element
	 * @param tokens
	 *            the tokens to turn into a string
	 * @param r
	 *            the sink to append to
	 * @return r param for convenience
	 */
	public static StringBuilder join(char delim, Object tokens[], StringBuilder r) {
		int length = tokens.length;
		if (length > 0)
			s(tokens[0], r);
		for (int i = 1; i < length; i++)
			s(tokens[i], r.append(delim));
		return r;
	}

	/**
	 * joins the array into a delimited string ordered by the elements of the array. An empty array will result in an empty string. the number of delims will be one less than the
	 * number of elements in the array
	 * 
	 * @param delim
	 *            the delim between each element
	 * @param tokens
	 *            the tokens to turn into a string
	 * @param r
	 *            the sink to append to
	 * @return r param for convenience
	 */
	public static StringBuilder join(char delim, String tokens[], StringBuilder r) {
		int length = tokens.length;
		if (length == 0)
			return r;
		int totlen = r.length() + length - 1;
		for (String s : tokens)
			totlen += s == null ? 4 : s.length();
		r.ensureCapacity(totlen);
		r.append(tokens[0]);
		for (int i = 1; i < length; i++)
			r.append(delim).append(tokens[i]);
		return r;
	}

	/**
	 * joins the array into a delimited string ordered by the elements of the array. the {@link Object#toString()} is used to convert the objects to a string An empty array will
	 * result in an empty string. the number of delims will be one less than the number of elements in the array
	 * 
	 * @param delim
	 *            the delim between each element
	 * @param tokens
	 *            the tokens to turn into a string
	 * @return the string delimited value
	 */
	public static <T extends Object> String join(char delim, T... tokens) {
		if (tokens.length < 2)
			return tokens.length == 0 ? "" : s(tokens[0]);
		return join(delim, tokens, new StringBuilder()).toString();
	}

	/**
	 * joins the array into a delimited string ordered by the elements of the array. An empty array will result in an empty string. the number of delims will be one less than the
	 * number of elements in the array
	 * 
	 * @param delim
	 *            the delim between each element
	 * @param tokens
	 *            the tokens to turn into a string
	 * @return the string delimited value
	 */
	public static String join(char delim, String... tokens) {
		if (tokens.length < 2)
			return tokens.length == 0 ? "" : OH.noNull(tokens[0], "null");
		return join(delim, tokens, new StringBuilder()).toString();
	}

	/**
	 * joins the subset array into a delimited string ordered by the elements of the array. the {@link Object#toString()} is used to convert the objects to a string. An empty array
	 * will result in an empty string. the number of delims will be one less than the number of elements in the array
	 * 
	 * @param delim
	 *            delimeter used to join
	 * @param start
	 *            start index (inclusive)
	 * @param end
	 *            end index (exclusive)
	 * @param tokens
	 *            tokens to take subset from
	 * @return the delimited string
	 */
	public static String joinSub(char delim, int start, int end, Object... tokens) {
		return joinSub(delim, start, end, tokens, new StringBuilder()).toString();
	}

	/**
	 * joins the subset array into a delimited string ordered by the elements of the array. An empty array will result in an empty string. the number of delims will be one less
	 * than the number of elements in the array
	 * 
	 * @param delim
	 *            delimeter used to join
	 * @param start
	 *            start index (inclusive)
	 * @param end
	 *            end index (exclusive)
	 * @param tokens
	 *            tokens to take subset from
	 * @return the delimited string
	 */
	public static String joinSub(char delim, int start, int end, String... tokens) {
		return joinSub(delim, start, end, tokens, new StringBuilder()).toString();
	}

	/**
	 * see {@link #join(char, Object[], StringBuilder), but works with primitive array instead of object.
	 * 
	 * @param delim
	 *            delimeter used to join
	 * @param tokens
	 *            tokens to take subset from
	 * @param r
	 *            the sink to append to
	 * @return r param for convenience
	 */
	public static StringBuilder join(char delim, byte tokens[], StringBuilder r) {
		int length = tokens.length;
		if (length > 0)
			r.append(tokens[0]);
		for (int i = 1; i < length; i++)
			r.append(delim).append(tokens[i]);
		return r;
	}

	/**
	 * see {@link #join(char, Object[], StringBuilder), but works with primitive array instead of object.
	 * 
	 * @param delim
	 *            delimeter used to join
	 * @param tokens
	 *            tokens to loop over
	 * @return the joined string
	 */
	public static String join(char delim, byte... tokens) {
		return join(delim, tokens, new StringBuilder()).toString();
	}

	/**
	 * see {@link #join(char, Object[], StringBuilder), but works with primitive array instead of object.
	 * 
	 * @param delim
	 *            delimeter used to join
	 * @param tokens
	 *            tokens to loop over
	 * @return the joined string
	 */
	public static StringBuilder join(char delim, int tokens[], StringBuilder r) {
		int length = tokens.length;
		if (length > 0)
			r.append(tokens[0]);
		for (int i = 1; i < length; i++)
			r.append(delim).append(tokens[i]);
		return r;
	}

	public static String join(char delim, int... tokens) {
		return join(delim, tokens, new StringBuilder()).toString();
	}

	/**
	 * see {@link #join(char, Object[], StringBuilder), but works with primitive array instead of object.
	 * 
	 * @param delim
	 *            delimeter used to join
	 * @param tokens
	 *            tokens to loop over
	 * @param r
	 *            the sink to append to
	 * @return r param for convenience
	 */
	public static StringBuilder join(char delim, long tokens[], StringBuilder r) {
		int length = tokens.length;
		if (length > 0)
			r.append(tokens[0]);
		for (int i = 1; i < length; i++)
			r.append(delim).append(tokens[i]);
		return r;
	}

	public static String join(char delim, long... tokens) {
		return join(delim, tokens, new StringBuilder()).toString();
	}

	/**
	 * see {@link #join(char, Object[], StringBuilder), but works with primitive array instead of object.
	 * 
	 * @param delim
	 *            delimeter used to join
	 * @param tokens
	 *            tokens to loop over
	 * @param r
	 *            the sink to append to
	 * @return r param for convenience
	 */
	public static StringBuilder join(char delim, double tokens[], StringBuilder r) {
		int length = tokens.length;
		if (length > 0)
			r.append(tokens[0]);
		for (int i = 1; i < length; i++)
			r.append(delim).append(tokens[i]);
		return r;
	}

	public static String join(char delim, double... tokens) {
		return join(delim, tokens, new StringBuilder()).toString();
	}

	/**
	 * see {@link #join(char, Object[], StringBuilder), but works with primitive array instead of object.
	 * 
	 * @param delim
	 *            delimeter used to join
	 * @param tokens
	 *            tokens to loop over
	 * @param r
	 *            the sink to append to
	 * @return r param for convenience
	 */
	public static StringBuilder join(char delim, float tokens[], StringBuilder r) {
		int length = tokens.length;
		if (length > 0)
			r.append(tokens[0]);
		for (int i = 1; i < length; i++)
			r.append(delim).append(tokens[i]);
		return r;
	}

	public static String join(char delim, float... tokens) {
		return join(delim, tokens, new StringBuilder()).toString();
	}

	/**
	 * see {@link #join(char, Object[], StringBuilder), but works with primitive array instead of object.
	 * 
	 * @param delim
	 *            delimeter used to join
	 * @param tokens
	 *            tokens to loop over
	 * @param r
	 *            the sink to append to
	 * @return r param for convenience
	 */
	public static StringBuilder join(char delim, short tokens[], StringBuilder r) {
		int length = tokens.length;
		if (length > 0)
			r.append(tokens[0]);
		for (int i = 1; i < length; i++)
			r.append(delim).append(tokens[i]);
		return r;
	}

	public static String join(char delim, short... tokens) {
		return join(delim, tokens, new StringBuilder()).toString();
	}

	/**
	 * see {@link #join(char, Object[], StringBuilder), but works with a collection instead of array of objects. Elements will be ordered based on Collection#iterator(). the
	 * collections' objects will to converted to strings by calling Object#toString()
	 * 
	 * @param delim
	 *            delimeter used to join
	 * @param tokens
	 *            tokens to loop over
	 * @param r
	 *            the sink to append to
	 * @return r param for convenience
	 */
	public static StringBuilder join(char delim, Iterable<?> tokens, StringBuilder r) {
		Iterator<?> i = tokens.iterator();
		if (i.hasNext()) {
			s(i.next(), r);
			while (i.hasNext())
				s(i.next(), r.append(delim));
		}
		return r;
	}

	public static String join(char delim, Iterable<?> tokens) {
		if (tokens instanceof Collection)
			return join(delim, (Collection<?>) tokens);
		return join(delim, tokens, new StringBuilder()).toString();
	}
	public static String join(char delim, Collection<?> tokens) {
		if (tokens.size() < 2)
			return tokens.size() == 0 ? "" : SH.s(CH.first(tokens));
		return join(delim, tokens, new StringBuilder()).toString();
	}

	/**
	 * see {@link #join(char, Object[], StringBuilder) but the delim is a String, which has slightly lower performance but more flexability
	 * 
	 * @param delim
	 *            the delimeter used to join
	 * @param tokens
	 *            tokens
	 * @param r
	 *            thes sink to append to
	 * @return r param for convenience
	 */
	public static StringBuilder join(String delim, Object tokens[], StringBuilder r) {
		int length = tokens.length;
		if (length > 0)
			s(tokens[0], r);
		for (int i = 1; i < length; i++)
			s(tokens[i], r.append(delim));
		return r;
	}

	public static StringBuilder join(String delim, String tokens[], StringBuilder r) {
		int length = tokens.length;
		if (length == 0)
			return r;
		if (delim != null && delim.length() == 1)
			return join(delim.charAt(0), tokens, r);

		int totlen = r.length() + (length - 1) * (delim == null ? 4 : delim.length());
		for (String s : tokens)
			totlen += s == null ? 4 : s.length();
		r.ensureCapacity(totlen);
		r.append(tokens[0]);
		for (int i = 1; i < length; i++)
			r.append(delim).append(tokens[i]);
		return r;
	}
	/**
	 * see {@link #join(char, Object[], StringBuilder) but the delim is a String, which has slightly lower performance but more flexability
	 * 
	 * @param delim
	 *            the delimeter used to jion
	 * @param tokens
	 *            tokens
	 * @return the
	 */
	public static String join(String delim, Object... tokens) {
		return join(delim, tokens, new StringBuilder()).toString();
	}
	public static String join(String delim, String... tokens) {
		if (tokens.length == 0)
			return "";
		if (tokens.length == 1)
			return tokens[0];
		return join(delim, tokens, new StringBuilder()).toString();
	}

	/*
	 * short hand for {@link SH#join(String, Object...)}
	 */
	public static String j(String delim, Object... tokens) {
		return SH.join(delim, tokens);
	}

	/*
	 * short hand for {@link SH#join(char, Object...)}
	 */
	public static String j(char delim, Object... tokens) {
		return SH.join(delim, tokens);
	}

	/*
	 * join no delim short hand for {@link SH#join("", Object...)}
	 */
	public static String jn(Object... tokens) {
		return SH.join(EMPTY_STRING, tokens);
	}

	/*
	 * short hand for {@link SH#join(String, Object[], StringBuilder)}
	 */
	public static StringBuilder j(String delim, Object[] tokens, StringBuilder r) {
		return SH.join(delim, tokens, r);
	}

	/*
	 * short hand for {@link SH#join(String, StringBuilder, Object... tokens)}
	 */
	public static StringBuilder j(String delim, StringBuilder r, Object... tokens) {
		return SH.join(delim, tokens, r);
	}

	/*
	 * short hand for {@link SH#join(char, Object[], StringBuilder)}
	 */
	public static StringBuilder j(char delim, Object[] tokens, StringBuilder r) {
		return SH.join(delim, tokens, r);
	}

	/*
	 * short hand for {@link SH#join(char, StringBuilder, Object...)}
	 */
	public static StringBuilder j(char delim, StringBuilder r, Object... tokens) {
		return SH.join(delim, tokens, r);
	}
	/*
	 * join no delim short hand for {@link SH#join("", Object[], StringBuilder)}
	 */
	public static StringBuilder jn(Object[] tokens, StringBuilder r) {
		return SH.join(EMPTY_STRING, tokens, r);
	}
	/*
	 * noin no delim short hand for {@link SH#join("", Object[], StringBuilder)}
	 */
	public static StringBuilder jn(StringBuilder r, Object... tokens) {
		return SH.join(EMPTY_STRING, tokens, r);
	}

	/*
	 * short hand for {@link SH#join(String, Collection<?>)}
	 */
	public static String j(String delim, Collection<?> tokens) {
		return SH.join(delim, tokens);
	}

	/*
	 * short hand for {@link SH#join(char, Collection<?>)}
	 */
	public static String j(char delim, Collection<?> tokens) {
		return SH.join(delim, tokens);
	}

	/*
	 * join no delim short hand for {@link SH#join("", Collection<?>)}
	 */
	public static String jn(Collection<?> tokens) {
		return SH.join(EMPTY_STRING, tokens);
	}

	/*
	 * short hand for {@link SH#join(String, Collection<?>, StringBuilder)}
	 */
	public static StringBuilder j(String delim, Collection<?> tokens, StringBuilder sb) {
		return SH.join(delim, tokens, sb);
	}

	/*
	 * short hand for {@link SH#join(char, Collection<?>, StringBuilder)}
	 */
	public static StringBuilder j(char delim, Collection<?> tokens, StringBuilder sb) {
		return SH.join(delim, tokens, sb);
	}

	/*
	 * join no delim short hand for {@link SH#join("", Collection<?>)}
	 */
	public static StringBuilder jn(Collection<?> tokens, StringBuilder sb) {
		return SH.join(EMPTY_STRING, tokens, sb);
	}

	public static <K, V> void join(char delim, char associator, Map<K, V> map, StringBuilder sb) {
		Iterator<Map.Entry<K, V>> entries = map.entrySet().iterator();
		if (!entries.hasNext())
			return;
		Map.Entry entry = entries.next();
		sb.append(entry.getKey()).append(associator).append(entry.getValue());
		while (entries.hasNext())
			sb.append(delim).append(entry.getKey()).append(associator).append(entry.getValue());
	}

	// byte
	public static StringBuilder join(String delim, byte tokens[], StringBuilder r) {
		int length = tokens.length;
		if (length > 0)
			r.append(tokens[0]);
		for (int i = 1; i < length; i++)
			r.append(delim).append(tokens[i]);
		return r;
	}

	public static String join(String delim, byte... tokens) {
		return join(delim, tokens, new StringBuilder()).toString();
	}

	// int
	public static StringBuilder join(String delim, int tokens[], StringBuilder r) {
		int length = tokens.length;
		if (length > 0)
			r.append(tokens[0]);
		for (int i = 1; i < length; i++)
			r.append(delim).append(tokens[i]);
		return r;
	}

	public static String join(String delim, int... tokens) {
		return join(delim, tokens, new StringBuilder()).toString();
	}

	// long
	public static StringBuilder join(String delim, long tokens[], StringBuilder r) {
		int length = tokens.length;
		if (length > 0)
			r.append(tokens[0]);
		for (int i = 1; i < length; i++)
			r.append(delim).append(tokens[i]);
		return r;
	}

	public static String join(String delim, long... tokens) {
		return join(delim, tokens, new StringBuilder()).toString();
	}

	// double
	public static StringBuilder join(String delim, double tokens[], StringBuilder r) {
		int length = tokens.length;
		if (length > 0)
			r.append(tokens[0]);
		for (int i = 1; i < length; i++)
			r.append(delim).append(tokens[i]);
		return r;
	}

	public static String join(String delim, double... tokens) {
		return join(delim, tokens, new StringBuilder()).toString();
	}

	// float
	public static StringBuilder join(String delim, float tokens[], StringBuilder r) {
		int length = tokens.length;
		if (length > 0)
			r.append(tokens[0]);
		for (int i = 1; i < length; i++)
			r.append(delim).append(tokens[i]);
		return r;
	}

	public static String join(String delim, float... tokens) {
		return join(delim, tokens, new StringBuilder()).toString();
	}

	// short
	public static StringBuilder join(String delim, short tokens[], StringBuilder r) {
		int length = tokens.length;
		if (length > 0)
			r.append(tokens[0]);
		for (int i = 1; i < length; i++)
			r.append(delim).append(tokens[i]);
		return r;
	}

	public static String join(String delim, short... tokens) {
		return join(delim, tokens, new StringBuilder()).toString();
	}

	public static String join(String delim, boolean... tokens) {
		return join(delim, tokens, new StringBuilder()).toString();
	}
	public static StringBuilder join(String delim, boolean tokens[], StringBuilder r) {
		int length = tokens.length;
		if (length > 0)
			r.append(tokens[0]);
		for (int i = 1; i < length; i++)
			r.append(delim).append(tokens[i]);
		return r;
	}
	public static String join(String delim, char... tokens) {
		return join(delim, tokens, new StringBuilder()).toString();
	}
	public static StringBuilder join(String delim, char tokens[], StringBuilder r) {
		int length = tokens.length;
		if (length > 0)
			r.append(tokens[0]);
		for (int i = 1; i < length; i++)
			r.append(delim).append(tokens[i]);
		return r;
	}

	// Iterable
	public static StringBuilder join(String delim, Iterable<?> tokens, StringBuilder r) {
		Iterator<?> i = tokens.iterator();
		if (i.hasNext())
			r.append(i.next());
		while (i.hasNext())
			r.append(delim).append(i.next());
		return r;
	}

	public static String join(String delim, Iterable<?> tokens) {
		return join(delim, tokens, new StringBuilder()).toString();
	}

	/**
	 * left aligns text by padding with extra chars after the text to meet the required length. If the text is already longer than total size then the string may be optionally
	 * chopped at the end if max length needs to be guaranteed
	 * <P>
	 * 
	 * for example left aligning HELLO with - as the padding and a length of 10 results in:<BR>
	 * <B>HELLO----- </B> <BR>
	 * if the length was 2 and crop is true:<BR>
	 * <B>HE</B><BR>
	 * if the length WAS 2 and crop is false:<BR>
	 * <B>HELLO</B><BR>
	 * 
	 * @param pad
	 *            the char to pad with
	 * @param text
	 *            the text to left align
	 * @param totalSize
	 *            the minimum length. If crop is true the the text will have the end removed so that its length does not exceed total size
	 * @param crop
	 *            if true the length is guaranteed to be exactly totalsize. In other words, if the total size is less than the length of the text then the text is cropped (by
	 *            discarding chars from the end)
	 * @param sb
	 *            the sink
	 * @return returns the sb param for convenience
	 */
	public static StringBuilder leftAlign(char pad, String text, int totalSize, boolean crop, StringBuilder sb) {
		int textLength = text.length();
		if (textLength > totalSize)
			return crop ? sb.append(text.substring(0, totalSize)) : sb.append(text);
		return repeat(pad, totalSize - textLength, sb.append(text));
	}

	/**
	 * see {@link #leftAlign(char, String, int, boolean, StringBuilder)}, but does not take a sink
	 */
	public static String leftAlign(char pad, String text, int totalSize, boolean crop) {
		if (text.length() == totalSize)
			return text;
		return leftAlign(pad, text, totalSize, crop, new StringBuilder()).toString();
	}

	/**
	 * right aligns text by prefixed with extra chars before the text to meet the required length. If the text is already longer than total size then the string may be optionally
	 * chopped at the beginning if max length needs to be guaranteed
	 * <P>
	 * 
	 * for example left aligning HELLO with - as the padding and a length of 10 results in:<BR>
	 * <B>-----HELLO </B> <BR>
	 * if the length was 2 and crop is true:<BR>
	 * <B>LO</B><BR>
	 * if the length WAS 2 and crop is false:<BR>
	 * <B>HELLO</B><BR>
	 * 
	 * @param pad
	 *            the char to pad with
	 * @param text
	 *            the text to right align
	 * @param totalSize
	 *            the minimum length. If crop is true the the text will have the beginning removed so that its length does not exceed total size
	 * @param crop
	 *            if true the length is guaranteed to be exactly totalsize. In other words, if the total size is less than the length of the text then the text is cropped (by
	 *            discarding chars from the beginning)
	 * @param sb
	 *            the sink
	 * @return returns the sb param for convenience
	 */
	public static StringBuilder rightAlign(char pad, String text, int totalSize, boolean crop, StringBuilder sb) {
		int textLength = text.length();
		if (textLength > totalSize)
			return crop ? sb.append(text.substring(textLength - totalSize)) : sb.append(text);
		return repeat(pad, totalSize - textLength, sb).append(text);
	}

	/**
	 * see {@link #rightAlign(char, String, int, boolean, StringBuilder)}, but does not take a sink
	 */
	public static String rightAlign(char pad, String text, int totalSize, boolean crop) {
		if (text.length() == totalSize)
			return text;
		return rightAlign(pad, text, totalSize, crop, new StringBuilder()).toString();
	}

	public static StringBuilder centerAlign(char pad, String text, int totalSize, boolean crop, StringBuilder sb) {
		int textLength = text.length();
		if (textLength > totalSize) {
			if (!crop)
				return sb.append(text);
			int half = (textLength - totalSize) / 2;
			return sb.append(text.substring(half, half + totalSize));
		}
		int half = (totalSize - textLength) / 2;
		repeat(pad, half, sb).append(text);
		return repeat(pad, totalSize - textLength - half, sb);
	}

	public static String centerAlign(char pad, String text, int totalSize, boolean crop) {
		return centerAlign(pad, text, totalSize, crop, new StringBuilder()).toString();
	}

	private static final char[] SPACES = new char[2048];
	static {
		for (int i = 0; i < SPACES.length; i++)
			SPACES[i] = ' ';
	}

	private static final char[] ZEROS = new char[2048];
	static {
		for (int i = 0; i < ZEROS.length; i++)
			ZEROS[i] = '0';
	}

	/**
	 * appends a space ( ) to the string builder a specified number of times.
	 * 
	 * @param repeatitions
	 *            number of spaces to append
	 * @param sb
	 *            sink
	 * @return sink for convenience
	 */
	public static StringBuilder repeatSpaces(int repetitions, StringBuilder sb) {
		if (repetitions < 1) {
			return sb;
		} else {
			while (repetitions > SPACES.length) {
				repetitions -= SPACES.length;
				sb.append(SPACES);
			}

			sb.append(SPACES, 0, repetitions);
		}
		return sb;
	}

	/**
	 * appends a zero (0 ) to the string builder a specified number of times.
	 * 
	 * @param repeatitions
	 *            number of zeros to append
	 * @param sb
	 *            sink
	 * @return sink for convenience
	 */
	public static StringBuilder repeatZeros(int repeatitions, StringBuilder sb) {
		if (repeatitions < 1)
			return sb;
		while (repeatitions > ZEROS.length) {
			repeatitions -= ZEROS.length;
			sb.append(ZEROS);
		}

		sb.append(ZEROS, 0, repeatitions);
		return sb;
	}

	/**
	 * appends a char to the string builder a specified number of times.
	 * 
	 * @param repeatitions
	 *            number of times to append the char
	 * @param sb
	 *            sink
	 * @return sink for convenience
	 */
	public static StringBuilder repeat(char c, int repeatitions, StringBuilder sb) {
		if (c == ' ')
			return repeatSpaces(repeatitions, sb);
		if (c == '0')
			return repeatZeros(repeatitions, sb);
		for (int i = 0; i < repeatitions; i++)
			sb.append(c);
		return sb;
	}

	/**
	 * appends a string to the string builder a specified number of times. Note the length of the string builder will increase by s.length()*repetitions
	 * 
	 * @param repeatitions
	 *            number of times to append the char
	 * @param sb
	 *            sink
	 * @return sink for convenience
	 */
	public static StringBuilder repeat(String s, int repetitions, StringBuilder sb) {
		int length = s.length();
		if (length == 0)
			return sb;
		if (repetitions > 2 && areAll(s, ' '))
			repeatSpaces(repetitions * length, sb);
		else
			for (int i = 0; i < repetitions; i++)
				sb.append(s);
		return sb;
	}

	/**
	 * returns true iff all of the chars in s are c
	 * 
	 * @param s
	 *            the string to inspect
	 * @param c
	 *            the char to compare against each char in the string
	 * @return true iff all of the chars in s are c. if s.length()==0 returns true
	 */
	private static boolean areAll(String s, char c) {
		if (s == null)
			return false;
		for (int i = 0; i < s.length(); i++)
			if (s.charAt(i) != c)
				return false;
		return true;
	}

	/** see {@link #repeat(char, int, StringBuilder)} but without a sink */
	public static String repeat(char c, int repetitions) {
		return repeat(c, repetitions, new StringBuilder(repetitions)).toString();
	}

	/** see {@link #repeat(String, int, StringBuilder)} but without a sink */
	public static String repeat(String s, int repetitions) {
		if (s.length() == 0 || repetitions <= 0)
			return "";
		return repeat(s, repetitions, new StringBuilder(repetitions * s.length())).toString();
	}

	/**
	 * returns a string equaling text but with the first char upper cased. An empty string, or a string whos first char is already uppercase will result in the same string being
	 * returned. see {@link Character#toUpperCase(char)}
	 * 
	 * @param text
	 *            the text to turn into an upper cased representation
	 * @return the upper cased version.
	 */
	public static String uppercaseFirstChar(String text) {
		if (text.length() == 0)
			return text;
		char c = text.charAt(0);
		if (c < 'a' || c > 'z')
			return text;
		return Character.toUpperCase(c) + text.substring(1);
	}

	public static StringBuilder uppercaseFirstChar(String text, StringBuilder sb) {
		if (text.length() == 0)
			return sb.append(text);
		char c = text.charAt(0);
		if (c < 'a' || c > 'z')
			return sb.append(text);
		return sb.append(Character.toUpperCase(c)).append(text, 1, text.length());
	}
	public static StringBuilder uppercaseFirstCharLowerRest(String text, StringBuilder sb) {
		if (text.length() == 0)
			return sb.append(text);
		char c = text.charAt(0);
		sb.append(Character.toUpperCase(c));
		return SH.toLowerCase(text, 1, text.length() - 1, sb);
	}

	/**
	 * returns a string equaling text but with the first char lower cased. An empty string, or a string whos first char is already lowercase will result in the same string being
	 * returned. see {@link Character#toLowerCase(char)}
	 * 
	 * @param text
	 *            the text to turn into an upper cased representation
	 * @return the upper cased version.
	 */
	public static String lowercaseFirstChar(String text) {
		if (text.length() == 0)
			return text;
		char c = text.charAt(0);
		if (c < 'A' || c > 'Z')
			return text;
		return Character.toLowerCase(text.charAt(0)) + text.substring(1);
	}

	/**
	 * Splits a string into a list by splitting based on the delimiter. Elements will be added to the sink in the order they appear in the string. Please note the delim will not be
	 * included in any part in the resulting list
	 * 
	 * @param sink
	 *            list to add elements to
	 * @param delim
	 *            the delim to split on.
	 * @param text
	 *            the text to read
	 * @return the sink for convenience
	 */

	public static List<String> splitToList(List<String> sink, String delim, String text) {
		if (text == null || text.length() == 0)
			return sink;
		int dl = delim.length();
		int j = 0;
		for (int i = 0; (i = text.indexOf(delim, i)) != -1;) {
			if (i == -1)
				break;
			sink.add(text.substring(j, i));
			i += dl;
			j = i;
		}
		sink.add(text.substring(j));
		return sink;
	}
	public static List<String> splitToList(List<String> sink, char delim, String text) {
		if (text == null || text.length() == 0)
			return sink;
		int j = 0;
		for (int i = 0;;) {
			i = text.indexOf(delim, i);
			if (i == -1)
				break;
			sink.add(text.substring(j, i));
			i++;
			j = i;
		}
		sink.add(text.substring(j));
		return sink;
	}

	/**
	 * see {@link #splitToList(List, String, String)} except it uses an empty {@link ArrayList} as the sink
	 */
	public static List<String> splitToList(String delim, String text) {
		return CH.l(split(delim, text));
	}

	public static Set<String> splitToSet(String delim, String text) {
		return CH.s(split(delim, text));
	}

	/**
	 * Splits a string into an array by splitting based on the delimiter. Elements in the array will be in the order they appear in the string. Please note the delim will not be
	 * included in any part in the resulting array
	 * 
	 * @param delim
	 *            the delim to split on.
	 * @param text
	 *            the text to read
	 * @return the array of elements.
	 */
	public static String[] split(String delim, String text) {
		if (text == null || text.length() == 0)
			return OH.EMPTY_STRING_ARRAY;
		int dl = delim.length(), c = 1;
		if (dl == 0) {
			String[] r = new String[text.length()];
			for (int i = 0; i < r.length; i++)
				r[i] = toString(text.charAt(i));
			return r;
		}
		for (int i = 0; (i = text.indexOf(delim, i)) != -1; c++)
			i += dl;
		String[] r = new String[c];
		c = 0;
		int j = 0;
		for (int i = 0; (i = text.indexOf(delim, i)) != -1; c++) {
			r[c] = text.substring(j, i);
			i += dl;
			j = i;
		}
		r[c] = text.substring(j);
		return r;
	}
	public static String[] splitIgnoreCase(String delim, String text) {
		if (text == null || text.length() == 0)
			return OH.EMPTY_STRING_ARRAY;
		int dl = delim.length(), c = 1;
		if (dl == 0) {
			String[] r = new String[text.length()];
			for (int i = 0; i < r.length; i++)
				r[i] = toString(text.charAt(i));
			return r;
		}
		for (int i = 0; (i = indexOfIgnoreCase(text, delim, i)) != -1; c++)
			i += dl;
		String[] r = new String[c];
		c = 0;
		int j = 0;
		for (int i = 0; (i = indexOfIgnoreCase(text, delim, i)) != -1; c++) {
			r[c] = text.substring(j, i);
			i += dl;
			j = i;
		}
		r[c] = text.substring(j);
		return r;
	}
	public static CharSequence[] split(String delim, CharSequence text) {
		if (text == null || text.length() == 0)
			return OH.EMPTY_STRING_ARRAY;
		int dl = delim.length(), c = 1;
		if (dl == 0) {
			String[] r = new String[text.length()];
			for (int i = 0; i < r.length; i++)
				r[i] = toString(text.charAt(i));
			return r;
		}
		for (int i = 0; (i = indexOf(text, delim, i)) != -1; c++)
			i += dl;
		CharSequence[] r = new CharSequence[c];
		c = 0;
		int j = 0;
		for (int i = 0; (i = indexOf(text, delim, i)) != -1; c++) {
			r[c] = text.subSequence(j, i);
			i += dl;
			j = i;
		}
		r[c] = text.subSequence(j, text.length());
		return r;
	}

	/**
	 * Splits a string into an array by splitting based on the delimiter. Elements in the array will be in the order they appear in the string. Please note the delim will not be
	 * included in any part in the resulting array
	 * 
	 * @param delim
	 *            the delim to split on.
	 * @param text
	 *            the text to read
	 * @return the array of elements.
	 */
	public static String[] split(char delim, String text) {
		if (text == null || text.length() == 0)
			return OH.EMPTY_STRING_ARRAY;
		int c = 1;
		for (int i = 0; (i = text.indexOf(delim, i)) != -1; c++)
			i++;
		String[] r = new String[c];
		c = 0;
		int j = 0;
		for (int i = 0; c < r.length - 1; c++) {
			i = text.indexOf(delim, i);
			r[c] = text.substring(j, i);
			i++;
			j = i;
		}
		r[c] = text.substring(j);
		return r;
	}
	public static String[] split(char delim, CharSequence text) {
		if (text == null || text.length() == 0)
			return OH.EMPTY_STRING_ARRAY;
		int c = 1;
		for (int i = 0; (i = indexOf(text, delim, i)) != -1; c++)
			i++;
		String[] r = new String[c];
		c = 0;
		int j = 0;
		for (int i = 0; c < r.length - 1; c++) {
			i = indexOf(text, delim, i);
			r[c] = substring(text, j, i);
			i++;
			j = i;
		}
		r[c] = substring(text, j, text.length());
		return r;
	}
	public static String[] splitWithEscape(char delim, char escape, String text) {
		return splitWithEscape(delim, escape, text, false);
	}
	public static String[] splitWithEscape(char delim, char escape, String text, boolean leaveEscape) {
		if (text == null || text.length() == 0)
			return OH.EMPTY_STRING_ARRAY;
		int c = 1;
		boolean hasEscape = false;
		for (int i = 0; i < text.length(); i++) {
			char ch = text.charAt(i);
			if (ch == escape) {
				hasEscape = true;
				i++;
			} else if (ch == delim)
				c++;
		}
		if (hasEscape) {
			StringBuilder buf = new StringBuilder();
			String[] r = new String[c];
			c = 0;
			for (int i = 0; i < text.length(); i++) {
				char ch = text.charAt(i);
				if (ch == escape) {
					i++;
					if (leaveEscape)
						buf.append(escape);
					buf.append(text.charAt(i));
				} else if (ch == delim)
					r[c++] = toStringAndClear(buf);
				else
					buf.append(ch);
			}
			r[c] = buf.toString();
			return r;
		} else if (c == 1) {
			return new String[] { text };
		} else {
			String[] r = new String[c];
			c = 0;
			int j = 0;
			for (int i = 0; (i = text.indexOf(delim, i)) != -1; c++) {
				r[c] = text.substring(j, i);
				i++;
				j = i;
			}
			r[c] = text.substring(j);
			return r;
		}
	}

	/**
	 * Splits a string into an array by splitting based on the delimiter, treating consecutive delimiters as a single delim. Elements in the array will be in the order they appear
	 * in the string. Please note the delim will not be included in any part in the resulting array
	 * 
	 * @param delim
	 *            the delim to split on.
	 * @param text
	 *            the text to read
	 * @return the array of elements.
	 */
	public static String[] splitContinous(char delim, String text) {
		if (text == null || text.length() == 0)
			return OH.EMPTY_STRING_ARRAY;
		int c = 1;
		for (int i = 0; (i = text.indexOf(delim, i)) != -1; c++) {
			i = indexOfNot(text, i + 1, delim);
			if (i == -1) {
				c++;
				break;
			}
		}
		String[] r = new String[c];
		c = 0;
		int j = 0;
		for (int i = 0; (i = text.indexOf(delim, i)) != -1; c++) {
			r[c] = text.substring(j, i);
			i = indexOfNot(text, i + 1, delim);
			if (i == -1) {
				j = text.length();
				c++;
				break;
			}
			j = i;
		}
		r[c] = text.substring(j);
		return r;
	}

	/**
	 * returns true if o is not null && o's toString has non-white space chars
	 * 
	 * @param o
	 *            the object to inspect
	 * @return false iff o is null or only whitespace or zero length
	 */
	public static boolean is(Object o) {
		return o != null && is(toCharSequence(o));
	}

	public static CharSequence toCharSequence(Object o) {
		return (o instanceof CharSequence || o == null) ? (CharSequence) o : s(o);
	}

	/**
	 * returns false iff o is null or o.toString() is only whitespace
	 * 
	 * @param o
	 *            the object to inspect
	 * @return true if o is not null && o's toString has non-white space chars
	 */
	public static boolean isnt(Object o) {
		return o == null || !is(toCharSequence(o));
	}

	/**
	 * returns true if s is not null && has non-white space chars
	 * 
	 * @param s
	 *            string to inspect
	 * @return false iff o is null or only whitespace or zero length
	 */
	public static boolean is(CharSequence s) {
		if (s == null)
			return false;
		for (int i = 0, l = s.length(); i < l; i++)
			if (s.charAt(i) > 0x20)
				return true;
		return false;
	}
	public static boolean is(char o) {
		return o > 0x20;
	}
	public static boolean isnt(char o) {
		return o <= 0x20;
	}
	/**
	 * returns true if s is not null && has non-white space chars
	 * 
	 * @param s
	 *            string to inspect
	 * @return false iff o is null or only whitespace or zero length
	 */
	public static boolean is(String s) {
		if (s == null)
			return false;
		for (int i = 0, l = s.length(); i < l; i++)
			if (s.charAt(i) > 0x20)
				return true;
		return false;
	}

	public static String trimWhitespace(String text) {
		if (text == null || text.length() == 0)
			return text;
		int start = 0, end = text.length() - 1;
		while (text.charAt(start) <= 0x20)
			if (start++ == end)
				return "";
		while (text.charAt(end) <= 0x20)
			end--;
		return text.substring(start, end + 1);
	}
	public static String trimTrailingWhitespace(String text) {
		if (text == null || text.length() == 0)
			return text;
		int end = text.length() - 1;
		while (text.charAt(end) <= 0x20)
			if (--end == -1)
				return "";
		return text.substring(0, end + 1);
	}

	/**
	 * returns false iff o is null or only whitespace
	 * 
	 * @param s
	 *            the string to inspect
	 * @return true if o is not null && o has non-white space chars
	 */
	public static boolean isnt(CharSequence s) {
		return !is(s);
	}
	public static boolean isnt(String s) {
		return !is(s);
	}

	/**
	 * converts a size (in bytes) to a human readable estimate. for example 8704 will result in: 8.50 KB
	 * 
	 * @param size
	 *            the number of bytes
	 * @return human readable representation
	 */
	public static String formatMemory(long size) {
		if (size < 0)
			return SH.toString(size);
		NumberFormat nf;
		if (size < MH.KILOBYTES * 2) {
			nf = new DecimalFormat("#,##0");
			return nf.format(size) + " B";
		} else
			nf = new DecimalFormat("#,##0.00");
		if (size < MH.MEGABYTES * 2)
			return nf.format((double) size / MH.KILOBYTES) + " KB";
		if (size < MH.GIGABYTES * 2)
			return nf.format((double) size / MH.MEGABYTES) + " MB";
		if (size < MH.TERABYTES * 2)
			return nf.format((double) size / MH.GIGABYTES) + " GB";
		return nf.format((double) size / MH.TERABYTES) + " TB";
	}

	public static long parseMemoryToBytes(String memory) {
		try {
			memory = SH.trim(memory);
			final long multiple;
			if (endsWithIgnoreCase(memory, "KB")) {
				multiple = MH.KILOBYTES;
			} else if (endsWithIgnoreCase(memory, "MB")) {
				multiple = MH.MEGABYTES;
			} else if (endsWithIgnoreCase(memory, "GB")) {
				multiple = MH.GIGABYTES;
			} else if (endsWithIgnoreCase(memory, "TB")) {
				multiple = MH.TERABYTES;
			} else
				return parseLong(trim(memory));
			return multiple * parseLong(trim(memory.substring(0, memory.length() - 2)));
		} catch (Exception e) {
			throw new RuntimeException("Expecting nnn, nnnKB, nnnMB, nnnGB or nnnTB. Not: '" + memory + "'", e);
		}
	}

	/**
	 * ensures the text is not null, if it is will be converted to an empty string.
	 * 
	 * @param text
	 *            the text to inspect for null
	 * @return text or empty string ("") if null. wont return null
	 */
	public static String noNull(String text) {
		return text == null ? "" : text;
	}

	public static String noNull(Object text) {
		return text == null ? "" : s(text);
	}

	/**
	 * A comparator for inspecting the string representation of objects. for example comparing the to integers 4 and 10 would result it 4 having a higher value then 10. Note that
	 * nulls are handled per {@link OH#compare(Comparable, Comparable)}
	 */
	public final static Comparator<Object> COMPARATOR = new Comparator<Object>() {

		@Override
		public int compare(Object o1, Object o2) {
			return OH.compare(OH.toString(o1), OH.toString(o2));
		}

	};

	/**
	 * compares the string representation of two objects, but in a case insensitive manner. This differs from the {@link String#CASE_INSENSITIVE_ORDER} in that two strings who are
	 * equal when ignore case are the compared in a case sensitive manner. This is a user friendly way of ordering elements, but preventing the collision of strings who are not
	 * exactly equal. <BR>
	 * For example, the following strings would be sorted, but none would be considered duplicates:
	 * 
	 * <B>a<BR>
	 * E<BR>
	 * e<BR>
	 * f<BR>
	 * F<BR>
	 * G<BR>
	 * </B><BR>
	 * (note the f and F do not collide)
	 * 
	 */
	public final static Comparator<Object> COMPARATOR_CASEINSENSITIVE = new Comparator<Object>() {

		@Override
		public int compare(Object o1, Object o2) {
			String s1 = OH.toString(o1);
			String s2 = OH.toString(o2);
			int r = OH.compare(s1, s2, true, String.CASE_INSENSITIVE_ORDER);
			if (r != 0)
				return r;
			return OH.compare(s1, s2, true);
		}

	};
	public final static Comparator<String> COMPARATOR_CASEINSENSITIVE_STRING = new Comparator<String>() {

		@Override
		public int compare(String s1, String s2) {
			int r = OH.compare(s1, s2, true, String.CASE_INSENSITIVE_ORDER);
			if (r != 0)
				return r;
			return OH.compare(s1, s2, true);
		}

	};
	public final static Comparator<String> COMPARATOR_CASEINSENSITIVE_STRING_REVERSE = new Comparator<String>() {

		@Override
		public int compare(String s1, String s2) {
			int r = OH.compare(s2, s1, true, String.CASE_INSENSITIVE_ORDER);
			if (r != 0)
				return r;
			return OH.compare(s2, s1, true);
		}

	};

	/**
	 * sorts a array of objects using the {@link #COMPARATOR}
	 * 
	 * @param objects
	 * @return the objects param for convenience
	 */
	public static <T> T[] sort(T[] objects) {
		return sort(objects, (Comparator<T>) COMPARATOR);
	}

	public static <T> T[] sort(T[] objects, Comparator<T> comparator) {
		Arrays.sort(objects, comparator);
		return objects;
	}

	/**
	 * returns the remaining text after the last instance of delim. For example:
	 * <P>
	 * 
	 * <B>afterLast("test/this/out","/")</B><BR>
	 * returns <B>"out"</B>
	 * <P>
	 * <B>afterLast("test this out","/")</B><BR>
	 * returns <B>"test this out"</B>
	 * <P>
	 * 
	 * @param str
	 *            the string to inspect for delim and return a portion of
	 * @param delim
	 *            the delim to look for (starting at the end)
	 * @return text after last occurance of delim, or str if delim not found
	 */
	public static String afterLast(String str, String delim) {
		return afterLast(str, delim, str);
	}

	/**
	 * returns the remaining text after the last instance of delim. For example:
	 * <P>
	 * 
	 * <B>afterLast("test/this/out","/","default")</B><BR>
	 * returns <B>"out"</B>
	 * <P>
	 * <B>afterLast("test this out","/","default")</B><BR>
	 * returns <B>"default"</B>
	 * <P>
	 * 
	 * @param str
	 *            the string to inspect for delim and return a portion of
	 * @param delim
	 *            the delim to look for (starting at the end)
	 * @param dflt
	 *            the value to return if the delim not found
	 * @return text after last occurance of delim, or dflt if delim not found
	 */
	public static String afterLast(String str, String delim, String dflt) {
		if (str == null)
			return dflt;
		int index = str.lastIndexOf(delim);
		return index == -1 ? dflt : str.substring(index + delim.length());
	}

	/**
	 * returns all the text before the last instance of delim. For example:
	 * <P>
	 * 
	 * <B>beforeLast("test/this/out","/")</B><BR>
	 * returns <B>"test/this"</B>
	 * <P>
	 * <B>beforeLast("test this out","/")</B><BR>
	 * returns <B>"test this out"</B>
	 * <P>
	 * 
	 * @param str
	 *            the string to inspect for delim and return a portion of
	 * @param delim
	 *            the delim to look for (starting at the end)
	 * @return text before last occurance of delim, or str if delim not found
	 */
	public static String beforeLast(String str, String delim) {
		return beforeLast(str, delim, str);
	}

	/**
	 * returns all the text before the last instance of delim. For example:
	 * <P>
	 * 
	 * <B>beforeLast("test/this/out","/","default")</B><BR>
	 * returns <B>"test/this/out"</B>
	 * <P>
	 * <B>beforeLast("test this out","/","default")</B><BR>
	 * returns <B>"default"</B>
	 * <P>
	 * 
	 * @param str
	 *            the string to inspect for delim and return a portion of
	 * @param delim
	 *            the delim to look for (starting at the end)
	 * @param dflt
	 *            the value to return if the delim not found
	 * @return text before last occurance of delim, or dflt if delim not found
	 */
	public static String beforeLast(String str, String delim, String dflt) {
		if (str == null)
			return dflt;
		int index = str.lastIndexOf(delim);
		return index == -1 ? dflt : str.substring(0, index);
	}

	/**
	 * returns the remaining text after the first instance of delim. For example:
	 * <P>
	 * 
	 * <B>afterFirst("test/this/out","/")</B><BR>
	 * returns <B>"this/out"</B>
	 * <P>
	 * <B>afterFirst("test this out","/")</B><BR>
	 * returns <B>"test this out"</B>
	 * <P>
	 * 
	 * @param str
	 *            the string to inspect for delim and return a portion of
	 * @param delim
	 *            the delim to look for (starting at the beginning)
	 * @return text after first occurance of delim, or str if delim not found
	 */
	public static String afterFirst(String str, String delim) {
		return afterFirst(str, delim, str);
	}

	/**
	 * returns the remaining text after the first instance of delim. For example:
	 * <P>
	 * 
	 * <B>afterFirst("test/this/out","/","default")</B><BR>
	 * returns <B>"this/out"</B>
	 * <P>
	 * <B>afterFirst("test this out","/","default")</B><BR>
	 * returns <B>"default"</B>
	 * <P>
	 * 
	 * @param str
	 *            the string to inspect for delim and return a portion of
	 * @param delim
	 *            the delim to look for (starting at the beginning)
	 * @param dflt
	 *            the value to return if the delim not found
	 * @return text after first occurance of delim, or dflt if delim not found
	 */
	public static String afterFirst(String str, String delim, String dflt) {
		if (str == null)
			return dflt;
		int index = str.indexOf(delim);
		return index == -1 ? dflt : str.substring(index + delim.length());
	}

	/**
	 * returns all the text before the first instance of delim. For example:
	 * <P>
	 * 
	 * <B>beforeFirst("test/this/out","/")</B><BR>
	 * returns <B>"test"</B>
	 * <P>
	 * <B>beforeFirst("test this out","/")</B><BR>
	 * returns <B>"test this out"</B>
	 * <P>
	 * 
	 * @param str
	 *            the string to inspect for delim and return a portion of
	 * @param delim
	 *            the delim to look for (starting at the beginning)
	 * @return text before first occurance of delim, or str if delim not found
	 */
	public static String beforeFirst(String str, String delim) {
		return beforeFirst(str, delim, str);
	}

	/**
	 * returns all the text before the last instance of delim. For example:
	 * <P>
	 * 
	 * <B>beforeFirst("test/this/out","/","default")</B><BR>
	 * returns <B>"test"</B>
	 * <P>
	 * <B>beforeFirst("test this out","/","default")</B><BR>
	 * returns <B>"default"</B>
	 * <P>
	 * 
	 * @param str
	 *            the string to inspect for delim and return a portion of
	 * @param delim
	 *            the delim to look for (starting at the beginning)
	 * @param dflt
	 *            the value to return if the delim not found
	 * @return text before first occurance of delim, or dflt if delim not found
	 */
	public static String beforeFirst(String str, String delim, String dflt) {
		if (str == null)
			return dflt;
		int index = str.indexOf(delim);
		return index == -1 ? dflt : str.substring(0, index);
	}

	/**
	 * see {@link #afterLast(String, String)} except works with single char delim for performance
	 */
	public static String afterLast(String str, char delim) {
		return afterLast(str, delim, str);
	}

	/**
	 * see {@link #afterLast(String, char,String)} except works with single char delim for performance
	 */
	public static String afterLast(String str, char delim, String dflt) {
		if (str == null)
			return dflt;
		int index = str.lastIndexOf(delim);
		return index == -1 ? dflt : str.substring(index + 1);
	}
	/**
	 * see {@link #afterLast(String, char,String)} except works with single char delim for performance
	 */
	public static StringBuilder afterLast(String str, char delim, String dflt, StringBuilder sink) {
		if (str == null)
			return sink.append(dflt);
		int index = str.lastIndexOf(delim);
		return index == -1 ? sink.append(dflt) : sink.append(str, index + 1, str.length());
	}

	/**
	 * see {@link #beforeLast(String, String)} except works with single char delim for performance
	 */
	public static String beforeLast(String str, char delim) {
		return beforeLast(str, delim, str);
	}

	/**
	 * see {@link #beforeLast(String, char,String)} except works with single char delim for performance
	 */
	public static String beforeLast(String str, char delim, String dflt) {
		if (str == null)
			return dflt;
		int index = str.lastIndexOf(delim);
		return index == -1 ? dflt : str.substring(0, index);
	}
	public static StringBuilder beforeLast(String str, char delim, String dflt, StringBuilder sink) {
		if (str == null)
			return sink.append(dflt);
		int index = str.lastIndexOf(delim);
		return index == -1 ? sink.append(dflt) : sink.append(str, 0, index);
	}

	/**
	 * see {@link #afterFirst(String, String)} except works with single char delim for performance
	 */
	public static String afterFirst(String str, char delim) {
		return afterFirst(str, delim, str);
	}

	/**
	 * see {@link #afterFirst(String, char,String)} except works with single char delim for performance
	 */
	public static String afterFirst(String str, char delim, String dflt) {
		if (str == null)
			return dflt;
		int index = str.indexOf(delim);
		return index == -1 ? dflt : str.substring(index + 1);
	}

	/**
	 * see {@link #beforeFirst(String, String)} except works with single char delim for performance. If the delim doesn't exist in the string, the entire string is returned
	 */
	public static String beforeFirst(String str, char delim) {
		return beforeFirst(str, delim, str);
	}

	/**
	 * see {@link #beforeFirst(String, char,String)} except works with single char delim for performance
	 */
	public static String beforeFirst(String str, char delim, String dflt) {
		if (str == null)
			return dflt;
		int index = str.indexOf(delim);
		return index == -1 ? dflt : str.substring(0, index);
	}

	public static long parseLong(CharSequence chars, int radix) {
		return parseLong(chars, 0, chars.length(), radix);
	}
	public static long parseLong(CharSequence chars, int start, int end, int radix) {
		int pos = start;
		if (end <= pos)
			throw new NumberFormatException("start <= end: " + start + " <= " + end);
		char c = chars.charAt(pos);
		boolean negative = c == '-';
		if (negative)
			c = chars.charAt(++pos);
		else if (c == '+')
			c = chars.charAt(++pos);
		long r = 0;
		int[] c2d = radix > 36 ? CHARS_TO_DIGIT : CHARS_TO_DIGIT_NOCASE;
		for (;;) {
			int digit = c2d[c];
			if (digit < 0 || digit >= radix)
				throw new NumberFormatException("bad char '" + chars.charAt(pos) + "' for radix " + radix + ":" + chars.subSequence(start, end).toString());

			r = r * radix + digit;
			if (r < 0)
				if (r == Long.MIN_VALUE && negative && pos + 1 == end)
					return r;
				else
					throw new NumberFormatException("overflow: " + chars.subSequence(start, end));
			else if (++pos == end)
				return negative ? -r : r;
			c = chars.charAt(pos);
		}
	}
	public static int parseInt(CharSequence chars) {
		return parseInt(chars, 0, chars.length(), 10);
	}
	public static int parseInt(CharSequence chars, int radix) {
		return parseInt(chars, 0, chars.length(), radix);
	}
	public static int parseInt(CharSequence chars, int start, int end, int radix) {
		int pos = start;
		if (end <= pos)
			throw new NumberFormatException("start <= end: " + start + " <= " + end);
		char c = chars.charAt(pos);
		boolean negative = c == '-';
		if (negative)
			chars.charAt(++pos);
		else if (c == '+')
			chars.charAt(++pos);
		int r = 0;
		int[] c2d = radix > 36 ? CHARS_TO_DIGIT : CHARS_TO_DIGIT_NOCASE;
		int limit = Integer.MIN_VALUE / -radix;
		if (end - pos < 10 && radix <= 10) {//can't overflow,TODO: make array for all lengths
			for (;;) {
				int digit = c2d[chars.charAt(pos)];
				if (digit < 0 || digit >= radix)
					throw new NumberFormatException("bad char '" + chars.charAt(pos) + "' for radix " + radix + ":" + chars.subSequence(start, end).toString());
				r = r * radix + digit;
				if (++pos == end)
					return negative ? -r : r;
			}
		} else {
			for (;;) {
				int digit = c2d[chars.charAt(pos)];
				if (digit < 0 || digit >= radix)
					throw new NumberFormatException("bad char '" + chars.charAt(pos) + "' for radix " + radix + ":" + chars.subSequence(start, end).toString());
				int old = r;
				if (r > limit)
					throw new NumberFormatException("overflow: " + chars.subSequence(start, end));
				r = r * radix + digit;
				if (r == Integer.MIN_VALUE && negative && pos + 1 == end)
					return r;
				if (r < old)
					throw new NumberFormatException("overflow: " + chars.subSequence(start, end));

				if (++pos == end)
					return negative ? -r : r;
			}
		}
	}

	public static final String CAUSED_BY = "caused by ";
	public static final String AT = "at ";
	public static final String WINDOWS_NEWLINE = "\r\n";
	private static final String NULL = "" + null;
	private static final String TRUE = String.valueOf(true);
	private static final String FALSE = String.valueOf(false);

	/**
	 * see {@link #printStackTrace(String, String, Throwable, Appendable)} but does not require a sink and defaults prefix and indent to reasonable defaults
	 */
	public static String printStackTrace(Throwable exception) {
		return printStackTrace("", "  ", exception, new StringBuilder()).toString();
	}
	public static String printStackTrace(String prefix, final String indent, Throwable exception) {
		return printStackTrace(prefix, indent, exception, new StringBuilder(500)).toString();
	}

	/**
	 * formats the exception into a legible stack trace. Please note {@link ConvertedException}s are also given special treatment.
	 * 
	 * @param prefix
	 *            text to prefix before every line
	 * @param indent
	 *            text to indent before each method call
	 * @param exception
	 *            the exception to inspect
	 * @param sb
	 *            the sink
	 * @return the sb param for convenience
	 */
	public static Appendable printStackTrace(final String prefix, final String indent, Throwable exception, final Appendable sb) {
		String hostName = null;
		try {
			String prefixWithIndent = null;
			while (exception != null) {
				String exceptionClassName;
				if (exception instanceof ConvertedException) {
					ConvertedException ce = (ConvertedException) exception;
					hostName = "[" + ce.getOriginatingHostName() + "@@" + ce.getPid() + "] ";
					sb.append("remote exception at ").append(hostName).append(" ");
					exceptionClassName = ce.getExceptionClassName();
				} else
					exceptionClassName = exception.getClass().getName();
				if (exception instanceof Legible) {
					Legible l = (Legible) exception;
					String text = l.toLegibleString();
					if (SH.is(text)) {
						StringBuilder sb2 = new StringBuilder(text.length() + 200);
						sb2.append(prefix).append(indent);
						centerAlign('*', "[ " + SH.afterLast(exceptionClassName, '.', exceptionClassName) + " ]", 50, false, sb2);
						sb2.append(SH.NEWLINE);
						if (prefixWithIndent == null)
							prefixWithIndent = prefix + indent + "*";
						SH.prefixLines(text, prefixWithIndent, true, sb2);
						sb2.append(prefix).append(indent);
						repeat('*', 50, sb2);
						sb2.append(SH.NEWLINE);
						sb.append(SH.NEWLINE).append(sb2);
					} else {
						sb.append(exceptionClassName);
						sb.append(NEWLINE);
					}
				} else {
					sb.append(exceptionClassName);
					if (exception.getMessage() != null)
						sb.append(':').append(exception.getMessage());
					sb.append(NEWLINE);
				}
				printStackTraceElements(exception.getStackTrace(), prefix, indent, sb, hostName);
				exception = exception.getCause();
				if (exception != null)
					sb.append(prefix).append(CAUSED_BY);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return sb;
	}

	public static Appendable printStackTraceElements(StackTraceElement[] elements, final String prefix, final String indent, final Appendable sb, String hostName)
			throws IOException {
		if (elements != null) {
			for (StackTraceElement element : elements) {
				if (element.getLineNumber() == 1)
					continue;//skip printing casts.
				sb.append(prefix).append(indent);
				if (hostName != null)
					sb.append(hostName);
				sb.append(AT);
				sb.append(element.getClassName());
				sb.append('.');
				sb.append(element.getMethodName());
				sb.append('(');
				String fn = element.getFileName();
				if (fn != null) {
					sb.append(element.getFileName());
					sb.append(':');
				}
				sb.append(toString(element.getLineNumber()));
				sb.append(')');
				sb.append(NEWLINE);
			}
		}
		return sb;
	}

	/**
	 * wraps the text with a char
	 * 
	 * @param c
	 *            the char to prepend and append after the text(o)
	 * @param text
	 *            the text
	 * @param sb
	 *            sink
	 * @return the sink param for convenience
	 */
	public static StringBuilder quote(char c, CharSequence text, StringBuilder sb) {
		return SH.escape(text, c, '\\', sb.append(c)).append(c);
	}

	/**
	 * wraps the text with a char
	 * 
	 * @param c
	 *            the char to prepend and append after the text(o)
	 * @param text
	 *            the text
	 * @param sb
	 *            sink
	 * @return the sink param for convenience
	 */
	@Deprecated
	public static StringBuildable quote(char c, String text, StringBuildable sb) {
		return sb.append(c).append(text).append(c);
	}

	/**
	 * see {@link #joinMap(char, char, Map, StringBuilder)} except not sink, simply return a string representation
	 */
	public static String joinMap(char delim, char associator, Map<?, ?> map) {
		return joinMap(delim, associator, map, new StringBuilder()).toString();
	}

	public static String joinMap(String delim, String associator, Map<?, ?> map) {
		return joinMap(delim, associator, map, new StringBuilder()).toString();
	}

	public static String joinMap(char delim, char associator, char escape, Map<?, ?> map) {
		return joinMap(delim, associator, escape, map, new StringBuilder()).toString();
	}

	/**
	 * joins the elements of the map into a string. Ordered per {@link Map#entrySet()}
	 * 
	 * @param <K>
	 *            the type of keys in the map
	 * @param <V>
	 *            the type of values in the map
	 * @param delim
	 *            joins the map entries
	 * @param associator
	 *            joins the key with the value
	 * @param map
	 *            the map to loop over.
	 * @param sb
	 *            the sink to append to
	 */
	public static StringBuilder joinMap(char delim, char associator, Map<?, ?> map, StringBuilder sb) {
		boolean first = true;
		for (Map.Entry<?, ?> e : map.entrySet()) {
			if (first) {
				first = false;
			} else
				sb.append(delim);
			s(e.getValue(), s(e.getKey(), sb).append(associator));
		}
		return sb;
	}

	public static StringBuilder joinMap(String delim, String associator, Map<?, ?> map, StringBuilder sb) {
		boolean first = true;
		for (Map.Entry<?, ?> e : map.entrySet()) {
			if (first) {
				first = false;
			} else
				sb.append(delim);
			s(e.getValue(), s(e.getKey(), sb).append(associator));
		}
		return sb;
	}
	public static StringBuilder joinMap(char delim, char associator, char escape, Map<?, ?> map, StringBuilder sb) {
		boolean first = true;
		for (Map.Entry<?, ?> e : map.entrySet()) {
			if (first) {
				first = false;
			} else {
				sb.append(delim);
			}

			int start = sb.length();
			s(e.getKey(), sb);
			escapeInplace(sb, start, sb.length(), delim, associator, escape);
			sb.append(associator);
			start = sb.length();
			s(e.getValue(), sb);
			escapeInplace(sb, start, sb.length(), delim, associator, escape);
		}
		return sb;
	}

	/*
	 * SH.p is an alias for System.out.print with AMI formatting of objects
	 */
	public static void p(Object o) {
		//		System.out.print(SH.s(o));
		StringBuilder sb = new StringBuilder();
		sb.append(SH.NEWLINE);
		sb.append(SH.s(o));
		System.out.println(sb.toString());
	}

	/*
	 * SH.pl is an alias for System.out.println with AMI formatting of objects
	 */
	public static void pl(Object o) {
		//		System.out.println(SH.s(o));
		StringBuilder sb = new StringBuilder();
		sb.append(SH.NEWLINE);
		sb.append(SH.s(o));
		System.out.println(sb.toString());
	}

	/*
	 * SH.p is an alias for System.out.print with AMI formatting of objects
	 */
	public static void p(Object... o) {
		//		for (Object i : o)
		//			System.out.print(SH.s(i));
		StringBuilder sb = new StringBuilder();
		sb.append(SH.NEWLINE);
		for (Object i : o)
			sb.append(SH.s(i));
		System.out.println(sb.toString());

	}

	/*
	 * SH.pl is an alias for System.out.println with AMI formatting of objects
	 */
	public static void pl(Object... o) {
		//		for (Object i : o)
		//			System.out.println(SH.s(i));
		StringBuilder sb = new StringBuilder();
		sb.append(SH.NEWLINE);
		for (Object i : o)
			sb.append(SH.s(i)).append(SH.NEWLINE);
		System.out.println(sb.toString());
	}

	/**
	 * SH.s creates a new {@link StringBuilder} and appends the string representation of o to it. see {@link #s(Object, StringBuilder))} for details
	 * 
	 * @param o
	 *            the value to convert to a string.
	 * @return the newly created StringBuilder
	 */
	public static String s(Object o) {
		if (o == null)
			return "null";
		else if (o instanceof CharSequence)
			return o.toString();
		else if (o instanceof ToStringable || o instanceof Map || o instanceof Collection)
			return s(o, new StringBuilder()).toString();
		if (o instanceof Number) {
			if (o instanceof Integer)
				return toString((int) (Integer) o);
			else if (o instanceof Long)
				return toString((long) (Long) o);
			else if (o instanceof Short)
				return toString((short) (Short) o);
			else if (o instanceof Byte)
				return toString((byte) (Byte) o);
			else if (o instanceof Double)
				return toString((double) (Double) o);
			else if (o instanceof Float)
				return toString((float) (Float) o);
		} else if (o instanceof Character)
			return toString((char) (Character) o);
		else if (o.getClass().isArray())
			return o.getClass().getComponentType().getSimpleName() + "[" + Array.getLength(o) + "]";
		return o.toString();
	}
	public static String toString(ToStringable ts) {
		return ts == null ? null : ts.toString(new StringBuilder()).toString();
	}

	/**
	 * SH.s takes the string value of o and appends it to the string builder.<BR>
	 * This method is intended to minimize object creation by working in conjunction with the {@link ToStringable} interface. If o an instance of ToStringable, the the sb is passed
	 * into the {@link ToStringable#toString(StringBuilder))} method. Collections and maps are also looped over interallay checking to see if keys and values are ToStringable. All
	 * of this can greatly reduce temporary object creation
	 * 
	 * @param o
	 *            the string to append, if null then "null" is appended
	 * @param sb
	 *            the sink
	 * @return the sb param for convenience
	 */
	public static StringBuilder s(Object o, StringBuilder sb, String nullValue) {
		if (o == null)
			return sb.append(nullValue);
		if (o instanceof ToStringable)
			return ((ToStringable) o).toString(sb);
		if (o instanceof CharSequence)
			return sb.append((CharSequence) o);
		Class clazz = o.getClass();
		if (o instanceof Number)// these avoid object creation also
		{
			if (clazz == Integer.class) {
				int i = ((Integer) o).intValue();
				if (OH.isBetween(i, -MAX_CACHED_INTS_SIZE, MAX_CACHED_INTS_SIZE))
					sb.append(getCachedStringForInt(i));
				else
					sb.append(i);
			} else if (clazz == Long.class) {
				long i = ((Long) o).longValue();
				if (OH.isBetween(i, -MAX_CACHED_INTS_SIZE, MAX_CACHED_INTS_SIZE))
					sb.append(getCachedStringForInt((int) i));
				else
					sb.append(i);
			} else if (clazz == Byte.class) {
				byte i = ((Byte) o).byteValue();
				sb.append(getCachedStringForInt((int) i));
			} else if (clazz == Short.class) {
				short i = ((Short) o).shortValue();
				if (OH.isBetween(i, -MAX_CACHED_INTS_SIZE, MAX_CACHED_INTS_SIZE))
					sb.append(getCachedStringForInt((int) i));
				else
					sb.append(i);
			} else if (clazz == Float.class) {
				float i = ((Float) o).floatValue();
				sb.append(i);
			} else if (clazz == Double.class) {
				double i = ((Double) o).doubleValue();
				sb.append(i);
			} else
				sb.append(o);
		} else if (o instanceof Collection) {
			sb.append('[');
			join(',', (Collection<?>) o, sb);
			sb.append(']');
		} else if (o instanceof Map) {
			sb.append('{');
			joinMap(',', '=', (Map<?, ?>) o, sb);
			sb.append('}');
		} else if (clazz == Character.class) {
			return sb.append(((Character) o).charValue());
		} else if (clazz.isArray()) {
			sb.append(clazz.getComponentType().getSimpleName()).append('[').append(Array.getLength(o)).append(']');
		} else
			sb.append(o);
		return sb;
	}
	public static StringBuilder s(Object o, StringBuilder sb) {
		return s(o, sb, "null");
	}
	/**
	 * convenience method for creating a path from combining a parent and child seperated by a delimiter. This method will only apply the delimiter if the parent and child are not
	 * blank. this prevents creating absolute directories when a parent is not supplied
	 * 
	 * For example:<BR>
	 * <B>path('/',"here","there");</B><br>
	 * returns <BR>
	 * <B> here/there</B>
	 * <P>
	 * 
	 * <B>path('/',"","there");</B><br>
	 * returns <BR>
	 * <B> there</B>
	 * <P>
	 * 
	 * <B>path('/',"here","");</B><br>
	 * returns <BR>
	 * <B> here</B>
	 * <P>
	 * 
	 * 
	 * @param c
	 *            delimiter
	 * @param parentName
	 *            left side
	 * @param name
	 *            right side
	 * @return left+c+right or left or right (see logic for weather c is embedded between)
	 */
	public static String path(char c, String... parts) {
		final int l = parts.length;
		if (l < 2)
			return l == 0 ? "" : OH.noNull(parts[0], "");
		StringBuilder sb = null;
		int first = -1;
		for (int i = 0; i < l; i++)
			if (isnt(parts[i]))
				continue;
			else if (first == -1)
				first = i;
			else if (sb == null)
				sb = new StringBuilder().append(parts[first]).append(c).append(parts[i]);
			else
				sb.append(c).append(parts[i]);
		return sb != null ? sb.toString() : AH.getOr(parts, first, "");
	}

	public static String escape(String text, char find, char escape) {
		int escapeIndex = text.indexOf(escape);
		int findIndex = text.indexOf(find);
		if (escapeIndex == -1 && findIndex == -1)
			return text;
		if (escapeIndex == -1 || (findIndex != -1 && findIndex < escapeIndex))
			escapeIndex = findIndex;
		int last = 0;
		StringBuilder sb = new StringBuilder();
		do {
			sb.append(text, last, last = escapeIndex++).append(escape);
			findIndex = text.indexOf(find, escapeIndex);
			escapeIndex = text.indexOf(escape, escapeIndex);
			if (escapeIndex == -1 || (findIndex != -1 && findIndex < escapeIndex))
				escapeIndex = findIndex;
		} while (escapeIndex != -1);
		sb.append(text, last, text.length());
		return sb.toString();
	}
	public static StringBuilder escape(char[] text, char find, char escape, StringBuilder sb) {
		for (char c : text)
			if (c == find || c == escape)
				sb.append(escape).append(c);
			else
				sb.append(c);
		return sb;
	}
	public static StringBuilder escapeAny(String text, char find[], char escape, StringBuilder sb) {
		for (int i = 0, l = text.length(); i < l; i++) {
			char c = text.charAt(i);
			if (AH.indexOf(c, find) != -1 || c == escape)
				sb.append(escape).append(c);
			else
				sb.append(c);
		}
		return sb;
	}
	public static String escapeAny(String text, char find[], char escape) {
		return escapeAny(text, find, escape, new StringBuilder()).toString();
	}
	public static StringBuilder escape(String text, char find, char escape, StringBuilder sb) {
		int escapeIndex = text.indexOf(escape);
		int findIndex = text.indexOf(find);
		if (escapeIndex == -1 && findIndex == -1)
			return sb.append(text);
		if (escapeIndex == -1 || (findIndex != -1 && findIndex < escapeIndex))
			escapeIndex = findIndex;
		int last = 0;
		do {
			sb.append(text, last, last = escapeIndex++).append(escape);
			findIndex = text.indexOf(find, escapeIndex);
			escapeIndex = text.indexOf(escape, escapeIndex);
			if (escapeIndex == -1 || (findIndex != -1 && findIndex < escapeIndex))
				escapeIndex = findIndex;
		} while (escapeIndex != -1);
		sb.append(text, last, text.length());
		return sb;
	}
	public static StringBuilder escape(CharSequence text, char find, char escape, StringBuilder sb) {
		return escape(text, 0, text.length(), find, escape, sb);
	}
	public static StringBuilder escape(CharSequence text, int start, int end, char find, char escape, StringBuilder sb) {
		if (text instanceof String && start == 0 && end == text.length())
			return escape((String) text, find, escape, sb);
		int escapeIndex = indexOf(text, escape, start, end);
		int findIndex = indexOf(text, find, start, end);
		if (escapeIndex == -1 && findIndex == -1)
			return sb.append(text, start, end);
		if (escapeIndex == -1 || (findIndex != -1 && findIndex < escapeIndex))
			escapeIndex = findIndex;
		int last = start;
		do {
			sb.append(text, last, last = escapeIndex++).append(escape);
			findIndex = indexOf(text, find, escapeIndex, end);
			escapeIndex = indexOf(text, escape, escapeIndex, end);
			if (escapeIndex == -1 || (findIndex != -1 && findIndex < escapeIndex))
				escapeIndex = findIndex;
		} while (escapeIndex != -1);
		sb.append(text, last, end);
		return sb;
	}

	public static String replaceAll(String text, char find, String replacement) {
		if (text.indexOf(find) == -1)
			return text;
		final StringBuilder sb = new StringBuilder(text.length() + Math.max(0, (replacement.length() - 1)) * 4);//assume 4 matches???
		return replaceAll(text, find, replacement, sb).toString();
	}
	public static String replaceAllForAny(String text, char find[], String replacement) {
		if (find.length < 2)
			return find.length == 0 ? text.toString() : replaceAll(text, find[0], replacement);
		int i = SH.indexOfFirst(text, 0, find);
		if (i == -1)
			return text;
		final StringBuilder sb = new StringBuilder(text.length() + Math.max(0, (replacement.length() - 1)) * 4);//assume 4 matches???
		sb.append(text, 0, i);
		i++;
		while (true) {
			sb.append(replacement);
			int j = SH.indexOfFirst(text, i, find);
			if (j == -1) {
				sb.append(text, i, text.length());
				break;
			}
			sb.append(text, i, j);
			i = j + 1;
		}
		return sb.toString();
	}
	public static String replaceAllForAny(String text, char find[], char replacement) {
		if (find.length < 2)
			return find.length == 0 ? text.toString() : replaceAll(text, find[0], replacement);
		int i = SH.indexOfFirst(text, 0, find);
		if (i == -1)
			return text;
		final StringBuilder sb = new StringBuilder(text.length());
		sb.append(text, 0, i);
		i++;
		while (true) {
			sb.append(replacement);
			int j = SH.indexOfFirst(text, i, find);
			if (j == -1) {
				sb.append(text, i, text.length());
				break;
			}
			sb.append(text, i, j);
			i = j + 1;
		}
		return sb.toString();
	}
	public static StringBuilder replaceAll(String text, char find, String replacement, StringBuilder sb) {
		int i = text.indexOf(find);
		if (i == -1)
			return sb.append(text);
		sb.append(text, 0, i);
		i++;
		while (true) {
			sb.append(replacement);
			int j = text.indexOf(find, i);
			if (j == -1) {
				sb.append(text, i, text.length());
				break;
			}
			sb.append(text, i, j);
			i = j + 1;
		}
		return sb;
	}

	public static String replaceAll(String text, String find, CharSequence replacement) {
		if (text.indexOf(find) == -1 || find.length() == 0)
			return text;
		final StringBuilder sb = new StringBuilder(text.length() + Math.max(0, (replacement.length() - find.length())) * 4);//assume 4 matches???
		return replaceAll(text, find, replacement, sb).toString();
	}
	public static StringBuilder replaceAll(String text, String find, CharSequence replacement, StringBuilder sb) {
		if (find.length() == 0)
			return sb.append(text);
		int i = text.indexOf(find);
		if (i == -1)
			return sb.append(text);
		sb.append(text, 0, i);
		i += find.length();
		while (true) {
			sb.append(replacement);
			int j = text.indexOf(find, i);
			if (j == -1) {
				sb.append(text, i, text.length());
				break;
			}
			sb.append(text, i, j);
			i = j + find.length();
		}
		return sb;
	}
	public static String replaceAll(String text, char find, char replacement) {
		if (find == replacement || text == null)
			return text;
		int i = text.indexOf(find);
		if (i == -1)
			return text;
		StringBuilder sb = new StringBuilder(text.length());
		sb.append(text, 0, i++);
		while (true) {
			sb.append(replacement);
			int j = text.indexOf(find, i);
			if (j == -1) {
				sb.append(text, i, text.length());
				break;
			}
			sb.append(text, i, j);
			i = j + 1;
		}
		return sb.toString();
	}

	public static StringBuilder replaceAll(String text, char find, char replacement, StringBuilder sb) {
		if (text == null)
			return null;
		if (find == replacement)
			return sb.append(text);
		int i = text.indexOf(find);
		if (i == -1)
			return sb.append(text);
		sb.append(text, 0, i++);
		while (true) {
			sb.append(replacement);
			int j = text.indexOf(find, i);
			if (j == -1) {
				sb.append(text, i, text.length());
				break;
			}
			sb.append(text, i, j);
			i = j + 1;
		}
		return sb;
	}

	public static StringBuilder quote(CharSequence text, StringBuilder sb) {
		if (text == null || text.length() == 0)
			return sb.append('\'').append('\'');

		ensureExtraCapacity(sb, text.length() + 2);
		sb.append('\'');
		for (int i = 0, l = text.length(); i < l; i++) {
			final char c = text.charAt(i);
			switch (c) {
				case '\\':
					sb.append('\\').append('\\');
					break;
				case '\'':
					sb.append('\\').append('\'');
					break;
				case '\n':
					sb.append("\\n");
					break;
				case '\r':
					continue;
				default:
					sb.append(c);
			}
		}
		return sb.append('\'');
	}
	public static StringBuilder doubleQuote(CharSequence text, StringBuilder sb) {
		if (text == null || text.length() == 0)
			return sb.append('"').append('"');

		ensureExtraCapacity(sb, text.length() + 2);
		sb.append('"');
		for (int i = 0, l = text.length(); i < l; i++) {
			final char c = text.charAt(i);
			switch (c) {
				case '\\':
					sb.append('\\').append('\\');
					break;
				case '"':
					sb.append('\\').append('"');
					break;
				case '\n':
					sb.append("\\n");
					break;
				case '\r':
					continue;
				default:
					sb.append(c);
			}
		}
		return sb.append('"');
	}
	public static void main(String[] args) {
		String text = "hello alex \"Jim \" 'bob' world";

		System.out.println(SH.quote(text));
		System.out.println(SH.doubleQuoteInPlace(new StringBuilder(text), 0, 1));
		System.out.println(SH.doubleQuoteInPlace(new StringBuilder(text), 5, text.length()));
		System.out.println(text);
	}
	public static StringBuilder doubleQuoteInPlace(StringBuilder sb, int start, int end) {
		if (start < 0 || end > sb.length())
			throw new RuntimeException("start end out of bounds " + start + " - " + end);
		if (sb.length() == 0 || start == end)
			return sb.insert(start, "\"\"");

		ensureExtraCapacity(sb, sb.length() + 2);
		sb.insert(start++, '"');
		end++;

		int i;
		for (i = start; i < end; i++) {
			final char c = sb.charAt(i);
			switch (c) {
				case '\\':
					sb.insert(i++, '\\');
					sb.insert(i++, '\\');
					end += 2;
					break;
				case '"':
					sb.insert(i++, '\\');
					sb.insert(i++, '"');
					end += 2;
					break;
				case '\n':
					sb.insert(i++, '\\');
					sb.insert(i++, 'n');
					end += 2;
					break;
				case '\r':
					continue;
				default:
					sb.setCharAt(i, c);
			}
		}
		return sb.insert(i, '"');
	}
	public static String doubleQuote(CharSequence text) {
		return doubleQuote(text, new StringBuilder()).toString();
	}
	public static String quote(String text) {
		return quote(text, new StringBuilder()).toString();
	}
	public static String doubleQuote(String text) {
		return doubleQuote(text, new StringBuilder()).toString();
	}
	public static String quoteOrNull(String text) {
		if (text == null)
			return "null";
		else
			return quote(text);
	}
	public static String doubleQuoteOrNull(String text) {
		if (text == null)
			return "null";
		else
			return doubleQuote(text);
	}

	public static String toStringDecode(CharSequence str) {
		return toStringDecode(str, 0, str.length());
	}
	public static String toStringDecode(CharSequence str, int start, int end) {
		if (indexOf(str, CHAR_BACKSLASH, start) == -1)
			return substring(str, start, end);
		return toStringDecode(str, start, end, new StringBuilder(str.length())).toString();
	}

	public static StringBuilder toStringDecode(CharSequence str, int start, int end, StringBuilder sb) {
		if (indexOf(str, CHAR_BACKSLASH, start) == -1)
			return sb.append(str);
		while (start < end) {
			start = decodeChar(str, start, sb);
		}
		return sb;
	}

	/*
	 * reads a char at start index, if its a backslash, it gets unescaped, also handls \\uNNNN case. returns the cursor;
	 */
	public static int decodeChar(CharSequence str, int start, StringBuilder sb) {
		char c = str.charAt(start++);
		if (c != CHAR_BACKSLASH) {
			sb.append(c);
		} else {
			c = str.charAt(start++);
			if (c == CHAR_UNICODE) {
				sb.append((char) parseInt(str, start, start += 4, 16));
			} else {
				char c2 = toSpecial(c);
				sb.append(c2 == SH.CHAR_NOT_SPECIAL ? c : c2);
			}
		}
		return start;
	}

	public static char toSpecial(char c) {
		switch (c) {
			case 't':
				return CHAR_TAB;
			case 'n':
				return CHAR_NEWLINE;
			case 'b':
				return CHAR_BACKSPACE;
			case 'f':
				return CHAR_FORMFEED;
			case 'r':
				return CHAR_RETURN;
			case '"':
				return CHAR_QUOTE;
			case '\'':
				return CHAR_SINGLE_QUOTE;
			case '\\':
				return CHAR_BACKSLASH;
			case '/':
				return CHAR_FORWARDSLASH;
			default:
				return CHAR_NOT_SPECIAL;
		}
	}
	public static char toSpecialIfSpecial(char c) {
		switch (c) {
			case 't':
				return CHAR_TAB;
			case 'n':
				return CHAR_NEWLINE;
			case 'b':
				return CHAR_BACKSPACE;
			case 'f':
				return CHAR_FORMFEED;
			case 'r':
				return CHAR_RETURN;
			case '"':
				return CHAR_QUOTE;
			case '\'':
				return CHAR_SINGLE_QUOTE;
			case '\\':
				return CHAR_BACKSLASH;
			default:
				return c;
		}
	}

	public static char fromSpecial(char c, char specialQuoteChar) {
		switch (c) {
			case CHAR_TAB:
				return 't';
			case CHAR_NEWLINE:
				return 'n';
			case CHAR_BACKSPACE:
				return 'b';
			case CHAR_FORMFEED:
				return 'f';
			case CHAR_RETURN:
				return 'r';
			case CHAR_QUOTE:
				return c == specialQuoteChar ? '"' : CHAR_NOT_SPECIAL;
			case CHAR_SINGLE_QUOTE:
				return c == specialQuoteChar ? '\'' : CHAR_NOT_SPECIAL;
			case CHAR_BACKSLASH:
				return '\\';
			default:
				return CHAR_NOT_SPECIAL;
		}
	}
	public static char fromSpecial(char c) {
		switch (c) {
			case CHAR_TAB:
				return 't';
			case CHAR_NEWLINE:
				return 'n';
			case CHAR_BACKSPACE:
				return 'b';
			case CHAR_FORMFEED:
				return 'f';
			case CHAR_RETURN:
				return 'r';
			case CHAR_QUOTE:
				return '"';
			case CHAR_SINGLE_QUOTE:
				return '\'';
			case CHAR_BACKSLASH:
				return '\\';
			default:
				return CHAR_NOT_SPECIAL;
		}
	}

	static public boolean isntUnicode(char c) {
		return c < 0x20 || c > 0x7e;

	}

	public static String toStringEncode(char cb, char quoteCharToEscape) {
		return toStringEncode(cb, quoteCharToEscape, new StringBuilder()).toString();
	}

	public static StringBuilder toStringEncode(char c, char quoteCharToEscape, StringBuilder sb) {
		char t = fromSpecial(c, quoteCharToEscape);
		if (t == CHAR_NOT_SPECIAL) {
			if (isntUnicode(c)) {
				toUnicodeHex(sb, c);
			} else
				sb.append(c);
		} else
			sb.append(CHAR_BACKSLASH).append(t);
		return sb;
	}

	public static String toStringEncode(String str, char quoteCharToEscape) {
		return toStringEncode(str, quoteCharToEscape, new StringBuilder()).toString();
	}
	public static String toStringEncode(String str) {
		return toStringEncode(str, new StringBuilder()).toString();
	}

	public static StringBuilder toStringEncode(String str, char quoteCharToEscape, StringBuilder sb) {
		int spanStart = 0;
		for (int i = 0, l = str.length(); i < l; i++) {
			char c = str.charAt(i);
			char t = fromSpecial(c, quoteCharToEscape);
			if (t == CHAR_NOT_SPECIAL) {
				if (isntUnicode(c)) {
					sb.append(str, spanStart, i);
					spanStart = i + 1;
					toUnicodeHex(sb, c);
				}
			} else {
				sb.append(str, spanStart, i);
				spanStart = i + 1;
				sb.append(CHAR_BACKSLASH).append(t);
			}
		}
		sb.append(str, spanStart, str.length());
		return sb;
	}
	public static StringBuilder toStringEncode(String str, StringBuilder sb) {
		int spanStart = 0;
		for (int i = 0, l = str.length(); i < l; i++) {
			char c = str.charAt(i);
			char t = fromSpecial(c);
			if (t == CHAR_NOT_SPECIAL) {
				if (isntUnicode(c)) {
					sb.append(str, spanStart, i);
					spanStart = i + 1;
					toUnicodeHex(sb, c);
				}
			} else {
				sb.append(str, spanStart, i);
				spanStart = i + 1;
				sb.append(CHAR_BACKSLASH).append(t);
			}
		}
		sb.append(str, spanStart, str.length());
		return sb;
	}

	public static void toUnicodeHex(Appendable sb, char c) {
		try {
			sb.append(CHAR_BACKSLASH).append(CHAR_UNICODE).append(toHexCharLower((c >> 12) & 0xf)).append(toHexCharLower((c >> 8) & 0xf)).append(toHexCharLower((c >> 4) & 0xf))
					.append(toHexCharLower(c & 0xf));
		} catch (Exception e) {
			throw OH.toRuntime(e);
		}
	}
	public static void toUnicodeHex(StringBuilder sb, char c) {
		sb.append(CHAR_BACKSLASH).append(CHAR_UNICODE).append(toHexCharLower((c >> 12) & 0xf)).append(toHexCharLower((c >> 8) & 0xf)).append(toHexCharLower((c >> 4) & 0xf))
				.append(toHexCharLower(c & 0xf));
	}

	public static StringBuilder toStringEncode(char[] chars, char quoteCharToEscape, StringBuilder sb) {
		int spanStart = 0;
		for (int i = 0, l = chars.length; i < l; i++) {
			char c = chars[i];
			char t = fromSpecial(c, quoteCharToEscape);
			if (t == CHAR_NOT_SPECIAL) {
				if (isntUnicode(c)) {
					sb.append(chars, spanStart, i - spanStart);
					spanStart = i + 1;
					toUnicodeHex(sb, c);
				}
			} else {
				sb.append(chars, spanStart, i - spanStart);
				spanStart = i + 1;
				sb.append(CHAR_BACKSLASH).append(t);
			}
		}
		sb.append(chars, spanStart, chars.length - spanStart);
		return sb;
	}

	public static char parseCharHandleSpecial(CharSequence str) {
		return parseCharHandleSpecial(str, 0, str.length());
	}
	public static char parseCharHandleSpecial(CharSequence str, int start, int end) {
		switch (end - start) {
			case 1:
				char c = str.charAt(start);
				if (c == '\\')
					break;
				return c;
			case 2:
				if (str.charAt(start) != '\\')
					break;
				c = str.charAt(start + 1);
				c = toSpecial(c);
				if (c == CHAR_NOT_SPECIAL)
					throw new RuntimeException(str.subSequence(start, end).toString());
				return c;
			case 6:
				if (str.charAt(start + 1) == CHAR_UNICODE)
					return (char) parseInt(str, start + 2, end, 16);

		}
		throw new RuntimeException("could not parse to a char: " + str);
	}

	public static char parseChar(String str) {
		if (str == null || str.length() != 1)
			throw new RuntimeException("could not parse to a char: " + str);
		return str.charAt(0);
	}

	public static int indexOf(String text, int start, Set<Character> set) {
		for (int l = text.length(); start < l; start++)
			if (set.contains(text.charAt(start)))
				return start;
		return -1;
	}
	public static int indexOf(String text, int start, CharMatcher cm) {
		for (int l = text.length(); start < l; start++)
			if (cm.matches(text.charAt(start)))
				return start;
		return -1;
	}

	public static int indexOfNot(String text, int start, Set<Character> set) {
		for (int l = text.length(); start < l; start++)
			if (!set.contains(text.charAt(start)))
				return start;
		return -1;
	}

	public static int indexOfNot(String text, int start, char c) {
		for (int l = text.length(); start < l; start++)
			if (c != text.charAt(start))
				return start;
		return -1;
	}

	public static String trim(char c, String text) {
		if (text == null || text.length() == 0)
			return text;
		int start = 0, end = text.length() - 1;
		while (text.charAt(start) == c)
			if (start++ == end)
				return "";
		while (text.charAt(end) == c)
			end--;
		return text.substring(start, end + 1);
	}

	public static String trim(char before, char after, String text) {
		if (text == null || text.length() == 0)
			return text;
		int start = 0, end = text.length() - 1;
		while (text.charAt(start) == before)
			if (start++ == end)
				return "";
		while (text.charAt(end) == after)
			if (end-- == start)
				return "";
		return text.substring(start, end + 1);
	}
	public static String trimStart(char before, String text) {
		if (text == null || text.length() == 0)
			return text;
		int start = 0, end = text.length() - 1;
		while (text.charAt(start) == before)
			if (start++ == end)
				return "";
		return start == 0 ? text : text.substring(start);
	}

	public static String ddd(String text, int max) {
		if (text == null || text.length() <= max)
			return text;
		return ddd(text, max, new StringBuilder()).toString();
	}

	public static StringBuilder ddd(CharSequence text, int max, StringBuilder sb) {
		if (max <= 3) {
			if (text.length() <= max)
				sb.append(text);
			else
				repeat('.', max, sb);
		} else if (text.length() > max)
			sb.append(text, 0, max - 3).append("...");
		else
			sb.append(text);
		return sb;
	}
	public static int indexOfFirst(String value_, int fromIndex, String... lookFor) {
		int r = -1;
		for (String s : lookFor) {
			int i = value_.indexOf(s, fromIndex);
			if (i != -1 && (r == -1 || i < r))
				r = i;
		}
		return r;
	}
	public static int indexOfLast(String value_, int fromIndex, String... lookFor) {
		int r = -1;
		for (String s : lookFor) {
			int i = value_.lastIndexOf(s, fromIndex);
			if (i != -1 && (r == -1 || i < r))
				r = i;
		}
		return r;
	}
	public static int indexOfFirst(String value_, int fromIndex, char... lookFor) {
		for (int i = fromIndex, len = value_.length(); i < len; i++)
			if (AH.indexOf(value_.charAt(i), lookFor) != -1)
				return i;
		return -1;
	}
	public static int indexOfLast(String value_, int fromIndex, char... lookFor) {
		int r = -1;
		for (char s : lookFor) {
			int i = value_.lastIndexOf(s, fromIndex);
			if (i != -1 && (r == -1 || i < r))
				r = i;
		}
		return r;
	}

	public static String stripSuffix(String text, String suffix, boolean throwOnNotSuffix) {
		if (text.endsWith(suffix))
			return text.substring(0, text.length() - suffix.length());
		if (throwOnNotSuffix)
			throw new RuntimeException("expected suffix '" + suffix + "' on: " + text);
		return text;
	}
	public static String stripSuffixIgnoreCase(String text, String suffix, boolean throwOnNotSuffix) {
		if (endsWithIgnoreCase(text, suffix))
			return text.substring(0, text.length() - suffix.length());
		if (throwOnNotSuffix)
			throw new RuntimeException("expected suffix '" + suffix + "' on: " + text);
		return text;
	}

	public static String stripPrefix(String text, String prefix, boolean throwOnNotPrefix) {
		if (text.startsWith(prefix))
			return text.substring(prefix.length());
		if (throwOnNotPrefix)
			throw new RuntimeException("expected prefix '" + prefix + "' on: " + text);
		return text;
	}
	public static String stripPrefixIgnoreCase(String text, String prefix, boolean throwOnNotPrefix) {
		if (startsWithIgnoreCase(text, prefix, 0))
			return text.substring(prefix.length());
		if (throwOnNotPrefix)
			throw new RuntimeException("expected prefix '" + prefix + "' on: " + text);
		return text;
	}

	public static String strip(String text, String prefix, String suffix, boolean throwOnNotPrefixSuffix) {
		if (throwOnNotPrefixSuffix) {
			if (!text.startsWith(prefix))
				throw new RuntimeException("expected prefix '" + prefix + "' on: " + text);
			if (!text.endsWith(suffix))
				throw new RuntimeException("expected suffix '" + suffix + "' on: " + text);
			final int start = prefix.length();
			final int end = text.length() - suffix.length();
			if (start > end)
				throw new RuntimeException("prefix '" + prefix + "' overlaps suffix '" + suffix + "' on: " + text);
			return text.substring(start, end);
		} else if (text.startsWith(prefix)) {
			final int end = text.length() - suffix.length();
			final int start = prefix.length();
			if (text.endsWith(suffix) && start <= end)
				return text.substring(start, end);
			return text.substring(start);
		} else {
			if (text.endsWith(suffix))
				return text.substring(0, text.length() - suffix.length());
			return text;
		}
	}

	/**
	 * Will return a new array containing all elements in the original array that were not blank (see {@link #is(Object)} ). All returned String will also be trimmed ( see
	 * {@link String#trim()})
	 * 
	 * @param parts
	 *            the array to trim
	 * @return an array of trimmed, nonblank elements in same order as supplied array
	 */
	public static String[] trimArray(String... parts) {
		int start = 0, end = parts.length;
		while (start < parts.length && isnt(parts[start]))
			start++;
		if (start == end)
			return OH.EMPTY_STRING_ARRAY;
		while (end > 0 && isnt(parts[end - 1]))
			end--;
		int len = end - start;
		for (int i = start + 1; i < end - 1; i++)
			if (isnt(parts[i]))
				len--;
		final String[] r = new String[len];
		if (len != end - start) {
			for (int i = start, j = 0; i < end; i++)
				if (is(parts[i]))
					r[j++] = parts[i].trim();
		} else
			for (int i = start, j = 0; i < end; i++)
				r[j++] = parts[i].trim();
		return r;
	}

	static public String trim(String text) {
		return text == null ? null : text.trim();
	}
	static public CharSequence trim(CharSequence cs) {
		if (cs instanceof String)
			return ((String) cs).trim();
		int len = cs.length();
		int st = 0;

		while (st < len && cs.charAt(st) <= ' ')
			st++;
		while (st < len && cs.charAt(len - 1) <= ' ')
			len--;
		return (st > 0 || len < cs.length()) ? cs.subSequence(st, len) : cs;
	}
	static public int findTrimStart(CharSequence cs, int st, int len) {
		while (st < len && cs.charAt(st) <= ' ')
			st++;
		return st;
	}
	static public int findTrimEnd(CharSequence cs, int st, int end) {
		while (st < end && cs.charAt(end - 1) <= ' ')
			end--;
		return end;
	}

	/**
	 * 
	 * @param text
	 * @return length of text of -1 on null
	 */
	static public int length(String text) {
		return text == null ? -1 : text.length();
	}

	static public int hashcode(String text) {
		return OH.hashCode(text);
	}

	public static int indexOfNotEscaped(CharSequence text, char toFind, int start, char escape) {
		//TODO: to index of and work backwards
		int length = text.length();
		int i = start;
		while (i != length) {
			final char c = text.charAt(i);
			if (c == toFind)
				return i;
			else if (c == escape) {
				if ((i += 2) > length)
					return -1;
			} else
				i++;
		}
		return -1;
	}
	public static int indexOfNotEscaped(CharSequence text, String toFind, int start, char escape) {
		//TODO: to index of and work backwards
		int toFindLength = toFind.length();
		if (toFindLength == 0)
			return start;
		char toFind1 = toFind.charAt(0);
		if (toFindLength == 1)
			return indexOfNotEscaped(text, toFind1, start, escape);
		int length = text.length();
		for (int i = start, l = length + 1 - toFindLength; i < l;) {
			final char c = text.charAt(i);
			if (c == toFind1 && equals(text, i + 1, i + toFindLength, toFind, 1, toFindLength)) {
				return i;
			} else if (c == escape) {
				if ((i += 2) > length)
					return -1;
			} else
				i++;
		}
		return -1;
	}

	public static boolean endsWith(CharSequence line, char c) {
		return line != null && line.length() > 0 && line.charAt(line.length() - 1) == c;
	}

	public static boolean startsWith(CharSequence line, char c) {
		return line != null && line.length() > 0 && line.charAt(0) == c;
	}

	public static Object parseConstant(CharSequence text) {
		final int len = text.length();
		if (len < 2)
			return SH.parseInt(text);
		final char lc = text.charAt(len - 1);
		try {
			switch (lc) {
				case SH.CHAR_QUOTE:
					if (!SH.startsWith(text, SH.CHAR_QUOTE))
						break;
					return SH.toStringDecode(text, 1, len - 1);
				case SH.CHAR_SINGLE_QUOTE:
					if (!SH.startsWith(text, SH.CHAR_SINGLE_QUOTE))
						break;
					return SH.parseCharHandleSpecial(text, 1, len - 1);
				case 'N':
					if (equals(text, NAN))
						return Double.NaN;
					break;
				case 'b':
				case 'B':
					return parseByte(text, 0, text.length() - 1, 10);
				case 's':
				case 'S':
					return parseShort(text, 0, text.length() - 1, 10);
				case 'u':
				case 'U': {
					String s = substring(text, 0, text.length() - 1);
					return s.indexOf('.') == -1 ? new BigInteger(s) : new BigDecimal(s);
				}
				case 'i':
				case 'I':
				case 'j':
				case 'J':
					return parseComplex(text);
				case 'D':
				case 'd':
					byte flags = getNumberFormatFlags(text);
					if (MH.areAnyBitsSet(flags, NFF_BIT_HEX))
						return parseIntOrLong(text, flags);
					return parseDouble(text);
				case 'F':
				case 'f':
					flags = getNumberFormatFlags(text);
					if (MH.areAnyBitsSet(flags, NFF_BIT_HEX))
						return parseIntOrLong(text, flags);
					return parseFloat(text);
				case 'l':
					if (len == 4 && equals(text, NULL))
						return null;
				case 'L': {
					flags = getNumberFormatFlags(text);
					if (MH.areAnyBitsSet(flags, NFF_BIT_HEX)) {
						if (MH.areAnyBitsSet(flags, NFF_BIT_NEG))
							return -parseLong(text, 3, text.length() - 1, 16);
						else
							return parseLong(text, 2, text.length() - 1, 16);
					}
					return parseLong(text, 0, text.length() - 1, 10);
				}
				case 'e':
					if (len > 3) {
						if (equals(text, TRUE))
							return true;
						if (equals(text, FALSE))
							return false;
					}
					flags = getNumberFormatFlags(text);
					return parseIntOrLong(text, flags);// Must be hex
				case 'y':
					if (text.equals(NEG_INFINITY))
						return Double.NEGATIVE_INFINITY;
					if (text.equals(POS_INFINITY))
						return Double.POSITIVE_INFINITY;
					break;
				default:
					for (int i = 0, l = len - 1; i < l; i++)
						switch (text.charAt(i)) {
							case '.':
							case 'e':
							case 'E':
								return parseDouble(text);
						}
					flags = getNumberFormatFlags(text);
					return parseIntOrLong(text, flags);
			}
		} catch (Exception e) {
			StringBuilder message = new StringBuilder("Invalid ");
			switch (lc) {
				case SH.CHAR_QUOTE:
					message.append("string");
					break;
				case SH.CHAR_SINGLE_QUOTE:
					message.append("char");
					break;
				case 'N':
				case 'D':
				case 'd':
					message.append("double");
					break;
				case 'F':
				case 'f':
					message.append("float");
					break;
				case 'l':
				case 'L':
					message.append("long");
					break;
				case 'e':
				case 'y':
				default:
					message.append("const");
					break;
			}
			throw new RuntimeException(message.append(": ").append(text).toString());
		}
		throw new RuntimeException("invalid constant: " + text);
	}

	// 1,2 are mutually exclusive. 4,8,16 are mutually exclusive
	private static final byte NFF_BIT_HEX = 1;// hex number
	private static final byte NFF_BIT_DEC = 2;// decimal number
	private static final byte NFF_BIT_OCT = 4;// octal number
	private static final byte NFF_BIT_POS = 8;// implicit positive number
	private static final byte NFF_BIT_NEG = 16;// explicitly prefixed with a -
	private static final byte NFF_BIT_PLS = 32;// explicitly prefixed with a +

	private static final byte NFF_HEX_POS = NFF_BIT_HEX | NFF_BIT_POS;
	private static final byte NFF_HEX_NEG = NFF_BIT_HEX | NFF_BIT_NEG;
	private static final byte NFF_HEX_PLS = NFF_BIT_HEX | NFF_BIT_PLS;
	private static final byte NFF_DEC_POS = NFF_BIT_DEC | NFF_BIT_POS;
	private static final byte NFF_DEC_NEG = NFF_BIT_DEC | NFF_BIT_NEG;
	private static final byte NFF_DEC_PLS = NFF_BIT_DEC | NFF_BIT_PLS;
	private static final byte NFF_OCT_POS = NFF_BIT_POS | NFF_BIT_POS;
	private static final byte NFF_OCT_NEG = NFF_BIT_POS | NFF_BIT_NEG;
	private static final byte NFF_OCT_PLS = NFF_BIT_POS | NFF_BIT_PLS;
	public static final int MAX_CACHED_INTS_SIZE = 10240;
	private static final int PRE_CACHED_INTS_SIZE = 1024;
	private static final String[] NEGATIVE_NUMBERS;
	private static final String[] POSITIVE_NUMBERS;
	private static final String[] CHARS;

	static {
		CHARS = new String[256];
		POSITIVE_NUMBERS = new String[MAX_CACHED_INTS_SIZE + 1];
		NEGATIVE_NUMBERS = new String[MAX_CACHED_INTS_SIZE + 1];
		NEGATIVE_NUMBERS[0] = POSITIVE_NUMBERS[0] = "0";
		OH.assertGt(PRE_CACHED_INTS_SIZE, Byte.MAX_VALUE);
		for (int i = 0; i < CHARS.length; i++)
			CHARS[i] = Character.toString((char) i);
		// not sure if you are trying to set it as 1024 or 10240 below
		for (int i = 1; i <= PRE_CACHED_INTS_SIZE && i <= MAX_CACHED_INTS_SIZE; i++) {
			NEGATIVE_NUMBERS[i] = Integer.toString(-i);
			POSITIVE_NUMBERS[i] = NEGATIVE_NUMBERS[i].substring(1);// reuse
		}
	}

	private static byte getNumberFormatFlags(CharSequence text) {
		if (text.length() == 1)
			return NFF_DEC_POS;
		char c = text.charAt(0);
		switch (c) {
			case '-':
				if (text.charAt(1) != '0' || text.length() == 2)
					return NFF_DEC_NEG;// -[1-9]... or -0
				c = text.charAt(2);
				return c == 'x' || c == 'X' ? NFF_HEX_NEG : NFF_DEC_NEG;
			case '+':
				if (text.charAt(1) != '0' || text.length() == 2)
					return NFF_DEC_PLS;// format: +[1-9]... or +0
				c = text.charAt(2);
				return (c == 'x' || c == 'X') ? NFF_HEX_PLS : NFF_DEC_PLS;
			case '0':
				c = text.charAt(1);
				return c == 'x' || c == 'X' ? NFF_HEX_POS : NFF_DEC_POS;
			default:
				return NFF_DEC_POS;
		}
	}

	private static int INT_DEC_LENGTH = Math.min(MAX_INT_DEC.length(), MIN_INT_DEC.length());
	private static int INT_HEX_LENGTH = Math.min(MAX_INT_HEX.length(), MIN_INT_HEX.length());
	private static int INT_OCT_LENGTH = Math.min(MAX_INT_OCT.length(), MIN_INT_OCT.length());

	// if numberic value of text is beyond Integer.MAX_INT & Integer.MIN_INT,
	// treate as a long
	private static Object parseIntOrLong(CharSequence text, byte flags) {
		if (MH.areAnyBitsSet(flags, NFF_BIT_DEC)) {
			if (text.length() >= INT_DEC_LENGTH) {
				long r = parseLong(text, flags);
				if (OH.isBetween(r, Integer.MIN_VALUE, Integer.MAX_VALUE))
					return (int) r;
				return r;
			}
		} else if (MH.areAnyBitsSet(flags, NFF_BIT_HEX)) {
			if (text.length() - 2 >= INT_HEX_LENGTH) // subtract for preceeding
														// 0x
			{
				long r = parseLong(text, flags);
				if (OH.isBetween(r, Integer.MIN_VALUE, Integer.MAX_VALUE))
					return (int) r;
				return r;
			}
		} else if (MH.areAnyBitsSet(flags, NFF_BIT_OCT)) {
			if (text.length() - 1 >= INT_OCT_LENGTH) // subtract for preceeding
														// 0
			{
				long r = parseLong(text, flags);
				if (OH.isBetween(r, Integer.MIN_VALUE, Integer.MAX_VALUE))
					return (int) r;
				return r;
			}
		}
		return parseInt(text, flags);
	}
	public static int parseInt(String text) {
		return parseInt(text, getNumberFormatFlags(text));
	}

	private static int parseInt(CharSequence text, byte flags) {
		switch (flags) {
			case NFF_HEX_POS:
				return parseInt(text, 2, text.length(), 16);
			case NFF_HEX_PLS:
				return parseInt(text, 3, text.length(), 16);
			case NFF_HEX_NEG:
				return -parseInt(text, 3, text.length(), 16);
			case NFF_DEC_PLS:
				return parseInt(text, 1, text.length(), 10);
			case NFF_DEC_NEG:
			case NFF_DEC_POS:
				return parseInt(text, 0, text.length(), 10);
			case NFF_OCT_PLS:
				return parseInt(text, 1, text.length(), 8);
			case NFF_OCT_NEG:
			case NFF_OCT_POS:
				return parseInt(text, 8);
			default:
				throw new RuntimeException(text.toString());
		}
	}

	public static long parseLong(String text) {
		return parseLong(text, getNumberFormatFlags(text));
	}

	private static long parseLong(CharSequence text, byte flags) {
		int len = text.length();
		final char lastChar = text.charAt(len - 1);
		boolean suffix = lastChar == 'L' || lastChar == 'l';
		if (suffix)
			len--;
		switch (flags) {
			case NFF_HEX_POS:
				return parseLong(text, 2, len, 16);
			case NFF_HEX_PLS:
				return parseLong(text, 3, len, 16);
			case NFF_HEX_NEG: {
				long r;
				r = parseLong(text, 3, len, 16);
				if (r == Long.MAX_VALUE)
					throw new NumberFormatException(text.toString());
				return -r;
			}
			case NFF_DEC_PLS:
				return parseLong(text, 1, len, 10);
			case NFF_DEC_NEG:
			case NFF_DEC_POS:
				return parseLong(text, 0, len, 10);
			case NFF_OCT_PLS:
				return parseLong(text, 1, len, 8);
			case NFF_OCT_NEG:
			case NFF_OCT_POS:
				return parseLong(text, 0, len, 8);
			default:
				throw new RuntimeException(text.toString());
		}
	}

	public static int getCount(String find, String text) {
		int r = 0, start = 0;
		final int len = find.length();
		while ((start = text.indexOf(find, start)) != -1) {
			r++;
			start += len;
		}
		return r;
	}

	public static String prefixLines(String text, String prefix) {
		return prefixLines(text, prefix, true);
	}

	public static String prefixLines(String text, String prefix, boolean prefixFirstLine) {
		if (prefix == null || prefix.length() == 0)
			return text;
		return prefixLines(text, prefix, prefixFirstLine, new StringBuilder()).toString();
	}
	public static StringBuilder prefixLines(CharSequence text, String prefix, boolean prefixFirstLine, StringBuilder sink) {
		if (prefix == null || prefix.length() == 0)
			return s(text, sink);
		if (prefixFirstLine)
			sink.append(prefix);
		int start = 0, end = 0;
		int len = text.length();
		while ((end = indexOf(text, '\n', start)) != -1) {
			sink.append(text, start, end + 1);
			start = end + 1;
			if (start == len)
				return sink;
			sink.append(prefix);
		}
		sink.append(text, start, len);
		return sink;
	}

	public static StringBuilder grep(String pattern, String text, StringBuilder sb) {
		final String[] parts = split('\n', text);
		TextMatcher p = m(pattern);
		for (String part : parts)
			if (p.matches(part))
				sb.append(part).append('\n');
		return sb;
	}

	public static String grep(String pattern, String text) {
		return grep(pattern, text, new StringBuilder()).toString();
	}

	public static String toString(long i) {
		if (OH.isBetween(i, -MAX_CACHED_INTS_SIZE, MAX_CACHED_INTS_SIZE))
			return getCachedStringForInt((int) i);
		return Long.toString(i);
	}

	public static String toString(int i) {
		if (OH.isBetween(i, -MAX_CACHED_INTS_SIZE, MAX_CACHED_INTS_SIZE))
			return getCachedStringForInt(i);
		return Integer.toString(i);
	}

	public static String toString(char c) {
		return c < CHARS.length ? CHARS[c] : String.valueOf(c);
	}
	public static String toString(Character c) {
		return c == null ? null : toString(c.charValue());
	}

	public static String toString(byte i) {
		return i < 0 ? NEGATIVE_NUMBERS[-i] : POSITIVE_NUMBERS[i];
	}

	public static String toString(short i) {
		if (OH.isBetween(i, -MAX_CACHED_INTS_SIZE, MAX_CACHED_INTS_SIZE))
			return getCachedStringForInt(i);
		return Short.toString(i);
	}

	private static String getCachedStringForInt(int i) {
		String r;
		if (i < 0) {
			if ((r = NEGATIVE_NUMBERS[-i]) == null)
				r = NEGATIVE_NUMBERS[-i] = Integer.toString(i);
		} else {
			if ((r = POSITIVE_NUMBERS[i]) == null)
				r = POSITIVE_NUMBERS[i] = Integer.toString(i);
		}
		return r;
	}

	public static String toString(boolean bool) {
		return bool ? "true" : "false";
	}

	public static String toString(double o) {
		return Double.toString(o);
	}
	public static String toString(float o) {
		return Float.toString(o);
	}
	public static String toString(Object o) {
		try {
			return s(o);
		} catch (Exception e) {
			return printStackTrace(e);
		}
	}
	public static String toStringOrNull(Object o) {
		if (o == null)
			return null;
		try {
			return s(o);
		} catch (Exception e) {
			return printStackTrace(e);
		}
	}

	public static String toHex(long i) {
		return PREFIX_HEX + Long.toHexString(i);
	}

	public static String toHex(int i) {
		return PREFIX_HEX + Integer.toHexString(i);
	}

	/**
	 * 
	 * @param str
	 * @param pos
	 * @return line number & position
	 */
	public static Tuple2<Integer, Integer> getLinePosition(String str, int pos) {
		int lineNumber = 0;
		int lineStart = 0;
		while (true) {
			int j = str.indexOf('\n', lineStart);
			if (j == -1 || j >= pos)
				return new Tuple2<Integer, Integer>(lineNumber, pos - lineStart);
			lineNumber++;
			lineStart = j + 1;
		}
	}
	public static Tuple3<Integer, Integer, String> getLinePositionAndText(String str, int pos) {
		pos = MH.clip(pos, 0, str.length() - 1);
		int lineNumber = 0;
		int lineStart = 0;
		while (true) {
			int j = str.indexOf('\n', lineStart);
			if (j == -1 || j >= pos)
				return new Tuple3<Integer, Integer, String>(lineNumber, pos - lineStart, j != -1 ? str.substring(pos, j) : str.substring(pos));
			lineNumber++;
			lineStart = j + 1;
		}
	}

	/**
	 * see {@link String#substring(int, int)} except this will never throw an out of bounds or null pointer exception
	 * 
	 * @param str
	 *            string to get the substring from
	 * @param start
	 *            the start index
	 * @param end
	 *            the end index
	 * @return the resulting substring (to the maximum extent based on the strings length), never null.
	 */
	public static String substring(CharSequence str, int start, int end) {
		if (str == null)
			return "";
		int len = str.length();
		start = Math.max(0, start);
		end = Math.min(end, len);
		if (start >= end)
			return "";
		if (start != 0 || end != len)
			str = str.subSequence(start, end);
		return str instanceof String ? (String) str : str.toString();
	}
	static public String password(String text) {
		if (text == null)
			return null;
		final int length = text.length();
		return repeat('*', length);
	}

	public static StringBuilder quantify(Collection<?> c, String singular, String plural, StringBuilder sb) {
		if (c == null)
			return sb.append("no ").append(plural);
		if (c.size() == 1)
			return sb.append("1 ").append(singular);
		return sb.append(c.size()).append(' ').append(plural);
	}

	public static String quantify(Collection<?> c, String singular, String plural) {
		return quantify(c, singular, plural, new StringBuilder()).toString();
	}

	public static StringBuilder quantify(Map<?, ?> c, String singular, String plural, StringBuilder sb) {
		if (c == null)
			return sb.append("no ").append(plural);
		if (c.size() == 1)
			return sb.append("1 ").append(singular);
		return sb.append(c.size()).append(' ').append(plural);
	}

	public static String quantify(Map<?, ?> c, String singular, String plural) {
		return quantify(c, singular, plural, new StringBuilder()).toString();
	}

	public static StringBuilder quantify(long c, String singular, String plural, StringBuilder sb) {
		if (c == 1)
			return sb.append("1 ").append(singular);
		if (c == -1)
			return sb.append("-1 ").append(singular);
		return sb.append(c).append(' ').append(plural);
	}

	public static StringBuilder quantify(Formatter numberFormatter, long c, String singular, String plural, StringBuilder sb) {
		if (c == 1)
			return sb.append("1 ").append(singular);
		if (c == -1)
			return sb.append("-1 ").append(singular);
		numberFormatter.format(c, sb);
		return sb.append(' ').append(plural);
	}

	public static String quantify(long c, String singular, String plural) {
		return quantify(c, singular, plural, new StringBuilder()).toString();
	}

	public static String quantify(Formatter numberFormatter, long c, String singular, String plural) {
		return quantify(numberFormatter, c, singular, plural, new StringBuilder()).toString();
	}

	/**
	 * short hand for {@link String#format(String, Object...)}
	 */
	public static String f(String text, Object... args) {
		return String.format(text, args);
	}

	public static String[] splitLines(String text) {
		return splitLines(text, true);
	}

	// CR+LF , LF , CR
	public static String[] splitLines(String text, boolean trimTrailing) {
		if (text == null)
			return OH.EMPTY_STRING_ARRAY;
		int len = text.length();
		if (len == 0)
			return OH.EMPTY_STRING_ARRAY;
		else if (trimTrailing && text.charAt(len - 1) == CHAR_LF) {
			if (len == 1)
				return OH.EMPTY_STRING_ARRAY;
			if (text.charAt(len - 2) == CHAR_CR) {
				if (len == 2)
					return OH.EMPTY_STRING_ARRAY;
				len -= 2;
			} else
				len -= 1;
		}

		int c = 1, i = 0;
		char ch = text.charAt(0);
		outer: for (;;) {
			switch (ch) {
				case '\r':
					c++;
					if (++i == len)
						break outer;
					ch = text.charAt(i);
					if (ch != '\n')
						continue;
					break;
				case '\n':
					c++;
					break;
			}
			if (++i == len)
				break;
			ch = text.charAt(i);
		}
		String[] r = new String[c];

		c = i = 0;
		int j = 0;
		ch = text.charAt(0);
		outer: for (;;) {
			switch (ch) {
				case '\r':
					r[c++] = text.substring(j, i);
					j = i + 1;
					if (++i == len)
						break outer;
					ch = text.charAt(i);
					if (ch != '\n') {
						continue;
					}
					j++;
					break;
				case '\n':
					r[c++] = text.substring(j, i);
					j = i + 1;
					break;
			}
			if (++i == len)
				break;
			ch = text.charAt(i);
		}
		if (c < r.length)
			r[c] = text.substring(j, j = i);
		return r;
	}
	public static boolean equals(CharSequence text, CharSequence text2) {
		if (text == text2)
			return true;
		if (text == null || text2 == null)
			return false;
		int l = text.length();
		if (l != text2.length())
			return false;
		//check first char, then work backwards... this will help fail fast
		if (l > 0) {
			if (text.charAt(0) != text2.charAt(0))
				return false;
			while (l > 1)
				if (text.charAt(--l) != text2.charAt(l))
					return false;
		}
		return true;
	}

	public static boolean equalsIgnoreCase(char c1, char c2) {
		return c1 == c2 || (c1 = Character.toUpperCase(c1)) == c2 || c1 == Character.toUpperCase(c2);
	}
	private static boolean equals(char c1, char c2, boolean ignoreCase) {
		return c1 == c2 || (ignoreCase && ((c1 = Character.toUpperCase(c1)) == c2 || c1 == Character.toUpperCase(c2)));
	}

	public static boolean equalsIgnoreCase(CharSequence text, CharSequence text2) {
		if (text == null || text2 == null)
			return text == text2;
		int l = text.length();
		if (l != text2.length())
			return false;
		//check first char, then work backwards... this will help fail fast
		if (l > 0) {
			if (!equalsIgnoreCase(text.charAt(0), text2.charAt(0)))
				return false;
			while (l > 1)
				if (!equalsIgnoreCase(text.charAt(--l), text2.charAt(l)))
					return false;
		}
		return true;
	}

	public static boolean isLowerCase(CharSequence text) {
		for (int i = 0, l = text.length(); i < l; i++)
			if (Character.isUpperCase(text.charAt(i)))
				return false;
		return true;
	}

	public static boolean isUpperCase(CharSequence text) {
		for (int i = 0, l = text.length(); i < l; i++)
			if (Character.isLowerCase(text.charAt(i)))
				return false;
		return true;
	}

	public static <T> String fromCamelHumps(String delimToInsert, CharSequence text) {
		if (isLowerCase(text))
			return text.toString();
		return fromCamelHumps(delimToInsert, text, new StringBuilder()).toString();
	}

	public static <T> String toCamelHumps(String delim, String text, boolean uppercaseFirst) {
		return toCamelHumps(delim, text, uppercaseFirst, new StringBuilder(text.length())).toString();
	}

	public static StringBuilder fromCamelHumps(String delimToInsert, CharSequence text, StringBuilder sb) {
		final int len = text.length();
		if (len == 0)
			return sb;
		sb.append(Character.toLowerCase(text.charAt(0)));
		int start = 1;
		for (int i = start; i < len; i++) {
			char c = text.charAt(i);
			if (Character.isUpperCase(text.charAt(i))) {
				sb.append(text, start, i);
				sb.append(delimToInsert);
				sb.append(Character.toLowerCase(c));
				start = i + 1;
			}
		}
		sb.append(text, start, len);
		return sb;
	}

	public static StringBuilder toCamelHumps(String delim, String text, boolean uppercaseFirst, StringBuilder sb) {
		final int len = text.length();
		if (len == 0)
			return sb;
		int dl = delim.length();
		int start = 0;
		for (int i = 0; i < len; i += dl, start = i) {
			i = indexOfOr(delim, text, i, len);
			if (start == 0 && !uppercaseFirst) {
				toLowerCase(text, start, i - start, sb);
			} else
				uppercaseFirstCharLowerRest(text.substring(start, i), sb);
		}
		return sb;
	}

	private static int indexOfOr(String delim, String text, int start, int defaultValue) {
		final int r = text.indexOf(delim, start);
		return r == -1 ? defaultValue : r;
	}

	public static StringBuilder clear(StringBuilder sb) {
		sb.setLength(0);
		return sb;
	}

	public static StringBuilder toObjectString(Object object, StringBuilder sb) {
		if (object == null)
			return sb.append("null");
		sb.append(object.getClass().getName()).append('@');
		SH.toString(System.identityHashCode(object), 16, sb);
		return sb;
	}
	public static String toObjectString(Object object) {
		if (object == null)
			return "null";
		return toObjectString(object, new StringBuilder()).toString();
	}
	public static StringBuilder toObjectStringSimple(Object object, StringBuilder sb) {
		if (object == null)
			return sb.append("null");
		getSimpleName(object.getClass(), sb);
		sb.append('@');
		SH.toString(System.identityHashCode(object), 16, sb);
		return sb;
	}
	public static String toObjectStringSimple(Object object) {
		if (object == null)
			return "null";
		return toObjectStringSimple(object, new StringBuilder()).toString();
	}

	private static final char DIGIT_TO_CHARS[] = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ!@#$%^&*()_=`~,.<>/\\?|".toCharArray();
	private static final int CHARS_TO_DIGIT[] = new int[256];
	private static final int CHARS_TO_DIGIT_NOCASE[] = new int[256];
	static {
		AH.fill(CHARS_TO_DIGIT, -1);
		AH.fill(CHARS_TO_DIGIT_NOCASE, -1);
		for (int i = 0; i < DIGIT_TO_CHARS.length; i++) {
			char c = DIGIT_TO_CHARS[i];
			CHARS_TO_DIGIT[c] = i;
			if (i < 62) {
				CHARS_TO_DIGIT_NOCASE[c] = OH.isBetween(c, 'A', 'Z') ? (i - 26) : i;
			}
		}
	}

	public static String toString(int value, int base) {
		if (base == 10)
			return toString(value);
		StringBuilder sb = new StringBuilder();
		return toString(value, base, sb).toString();
	}

	public static String toString(long value, int base) {
		if (base == 10)
			return toString(value);
		StringBuilder sb = new StringBuilder();
		return toString(value, base, sb).toString();
	}

	public static char getDigitToChar(int digit) {
		return DIGIT_TO_CHARS[digit];
	}

	public static StringBuilder toString(long value, int base, StringBuilder sink) {
		if (base == 10 && OH.isBetween(value, -MAX_CACHED_INTS_SIZE, MAX_CACHED_INTS_SIZE))
			return sink.append(getCachedStringForInt((int) value));
		OH.assertBetween(base, 2, DIGIT_TO_CHARS.length);
		boolean wasMinValue = false;
		if (value < 0) {
			if (value == Long.MIN_VALUE) {
				value = value + 1;
				wasMinValue = true;
			}
			sink.append('-');
			value = -value;
		}
		int cnt = 1, start = sink.length();
		for (long i = value; i >= base; i /= base)
			cnt++;
		sink.setLength(start + cnt);
		if (wasMinValue) {
			sink.setCharAt(start + --cnt, DIGIT_TO_CHARS[1 + (int) (value % base)]);
			value /= base;
		}
		while (cnt > 0) {
			sink.setCharAt(start + --cnt, DIGIT_TO_CHARS[(int) (value % base)]);
			value /= base;
		}
		return sink;
	}
	public static String comma(long value) {
		return toStringWithCommas(',', value);
	}
	public static String toStringWithCommas(char comma, long value) {
		return toStringWithCommas(comma, value, new StringBuilder()).toString();
	}
	public static StringBuilder toStringWithCommas(char comma, long value, StringBuilder sink) {
		boolean wasMinValue = false;
		if (value < 0) {
			if (value == Long.MIN_VALUE) {
				value = value + 1;
				wasMinValue = true;
			}
			sink.append('-');
			value = -value;
		}
		int cnt = 1, start = sink.length();
		for (long i = value; i >= 10; i /= 10)
			cnt++;
		int commasCnt = (cnt - 1) / 3;
		int nextComma = 0;
		sink.setLength(start + cnt + commasCnt);
		if (wasMinValue) {
			nextComma++;
			sink.setCharAt(start + commasCnt + --cnt, DIGIT_TO_CHARS[1 + (int) (value % 10)]);
			value /= 10;
		}
		while (cnt > 0) {
			if (nextComma == 3) {
				sink.setCharAt(start + --commasCnt + cnt, comma);
				nextComma = 0;
			}
			sink.setCharAt(start + commasCnt + --cnt, DIGIT_TO_CHARS[(int) (value % 10)]);
			value /= 10;
			nextComma++;
		}
		return sink;
	}

	public static StringBuilder toString(int value, int base, StringBuilder sink) {
		if (base == 10 && OH.isBetween(value, -MAX_CACHED_INTS_SIZE, MAX_CACHED_INTS_SIZE))
			return sink.append(getCachedStringForInt(value));
		OH.assertBetween(base, 2, DIGIT_TO_CHARS.length);
		boolean wasMinValue = false;
		if (value < 0) {
			if (value == Integer.MIN_VALUE) {
				value = value + 1;
				wasMinValue = true;
			}
			sink.append('-');
			value = -value;
		}
		int cnt = 1, start = sink.length();
		for (int i = value; i >= base; i /= base)
			cnt++;
		sink.setLength(start + cnt);
		if (wasMinValue) {
			sink.setCharAt(start + --cnt, DIGIT_TO_CHARS[1 + (int) (value % base)]);
			value /= base;
		}
		while (cnt > 0) {
			sink.setCharAt(start + --cnt, DIGIT_TO_CHARS[value % base]);
			value /= base;
		}
		return sink;
	}

	public static String trim(StringBuilder s) {
		int start = 0, end = s.length();
		for (;;) {
			if (start == end)
				return "";
			if (s.charAt(start) > 0x20)
				break;
			start++;
		}
		for (;;) {
			if (s.charAt(end - 1) > 0x20)
				break;
			end--;
		}
		return s.substring(start, end);
	}
	public static StringBuilder trimInplace(StringBuilder s) {
		int start = 0, end = s.length();
		for (;;) {
			if (start == end)
				return substringInplace(s, 0, 0);
			if (s.charAt(start) > 0x20)
				break;
			start++;
		}
		for (;;) {
			if (s.charAt(end - 1) > 0x20)
				break;
			end--;
		}
		return substringInplace(s, start, end);
	}

	public static StringBuilder substringInplace(StringBuilder s, int start, int end) {
		if (s.length() != end)
			s.setLength(end);
		if (start != 0)
			s.delete(0, start);
		return s;
	}

	public static StringBuilder trim(String text, StringBuilder sink) {
		if (text == null)
			return sink;
		int start = 0, end = text.length();
		for (;;)
			if (start == end)
				return sink;
			else if (text.charAt(start) > 0x20) {
				for (;;)
					if (text.charAt(--end) > 0x20)
						return sink.append(text, start, end + 1);
					else if (start == end)
						return sink;
			} else
				start++;
	}

	public static int indexOfIgnoreCase(CharSequence text, CharSequence find, int i) {
		int len = find.length();
		if (len == 0)
			return i;
		if (text == null)
			return -1;
		final int end = text.length() - len;
		len--;
		final char c1 = Character.toUpperCase(find.charAt(0));
		final char c2 = Character.toLowerCase(c1);
		while (i <= end) {
			char c3 = text.charAt(i);
			if (c3 == c1 || c3 == c2) {
				for (int j = len;;)
					if (j == 0)
						return i;
					else if (Character.toUpperCase(text.charAt(i + j)) != Character.toUpperCase(find.charAt(j--)))
						break;
			}
			i += 1;
		}
		return -1;
	}
	public static int lastIndexOfIgnoreCase(CharSequence text, CharSequence find, int i) {
		int len = find.length();
		if (len == 0)
			return i;
		if (text == null)
			return -1;
		final int end = text.length() - len;
		len--;
		final char c1 = Character.toUpperCase(find.charAt(0));
		final char c2 = Character.toLowerCase(c1);
		if (i > end)
			i = end;
		while (i >= 0) {
			char c3 = text.charAt(i);
			if (c3 == c1 || c3 == c2) {
				for (int j = len;;)
					if (j == 0)
						return i;
					else if (Character.toUpperCase(text.charAt(i + j)) != Character.toUpperCase(find.charAt(j--)))
						break;
			}
			i -= 1;
		}
		return -1;
	}
	public static int indexOf(CharSequence text, CharSequence find, int start) {
		int len = find.length();
		if (len == 0)
			return start;
		if (text == null)
			return -1;
		final int end = text.length() - len;
		len--;
		final char c1 = find.charAt(0);
		while (start <= end) {
			char c3 = text.charAt(start);
			if (c3 == c1) {
				for (int j = len;;)
					if (j == 0)
						return start;
					else if (text.charAt(start + j) != find.charAt(j--))
						break;
			}
			start += 1;
		}
		return -1;
	}
	public static int indexOf(CharSequence text, char find, int start) {
		if (text == null)
			return -1;
		if (text instanceof String)
			return ((String) text).indexOf(find, start);
		for (final int end = text.length(); start < end; start++)
			if (text.charAt(start) == find)
				return start;
		return -1;
	}
	public static int indexOfNth(int n, CharSequence text, char find, int start) {
		if (text == null)
			return -1;
		OH.assertGe(n, 1);
		for (final int end = text.length(); start < end; start++)
			if (text.charAt(start) == find)
				if (--n == 0)
					return start;
		return -1;
	}
	public static int indexOfNthContinous(int n, CharSequence text, char find, int start) {
		if (text == null)
			return -1;
		OH.assertGe(n, 1);
		boolean justHit = false;
		for (final int end = text.length(); start < end; start++) {
			if (text.charAt(start) == find) {
				if (!justHit)
					if (--n == 0)
						return start;
			} else
				justHit = false;
		}
		return -1;
	}
	public static int indexOf(CharSequence text, char find, int start, int end) {
		if (text == null)
			return -1;
		if (text instanceof String)
			return ((String) text).indexOf(find, start);
		for (; start < end; start++)
			if (text.charAt(start) == find)
				return start;
		return -1;
	}
	public static boolean startsWith(CharSequence text, CharSequence find) {
		return startsWith(text, find, 0);
	}
	public static boolean endsWith(CharSequence text, CharSequence find) {
		return startsWith(text, find, text.length() - find.length());
	}
	public static boolean endsWithIgnoreCase(CharSequence text, CharSequence find) {
		int start = text.length() - find.length();
		return start >= 0 && startsWithIgnoreCase(text, find, start);
	}
	public static boolean startsWith(CharSequence text, CharSequence find, int start) {
		if (text == null)
			return false;
		int pos = find.length();
		if (pos == 0)
			return true;
		if (start < 0 || start + pos > text.length())
			return false;
		if (find.charAt(0) == text.charAt(start))
			for (;;)
				if (pos == 1)
					return true;
				else if (text.charAt(start + --pos) != find.charAt(pos))
					break;
		return false;
	}

	public static boolean startsWithIgnoreCase(CharSequence text, CharSequence find) {
		return startsWithIgnoreCase(text, find, 0);
	}
	public static boolean startsWithIgnoreCase(CharSequence text, CharSequence find, int start) {
		if (text == null || start < 0)
			return false;
		int pos = find.length();
		if (pos == 0)
			return true;
		if (start + pos > text.length())
			return false;
		if (equalsIgnoreCase(find.charAt(0), text.charAt(start)))
			for (;;)
				if (pos == 1)
					return true;
				else if (!equalsIgnoreCase(text.charAt(start + --pos), find.charAt(pos)))
					break;
		return false;
	}

	public static boolean isSubstringIgnoreCase(CharSequence text, CharSequence find) {
		if (text == null)
			return false;

		int pos = find.length();
		if (pos == 0)
			return true;

		if (pos > text.length())
			return false;

		for (int i = 0; i <= text.length() - find.length(); i++) {
			int j = 0;
			while (j < pos && equalsIgnoreCase(text.charAt(i + j), find.charAt(j)))
				j++;
			if (j == pos)
				return true;
		}

		return false;
	}

	public static TextMatcher m(String text) {
		try {
			return TextMatcherFactory.DEFAULT.toMatcher(text);
		} catch (Exception e) {
			return ConstTextMatcher.FALSE;
		}
	}
	public static TextMatcher m(CharReader text, StringBuilder sb) {
		try {
			return TextMatcherFactory.DEFAULT.toMatcher(text, sb);
		} catch (Exception e) {
			return ConstTextMatcher.FALSE;
		}
	}
	public static TextMatcher m(CharReader text, StringBuilder sb, boolean isCaseSensetive, boolean isFullMatch) {
		try {
			return TextMatcherFactory.getFactory(isCaseSensetive, isFullMatch).toMatcher(text, sb);
		} catch (Exception e) {
			return ConstTextMatcher.FALSE;
		}
	}
	public static TextMatcher mFilePattern(String text) {
		try {
			return TextMatcherFactory.DEFAULT_FILE_PATTERN.toMatcher(text);
		} catch (Exception e) {
			return ConstTextMatcher.FALSE;
		}
	}

	public static List<String> find(TextMatcher matcher, Iterable<String> text) {
		final List<String> r = new ArrayList<String>();
		for (String s : text)
			if (matcher.matches(s))
				r.add(s);
		return r;
	}

	public static Map<String, String> splitToMap(char delim, char associator, String text) {
		return splitToMap(new HashMap<String, String>(), delim, associator, text);
	}

	public static Map<String, String> splitToMapWithTrim(char delim, char associator, String text) {
		return splitToMapWithTrim(new HashMap<String, String>(), delim, associator, text);
	}

	public static Map<String, String> splitToMap(String delim, String associator, String text) {
		return splitToMap(new HashMap<String, String>(), delim, associator, text);
	}

	public static Map<String, String> splitToMap(char delim, char associator, char escape, String text) {
		return splitToMap(new HashMap<String, String>(), delim, associator, escape, text);
	}

	public static Map<String, String> splitToMap(Map<String, String> sink, char delim, char associator, String text) {
		if (text == null || text.length() == 0)
			return sink;
		for (int last = 0;; last++) {
			int i = text.indexOf(associator, last);
			if (i == -1)
				throw new RuntimeException("trailing text after char " + last + ": " + text);
			final String key = text.substring(last, i);
			last = text.indexOf(delim, ++i);
			if (last == -1) {
				sink.put(key, text.substring(i));
				return sink;
			} else
				sink.put(key, text.substring(i, last));
		}
	}
	public static Map<String, String> splitToMap(Map<String, String> sink, char delim, char associator, CharSequence text, int start, int end) {
		if (text == null || start == end)
			return sink;
		for (int last = start;; last++) {
			int i = indexOf(text, associator, last, end);
			if (i == -1)
				throw new RuntimeException("trailing text after char " + last + ": " + text);
			final String key = substring(text, last, i);
			last = indexOf(text, delim, ++i, end);
			if (last == -1) {
				sink.put(key, substring(text, i, end));
				return sink;
			} else
				sink.put(key, substring(text, i, last));
		}
	}
	public static Map<String, String> splitToMapNoThrow(Map<String, String> sink, char delim, char associator, CharSequence text, int start, int end) {
		if (text == null || start == end)
			return sink;
		for (int last = start;; last++) {
			int i = indexOf(text, associator, last, end);
			if (i == -1)
				return sink;
			final String key = substring(text, last, i);
			last = indexOf(text, delim, ++i, end);
			if (last == -1) {
				sink.put(key, substring(text, i, end));
				return sink;
			} else
				sink.put(key, substring(text, i, last));
		}
	}

	public static Map<String, String> splitToMap(Map<String, String> sink, char delim, char associator, char escape, CharSequence text, int start, int end) {
		//TODO: handle escape
		if (text == null || text.length() == 0)
			return sink;
		if (indexOf(text, escape, start) == -1)
			return splitToMap(sink, delim, associator, text, start, end);
		StringBuilder buf = new StringBuilder();
		int pos = start;
		for (;;) {
			final String key, val;
			for (;;) {
				if (pos == end) {
					if (buf.length() != 0)
						throw new RuntimeException("trailing text : " + buf);
					return sink;
				}
				char c = text.charAt(pos++);
				if (c == associator) {
					key = toStringAndClear(buf);
					break;
				}
				if (c == escape) {
					if (pos == end)
						throw new RuntimeException("trailing escape");
					buf.append(text.charAt(pos++));
				} else
					buf.append(c);
			}
			for (;;) {
				if (pos == end) {
					val = toStringAndClear(buf);
					sink.put(key, val);
					return sink;
				}
				char c = text.charAt(pos++);
				if (c == delim) {
					val = toStringAndClear(buf);
					break;
				}
				if (c == escape) {
					if (pos == end)
						throw new RuntimeException("trailing escape");
					buf.append(text.charAt(pos++));
				} else
					buf.append(c);
			}
			sink.put(key, val);
		}
	}
	public static Map<String, String> splitToMap(Map<String, String> sink, char delim, char associator, char escape, String text) {
		return text == null ? sink : splitToMap(sink, delim, associator, escape, text, 0, text.length());
	}

	public static Map<String, String> splitToMapWithTrim(Map<String, String> sink, char delim, char associator, String text) {
		if (text == null || text.length() == 0)
			return sink;
		for (int last = 0;; last++) {
			int i = text.indexOf(associator, last);
			if (i == -1)
				throw new RuntimeException("trailing text after char " + last + ": " + text);
			final String key = text.substring(last, i).trim();
			last = text.indexOf(delim, ++i);
			if (last == -1) {
				sink.put(key, text.substring(i).trim());
				return sink;
			} else
				sink.put(key, text.substring(i, last).trim());
		}
	}

	public static Map<String, String> splitToMap(Map<String, String> sink, String delim, String associator, String text) {
		if (text == null || text.length() == 0)
			return sink;
		final int associatorLength = associator.length(), delimLength = delim.length();
		for (int last = 0;; last += delimLength) {
			int i = text.indexOf(associator, last);
			if (i == -1)
				throw new RuntimeException("trailing text after char " + last + ": " + text);
			final String key = text.substring(last, i);
			last = text.indexOf(delim, i += associatorLength);
			if (last == -1) {
				sink.put(key, text.substring(i));
				return sink;
			} else
				sink.put(key, text.substring(i, last));
		}
	}

	public static String toUpperCase(String text) {
		return text == null ? null : text.toUpperCase();
	}

	public static String toLowerCase(String text) {
		return text == null ? null : text.toLowerCase();
	}

	public static StringBuilder toHex(byte buf[], StringBuilder sb) {
		for (int i = 0; i < buf.length; i++) {
			if (((int) buf[i] & 0xff) < 0x10)
				sb.append("0");
			sb.append(Long.toString((int) buf[i] & 0xff, 16));
		}

		return sb;
	}

	public static byte[] fromHex(String text) {
		final int len = text.length();
		if (len % 2 != 0)
			throw new IndexOutOfBoundsException("expecting even size text: " + text.length());
		if (len == 0)
			return OH.EMPTY_BYTE_ARRAY;
		final byte[] r = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			r[i / 2] = (byte) ((fromHex(text.charAt(i)) << 4) + fromHex(text.charAt(i + 1)));
		}
		return r;
	}

	private static int fromHex(char c) {
		switch (c) {
			case '0':
				return 0;
			case '1':
				return 1;
			case '2':
				return 2;
			case '3':
				return 3;
			case '4':
				return 4;
			case '5':
				return 5;
			case '6':
				return 6;
			case '7':
				return 7;
			case '8':
				return 8;
			case '9':
				return 9;
			case 'a':
			case 'A':
				return 10;
			case 'b':
			case 'B':
				return 11;
			case 'c':
			case 'C':
				return 12;
			case 'd':
			case 'D':
				return 13;
			case 'e':
			case 'E':
				return 14;
			case 'f':
			case 'F':
				return 15;
		}
		throw new RuntimeException("invalid hex char: " + c);
	}

	private static char toHexChar(int c) {
		switch (c) {
			case 0:
				return '0';
			case 1:
				return '1';
			case 2:
				return '2';
			case 3:
				return '3';
			case 4:
				return '4';
			case 5:
				return '5';
			case 6:
				return '6';
			case 7:
				return '7';
			case 8:
				return '8';
			case 9:
				return '9';
			case 10:
				return 'A';
			case 11:
				return 'B';
			case 12:
				return 'C';
			case 13:
				return 'D';
			case 14:
				return 'E';
			case 15:
				return 'F';
		}
		throw new RuntimeException("invalid hex char: " + c);
	}
	public static char toHexCharLower(int c) {
		switch (c) {
			case 0:
				return '0';
			case 1:
				return '1';
			case 2:
				return '2';
			case 3:
				return '3';
			case 4:
				return '4';
			case 5:
				return '5';
			case 6:
				return '6';
			case 7:
				return '7';
			case 8:
				return '8';
			case 9:
				return '9';
			case 10:
				return 'a';
			case 11:
				return 'b';
			case 12:
				return 'c';
			case 13:
				return 'd';
			case 14:
				return 'e';
			case 15:
				return 'f';
		}
		throw new RuntimeException("invalid hex char: " + c);
	}

	public static String toHex(byte[] data) {
		return toHex(data, new StringBuilder(data.length * 2)).toString();
	}

	public static int length(CharSequence cs) {
		return cs == null ? 0 : cs.length();
	}

	public static boolean isAscii(char a) {
		return (a >= 0x20 && a < 0x7f) || a == BYTE_TAB || a == BYTE_CR || a == BYTE_NEWLINE;
	}

	public static boolean isAscii(byte a) {
		return (a >= 0x20 && a < 0x7f) || a == BYTE_TAB || a == BYTE_CR || a == BYTE_NEWLINE;
	}

	private static final boolean[] URL_ENCODING_NECESSARY;
	static {
		URL_ENCODING_NECESSARY = new boolean[256];
		Arrays.fill(URL_ENCODING_NECESSARY, true);
		for (char c : "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ~-._".toCharArray())
			URL_ENCODING_NECESSARY[c] = false;
	}

	public static boolean needsUrlEncoding(char a) {
		return a > 255 || URL_ENCODING_NECESSARY[a];

	}

	public static final char[] SLASHES = new char[] { '/', '\\' };

	public static boolean isValidDomainName(String domainName) {
		for (int i = 0; i < domainName.length(); i++) {
			char c = domainName.charAt(i);
			if (c != '.' && c != '-' && c != '_' && OH.isntBetween(c, 'a', 'z') && OH.isntBetween(c, 'A', 'Z') && OH.isntBetween(c, '0', '9'))
				return false;
		}
		String[] parts = SH.split('.', domainName);
		if (OH.isntBetween(parts.length, 2, 127))
			return false;
		if (AH.last(parts).length() < 2)
			return false;
		for (String part : parts) {
			if (OH.isntBetween(part.length(), 1, 63) || startsWith(part, '-') || endsWith(part, '-'))
				return false;
		}
		return true;

	}
	public static boolean isValidEmail(String email) {
		int angle = email.indexOf('<');
		if (angle != -1) {
			int closeAngle = email.indexOf('>');
			if (closeAngle < angle)
				return false;
			String inner = email.substring(angle + 1, closeAngle);
			if (inner.indexOf('<') != -1)
				return false;
			return isValidEmail(inner);
		}
		int split = email.lastIndexOf('@');
		if (split == -1)
			return false;
		if (!isValidDomainName(email.substring(split + 1)))
			return false;
		email = email.substring(0, split);
		if (email.length() < 1)
			return false;
		if (startsWith(email, '"') && endsWith(email, '"'))
			return indexOf(email, '"', 1) == email.length() - 1;
		for (int i = 0; i < email.length(); i++) {
			char c = email.charAt(i);
			if (OH.isntBetween(c, 'a', 'z') && OH.isntBetween(c, 'A', 'Z') && c != '.' && c != '-' && c != '_' && c != '+' && OH.isntBetween(c, '0', '9'))
				return false;
		}
		return true;
	}

	public static boolean isAscii(byte[] b) {
		for (int i = 0; i < b.length; i++)
			if (!isAscii(b[i]))
				return false;
		return true;
	}
	public static boolean isAscii(byte[] b, int start, int end, double pctThreashold) {
		OH.assertBetween(pctThreashold, 0, 1, "PctThreshold");
		final int length = end - start;
		if (length == 0)
			return true;
		int count = 0;
		for (int i = start; i < end; i++)
			if (isAscii(b[i]))
				count++;
		return count >= pctThreashold * length;
	}
	public static boolean isAscii(byte[] b, double pctThreashold) {
		OH.assertBetween(pctThreashold, 0, 1, "PctThreshold");
		final int length = b.length;
		if (length == 0)
			return true;
		int count = 0;
		for (int i = 0; i < length; i++)
			if (isAscii(b[i]))
				count++;
		return count >= pctThreashold * length;
	}
	public static boolean isAscii(String b) {
		for (int i = 0, l = b.length(); i < l; i++)
			if (!isAscii(b.charAt(i)))
				return false;
		return true;
	}

	public static String encodeUrl(String url) {
		return encodeUrl(url, true);

	}
	public static String encodeUrl(String url, boolean spaceToPlus) {
		if (url == null)
			return null;
		final int len = url.length();
		int i = 0;
		char c;
		for (;;) {
			if (i == len)
				return url;
			c = url.charAt(i);
			if (needsUrlEncoding(c))
				break;
			i++;
		}
		int start = 0;
		StringBuilder sb = new StringBuilder(url.length());
		for (;;) {
			if (start == 0 || needsUrlEncoding(c)) {
				if (isSurrogate(c)) {
					encodeUrlSurrogate(c, url.charAt(++i), sb);
					start = i + 1;
				} else if (c == ' ' && spaceToPlus) {
					sb.append(url, start, i).append('+');
					start = i + 1;
				} else {
					sb.append(url, start, i);
					encodeUrl(c, sb);
					start = i + 1;
				}
			}
			if (++i == len) {
				sb.append(url, start, i);
				return sb.toString();
			}
			c = url.charAt(i);
		}
	}
	public static StringBuilder encodeUrl(String url, StringBuilder sb) {
		final int len = url.length();
		int i = 0;
		char c;
		for (;;) {
			if (i == len)
				return sb.append(url);
			c = url.charAt(i);
			if (needsUrlEncoding(c))
				break;
			i++;
		}
		int start = 0;
		for (;;) {
			if (start == 0 || needsUrlEncoding(c)) {
				if (isSurrogate(c)) {
					encodeUrlSurrogate(c, url.charAt(++i), sb);
				} else {
					if (c == ' ') {
						sb.append(url, start, i).append('+');
						start = i + 1;
					} else {
						sb.append(url, start, i);
						encodeUrl(c, sb);
						start = i + 1;
					}
				}
			}
			if (++i == len) {
				sb.append(url, start, i);
				return sb;
			}
			c = url.charAt(i);
		}
	}
	private static void encodeUrlSurrogate(char c, char c2, StringBuilder sb) {
		int uc = Character.toCodePoint(c, c2);
		encodeUrl2(0xf0 | ((uc >> 18)), sb);
		encodeUrl2(0x80 | ((uc >> 12) & 0x3f), sb);
		encodeUrl2(0x80 | ((uc >> 6) & 0x3f), sb);
		encodeUrl2(0x80 | (uc & 0x3f), sb);
	}

	public static void encodeUrl(char c, StringBuilder sb) {
		if ((c >= 0x0001) && (c <= 0x007F)) {
			encodeUrl2(c, sb);
		} else if (c > 0x07FF) {
			encodeUrl2(0xE0 | ((c >> 12) & 0x0F), sb);
			encodeUrl2(0x80 | ((c >> 6) & 0x3F), sb);
			encodeUrl2(0x80 | ((c >> 0) & 0x3F), sb);
		} else {
			encodeUrl2(0xC0 | ((c >> 6) & 0x1F), sb);
			encodeUrl2(0x80 | ((c >> 0) & 0x3F), sb);
		}

	}
	private static void encodeUrl2(int c, StringBuilder sb) {
		sb.append('%').append(toHexChar(c >> 4)).append(toHexChar(c & 0x0F));
	}

	public static String decodeUrl(String url) {
		try {
			final int len = url.length();
			int i = 0;
			char c;
			for (;;) {
				if (i == len)
					return url;
				c = url.charAt(i);
				if (c == '+' || c == '%')
					break;
				i++;
			}
			int start = 0;
			StringBuilder sb = new StringBuilder(url.length());
			for (;;) {
				if (c == '+') {
					sb.append(url, start, i).append(' ');
					start = ++i;
				} else if (c == '%') {
					sb.append(url, start, i);
					i = decodeUrl(url, i, sb);
					start = i;
				} else
					i++;
				if (i == len) {
					sb.append(url, start, i);
					return sb.toString();
				}
				c = url.charAt(i);
			}
		} catch (Exception e) {
			throw new RuntimeException("Error decoding url: " + url, e);
		}
	}

	private static int decodeUrl(CharSequence url, int i, StringBuilder sb) {
		int h = decodeUrl2(url, i);
		i = i + 3;
		final int c2, c3, c = (int) h & 0xff;
		switch (c >> 4) {
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
				sb.append((char) c);
				return i;
			case 12:
			case 13:
				/* 110x xxxx   10xx xxxx*/
				c2 = decodeUrl2(url, i);
				i = i + 3;
				if ((c2 & 0xC0) != 0x80)
					throw new RuntimeException("bad byte at " + (i - 2));
				sb.append((char) (((c & 0x1F) << 6) | (c2 & 0x3F)));
				return i;
			case 14:
				/* 1110 xxxx  10xx xxxx  10xx xxxx */
				c2 = decodeUrl2(url, i);
				i = i + 3;
				if ((c2 & 0xC0) != 0x80)
					throw new RuntimeException("bad byte at " + (i - 2));
				c3 = decodeUrl2(url, i);
				i = i + 3;
				if ((c3 & 0xC0) != 0x80)
					throw new RuntimeException("bad byte at " + (i - 2));
				sb.append((char) (((c & 0x0F) << 12) | ((c2 & 0x3F) << 6) | ((c3 & 0x3F) << 0)));
				return i;
			case 15:
				/* 1111 0xxx 10xx xxxx 10xx xxxx 10xx xxxx*/
				if ((c & 0x8) != 0)
					throw new RuntimeException("bad byte at " + (i - 2));
				c2 = decodeUrl2(url, i);
				i = i + 3;
				if ((c2 & 0xC0) != 0x80)
					throw new RuntimeException("bad byte at " + (i - 2));
				c3 = decodeUrl2(url, i);
				i = i + 3;
				if ((c3 & 0xC0) != 0x80)
					throw new RuntimeException("bad byte at " + (i - 2));
				int c4 = decodeUrl2(url, i);
				i = i + 3;
				if ((c4 & 0xC0) != 0x80)
					throw new RuntimeException("bad byte at " + (i - 2));
				int uc = (((byte) c << 18) ^ ((byte) c2 << 12) ^ ((byte) c3 << 6)
						^ ((byte) c4 ^ (((byte) 0xF0 << 18) ^ ((byte) 0x80 << 12) ^ ((byte) 0x80 << 6) ^ ((byte) 0x80 << 0))));
				sb.append(highSurrogate(uc));
				sb.append(lowSurrogate(uc));
				return i;

			default:
				throw new RuntimeException("bad byte at " + (i - 2));
		}
	}
	public static char highSurrogate(int codePoint) {
		return (char) ((codePoint >>> 10) + (Character.MIN_HIGH_SURROGATE - (Character.MIN_SUPPLEMENTARY_CODE_POINT >>> 10)));
	}
	public static char lowSurrogate(int codePoint) {
		return (char) ((codePoint & 0x3ff) + Character.MIN_LOW_SURROGATE);
	}
	public static boolean isSurrogate(char ch) {
		return ch >= Character.MIN_SURROGATE && ch < (Character.MAX_SURROGATE + 1);
	}
	private static int decodeUrl2(CharSequence url, int i) {
		OH.assertEq(url.charAt(i), '%');
		return (fromHex(url.charAt(i + 1)) << 4) + (fromHex(url.charAt(i + 2)));
	}

	public static boolean needsUrlDecoding(CharSequence cs, int start, int end) {
		while (start < end) {
			char c = cs.charAt(start++);
			if (c == '+' || c == '%')
				return true;
		}
		return false;
	}
	public static StringBuilder decodeUrl(CharSequence url, int start, int end, StringBuilder sink) {
		try {
			char c;
			for (int i = start; i < end; i++) {
				c = url.charAt(i);
				if (c == '+') {
					sink.append(url, start, i).append(' ');
					start = i + 1;
				} else if (c == '%') {
					sink.append(url, start, i);
					i = decodeUrl(url, i, sink) - 1;
					start = i + 1;
				}
			}
			return sink.append(url, start, end);
		} catch (Exception e) {
			throw new RuntimeException("Error decoding url: " + url, e);
		}
	}

	public static List<Character> toCharList(CharSequence cs) {
		final int len = cs.length();
		List<Character> r = new ArrayList<Character>(len);
		for (int i = 0; i < len; i++)
			r.add(cs.charAt(i));
		return r;
	}

	public static String fromCharList(List<Character> chars) {
		final StringBuilder sb = new StringBuilder(chars.size());
		for (char c : chars)
			sb.append(c);
		return sb.toString();
	}

	public static String intern(String string) {
		return string == null ? null : string.intern();
	}

	public static String formatDuration(long millis) {
		return formatDuration(millis, new StringBuilder()).toString();
	}
	public static StringBuilder formatDuration(long millis, StringBuilder sb) {
		if (millis < 0) {
			millis = -millis;
			sb.append('-');
		}
		int days = (int) TimeUnit.DAYS.convert(millis, TimeUnit.MILLISECONDS);
		if (days > 0) {
			millis -= TimeUnit.MILLISECONDS.convert(days, TimeUnit.DAYS);
			sb.append(days).append(days == 1 ? " day, " : " days, ");
		}

		int hours = (int) TimeUnit.HOURS.convert(millis, TimeUnit.MILLISECONDS);
		millis -= TimeUnit.MILLISECONDS.convert(hours, TimeUnit.HOURS);
		if (hours < 10)
			sb.append('0');
		sb.append(hours).append(":");

		int minutes = (int) TimeUnit.MINUTES.convert(millis, TimeUnit.MILLISECONDS);
		millis -= TimeUnit.MILLISECONDS.convert(minutes, TimeUnit.MINUTES);
		if (minutes < 10)
			sb.append('0');
		sb.append(minutes);

		int seconds = (int) TimeUnit.SECONDS.convert(millis, TimeUnit.MILLISECONDS);
		millis -= TimeUnit.MILLISECONDS.convert(seconds, TimeUnit.SECONDS);
		if (seconds > 0 || millis > 0) {
			sb.append(':');
			if (seconds < 10)
				sb.append('0');
			sb.append(seconds);
			if (millis > 0) {
				sb.append('.');
				if (millis < 10)
					sb.append("00");
				else if (millis < 100)
					sb.append('0');
				sb.append(millis);
			}
		}
		return sb;
	}
	public static boolean startsAndEndsWith(String text, String prefix, String suffix) {
		return startsWith(text, prefix) && endsWith(text, suffix);
	}

	public static boolean startsWith(String text, String prefix) {
		if (text == null)
			return false;
		int len = prefix.length();
		if (len > text.length())
			return false;
		else if (len == 0)
			return true;
		else if (prefix.charAt(len - 1) != text.charAt(len - 1))
			return false;
		return text.startsWith(prefix);
	}

	public static void toUpperCase(String text, int start, int length, StringBuilder sb) {
		if (start < 0 || start + length > text.length())
			throw new IndexOutOfBoundsException("start=" + start + ", length=" + length + ", text length: " + text.length());
		while (length-- > 0)
			sb.append(Character.toUpperCase(text.charAt(start++)));
	}
	public static StringBuilder toLowerCase(String text, int start, int length, StringBuilder sb) {
		if (start < 0 || start + length > text.length())
			throw new IndexOutOfBoundsException("start=" + start + ", length=" + length + ", text length: " + text.length());
		while (length-- > 0)
			sb.append(Character.toLowerCase(text.charAt(start++)));
		return sb;
	}

	//returns index of stopChar, or -1 if end reached
	public static int unescapeUntil(CharSequence text, int start, char escape, char stopChar, StringBuilder sink) {
		return unescapeUntil(text, start, text.length(), escape, stopChar, sink);
	}
	public static int unescapeUntil(CharSequence text, int start, int end, char escape, char stopChar, StringBuilder sink) {
		int i = start, tail = start;
		while (i < end) {
			char c = text.charAt(i);
			if (c == escape) {
				sink.append(text, tail, i);
				if (i++ == end)
					throw new IndexOutOfBoundsException("Last char can not be an escape '" + escape + "': " + text);
				tail = i;
			} else if (c == stopChar) {
				sink.append(text, tail, i);
				return i;
			}
			i++;
		}
		sink.append(text, tail, i);
		return -1;
	}
	public static int unescape(CharSequence text, int start, int end, char escape, StringBuilder sink) {
		int i = start, tail = start;
		while (i < end) {
			char c = text.charAt(i);
			if (c == escape) {
				sink.append(text, tail, i);
				if (i++ == end)
					throw new IndexOutOfBoundsException("Last char can not be an escape '" + escape + "': " + text);
				tail = i;
			}
			i++;
		}
		sink.append(text, tail, i);
		return -1;
	}

	public static String toStringAndClear(StringBuilder sb) {
		if (sb.length() == 0)
			return "";
		String r = sb.toString();
		sb.setLength(0);
		return r;
	}

	public static boolean areBetween(CharSequence text, char lower, char upper) {
		return areBetween(text, 0, text.length(), lower, upper);
	}
	public static boolean areBetween(CharSequence text, int start, int end, char lower, char upper) {
		while (start < end)
			if (!OH.isBetween(text.charAt(start++), lower, upper))
				return false;
		return true;
	}

	public static String last(String text, int maxLength) {
		return text == null || text.length() <= maxLength ? text : text.substring(text.length() - maxLength);
	}
	public static String dddLast(String text, int maxLength) {
		return text == null || text.length() <= maxLength ? text : ("..." + text.substring(text.length() - (maxLength - 3)));
	}
	public static String dddMiddle(String text, int maxLength) {
		if (text == null || text.length() <= maxLength)
			return text;
		return text.substring(0, maxLength / 2) + "..." + last(text, maxLength / 2);
	}

	public static StringBuilder last(String text, int maxLength, StringBuilder sink) {
		return text == null || text.length() <= maxLength ? sink.append(text) : sink.append(text, text.length() - maxLength, text.length());
	}
	public static StringBuilder dddLast(String text, int maxLength, StringBuilder sink) {
		return text == null || text.length() <= maxLength ? sink.append(text) : sink.append("...").append(text, text.length() - (maxLength - 3), text.length());
	}
	public static StringBuilder dddMiddle(String text, int maxLength, StringBuilder sink) {
		if (text == null || text.length() <= maxLength)
			return sink.append(text);
		sink.append(text, 0, maxLength / 2).append("...");
		return last(text, maxLength / 2, sink);
	}
	public static StringBuilder appendBytes(byte[] text, int start, int end, StringBuilder sink) {
		if (text == null)
			sink.append("null");
		else {
			sink.ensureCapacity(text.length + sink.length());
			while (start < end)
				sink.append((char) text[start++]);
		}
		return sink;
	}

	public static Character[] toCharacterArray(CharSequence s1) {
		final Character[] r = new Character[s1.length()];
		for (int i = 0; i < r.length; i++)
			r[i] = s1.charAt(i);
		return r;
	}
	public static char[] toCharArray(CharSequence s1) {
		if (s1 == null)
			return null;
		return toCharArray(s1, 0, s1.length());
	}
	public static char[] toCharArray(CharSequence s1, int start, int length) {
		if (s1 == null)
			return null;
		if (length == 0)
			return OH.EMPTY_CHAR_ARRAY;
		else if (s1 instanceof String && start == 0 && length == s1.length())
			return ((String) s1).toCharArray();
		final char[] r = new char[length];
		if (start == 0)
			for (int i = 0; i < length; i++)
				r[i] = s1.charAt(i);
		else
			for (int i = 0; i < length; i++)
				r[i] = s1.charAt(i + start);
		return r;
	}

	public static String[] trimStrings(String[] strings, char c) {
		for (int i = 0; i < strings.length; i++)
			strings[i] = SH.trim(c, strings[i]);
		return strings;
	}
	public static String[] trimStrings(String[] strings) {
		for (int i = 0; i < strings.length; i++)
			strings[i] = SH.trim(strings[i]);
		return strings;
	}

	public static String splice(String source, int start, int charsToRemove, String insert) {
		if (charsToRemove == 0 && insert.length() == 0)
			return source;
		return splice(source, start, charsToRemove, insert, new StringBuilder()).toString();
	}
	public static StringBuilder splice(String source, int start, int charsToRemove, String insert, StringBuilder out) {
		int length = source.length();
		if (start + charsToRemove > length)
			throw new IndexOutOfBoundsException("start + toRemove > length: " + start + " + " + charsToRemove + " > " + length);
		if (charsToRemove == 0 && insert.length() == 0)
			out.append(source);
		else {
			out.append(source, 0, start).append(insert).append(source, charsToRemove + start, source.length());
		}
		return out;
	}

	public static void escapeInplace(StringBuilder sb, int start, int end, char find, char escape) {
		int cnt = 0;
		for (int i = start; i < end; i++) {
			char c = sb.charAt(i);
			if (c == find || c == escape)
				cnt++;
		}
		if (cnt > 0) {
			int srcPos = sb.length() - 1;
			int dstPos = srcPos + cnt;
			sb.setLength(dstPos + 1);
			while (dstPos > srcPos) {
				final char c = sb.charAt(srcPos--);
				sb.setCharAt(dstPos--, c);
				if (c == find || c == escape)
					sb.setCharAt(dstPos--, escape);
			}
		}
	}
	public static void escapeInplace(StringBuilder sb, int start, int end, char find, char find2, char escape) {
		int cnt = 0;
		for (int i = start; i < end; i++) {
			char c = sb.charAt(i);
			if (c == find || c == find2 || c == escape)
				cnt++;
		}
		if (cnt > 0) {
			int srcPos = sb.length() - 1;
			int dstPos = srcPos + cnt;
			sb.setLength(dstPos + 1);
			while (dstPos > srcPos) {
				final char c = sb.charAt(srcPos--);
				sb.setCharAt(dstPos--, c);
				if (c == find || c == find2 || c == escape)
					sb.setCharAt(dstPos--, escape);
			}
		}
	}

	public static float parseFloat(CharSequence f) {
		return (float) parseDouble(f, 0, f.length());
	}
	public static float parseFloat(CharSequence f, int start, int end) {
		return (float) parseDouble(f, start, end);
	}
	public static double parseDouble(CharSequence f) {
		return parseDouble(f, 0, f.length());
	}
	public static double parseDouble(CharSequence f, int start, int end) {
		char c = f.charAt(start);
		if ((c == 'N' || c == 'n') && SH.equalsIgnoreCase(f, "NaN"))
			return Double.NaN;
		else if (c == 'I' || c == 'i') {
			if (SH.equalsIgnoreCase(f, "Infinity") || SH.equalsIgnoreCase(f, "+Infinity"))
				return Double.POSITIVE_INFINITY;
		} else if (c == '-' && end - start > 1) {
			c = f.charAt(start + 1);
			if (SH.equalsIgnoreCase(f, "-Infinity"))
				return Double.NEGATIVE_INFINITY;
		}
		return Double.parseDouble(substring(f, start, end));
	}

	public static StringBuilder appendWithDelim(char delim, Object string, StringBuilder sb) {
		if (sb.length() > 0)
			sb.append(delim);
		return sb.append(string);
	}

	public static String describe(Exception e) {
		if (e == null)
			return "null";
		String message = e.getMessage();
		return SH.is(message) ? message : e.getClass().getSimpleName();
	}

	public static void ensureExtraCapacity(StringBuilder sink, int extraCapacity) {
		sink.ensureCapacity(sink.length() + extraCapacity);
	}

	public static StringBuilder shuffle(StringBuilder text, Random rnd) {
		for (int i = text.length(); i > 1; i--)
			swapChars(text, i - 1, rnd.nextInt(i));
		return text;
	}

	public static void swapChars(StringBuilder text, int pos1, int pos2) {
		if (pos1 == pos2)
			return;
		final char t = text.charAt(pos1);
		text.setCharAt(pos1, text.charAt(pos2));
		text.setCharAt(pos2, t);
	}

	public final static StringBuilder bytesToUtf(byte bytearr[], StringBuilder sink) {
		return bytesToUtf(bytearr, 0, bytearr.length, sink);
	}

	public final static StringBuilder bytesToUtf(byte bytes[], int start, int end, StringBuilder sink) {
		if (end > bytes.length || start < 0 || start > end)
			throw new IndexOutOfBoundsException("for array length " + bytes.length + ": start=" + start + ", length=" + end);

		for (;;) {
			if (start == end)
				return sink;
			final byte b = bytes[start++];
			if (b < 0) {
				start--;
				break;
			}
			sink.append((char) b);
		}

		while (start < end) {
			final int c2, c3, c = (int) bytes[start++] & 0xff;
			switch (c >> 4) {
				case 0:
				case 1:
				case 2:
				case 3:
				case 4:
				case 5:
				case 6:
				case 7:
					sink.append((char) c);
					break;
				case 12:
				case 13:
					/* 110x xxxx   10xx xxxx*/
					c2 = (int) bytes[start++];
					if ((c2 & 0xC0) != 0x80)
						throw new RuntimeException("bad byte at " + (start - 1));
					sink.append((char) (((c & 0x1F) << 6) | (c2 & 0x3F)));
					break;
				case 14:
					/* 1110 xxxx  10xx xxxx  10xx xxxx */
					c2 = (int) bytes[start++];
					if ((c2 & 0xC0) != 0x80)
						throw new RuntimeException("bad byte at " + (start - 1));
					c3 = (int) bytes[start++];
					if ((c3 & 0xC0) != 0x80)
						throw new RuntimeException("bad byte at " + (start - 1));
					sink.append((char) (((c & 0x0F) << 12) | ((c2 & 0x3F) << 6) | ((c3 & 0x3F) << 0)));
					break;
				default:
					throw new RuntimeException("bad byte at " + (start - 1));
			}
		}
		return sink;
	}
	public static int writeUTF(CharSequence str, OutputStream sink) throws IOException {
		final int strlen = str.length();
		final byte[] bytearr = new byte[Math.min(strlen + 3, 100)];
		return writeUTF(str, sink, bytearr);
	}
	public static int writeUTF(CharSequence str, OutputStream sink, byte[] bytearr) throws IOException {
		final int strlen = str.length();
		int buffSize = 0;
		int r = 0;

		for (int i = 0; i < strlen; i++) {
			int c = str.charAt(i);
			if ((c >= 0x0001) && (c <= 0x007F)) {
				bytearr[buffSize++] = (byte) c;

			} else if (c > 0x07FF) {
				bytearr[buffSize++] = (byte) (0xE0 | ((c >> 12) & 0x0F));
				bytearr[buffSize++] = (byte) (0x80 | ((c >> 6) & 0x3F));
				bytearr[buffSize++] = (byte) (0x80 | ((c >> 0) & 0x3F));
			} else {
				bytearr[buffSize++] = (byte) (0xC0 | ((c >> 6) & 0x1F));
				bytearr[buffSize++] = (byte) (0x80 | ((c >> 0) & 0x3F));
			}
			if (buffSize + 3 > bytearr.length) {
				sink.write(bytearr, 0, buffSize);
				r += buffSize;
				buffSize = 0;
			}
		}
		if (buffSize > 0)
			sink.write(bytearr, 0, buffSize);
		return r + buffSize;
	}

	/**
	 * the substring of l starting at lStart begins with r
	 * 
	 * @param l
	 * @param lStart
	 * @param r
	 * @return
	 */
	public static boolean equalsAt(CharSequence l, int lStart, CharSequence r) {
		int rStart = 0, lEnd = lStart + r.length();
		if (lEnd > l.length())
			return false;
		while (lStart < lEnd)
			if (l.charAt(lStart++) != r.charAt(rStart++))
				return false;
		return true;
	}
	public static boolean equals(CharSequence l, int lStart, int lEnd, CharSequence r, int rStart, int rEnd) {
		if (lEnd - lStart != rEnd - rStart || lEnd > l.length() || rEnd > r.length() || lStart > lEnd || rStart > rEnd)
			return false;
		while (lStart < lEnd)
			if (l.charAt(lStart++) != r.charAt(rStart++))
				return false;
		return true;
	}
	public static boolean equals(CharSequence l, CharSequence r, int rStart) {
		if (rStart + l.length() > r.length())
			return false;
		for (int n = 0; n < l.length();)
			if (l.charAt(n++) != r.charAt(rStart++))
				return false;
		return true;
	}
	public static boolean equalsIgnoreCase(CharSequence l, CharSequence r, int rStart) {
		if (rStart + l.length() > r.length())
			return false;
		for (int n = 0; n < l.length(); n++)
			if (!equalsIgnoreCase(l.charAt(n), r.charAt(rStart++)))
				return false;
		return true;
	}
	public static boolean equalsIgnoreCase(CharSequence l, CharSequence r, int rStart, int rEnd) {
		if (l.length() != rEnd - rStart)
			return false;
		for (int n = 0; n < l.length(); n++)
			if (!equalsIgnoreCase(l.charAt(n), r.charAt(rStart++)))
				return false;
		return true;
	}

	public static int count(char find, CharSequence text) {
		return count(find, text, 0, text.length());
	}
	public static int count(char find, CharSequence text, int start, int end) {
		int r = 0;
		for (int i = start; i < end; i++)
			if (text.charAt(i) == find)
				r++;
		return r;
	}
	public static int count(CharSequence find, CharSequence text, boolean checkOverlap) {
		return count(find, text, 0, text.length(), checkOverlap);
	}
	public static int count(CharSequence find, CharSequence text, int start, int end, boolean checkOverlap) {
		int r = 0;
		int len = find.length();
		if (len < 2)
			return len == 0 ? 0 : count(find.charAt(0), text, start, end);
		end -= len - 1;
		final char findChar = find.charAt(0);
		outer: for (int i = start; i < end; i++)
			if (text.charAt(i) == findChar) {
				for (int j = 1; j < len; j++)
					if (text.charAt(i + j) != find.charAt(j))
						continue outer;
				r++;
				if (!checkOverlap)
					i += len - 1;
			}
		return r;
	}

	public static StringBuilder lowercaseInplace(StringBuilder sb) {
		return lowercaseInplace(sb, 0, sb.length());
	}
	public static StringBuilder uppercaseInplace(StringBuilder sb) {
		return uppercaseInplace(sb, 0, sb.length());
	}
	public static StringBuilder uppercaseInplace(StringBuilder sb, int start, int end) {
		for (int i = start; i < end; i++)
			sb.setCharAt(i, Character.toUpperCase(sb.charAt(i)));
		return sb;
	}
	public static StringBuilder lowercaseInplace(StringBuilder sb, int start, int end) {
		for (int i = start; i < end; i++)
			sb.setCharAt(i, Character.toLowerCase(sb.charAt(i)));
		return sb;
	}

	public static void trimTrailingInPlace(StringBuilder sb, char c) {
		int i = sb.length();
		while (i > 0 && sb.charAt(i - 1) == c)
			i--;
		sb.setLength(i);
	}

	private static final byte CASE_DIFF = 'a' - 'A';

	public static void uppercaseInplace(byte[] bytes) {
		uppercaseInplace(bytes, 0, bytes.length);
	}
	public static void lowercaseInplace(byte[] bytes) {
		lowercaseInplace(bytes, 0, bytes.length);
	}
	public static void uppercaseInplace(char[] bytes) {
		uppercaseInplace(bytes, 0, bytes.length);
	}
	public static void lowercaseInplace(char[] bytes) {
		lowercaseInplace(bytes, 0, bytes.length);
	}
	public static void uppercaseInplace(char[] bytes, int start, int end) {
		for (int i = start; i < end; i++) {
			final char b = bytes[i];
			if (b >= 'a' && b <= 'z')
				bytes[i] = Character.toUpperCase(bytes[i]);
		}
	}
	public static void lowercaseInplace(char[] bytes, int start, int end) {
		for (int i = start; i < end; i++) {
			final char b = bytes[i];
			if (b >= 'a' && b <= 'z')
				bytes[i] = Character.toLowerCase(bytes[i]);
		}
	}
	public static void uppercaseInplace(byte[] bytes, int start, int end) {
		for (int i = start; i < end; i++) {
			final byte b = bytes[i];
			if (b >= 'a' && b <= 'z')
				bytes[i] = (byte) (b - CASE_DIFF);
		}
	}
	public static void lowercaseInplace(byte[] bytes, int start, int end) {
		for (int i = start; i < end; i++) {
			final byte b = bytes[i];
			if (b >= 'A' && b <= 'Z')
				bytes[i] = (byte) (b + CASE_DIFF);
		}
	}

	public static char removeChar(StringBuilder buf, int pos) {
		final char r = buf.charAt(pos);
		final int length = buf.length() - 1;
		while (pos < length)
			buf.setCharAt(pos++, buf.charAt(pos));
		buf.setLength(length);
		return r;

	}

	public static StringBuilder getSimpleName(Class<?> clazz, StringBuilder sb) {
		if (clazz.isArray())
			return getSimpleName(clazz.getComponentType(), sb).append("[]");
		final String n = clazz.getName();
		return afterLast(n, clazz.getEnclosingClass() == null ? '.' : '$', n, sb);
	}

	public static int lastIndexOf(CharSequence text, char ch) {
		return lastIndexOf(text, text.length() - 1, ch);
	}
	public static int lastIndexOf(CharSequence text, int fromIndex, char ch) {
		for (int i = fromIndex; i >= 0; i--)
			if (text.charAt(i) == ch)
				return i;
		return -1;
	}
	public static String getNextId(String id, Set<String> existing) {
		return getNextId(id, existing, 0);
	}
	public static String getNextId(String id, Set<String> existing, int startId) {
		if (!existing.contains(id)) {
			if (id == null || id.length() == 0)
				id = "0";
			else
				return id;
		}
		int num = Math.max(startId - 1, 0);
		int numStart = id.length();
		// Check that last few characters are digits. 
		// Decrement numStart to be equal to 
		// id.length() - (# of digits at the end of the string)  
		while (numStart > 0 && OH.isBetween(id.charAt(numStart - 1), '0', '9'))
			numStart--;
		if (numStart < id.length()) {
			num = parseInt(id, numStart, id.length(), 10);
			id = id.substring(0, numStart);
		}
		if (id.length() == 0) {
			for (;;) {
				String r = SH.toString(++num);
				if (!existing.contains(r))
					return r;
			}
		} else {
			for (;;) {
				String r = id + (++num);
				if (!existing.contains(r))
					return r;
			}
		}
	}

	public static String quoteToJavaConst(char quote, String text) {
		return quoteToJavaConst(quote, text, new StringBuilder()).toString();
	}
	public static StringBuilder quoteToJavaConst(char quote, CharSequence text, StringBuilder sink) {
		return quoteToJavaConst(quote, text, 0, text.length(), sink);
	}

	public static StringBuilder quoteToJavaConst(char quote, CharSequence text, int start, int end, StringBuilder sink) {
		sink.append(quote);
		for (int i = start; i < end; i++) {
			final char c = text.charAt(i);
			final char c2 = fromSpecial(c, quote);
			if (c2 == CHAR_NOT_SPECIAL)
				sink.append(c);
			else {
				sink.append(CHAR_BACKSLASH).append(c2);
			}
		}
		sink.append(quote);
		return sink;
	}

	public static void assertIs(String string) {
		if (isnt(string))
			if (string == null)
				throw new NullPointerException("String required");
			else if (string.length() == 0)
				throw new RuntimeException("empty string");
			else if (string.length() > 0)
				throw new RuntimeException("string contains only whitespaces");
	}
	public static void assertIs(String string, String description) {
		if (isnt(string))
			if (string == null)
				throw new NullPointerException(description + " required");
			else if (string.length() == 0)
				throw new RuntimeException(description + " is empty");
			else if (string.length() != 0)
				throw new RuntimeException(description + " contains only whitespaces");
	}

	/**
	 * Valid foramts are: <BR>
	 * HH:MM <BR>
	 * HH:MM:SS <BR>
	 * HH:MM::SS.sss <BR>
	 * NN HOURS|MINUTES|SECONDS|...<see java.concurrent.TimeUnit> <BR>
	 */
	static public long parseDurationTo(String quantityAndTimeUnit, TimeUnit target) {
		if (quantityAndTimeUnit == null)
			throw new NullPointerException("Invalid null quantity and time unit");
		try {
			final long sourceQuantity;
			final TimeUnit sourceUnit;
			if (quantityAndTimeUnit.indexOf(':') != -1) {//format is hh:mm or hh:mm:ss or hh:mm:ss.sss
				sourceQuantity = parseTime(quantityAndTimeUnit);
				sourceUnit = TimeUnit.MILLISECONDS;
			} else {
				sourceQuantity = SH.parseLong(SH.trim(SH.beforeFirst(quantityAndTimeUnit, ' ')));
				final String unit = SH.toUpperCase(SH.trim(SH.afterFirst(quantityAndTimeUnit, ' ')));
				sourceUnit = TimeUnit.valueOf(unit.endsWith("S") ? unit : (unit + 'S'));
			}
			return target.convert(sourceQuantity, sourceUnit);
		} catch (Exception e) {
			throw new RuntimeException(
					"Invalid duration format: '" + quantityAndTimeUnit + "' (expecting: #### units, ex: 13 HOURS) Valid units are : " + EnumSet.allOf(TimeUnit.class));
		}
	}

	/**
	 * Valid foramts are: <BR>
	 * HH:MM <BR>
	 * HH:MM:SS <BR>
	 * HH:MM::SS.sss <BR>
	 * NN HOURS|MINUTES|SECONDS|...<see java.concurrent.TimeUnit> <BR>
	 */
	static public long parseDurationTo(String quantityAndTimeUnit, TimeUnit target, long defaultDuration) {
		if (quantityAndTimeUnit == null)
			return defaultDuration;
		try {
			final long sourceQuantity;
			final TimeUnit sourceUnit;
			if (quantityAndTimeUnit.indexOf(':') != -1) {//format is hh:mm or hh:mm:ss or hh:mm:ss.sss
				sourceQuantity = parseTime(quantityAndTimeUnit);
				sourceUnit = TimeUnit.MILLISECONDS;
			} else {
				sourceQuantity = SH.parseLong(SH.trim(SH.beforeFirst(quantityAndTimeUnit, ' ')));
				final String unit = SH.toUpperCase(SH.trim(SH.afterFirst(quantityAndTimeUnit, ' ')));
				sourceUnit = TimeUnit.valueOf(unit.endsWith("S") ? unit : (unit + 'S'));
			}
			return target.convert(sourceQuantity, sourceUnit);
		} catch (Exception e) {
			return defaultDuration;
		}
	}

	//supported formats: hh:mm    hh:mm:ss    hh:mm:ss.sss
	public static long parseTime(CharSequence time) {
		int i = indexOf(time, ':', 0);
		if (i != -1) {
			boolean neg = time.charAt(0) == '-';

			try {
				long r = OH.assertBetween(SH.parseInt(time, neg ? 1 : 0, i++, 10), 0, 23) * 3600000L;
				int j = indexOf(time, ':', i);
				if (j == -1) {
					r += OH.assertBetween(SH.parseInt(time, i, time.length(), 10), 0, 59) * 60000L;
				} else {
					r += OH.assertBetween(SH.parseInt(time, i, j++, 10), 0, 59) * 60000L;
					int k = indexOf(time, '.', j);
					if (k == -1)
						r += OH.assertBetween(SH.parseInt(time, j, time.length(), 10), 0, 59) * 1000L;
					else {
						r += OH.assertBetween(SH.parseInt(time, j, k++, 10), 0, 59) * 1000L;
						r += OH.assertBetween(SH.parseInt(time, k, time.length(), 10), 0, 999);
					}
				}
				return neg ? -r : r;
			} catch (AssertionException e) {
				throw new RuntimeException("Invalid time (" + e.getMessage() + ") ==> " + time);
			} catch (Exception e) {
			}
		}
		throw new RuntimeException("Invalid time format (supported include hh:mm, hh:mm:ss, hh:mm:ss.SSS): " + time);
	}

	public static char toUpperCase(char c) {
		if ((c & 0xff) == c)
			return c >= 'a' && c <= 'z' ? ((char) (c - CASE_DIFF)) : c;
		return Character.toUpperCase(c);
	}
	public static char toLowerCase(char c) {
		if ((c & 0xff) == c)
			return c >= 'a' && c <= 'z' ? ((char) (c + CASE_DIFF)) : c;
		return Character.toLowerCase(c);
	}

	public static final String getOrdinalIndicator(int n) {
		if (n < 0)
			n = -n;
		if (OH.isBetween(n % 100, 11, 13))
			return "th";
		switch (n % 10) {
			case 1:
				return "st";
			case 2:
				return "nd";
			case 3:
				return "rd";
			default:
				return "th";
		}
	}

	public static StringBuilder reverse(StringBuilder sb, int start, int end) {
		for (; start < --end; start++) {
			final char c = sb.charAt(start);
			sb.setCharAt(start, sb.charAt(end));
			sb.setCharAt(end, c);
		}
		return sb;
	}

	public static String joinWithEscape(char delim, char escape, Object[] values) {
		return joinWithEscape(delim, escape, values, new StringBuilder()).toString();
	}
	public static StringBuilder joinWithEscape(char delim, char escape, Object[] values, StringBuilder sb) {
		for (int i = 0; i < values.length; i++) {
			if (i > 0)
				sb.append(delim);
			int start = sb.length();
			SH.s(values[i], sb);
			SH.escapeInplace(sb, start, sb.length(), delim, '\\');
		}
		return sb;
	}

	/**
	 * 
	 * @param text
	 * @return false if either the text is null or zero length... Any text including just white space will return true
	 */
	public static boolean isntEmpty(CharSequence text) {
		return text != null && text.length() > 0;
	}
	/**
	 * 
	 * @param text
	 * @return true if either the text is null or zero length... Any text including just white space will return false
	 */
	public static boolean isEmpty(CharSequence text) {
		return text == null || text.length() == 0;
	}

	private static final String SUPPRESS_MESSAGE_PREFIX = "<supressed ";
	private static final String SUPPRESS_MESSAGE_SUFFIX = " chars>";

	public static String suppress(String str, int max) {
		if (str == null || str.length() <= max)
			return str;
		int digits = MH.getDigitsCount(str.length(), 10);
		int extra = SUPPRESS_MESSAGE_PREFIX.length() + SUPPRESS_MESSAGE_SUFFIX.length() + digits;
		max = Math.max(0, max - extra);
		StringBuilder sb = new StringBuilder(max + extra);
		return sb.append(str, 0, max).append(SUPPRESS_MESSAGE_PREFIX).append(str.length() - max).append(SUPPRESS_MESSAGE_SUFFIX).toString();
	}

	public static byte parseByte(CharSequence s) {
		return parseByte(s, 0, s.length(), 10);
	}
	public static short parseShort(CharSequence s) {
		return parseShort(s, 0, s.length(), 10);
	}
	public static byte parseByte(CharSequence s, int start, int end, int radix) {
		final int r = parseInt(s, start, end, radix);
		if (r < Byte.MIN_VALUE || r > Byte.MAX_VALUE)
			throw new NumberFormatException("overflow: " + s.subSequence(start, end));
		return (byte) r;
	}
	public static short parseShort(CharSequence s, int start, int end, int radix) {
		final int r = parseInt(s, start, end, radix);
		if (r < Short.MIN_VALUE || r > Short.MAX_VALUE)
			throw new NumberFormatException("overflow: " + s.subSequence(start, end));
		return (short) r;
	}

	private static final RuntimeException PLACEHOLDER = new RuntimeException("Number Format Exception");

	public static byte parseByteSafe(CharSequence o, boolean throwNewOnError, boolean throwOnOverflow) {
		final int n = evaluateNumber(o);
		switch (n) {
			case NUMBER_EVALUATED_TO_FLOAT_NEG_INF:
				return Byte.MIN_VALUE;
			case NUMBER_EVALUATED_TO_FLOAT_POS_INF:
				return Byte.MAX_VALUE;
			case NUMBER_EVALUATED_TO_FLOAT_NAN:
				return 0;
			case NUMBER_EVALUATED_TO_STRING:
				if (throwNewOnError)
					parseShort(o);
		}
		long r = parseLongSafe(o, n, throwNewOnError);
		if (throwOnOverflow)
			OH.assertBetween(r, Byte.MIN_VALUE, Byte.MAX_VALUE);
		return (byte) r;
	}

	public static long parseLongSafe(CharSequence o, boolean throwNewOnError) {
		final int n = evaluateNumber(o);
		return parseLongSafe(o, n, throwNewOnError);
	}
	private static long parseLongSafe(CharSequence o, int n, boolean throwNewOnError) {
		if (n == NUMBER_EVALUATED_TO_STRING)
			if (throwNewOnError)
				return SH.parseLong(o, 10);
			else
				throw PLACEHOLDER;
		int length = o.length();
		outer: switch (o.charAt(length - 1)) {
			case 'F':
			case 'f':
			case 'D':
			case 'd':
				switch (n) {
					case NUMBER_EVALUATED_TO_HEX:
					case NUMBER_EVALUATED_TO_HEX_NEG:
					case NUMBER_EVALUATED_TO_HEX_POS:
						break outer;
				}
			case 'L':
			case 'l':
			case 'I':
			case 'i':
				length--;
		}
		switch (n) {
			case NUMBER_EVALUATED_TO_FLOAT_POS_INF:
				return Long.MAX_VALUE;
			case NUMBER_EVALUATED_TO_FLOAT_NEG_INF:
				return Long.MIN_VALUE;
			case NUMBER_EVALUATED_TO_FLOAT_NAN:
				return 0;
			case NUMBER_EVALUATED_TO_FLOAT_NO_DEC:
				return (long) SH.parseDouble(o, 0, length);
			case NUMBER_EVALUATED_TO_NO_DEC:
				return SH.parseLong(o, 0, length, 10);
			case NUMBER_EVALUATED_TO_HEX:
				return SH.parseLong(o, 2, length, 16);
			case NUMBER_EVALUATED_TO_HEX_NEG:
				return -SH.parseLong(o, 3, length, 16);
			case NUMBER_EVALUATED_TO_HEX_POS:
				return SH.parseLong(o, 3, length, 16);
			default:
				return SH.parseLong(o, 0, n, 10);
		}
	}
	public static short parseShortSafe(CharSequence o, boolean throwNewOnError, boolean throwOnOverflow) {
		final int n = evaluateNumber(o);
		switch (n) {
			case NUMBER_EVALUATED_TO_FLOAT_NEG_INF:
				return Short.MIN_VALUE;
			case NUMBER_EVALUATED_TO_FLOAT_POS_INF:
				return Short.MAX_VALUE;
			case NUMBER_EVALUATED_TO_FLOAT_NAN:
				return 0;
			case NUMBER_EVALUATED_TO_STRING:
				if (throwNewOnError)
					parseShort(o);
		}
		long r = parseLongSafe(o, n, throwNewOnError);
		if (throwOnOverflow)
			OH.assertBetween(r, Short.MIN_VALUE, Short.MAX_VALUE);
		return (short) r;
	}
	public static int parseIntSafe(CharSequence o, boolean throwNewOnError, boolean throwOnOverflow) {
		final int n = evaluateNumber(o);
		switch (n) {
			case NUMBER_EVALUATED_TO_FLOAT_NEG_INF:
				return Integer.MIN_VALUE;
			case NUMBER_EVALUATED_TO_FLOAT_POS_INF:
				return Integer.MAX_VALUE;
			case NUMBER_EVALUATED_TO_FLOAT_NAN:
				return 0;
			case NUMBER_EVALUATED_TO_STRING:
				if (throwNewOnError)
					parseShort(o);
		}
		long r = parseLongSafe(o, n, throwNewOnError);
		if (throwOnOverflow)
			OH.assertBetween(r, Integer.MIN_VALUE, Integer.MAX_VALUE);
		return (int) r;
	}
	public static double parseDoubleSafe(CharSequence o, boolean throwNewOnError) {
		final int n = evaluateNumber(o);
		if (n == NUMBER_EVALUATED_TO_STRING)
			if (throwNewOnError)
				return SH.parseDouble(o);
			else
				throw PLACEHOLDER;
		int length = o.length();
		outer: switch (o.charAt(length - 1)) {
			case 'F':
			case 'f':
			case 'D':
			case 'd':
				switch (n) {
					case NUMBER_EVALUATED_TO_HEX:
					case NUMBER_EVALUATED_TO_HEX_NEG:
					case NUMBER_EVALUATED_TO_HEX_POS:
						break outer;
				}
			case 'L':
			case 'l':
			case 'I':
			case 'i':
				length--;
		}
		switch (n) {
			case NUMBER_EVALUATED_TO_HEX:
				return SH.parseLong(o, 2, length, 16);
			case NUMBER_EVALUATED_TO_HEX_NEG:
				return -SH.parseLong(o, 3, length, 16);
			case NUMBER_EVALUATED_TO_HEX_POS:
				return SH.parseLong(o, 3, length, 16);
			case NUMBER_EVALUATED_TO_FLOAT_NAN:
				return Double.NaN;
			case NUMBER_EVALUATED_TO_FLOAT_POS_INF:
				return Double.POSITIVE_INFINITY;
			case NUMBER_EVALUATED_TO_FLOAT_NEG_INF:
				return Double.NEGATIVE_INFINITY;
			default: {
				return SH.parseDouble(o, 0, length);
			}
		}
	}
	public static float parseFloatSafe(CharSequence o, boolean throwNewOnError) {
		return (float) parseDoubleSafe(o, throwNewOnError);
	}

	public static final int NUMBER_EVALUATED_TO_FLOAT_NAN = -9;
	public static final int NUMBER_EVALUATED_TO_FLOAT_POS_INF = -8;
	public static final int NUMBER_EVALUATED_TO_FLOAT_NEG_INF = -7;
	public static final int NUMBER_EVALUATED_TO_HEX_NEG = -6;
	public static final int NUMBER_EVALUATED_TO_HEX_POS = -5;
	public static final int NUMBER_EVALUATED_TO_HEX = -4;
	public static final int NUMBER_EVALUATED_TO_STRING = -3;
	public static final int NUMBER_EVALUATED_TO_FLOAT_NO_DEC = -2;
	public static final int NUMBER_EVALUATED_TO_NO_DEC = -1;

	//return -3=bad number, return -2 = float but no decimal, -1 no decimal, but float, return 1> decimal position
	public static int evaluateNumber(CharSequence o) {
		int decPos = -1;
		int ePos = -1;
		char sign = 0;
		boolean isHex = false;
		final int l = o.length();
		if (l == 0)
			return NUMBER_EVALUATED_TO_STRING;
		for (int i = 0; i < l; i++) {
			switch (o.charAt(i)) {
				case '0':
				case '1':
				case '2':
				case '3':
				case '4':
				case '5':
				case '6':
				case '7':
				case '8':
				case '9':
					continue;
				case 'x':
				case 'X':
					if (o.charAt(i - 1) != '0')
						return NUMBER_EVALUATED_TO_STRING;
					if (i != (sign != 0 ? 2 : 1))
						return NUMBER_EVALUATED_TO_STRING;
					isHex = true;
					continue;
				case '-':
				case '+':
					if (i == 0) {
						sign = o.charAt(i);
						continue;
					}
					if (ePos != i - 1)
						return NUMBER_EVALUATED_TO_STRING;
					continue;
				case 'L':
				case 'l':
					if (i != l - 1 || l == 1)
						return NUMBER_EVALUATED_TO_STRING;
					continue;
				case 'A':
				case 'a':
				case 'B':
				case 'b':
				case 'C':
				case 'c':
					if (isHex)
						continue;
					return NUMBER_EVALUATED_TO_STRING;
				case 'D':
				case 'd':
					if (isHex)
						continue;
					if (i != l - 1 || l == 1)
						return NUMBER_EVALUATED_TO_STRING;
					else
						return decPos != -1 ? decPos : NUMBER_EVALUATED_TO_FLOAT_NO_DEC;
				case 'E':
				case 'e':
					if (isHex)
						continue;
					if (ePos != -1)
						return NUMBER_EVALUATED_TO_STRING;
					ePos = i;
					continue;
				case 'F':
				case 'f':
					if (isHex)
						continue;
					if (i != l - 1 || l == 1)
						return NUMBER_EVALUATED_TO_STRING;
					else
						return decPos != -1 ? decPos : NUMBER_EVALUATED_TO_FLOAT_NO_DEC;
				case 'N':
				case 'n':
					if (sign != 0)
						return equalsIgnoreCase(o, "-nan") || equalsIgnoreCase(o, "+nan") ? NUMBER_EVALUATED_TO_FLOAT_NAN : NUMBER_EVALUATED_TO_STRING;
					else
						return equalsIgnoreCase(o, "nan") ? NUMBER_EVALUATED_TO_FLOAT_NAN : NUMBER_EVALUATED_TO_STRING;
				case 'I':
				case 'i':
					if (sign != 0 && i == 1) {
						if (equalsIgnoreCase(o, "-infinity"))
							return NUMBER_EVALUATED_TO_FLOAT_NEG_INF;
						else if (equalsIgnoreCase(o, "+infinity"))
							return NUMBER_EVALUATED_TO_FLOAT_POS_INF;
					} else if (i == 0 && equalsIgnoreCase(o, "infinity"))
						return NUMBER_EVALUATED_TO_FLOAT_POS_INF;
					if (i != l - 1 || l == 1)
						return NUMBER_EVALUATED_TO_STRING;
					break;
				case '.':
					if (decPos != -1 || isHex)
						return NUMBER_EVALUATED_TO_STRING;
					decPos = i;
					continue;
				default:
					return NUMBER_EVALUATED_TO_STRING;
			}
		}
		if (isHex) {
			switch (sign) {
				case 0:
					return NUMBER_EVALUATED_TO_HEX;
				case '-':
					return NUMBER_EVALUATED_TO_HEX_NEG;
				case '+':
					return NUMBER_EVALUATED_TO_HEX_POS;
			}
		}
		return ePos != -1 ? NUMBER_EVALUATED_TO_FLOAT_NO_DEC : decPos != -1 ? decPos : NUMBER_EVALUATED_TO_NO_DEC;
	}
	public static String commonPrefix(Collection<String> values) {
		String r = null;
		for (String s : values)
			if (s == null)
				return null;
			else if (r == null)
				r = s;
			else if ((r = commonPrefix(r, s)).length() == 0)
				return "";
		return r;
	}

	public static String commonPrefix(String a, String b) {
		if (a == null || b == null)
			return null;
		int al = a.length();
		int bl = b.length();
		int l = Math.min(al, bl);
		int i = 0;
		for (; i < l; i++)
			if (a.charAt(i) != b.charAt(i))
				break;
		return i == 0 ? "" : (i == al ? a : (i == bl ? b : a.substring(0, i)));
	}
	public static String commonSuffix(Collection<String> values) {
		String r = null;
		for (String s : values)
			if (s == null)
				return null;
			else if (r == null)
				r = s;
			else if ((r = commonSuffix(r, s)).length() == 0)
				return "";
		return r;
	}

	public static String commonSuffix(String a, String b) {
		if (a == null || b == null)
			return null;
		int al = a.length();
		int bl = b.length();
		int l = Math.min(al, bl);
		int i = 0, an = al - 1, bn = bl - 1;
		for (; i < l; i++, an--, bn--)
			if (a.charAt(an) != b.charAt(bn))
				break;
		return i == 0 ? "" : (i == al ? a : (i == bl ? b : a.substring(al - i, a.length())));
	}

	static public int getDistance(CharSequence s, CharSequence t) {
		return getDistance(s, t, false);
	}

	static public int getDistance(CharSequence s, CharSequence t, boolean ignoreCase) {
		if (s == t)
			return 0;
		int sLength = s.length(), tLength = t.length();
		if (sLength == 0)
			return tLength;
		if (tLength == 0)
			return sLength;
		while (equals(s.charAt(sLength - 1), t.charAt(tLength - 1), ignoreCase)) {
			if (--sLength == 0)
				return tLength - 1;
			if (--tLength == 0)
				return sLength;
		}
		int sStart = 0, tStart = 0;
		while (equals(s.charAt(sStart), t.charAt(tStart), ignoreCase)) {
			if (--sLength == 0)
				return tLength - 1;
			if (--tLength == 0)
				return sLength;
			sStart++;
			tStart++;
		}

		final int len = tLength + 1;
		final int[] v0 = new int[len];
		final int[] v1 = v0.clone();

		for (int i = 0; i < len; i++)
			v0[i] = i;

		char[] schar = toCharArray(s, sStart, sLength), tchar = toCharArray(t, tStart, tLength);
		if (ignoreCase) {
			lowercaseInplace(schar);
			lowercaseInplace(tchar);
		}

		for (int i = 0; i < sLength; i++) {
			v1[0] = i + 1;
			for (int j = 0; j < tLength; j++)
				v1[j + 1] = Math.min(Math.min(v1[j], v0[j + 1]) + 1, schar[i] == tchar[j] ? v0[j] : v0[j] + 1);
			System.arraycopy(v1, 0, v0, 0, len);
		}

		return v1[tLength];
	}

	public static void main2(String a[]) {

		System.out.println(getDistance("CHOCOLATIER", "CHOCLOTIER"));
		System.out.println(getDistance("BRENDAN WHTA", "BRANDON WHAT"));
	}

	public static <T extends CharSequence> T getLongest(T[] candidates) {
		if (candidates == null || candidates.length == 0)
			return null;
		T r = candidates[0];
		int rlen = r.length();
		for (int i = 1; i < candidates.length; i++) {
			int len = candidates[i].length();
			if (len > rlen) {
				rlen = len;
				r = candidates[i];
			}
		}
		return r;
	}
	public static <T extends CharSequence> T getShortest(T[] candidates) {
		if (candidates == null || candidates.length == 0)
			return null;
		T r = candidates[0];
		int rlen = r.length();
		for (int i = 1; i < candidates.length; i++) {
			int len = candidates[i].length();
			if (len < rlen) {
				rlen = len;
				r = candidates[i];
			}
		}
		return r;
	}

	public static String[] splitLongLines(String lines[], int max, boolean breakOnWhiteSpace) {
		String t = getLongest(lines);
		if (t == null || t.length() < max)
			return lines.clone();
		ArrayList<String> r = new ArrayList<String>(lines.length * 2);
		for (String line : lines) {
			if (line.length() <= max)
				r.add(line);
			else if (breakOnWhiteSpace) {
				for (int i = 0; i < line.length();) {
					while (line.charAt(i) == ' ')
						if (++i == line.length())
							break;
					if (i + max >= line.length()) {
						r.add(line.substring(i, line.length()));
						break;
					}
					int n = line.lastIndexOf(' ', i + max + 1);
					if (n <= i) {
						r.add(line.substring(i, i += max));
					} else {
						r.add(line.substring(i, i = n).trim());
					}
				}
			} else {
				for (int i = 0; i < line.length();)
					r.add(line.substring(i, i = Math.min(line.length(), i + max)));
			}
		}
		return AH.toArray(r, String.class);
	}

	public static void main3(String a[]) throws IOException {
		String ss = "\uD83D\uDE0A";
		String s = "%F0%9F%98%8A";
		System.out.println((int) ss.charAt(0));
		System.out.println((int) ss.charAt(1));
		String base = "??";
		String s4 = toStringEncode(base);
		String s6 = toStringDecode(s4);
		System.out.println(s4);
		System.out.println(s6);
		//		String s3 = java.net.URLDecoder.decode(s, StandardCharsets.UTF_8.name());

		String s2 = decodeUrl(s);
		System.out.println((int) base.charAt(0));
		System.out.println((int) base.charAt(1));
		//		System.out.println((int) s3.charAt(0));
		//		System.out.println((int) s3.charAt(1));
		System.out.println((int) s2.charAt(0));
		System.out.println((int) s2.charAt(1));
		OH.assertEq(s2, base);
		System.out.println(s2);
		String s5 = encodeUrl(base);
		OH.assertEq(s, s5);
		OH.assertEq(base, ss);
	}
	public static boolean isWholeNumber(CharSequence o) {
		final int l = o.length();
		for (int i = 0; i < l; i++) {
			switch (o.charAt(i)) {
				case '0':
				case '1':
				case '2':
				case '3':
				case '4':
				case '5':
				case '6':
				case '7':
				case '8':
				case '9':
					continue;
				default:
					return false;
			}
		}
		return true;
	}

	public static StringBuilder replaceInline(StringBuilder target, int targetStartChar, int targetEndChar, CharSequence source, int sourceStartChar, int sourceEndChar) {
		int grow = (sourceEndChar - sourceStartChar) - (targetEndChar - targetStartChar);
		if (grow < 0) {
			target.delete(targetEndChar += grow, targetEndChar - grow);
		} else if (grow > 0)
			target.insert(targetEndChar, source, sourceEndChar -= grow, sourceEndChar + grow);
		while (sourceStartChar < sourceEndChar)
			target.setCharAt(targetStartChar++, source.charAt(sourceStartChar++));
		return target;
	}
	public static String indentAllLines(String input) {
		String[] lines = SH.splitLines(input);
		for (int i = 0; i < lines.length; i++) {
			lines[i] = "\t" + lines[i] + NEWLINE;
		}
		return SH.join("", lines);
	}

	public static String cut(CharSequence text, String delim, int... ranges) {
		if (ranges.length == 0 || text.length() == 0)
			return "";
		if ((ranges.length & 1) != 0)
			throw new IllegalArgumentException("ranges should be: start1,stop1,start2,stop2,....");

		boolean inOrder = true;
		for (int i = 0; i < ranges.length; i += 2)
			if (ranges[i] > ranges[i + 1]) {
				inOrder = false;
				break;
			} else if (i + 2 < ranges.length && ranges[i + 1] >= ranges[i + 2]) {
				inOrder = false;
				break;
			}
		int len = text.length();
		StringBuilder r = null;
		if (!inOrder) {
			CharSequence[] parts = split(delim, text);
			if (parts.length == 0)
				return "";
			for (int rangesPos = 0; rangesPos < ranges.length; rangesPos += 2) {
				int rangeStart = MH.clip(ranges[rangesPos], 0, parts.length);
				int rangeEnd = MH.clip(ranges[rangesPos + 1], 0, parts.length);
				if (rangeEnd >= rangeStart) {
					for (int i = rangeStart; i <= rangeEnd; i++) {
						if (r == null)
							r = new StringBuilder();
						else
							r.append(delim);
						r.append(parts[i]);
					}
				} else {
					for (int i = rangeStart; i >= rangeEnd; i--) {
						if (r == null)
							r = new StringBuilder();
						else
							r.append(delim);
						r.append(parts[i]);
					}
				}
			}
		} else if (delim.length() == 0) {
			for (int rangesPos = 0; rangesPos < ranges.length; rangesPos += 2) {
				int rangeStart = ranges[rangesPos];
				if (rangeStart >= len)
					break;
				if (r == null)
					r = new StringBuilder();
				int rangeEnd = ranges[rangesPos + 1] + 1;
				if (rangeEnd > len) {
					r.append(text, rangeStart, len);
					break;
				}
				r.append(text, rangeStart, rangeEnd);
			}
		} else if (delim.length() == 1) {
			char c = delim.charAt(0);
			int rangesPos = 0;
			int rangeStart = ranges[rangesPos];
			int rangeEnd = ranges[rangesPos + 1];
			int tokenPos = 0;
			for (int charPos = 0; charPos < len;) {
				int nextCharPos = indexOf(text, c, charPos);
				if (nextCharPos == -1)
					nextCharPos = len;
				if (tokenPos > rangeEnd) {
					rangesPos += 2;
					if (rangesPos >= ranges.length)
						break;
					rangeStart = ranges[rangesPos];
					rangeEnd = ranges[rangesPos + 1];
				}
				if (tokenPos >= rangeStart) {
					if (r == null)
						r = new StringBuilder();
					else
						r.append(c);
					r.append(text, charPos, nextCharPos);
				}
				charPos = nextCharPos + 1;
				tokenPos++;
			}
		} else {
			int rangesPos = 0;
			int rangeStart = ranges[rangesPos];
			int rangeEnd = ranges[rangesPos + 1];
			int tokenPos = 0;
			for (int charPos = 0; charPos < len;) {
				int nextCharPos = indexOf(text, delim, charPos);
				if (nextCharPos == -1)
					nextCharPos = len;
				if (tokenPos > rangeEnd) {
					rangesPos += 2;
					if (rangesPos >= ranges.length)
						break;
					rangeStart = ranges[rangesPos];
					rangeEnd = ranges[rangesPos + 1];
				}
				if (tokenPos >= rangeStart) {
					if (r == null)
						r = new StringBuilder();
					else
						r.append(delim);
					r.append(text, charPos, nextCharPos);
				}
				charPos = nextCharPos + delim.length();
				tokenPos++;
			}
		}
		return r == null ? "" : r.toString();
	}

	public static boolean isByte(CharSequence cs) {
		return Caster_Byte.INSTANCE.cast(cs, false, false) != null;
	}
	public static boolean isShort(CharSequence cs) {
		return Caster_Short.INSTANCE.cast(cs, false, false) != null;
	}
	public static boolean isInt(CharSequence cs) {
		return Caster_Integer.INSTANCE.cast(cs, false, false) != null;
	}
	public static boolean isLong(CharSequence cs) {
		return Caster_Long.INSTANCE.cast(cs, false, false) != null;
	}
	public static boolean isFloat(CharSequence cs) {
		return Caster_Float.INSTANCE.cast(cs, false, false) != null;
	}
	public static boolean isDouble(CharSequence cs) {
		return Caster_Double.INSTANCE.cast(cs, false, false) != null;
	}
	public static boolean isBoolean(CharSequence cs) {
		return Caster_Boolean.INSTANCE.cast(cs, false, false) != null;
	}
	public static boolean isChar(CharSequence cs) {
		return cs != null && cs.length() == 1;
	}
	public static Complex parseComplex(CharSequence string) {
		return string == null ? null : parseComplex(string, 0, string.length());
	}
	public static Complex parseComplex(CharSequence cs, int start, int end) {
		char suffix = cs.charAt(end - 1);
		if (suffix == 'i' || suffix == 'I' || suffix == 'j' || suffix == 'J') {
			for (int n = end - 2;; n--) {
				char c = cs.charAt(n);
				if (n == 0)
					return new Complex(0, SH.parseDouble(cs, 0, end - 1));
				if (c == '+' || c == '-') {
					double i = SH.parseDouble(cs, n, end - 1);
					double r = SH.parseDouble(cs, 0, n);
					return new Complex(r, i);
				}
			}
		}
		return new Complex(SH.parseDouble(cs, 0, end), 0);
	}

	public static int[] indexOfAll(CharSequence text, char c) {
		int count = count(c, text);
		if (count == 0)
			return OH.EMPTY_INT_ARRAY;
		int r[] = new int[count];
		for (int i = 0, pos = 0; i < count; i++, pos++) {
			pos = SH.indexOf(text, c, pos);
			r[i] = pos;
		}
		return r;

	}

	/**
	 * empty strings are converted to null
	 */
	public static String toNull(String s) {
		return isnt(s) ? null : s;
	}

	public static final String SUFFIXES[] = new String[] { "k", "m", "b", "t" };

	static public String formatNumberCompact(Number number) {
		return formatNumberCompact(number, new StringBuilder(), SUFFIXES).toString();
	}

	static public StringBuilder formatNumberCompact(Number number, StringBuilder sb, String suffixes[]) {
		double d = number.doubleValue();
		// checks NaN and infinity
		if (Double.isNaN(d) || Double.isInfinite(d)) {
			sb.append(Double.toString(d));
		}
		if (d < 0) {
			sb.append('-');
			d = -d;
		}
		if (d < 1) {
			if (d < .0000000001)
				return sb.append(0);
			int n = 1;
			d *= 10;
			for (; d < 1 & n < 10; n++)
				d *= 10;
			switch (n) {
				case 1:
					return sb.append(".").append((long) d);
				case 2:
					return sb.append(".0").append((long) d);
				case 3:
					return sb.append(".00").append((long) d);
				default:
					return sb.append((long) d).append("E-").append(n - 1);
			}
		} else {
			long n = number.longValue();
			if (n < 0)
				n = MH.abs(n);
			for (int c = 0;; c++, n /= 1000)
				if (n < 1000L || c == suffixes.length) {//nnn
					toString(n, 10, sb);
					if (c > 0)
						sb.append(suffixes[c - 1]);
					break;
				} else if (n < 10000L) {//n.n
					long n2 = (n / 100) % 10;
					toString(n / 1000, 10, sb);
					if (n2 > 0)
						sb.append('.').append(n2);
					sb.append(suffixes[c]);
					break;
				}

		}
		return sb;
	}

}
