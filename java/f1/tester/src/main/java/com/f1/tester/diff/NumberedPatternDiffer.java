package com.f1.tester.diff;

import com.f1.utils.MH;
import com.f1.utils.OH;

public class NumberedPatternDiffer implements Differ {

	@Override
	public DiffResult diff(String path, Object left, Object right, DiffSession session) {
		final String s1 = left.toString();
		final String s2 = right.toString();
		int i1 = 0, i2 = 0;
		final int l1 = s1.length(), l2 = s2.length();
		boolean inNumber = false;
		while (true) {
			int b1 = i1, b2 = i2, t1 = 0, t2 = 0;
			for (;;) {
				if (i1 == l1) {
					t1 |= TYPE_EOF;
					break;
				}
				final int type = getType(s1.charAt(i1++));
				if (type == TYPE_OTHER)
					break;
				else
					t1 |= type;
			}
			for (;;) {
				if (i2 == l2) {
					t2 |= TYPE_EOF;
					break;
				}
				final int type = getType(s2.charAt(i2++));
				if (type == TYPE_OTHER)
					break;
				else
					t2 |= type;
			}
			if (t1 != t2)
				return new DiffResult(left, right, "NumberPatternMismatch");
			if (MH.areAnyBitsSet(t1, TYPE_TEXT) && !MH.areAnyBitsSet(t1, TYPE_NUMBER)) {
				if (!s1.substring(b1, i1).equals(s2.substring(b2, i2))) {
					return new DiffResult(left, right, "NumberPatternMismatch");
				}
			}
			if (MH.areAnyBitsSet(t1, TYPE_EOF))
				return null;
		}
	}

	private static final int TYPE_OTHER = 0;
	private static final int TYPE_TEXT = 1;
	private static final int TYPE_NUMBER = 2;
	private static final int TYPE_EOF = 4;

	public static int getType(char c) {
		if (OH.isBetween(c, 'a', 'z'))
			return TYPE_TEXT;
		else if (OH.isBetween(c, 'A', 'Z'))
			return TYPE_TEXT;
		else if (c == '_' || c == ' ')
			return TYPE_TEXT;
		else if (OH.isBetween(c, '0', '9'))
			return TYPE_NUMBER;
		else if (c == '-')
			return TYPE_NUMBER;
		return TYPE_OTHER;
	}

	@Override
	public boolean canDiff(Object left, Object right) {
		return left != null && right != null && OH.isImmutable(left) && OH.isImmutable(right);
	}

}

