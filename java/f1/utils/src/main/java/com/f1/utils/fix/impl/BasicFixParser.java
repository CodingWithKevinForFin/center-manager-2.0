package com.f1.utils.fix.impl;

import java.util.logging.Logger;

import com.f1.utils.CharReader;
import com.f1.utils.LH;
import com.f1.utils.SH;
import com.f1.utils.fix.FixDictionary;
import com.f1.utils.fix.FixParseException;
import com.f1.utils.fix.FixParser;
import com.f1.utils.fix.FixTag;
import com.f1.utils.impl.StringCharReader;

public class BasicFixParser implements FixParser {

	private static final Logger log = Logger.getLogger(BasicFixParser.class.getName());
	private static final int[] EQUAL_OR_EOL = new int[] { '=', CharReader.EOF };
	private final FixDictionary dictionary;
	private final char delimiter;
	private final char seperator;
	private boolean allowUnknownTags = false;

	public BasicFixParser(FixDictionary dictionary, char delimiter, char seperator) {
		this.dictionary = dictionary;
		if (dictionary == null)
			throw new NullPointerException("dictionary");
		this.delimiter = delimiter;
		this.seperator = seperator;
	}

	public BasicFixMap parse(String text, char delim) {
		return parse(text.toCharArray(), delim);
	}

	@Override
	public BasicFixMap parse(char[] text) {
		return parse(text, delimiter);

	}

	public BasicFixMap parse(char[] text, char delim) {
		StringCharReader reader = new StringCharReader(text);
		BasicFixMap r = new BasicFixMap();
		try {
			parseGroup(null, -1, reader, new int[] { CharReader.EOF, (int) delim }, r, new StringBuilder());
		} catch (FixParseException e) {
			throw new FixParseException("Error parsing: " + new String(text), e);
		}
		return r;
	}

	private void parseGroup(FixTag groupTag, int expectedOccurences, CharReader reader, int[] delim, BasicFixMap sink, StringBuilder tmp) {
		BasicFixMap currentFixMap = sink;
		if (groupTag != null) {
			sink.prepareGroup(groupTag.getTag(), expectedOccurences);
			if (expectedOccurences == 0)
				return;
			currentFixMap = null;
		}

		int occurences = 0;
		while (reader.peakOrEof() != CharReader.EOF) {
			reader.mark();
			int key = readKey(reader);
			if (reader.readUntilAny(delim, SH.clear(tmp)) != CharReader.EOF)
				reader.readChar();// pop delim
			final FixTag tag = dictionary.getFixTag(key);
			if (groupTag != null) {
				if (key == groupTag.getStartTag()) {
					currentFixMap = new BasicFixMap();
					if (++occurences > expectedOccurences)
						throw new FixParseException("At offset " + reader.getCountRead() + ": Found too many groups: Only expected " + expectedOccurences + " group(s)");
					sink.put(groupTag.getTag(), currentFixMap);
				} else {
					if (currentFixMap == null)
						throw new FixParseException("At offset " + reader.getCountRead() + ": Expecting first tag to be " + groupTag.getStartTag() + " for group "
								+ groupTag.getTag());
					if (!groupTag.getIsInGroup(key)) {
						reader.returnToMark();
						break;
					}
				}
			}

			if (tag == null) {
				if (allowUnknownTags) {
					currentFixMap.put(key, tmp.toString());
				} else {
					LH.warning(log, "Fix tag not found: ", key, "=", tmp.toString());
					continue;
				}
			} else if (tag.isGroup()) {
				int groupsCount;
				try {
					groupsCount = SH.parseInt(tmp);
				} catch (NumberFormatException e) {
					throw new FixParseException("error while processing grouping tag " + key + " (" + tag + ")" + " must have a number of repeating groups for it's value", e);
				}
				try {
					parseGroup(tag, groupsCount, reader, delim, currentFixMap, tmp);
				} catch (FixParseException e) {
					throw new FixParseException("error while processing grouping tag " + key + " (" + tag + ")", e);
				}
			} else {
				currentFixMap.put(key, tmp.toString());
			}
		}

		if (groupTag != null && occurences != expectedOccurences)
			throw new FixParseException("At offset " + reader.getCountRead() + ":End of tags reached before expected group count reached: " + expectedOccurences + " group(s)");
	}

	static private int readKey(CharReader reader) {
		int key = 0;
		char c = reader.readChar();
		if (c == '=')
			throw new RuntimeException("missing key at char " + reader.getCountRead());
		for (;;) {
			switch (c) {
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
					key = key * 10 + (c - '0');
					c = reader.readChar();
					continue;
				case '=':
					return key;
				default:
					throw new RuntimeException("unexpected char at " + reader.getCountRead() + ": " + c);
			}
		}
	}

	public void setAllowUnknownTags(boolean allowUnkownTags) {
		this.allowUnknownTags = allowUnkownTags;
	}

	public boolean getAllowUnknownTags() {
		return allowUnknownTags;
	}

	public static void main(String a[]) {
		String s2 = "8=FIXT.1.1/9=84/35=A/49=ME/56=ICAP_AI_Server/34=1/52=20090402-12:57:29/98=0/108=1/1137=7/";
		int l = 0;
		for (byte c : s2.getBytes()) {
			if (c == '/')
				c = 1;
			System.out.println("" + l + " + " + c + " = " + (l + c));
			l += c == '/' ? 1 : (int) c;
		}
		System.out.println(l & 255);
	}

}

