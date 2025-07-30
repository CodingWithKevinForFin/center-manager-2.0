package com.f1.utils.xml;

import java.io.File;
import java.io.IOException;

import com.f1.utils.AH;
import com.f1.utils.CharReader;
import com.f1.utils.IOH;
import com.f1.utils.SH;
import com.f1.utils.impl.StringCharReader;
import com.f1.utils.string.ExpressionParserException;

public class XmlParser {
	private static final char[] CDATA = "![CDATA[".toCharArray();
	private static final char[] CDATA_END = "]]>".toCharArray();
	private static final char OPEN = '<';
	private static final char CLOSE = '>';
	private static final char SPACE = ' ';
	private static final char SLASH = '/';
	private static final char EQUALS = '=';
	private static final char QUOTE = '\"';
	private static final char QUOTE_SINGLE = '\'';
	private static final int[] QUOTES = new int[] { QUOTE, QUOTE_SINGLE };
	private static final char AMP = '&';
	private static final int[] SPACE_OR_CLOSE_OR_SLASH = new int[] { SPACE, '\n', '\r', '\t', CLOSE, SLASH };
	private static final int[] SPACE_OR_CLOSE = new int[] { SPACE, '\n', '\r', '\t', CLOSE };
	private static final int[] SPACE_OR_EQUALS = new int[] { SPACE, '\n', '\r', '\t', EQUALS };
	private static final int[] QUOTE_OR_AMP = new int[] { QUOTE, AMP };
	private static final int[] SINGLE_QUOTE_OR_AMP = new int[] { QUOTE_SINGLE, AMP };
	private static final int[] OPEN_OR_AMP = new int[] { OPEN, AMP };
	private static final char[] ESC_LT = "lt;".toCharArray();
	private static final char[] ESC_GT = "gt;".toCharArray();
	private static final char[] ESC_AMP = "amp;".toCharArray();
	private static final char[] ESC_APOS = "apos;".toCharArray();
	private static final char[] ESC_QUOT = "quot;".toCharArray();
	private static final char[] COMMENT_OPEN = "!--".toCharArray();
	private static final char QMARK = '?';
	private static final char[] COMMENT_CLOSE = "--".toCharArray();
	private static final int[] WHITE_SPACE = new int[] { ' ', '\n', '\r', '\t' };

	private final StringBuilder sink = new StringBuilder();
	private boolean skipEmptyText = true;

	public static void main(String a[]) throws IOException {
		try {
			XmlParser parser = new XmlParser();
			XmlElement doc = parser.parseDocument(IOH.readText(new File("/tmp/jersey-project-1.8.pom")));
			System.out.println(doc.toString());
		} catch (ExpressionParserException e) {
			System.out.println(e.toLegibleString());
			e.printStackTrace(System.out);
		}
	}
	public XmlElement parseDocument(String text) {
		return parseDocument(new StringCharReader(text, true));
	}
	public XmlElement parseDocument(CharReader in) {
		boolean ci = in.getCaseInsensitive();
		try {
			in.setCaseInsensitive(false);
			for (;;) {//keep looking to skip comments
				in.skipAny(WHITE_SPACE);
				in.expect(OPEN);
				XmlElement r = parseNode(in);
				if (r != null)
					return r;
			}
		} finally {
			in.setCaseInsensitive(ci);
		}
	}

	private XmlElement parseNode(CharReader in) {
		if (in.peakSequence(COMMENT_OPEN)) {
			in.expectSequence(COMMENT_OPEN);
			in.readUntilSequence(COMMENT_CLOSE, SH.clear(sink));
			in.expectSequence(COMMENT_CLOSE);
			in.expect(CLOSE);
			return null;
		} else if (in.peak() == QMARK) {
			in.expect(QMARK);
			in.readUntil(QMARK, sink);
			in.expect(QMARK);
			in.expect(CLOSE);
			return null;
		} else {

			in.skipAny(WHITE_SPACE);
			in.readUntilAny(SPACE_OR_CLOSE_OR_SLASH, SH.clear(sink));
			final XmlElement r = new XmlElement(sink.toString());
			if (parseAttributes(r, in)) {
				parseContent(r, in);
			}
			return r;
		}
	}
	//True = has content
	private boolean parseAttributes(XmlElement r, CharReader in) {
		for (;;) {
			int token = in.expectAny(SPACE_OR_CLOSE_OR_SLASH);

			final boolean hasSpace;
			if (AH.indexOf(token, WHITE_SPACE) != -1) {
				hasSpace = true;
				in.skipAny(WHITE_SPACE);
				token = in.readChar();
			} else
				hasSpace = false;
			switch (token) {
				case SLASH:
					in.expect(CLOSE);
					return false;
				case CLOSE:
					return true;
				default: {
					if (!hasSpace)
						throw new RuntimeException("expecting ' ' or '>' or '/>' at " + in.getCountRead() + ": " + in);
				}
			}

			in.readUntilAny(SPACE_OR_EQUALS, SH.clear(sink));
			in.skipAny(WHITE_SPACE);
			in.expect('=');
			String key = (char) token + sink.toString();
			in.skipAny(WHITE_SPACE);
			int quote = in.expectAny(QUOTES);
			if (quote == QUOTE)
				parseTextUntil(in, QUOTE_OR_AMP, SH.clear(sink));
			else
				parseTextUntil(in, SINGLE_QUOTE_OR_AMP, SH.clear(sink));
			String value = sink.toString();
			in.expect(quote);
			r.addAttribute(key, value);
		}
	}

	private String parseTextUntil(CharReader in, int[] endOrAmp, StringBuilder sink) {
		for (;;) {
			if (in.readUntilAny(endOrAmp, sink) == AMP) {
				in.expect(AMP);
				switch (in.peak()) {
					case 'l':
					case 'L':
						in.expectSequence(ESC_LT);
						sink.append('<');
						continue;
					case 'g':
					case 'G':
						in.expectSequence(ESC_GT);
						sink.append('>');
						continue;
					case 'q':
					case 'Q':
						in.expectSequence(ESC_QUOT);
						sink.append('"');
						continue;
					case 'a':
					case 'A':
						if (in.peakSequence(ESC_AMP)) {
							in.expectSequence(ESC_AMP);
							sink.append('&');
						} else {
							in.expectSequence(ESC_APOS);
							sink.append('\'');
						}

				}
			} else {
				return sink.toString();
			}
		}
	}
	private void parseContent(XmlElement r, CharReader in) {
		for (;;) {
			if (in.peak() != OPEN) {
				if (skipEmptyText) {
					SH.clear(sink);
					in.readWhileAny(WHITE_SPACE, sink);
					if (in.peak() != OPEN) {
						parseTextUntil(in, OPEN_OR_AMP, SH.clear(sink));
						r.addChild(new XmlText(sink.toString()));
					}
				} else {
					parseTextUntil(in, OPEN_OR_AMP, SH.clear(sink));
					r.addChild(new XmlText(sink.toString()));
				}
			}
			in.expect(OPEN);
			if (in.peak() == SLASH) {
				in.expect(SLASH);
				in.readUntilAny(SPACE_OR_CLOSE, SH.clear(sink));
				in.skipAny(WHITE_SPACE);
				in.expect(CLOSE);
				if (!SH.equals(r.getName(), sink))
					throw new RuntimeException("closing tag does not match opening tag: '" + sink + "' != '" + r.getName() + "'");
				return;
			} else if (in.peakSequence(CDATA)) {
				in.expectSequence(CDATA);
				in.readUntilSequence(CDATA_END, SH.clear(sink));
				in.expectSequence(CDATA_END);
				r.addChild(new XmlText(sink.toString()));

			} else {
				XmlElement child = parseNode(in);
				if (child != null)
					r.addChild(child);
			}
		}
	}
	public boolean getSkipEmptyText() {
		return skipEmptyText;
	}
	public void setSkipEmptyText(boolean skipEmptyText) {
		this.skipEmptyText = skipEmptyText;
	}
	public XmlElement parseDocument(File file) throws IOException {
		try {
			return parseDocument(IOH.readText(file));
		} catch (Exception e) {
			throw new RuntimeException("Error reading file: " + IOH.getFullPath(file), e);
		}
	}
}
