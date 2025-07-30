package com.f1.utils.fix.impl;

import com.f1.base.IntIterator;
import com.f1.utils.SH;
import com.f1.utils.fix.FixBuilder;
import com.f1.utils.fix.FixDictionary;
import com.f1.utils.fix.FixGroup;
import com.f1.utils.fix.FixMap;
import com.f1.utils.fix.FixParseException;
import com.f1.utils.fix.FixTag;

public class BasicFix44Builder implements FixBuilder {

	public static final int TAG_BEGINSTRING = 8;
	public static final int TAG_BODYLENGTH = 9;
	public static final int TAG_MSGTYPE = 35;
	public static final int TAG_CHECKSUM = 10;

	private FixDictionary dictionary;
	private char delimiter;
	private static final int PREFIX_SIZE = "8=FIX.4.4|9=|".length();
	private static final int SUFFIX_SIZE = "10=000|".length();

	public BasicFix44Builder(FixDictionary dictionary, char delimiter) {
		this.dictionary = dictionary;
		this.delimiter = delimiter;
	}

	private static FixTag getOrThrow(FixDictionary dictionary, int id) {
		FixTag r = dictionary.getFixTag(id);
		if (r == null)
			throw new FixParseException("Fix.4.4 requires dictionary to have tag: " + id);
		return r;
	}
	@Override
	public char[] buildFix(FixMap fix) {
		final StringBuilder sb = new StringBuilder();
		final String msgType = fix.get(TAG_MSGTYPE);
		sb.append(TAG_MSGTYPE).append('=').append(msgType).append(delimiter);
		buildFixInner(fix, -1, sb);
		final int bodyLength = sb.length();
		final String bodyLengthStr = SH.toString(bodyLength);
		final int prefixLength = PREFIX_SIZE + bodyLengthStr.length();
		final int suffixStart = prefixLength + bodyLength;
		final char[] r = new char[suffixStart + SUFFIX_SIZE];
		r[0] = '8';
		r[1] = '=';
		r[2] = 'F';
		r[3] = 'I';
		r[4] = 'X';
		r[5] = '.';
		r[6] = '4';
		r[7] = '.';
		r[8] = '4';
		r[9] = delimiter;
		r[10] = '9';
		r[11] = '=';
		bodyLengthStr.getChars(0, bodyLengthStr.length(), r, 12);
		r[prefixLength - 1] = delimiter;
		sb.getChars(0, sb.length(), r, prefixLength);
		int checkSum = 0;
		if (delimiter == 1) {
			for (int i = 0; i < suffixStart; i++)
				checkSum += r[i];
		} else {
			for (int i = 0; i < suffixStart; i++) {
				char c = r[i];
				checkSum += c == delimiter ? 1 : delimiter;
			}
		}
		checkSum &= 255;
		r[suffixStart] = '1';
		r[suffixStart + 1] = '0';
		r[suffixStart + 2] = '=';
		r[suffixStart + 3] = (char) ('0' + (checkSum / 100));
		r[suffixStart + 4] = (char) ('0' + ((checkSum / 10) % 10));
		r[suffixStart + 5] = (char) ('0' + (checkSum % 10));
		r[suffixStart + 6] = delimiter;
		return r;

	}

	private void buildFixInner(FixMap fix, int ignoreTag, StringBuilder sb) {
		IntIterator iterator = fix.getKeys();
		while (iterator.hasNext()) {
			int key = iterator.next();
			switch (key) {
				case TAG_BEGINSTRING:
				case TAG_BODYLENGTH:
				case TAG_CHECKSUM:
				case TAG_MSGTYPE:
					continue;
			}
			if (key == ignoreTag)
				continue;
			sb.append(SH.toString(key));
			sb.append('=');
			sb.append(fix.get(key));
			sb.append(delimiter);
			if (fix.isGroup(key)) {
				FixTag groupFixTag = dictionary.getFixTag(key);
				int startTag = groupFixTag.getStartTag();
				FixGroup group = fix.getGroups(key);
				for (int i = 0, l = group.size(); i < l; i++) {
					FixMap inner = group.get(i);
					String startValue = inner.get(startTag);
					sb.append(SH.toString(startTag));
					sb.append('=');
					sb.append(startValue);
					sb.append(delimiter);
					buildFixInner(inner, startTag, sb);
				}
			}
		}

	}
}
