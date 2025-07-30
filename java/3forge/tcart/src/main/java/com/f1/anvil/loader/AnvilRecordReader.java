package com.f1.anvil.loader;

import com.f1.utils.SH;

public class AnvilRecordReader {

	public StringBuilder buffer = new StringBuilder();
	private int position = 0;
	private int length;
	private Exception exception;
	private boolean wasEmpty;

	public void reset(StringBuilder buffer) {
		this.buffer = buffer;
		this.position = 0;
		this.length = buffer.length();
		this.exception = null;
		this.wasEmpty = false;
	}

	public StringBuilder getBuffer() {
		return this.buffer;
	}

	public long readLongOr(char delim, long orValue) {
		int start = position;
		int end = moveToAndSkipDelim(delim);
		if (!wasEmpty)
			try {
				return SH.parseLong(buffer, start, end, 10);
			} catch (Exception e) {
				this.exception = e;
			}
		return orValue;
	}
	public int readIntOr(char delim, int orValue) {
		int start = position;
		int end = moveToAndSkipDelim(delim);
		if (!wasEmpty)
			try {
				return SH.parseInt(buffer, start, end, 10);
			} catch (Exception e) {
				this.exception = e;
			}
		return orValue;
	}
	public double readDoubleOr(char delim, double orValue) {
		int start = position;
		int end = moveToAndSkipDelim(delim);
		if (!wasEmpty)
			try {
				return SH.parseDouble(buffer, start, end);
			} catch (Exception e) {
				this.exception = e;
			}
		return orValue;
	}
	public float readFloatOr(char delim, float orValue) {
		int start = position;
		int end = moveToAndSkipDelim(delim);
		if (!wasEmpty)
			try {
				return SH.parseFloat(buffer, start, end);
			} catch (Exception e) {
				this.exception = e;
			}
		return orValue;
	}

	private void resetError() {
		this.exception = null;
		this.wasEmpty = false;
	}

	public boolean wasEmpty() {
		return wasEmpty;
	}
	public boolean wasError() {
		return this.exception != null;
	}

	public Exception getError() {
		return this.exception;
	}

	public boolean isEof() {
		return position >= length;
	}

	public int findNext(char delim) {
		for (int i = position; i < length; i++)
			if (buffer.charAt(i) == delim)
				return i;
		return -1;
	}
	public int getLength() {
		return length;
	}
	public void moveCursor(int position) {
		this.position = position;
	}

	public char readCharOr(char delim, char orValue) {
		resetError();
		if (isEof())
			return orValue;
		int start = position;
		int end = moveToAndSkipDelim(delim);
		if (end - start != 1) {
			this.exception = new RuntimeException("char length not one: " + (end - start));
			return orValue;
		}
		try {
			return buffer.charAt(start);
		} catch (Exception e) {
			this.exception = e;
			return orValue;
		}
	}

	public int getPosition() {
		return this.position;
	}

	public int moveToAndSkipDelim(char delim) {
		resetError();
		int r = findNext(delim);
		if (r == -1) {
			wasEmpty = position == length;
			position = r = length;
		} else {
			wasEmpty = r == position;
			position = r + 1;
		}
		return r;
	}

	public void setPosition(int i) {
		this.position = i;
	}

}
