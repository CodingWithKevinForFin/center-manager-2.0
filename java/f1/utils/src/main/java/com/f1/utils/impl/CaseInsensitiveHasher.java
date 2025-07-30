package com.f1.utils.impl;

import com.f1.utils.Hasher;
import com.f1.utils.SH;

public class CaseInsensitiveHasher implements Hasher<CharSequence> {

	public static final CaseInsensitiveHasher INSTANCE = new CaseInsensitiveHasher();

	@Override
	public int hashcode(CharSequence o) {
		int len = o.length();
		if (len == 0)
			return 0;
		int h = charAt(o, 0);
		for (int i = 1; i < len; i++)
			h = 31 * h + charAt(o, i);
		return h;
	}

	private static char charAt(CharSequence o, int i) {
		return SH.toUpperCase(o.charAt(i));
	}

	@Override
	public boolean areEqual(CharSequence l, CharSequence r) {
		int len = l.length();
		if (len != r.length())
			return false;
		if (len == 0)
			return true;
		if (charAt(l, 0) != charAt(r, 0))
			return false;
		while (--len > 0)
			if (charAt(l, len) != charAt(r, len))
				return false;
		return true;
	}

}
