package com.f1.tester;

import com.f1.utils.AH;
import com.f1.utils.MH;

public class TestH {

	static public <T> T cycle(long i, T... values) {
		return AH.isEmpty(values) ? null : values[(int) MH.mod(i, values.length)];
	}

}
