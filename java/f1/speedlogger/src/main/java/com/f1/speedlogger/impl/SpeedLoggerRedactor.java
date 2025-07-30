package com.f1.speedlogger.impl;

import java.io.IOException;

import com.f1.speedlogger.SpeedLoggerException;
import com.f1.utils.AH;
import com.f1.utils.SH;

public class SpeedLoggerRedactor {

	final private String[] prefixes;
	final private String[] suffixes;
	final private long firstCharMask;
	final private int[][] prefixesByFirstChar;
	private int minLength;
	private String replaceText;

	public SpeedLoggerRedactor(String redactFind, String redactReplace) throws SpeedLoggerException {
		String[] parts = SH.splitWithEscape(',', '\\', redactFind, true);
		this.replaceText = redactReplace;
		this.prefixes = new String[parts.length];
		this.suffixes = new String[parts.length];
		StringBuilder buf = new StringBuilder();
		for (int i = 0; i < parts.length; i++) {
			String part = parts[i];
			int starPos = SH.indexOfNotEscaped(part, '*', 0, '\\');
			if (starPos == -1)
				throw new SpeedLoggerException("Redact Option invalid, missing start(*): " + part);
			SH.unescape(part, 0, starPos, '\\', buf);
			final String prefix = SH.toStringAndClear(buf);
			SH.unescape(part, starPos + 1, part.length(), '\\', buf);
			final String suffix = SH.toStringAndClear(buf);
			if (prefix.length() == 0)
				throw new SpeedLoggerException("Redact Option invalid, empty prefix: " + part);
			if (suffix.length() == 0)
				throw new SpeedLoggerException("Redact Option invalid, empty prefix: " + part);
			this.prefixes[i] = prefix;
			this.suffixes[i] = suffix;
		}
		int max = 0;
		int minLength = -1;
		for (int i = 0; i < this.prefixes.length; i++) {
			int c = this.prefixes[i].charAt(0);
			max = Math.max(max, (int) c);
			int len = this.prefixes[i].length() + this.suffixes[i].length();
			if (i == 0)
				minLength = len;
			else
				minLength = len;
		}
		this.minLength = minLength;
		prefixesByFirstChar = new int[max + 1][];
		long mask = 0;
		for (int i = 0; i < this.prefixes.length; i++) {
			int c = this.prefixes[i].charAt(0);
			int[] t = prefixesByFirstChar[c];
			if (t == null)
				t = new int[] { i };
			else
				t = AH.insert(t, t.length, i);
			prefixesByFirstChar[c] = t;
			mask |= 1L << c;
			System.out.println("F1 " + getClass().getSimpleName() + " REDACTING:  '" + prefixes[i] + "' followed by '" + suffixes[i] + "'");
		}
		this.firstCharMask = mask;
	}

	public void redact(int start, AppendableBuffer sink) throws IOException {
		StringBuilder buf = null;
		int lastEnd = start;
		outer: for (int pos = start, l = sink.length() - minLength; pos < l; pos++) {
			int c = sink.charAt(pos);
			if ((this.firstCharMask & (1L << c)) == 0L)
				continue;
			if (c >= prefixesByFirstChar.length)
				continue;
			int[] indexes = prefixesByFirstChar[c];
			if (indexes != null) {
				int bestSuffixEnd = -1;
				int bestIdx = -1;
				for (int i = 0; i < indexes.length; i++) {
					int idx = indexes[i];
					if (SH.startsWith(sink, this.prefixes[idx], pos)) {
						int prefixEnd = pos + this.prefixes[idx].length();
						int suffixBegin = SH.indexOf(sink, this.suffixes[idx], prefixEnd);
						if (suffixBegin != -1) {
							int suffixEnd = suffixBegin + this.suffixes[idx].length();
							if (bestSuffixEnd == -1 || bestSuffixEnd > suffixEnd) {
								bestSuffixEnd = suffixEnd;
								bestIdx = idx;
							}
						}
					}
				}
				if (bestSuffixEnd != -1) {
					if (buf == null) {
						buf = new StringBuilder(sink.length() + this.replaceText.length() * 4);
						buf.append(sink, 0, start);
					}
					int prefixEnd = pos + this.prefixes[bestIdx].length();
					buf.append(sink, lastEnd, prefixEnd).append(replaceText).append(this.suffixes[bestIdx]);
					lastEnd = bestSuffixEnd;
					pos = lastEnd - 1;
				}
			}
		}
		if (buf != null) {
			buf.append(sink, lastEnd, sink.length());
			sink.clear();
			sink.append(buf);
		}
	}
}
