package com.f1.ami.web;

import java.io.File;
import java.io.IOException;

import com.f1.utils.CharReader;
import com.f1.utils.IOH;
import com.f1.utils.SH;
import com.f1.utils.impl.BasicCharMatcher;
import com.f1.utils.impl.CharMatcher;
import com.f1.utils.impl.StringCharReader;

public class AmiWebHtmlJavascriptCleaner {
	private static CharMatcher TAG_LETTERS = new BasicCharMatcher("a-zA-Z!\\-", true);

	public static String clean(CharSequence cs, boolean removeComments) {
		if (cs == null)
			return null;
		int startPos = SH.indexOf(cs, '<', 0);
		if (startPos == -1)
			return cs.toString();
		final StringCharReader cr = new StringCharReader(cs, startPos, cs.length() - startPos);
		cr.setCaseInsensitive(true);
		final StringBuilder out = new StringBuilder();
		out.append(cs, 0, startPos);
		final StringBuilder buf = new StringBuilder();
		while (!cr.isEof()) {
			sws(cr, out);
			cr.readUntil('<', out);
			if (cr.expectNoThrow('<')) {
				if (cr.expectSequenceNoThrow("!--"))
					skipComments(cr, removeComments ? null : out);
				else
					parseElement(cr, out, buf);
			}
		}
		return out.toString();
	}

	private static void skipComments(StringCharReader cr, StringBuilder out) {
		if (out != null)
			out.append("<!--");
		cr.readUntilSequence("-->", out);
		if (!expect(cr, "-->", out))
			cr.readUntil(CharReader.EOF, out);
	}

	private static void parseElement(StringCharReader cr, StringBuilder out, StringBuilder buf) {
		SH.clear(buf);
		cr.readWhileAny(TAG_LETTERS, buf);
		boolean isScript = false;
		boolean isStyle = false;
		if (SH.equalsIgnoreCase("script", buf)) {
			isScript = true;
			out = null;
		} else if (SH.equalsIgnoreCase("style", buf)) {
			isStyle = true;
		}
		if (buf.length() == 0) {
			out.append('<');
			return;
		} else if (out != null)
			out.append('<').append(buf);
		for (;;) {
			sws(cr, out);
			if (expect(cr, '>', out)) {
				break;
			} else if (expect(cr, "/>", out)) {
				break;
			} else if (cr.isEof()) {
				return;
			} else if (StringCharReader.ALPHA.matches(cr.peak())) {
				SH.clear(buf);
				cr.readWhileAny(TAG_LETTERS, buf);
				StringBuilder t = out;
				if (buf.length() > 2 && SH.startsWithIgnoreCase(buf, "on") || SH.startsWithIgnoreCase(buf, "seeksegmenttime"))
					t = null;
				sws(cr, buf);
				if (expect(cr, '=', buf)) {
					sws(cr, buf);
					if (cr.peakSequence("'amiJsCallback(") || cr.peakSequence("\"amiJsCallback(")) {
						t = out;
						skipQuoted(cr, buf);
						//TODO:confirm buf is valid
					} else {
						skipQuoted(cr, buf);
					}
					sws(cr, buf);
					if (SH.indexOfIgnoreCase(buf, "javascript", 0) != -1)
						t = null;
				}
				if (t != null)
					t.append(buf);
			} else if (out == null)
				cr.readChar();
			else
				out.append(cr.readChar());

		}
		if (isScript)
			readCodeUntilClosingTag("script", cr, out);
		else if (isStyle)
			readCodeUntilClosingTag("style", cr, out);

	}

	private static void readCodeUntilClosingTag(String string, StringCharReader cr, StringBuilder out) {
		for (;;) {
			int c = cr.readUntilAny("<'\"/\\", true, out);
			switch (c) {
				case CharReader.EOF:
					cr.readUntil(CharReader.EOF, out);
					return;
				case '\'':
				case '\"':
					skipQuoted(cr, out);
					break;
				case '/':
					expect(cr, '/', out);
					if (expect(cr, '*', out)) {
						cr.readUntilSequence("*/", out);
						if (!expect(cr, "*/", out))
							cr.readUntil(CharReader.EOF, out);
					} else if (expect(cr, '/', out)) {
						cr.readUntil('\n', out);
						if (!expect(cr, '\n', out))
							cr.readUntil(CharReader.EOF, out);
					}
					continue;
				case '<': {
					expect(cr, '<', out);
					if (expect(cr, '/', out)) {
						if (expect(cr, string, out)) {
							sws(cr, out);
							if (expect(cr, '>', out))
								return;
						}
					}
				}
			}
		}
	}

	private static boolean expect(StringCharReader cr, char c, StringBuilder out) {
		if (!cr.expectNoThrow(c))
			return false;
		if (out != null)
			out.append(c);
		return true;
	}
	private static boolean expect(StringCharReader cr, String s, StringBuilder out) {
		if (!cr.expectSequenceNoThrow(s))
			return false;
		if (out != null)
			out.append(s);
		return true;
	}

	private static void skipQuoted(StringCharReader cr, StringBuilder out) {
		int c = cr.peakOrEof();
		switch (c) {
			case '"':
			case '\'':
				expect(cr, (char) c, out);
				cr.readUntil(c, '\\', out);
				expect(cr, (char) c, out);
		}
	}

	private static void sws(CharReader c, StringBuilder out) {
		c.readWhileAny(StringCharReader.WHITE_SPACE, out);
	}

	public static void main(String a[]) throws IOException {
		System.out.println(AmiWebHtmlJavascriptCleaner.clean("<a href=\"javascript:alert(123)\">test2</a>", true));
		System.out.println(AmiWebHtmlJavascriptCleaner.clean("blah<a href=\"jav#x09;script:alert(123)\">test2</a>", true));
		System.out.println(AmiWebHtmlJavascriptCleaner.clean("blah", true));
		IOH.writeText(new File("f:/temp/html2.html"), AmiWebHtmlJavascriptCleaner.clean(IOH.readText(new File("f:/temp/html.html")), false));
	}
}
