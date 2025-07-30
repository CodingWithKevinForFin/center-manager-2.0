package com.f1.utils;

import com.f1.base.Legible;
import com.f1.utils.structs.Tuple2;

public class StringFormatException extends RuntimeException implements Legible {

	private int offset;
	private String target;

	public StringFormatException(String message, Throwable cause, String target, int offset) {
		super(message, cause);
		this.target = target;
		this.offset = offset;
	}

	@Override
	public String toLegibleString() {
		StringBuilder sb = new StringBuilder();
		Tuple2<Integer, Integer> position = SH.getLinePosition(target, offset);
		String[] lines = SH.split(SH.CHAR_NEWLINE, SH.replaceAll(target, SH.CHAR_RETURN, ""));
		int numSize = SH.toString(lines.length + 1).length();
		for (int i = 0; i < lines.length; i++) {
			SH.rightAlign('0', SH.toString(i + 1), numSize, false, sb);
			sb.append(": ").append(lines[i]).append(SH.NEWLINE);
			if (i == position.getA()) {
				SH.repeat(' ', 2 + numSize + position.getB(), sb).append("^---------  ");
				if (getCause() != null)
					sb.append(SH.toStringEncode(getCause().toString(), '"'));
				sb.append(SH.NEWLINE);
			}
		}
		return sb.toString();
	}
}

