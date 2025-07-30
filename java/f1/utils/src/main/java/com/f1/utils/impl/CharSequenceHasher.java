package com.f1.utils.impl;

import com.f1.utils.Hasher;

public class CharSequenceHasher implements Hasher<CharSequence> {

	public static final CharSequenceHasher INSTANCE = new CharSequenceHasher();

	@Override
	public int hashcode(CharSequence o) {
		int len = o.length();
		if (len == 0)
			return 0;
		int h = o.charAt(0);
		for (int i = 1; i < len; i++)
			h = 31 * h + o.charAt(i);
		return h;
	}

	@Override
	public boolean areEqual(CharSequence l, CharSequence r) {
		int len = l.length();
		if (len != r.length())
			return false;
		if (len == 0)
			return true;
		if (l.charAt(0) != r.charAt(0))
			return false;
		while (--len > 0)
			if (l.charAt(len) != r.charAt(len))
				return false;
		return true;
	}

}
