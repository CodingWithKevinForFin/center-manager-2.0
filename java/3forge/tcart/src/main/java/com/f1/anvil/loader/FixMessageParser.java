package com.f1.anvil.loader;

import java.util.BitSet;

import com.f1.base.Clearable;
import com.f1.base.ToStringable;
import com.f1.utils.CharSubSequence;
import com.f1.utils.Duration;
import com.f1.utils.SH;
import com.f1.utils.structs.IntKeyMap;
import com.f1.utils.structs.IntKeyMap.Node;

/**
 * Object free Fix parser in that it reuses charsequences from build to build. Important: Do not hold onto references of the charsequences representing values, copy (via toString)
 * if needed.
 */
public class FixMessageParser implements ToStringable, Clearable {

	private BitSet existingFields = new BitSet();
	private IntKeyMap<CharSubSequence> values = new IntKeyMap<CharSubSequence>();
	private StringBuilder sb;
	private char associator = '=';
	private char delimiter = SH.CHAR_SOH;

	public FixMessageParser() {
		this.sb = new StringBuilder();
	}

	public StringBuilder getBufferUnsafe() {
		return this.sb;
	}

	public void setData(CharSequence chars) {
		SH.clear(sb);
		this.sb.append(chars);
		this.build();
	}

	@Override
	public void clear() {
		this.existingFields.clear();
		this.sb.setLength(0);
	}
	public void build() {
		existingFields.clear();
		final int len = sb.length();
		int pos = 0;
		while (pos < len) {
			//process key
			int keyVal = 0;
			while (pos < len) {
				char c = sb.charAt(pos++);
				if (c >= '0' && c <= '9')
					keyVal = keyVal * 10 + c - '0';
				else if (c == this.associator) {
					//process val
					int start = pos, end;
					for (;;) {
						if (pos == len) {
							end = pos;
							break;
						} else if (sb.charAt(pos++) == this.delimiter) {
							end = pos - 1;
							break;
						}
					}
					if (keyVal < 0 || keyVal > 10000)
						break;
					existingFields.set(keyVal);
					final Node<CharSubSequence> node = values.getNodeOrCreate(keyVal);
					if (node.getValue() == null)
						node.setValue(new CharSubSequence());
					node.getValue().reset(this.sb, start, end);
					break;
				} else
					break;
			}
		}
	}

	public CharSequence getValue(int tag) {
		if (tag < 0)
			return null;
		return existingFields.get(tag) ? this.values.get(tag) : null;
	}

	public char getAssociator() {
		return associator;
	}

	public void setAssociator(char associator) {
		this.associator = associator;
	}

	public char getDelimiter() {
		return delimiter;
	}

	public void setDelimiter(char delimiter) {
		this.delimiter = delimiter;
	}

	public int getFirstTag() {
		return this.existingFields.nextSetBit(0);
	}
	public int getNextTagAfter(int i) {
		return this.existingFields.nextSetBit(i + 1);
	}

	@Override
	public StringBuilder toString(StringBuilder sink) {
		boolean first = true;
		for (int tag = getFirstTag(); tag != -1; tag = getNextTagAfter(tag)) {
			if (first)
				first = false;
			else
				sink.append('|');
			sink.append(tag).append('=').append(this.values.get(tag));
		}
		return sink;
	}

	public String toString() {
		return toString(new StringBuilder()).toString();
	}

	public static void main(String a[]) {
		StringBuilder sb = new StringBuilder();
		for (int i = 10; i < 110; i++) {
			sb.append(i).append("=TEST").append(i).append('|');
		}
		sb.setLength(sb.length() - 1);
		System.out.println(sb);
		System.out.println(sb.length());
		FixMessageParser fm = new FixMessageParser();
		fm.setDelimiter('|');
		fm.setData(sb);
		for (int j = 0; j < 10; j++) {
			Duration d = new Duration();
			for (int i = 0; i < 1000000; i++) {
				fm.setData(sb);
			}
			d.stampStdout();
		}
	}
}
