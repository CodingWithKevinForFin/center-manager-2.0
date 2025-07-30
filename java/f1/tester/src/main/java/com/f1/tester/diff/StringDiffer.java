package com.f1.tester.diff;

import com.f1.utils.OH;

public class StringDiffer implements Differ {

	@Override
	public DiffResult diff(String path, Object left, Object right, DiffSession session) {
		if (OH.eq(left.toString(), right.toString()))
			return null;
		return new DiffResult(left, right, DifferConstants.STRING_MISMATCH);
	}

	@Override
	public boolean canDiff(Object left, Object right) {
		return left instanceof String || right instanceof String || left instanceof StringBuilder || right instanceof StringBuilder;
	}

}
