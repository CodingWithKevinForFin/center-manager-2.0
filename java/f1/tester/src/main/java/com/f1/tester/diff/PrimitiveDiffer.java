package com.f1.tester.diff;

import com.f1.utils.OH;

public class PrimitiveDiffer implements Differ {

	@Override
	public DiffResult diff(String path, Object left, Object right, DiffSession session) {
		// can't think of a case 2 primitives would get here & still be equal
		return new DiffResult(left, right, DifferConstants.VALUE_MISMATCH);
	}

	@Override
	public boolean canDiff(Object left, Object right) {
		return OH.isImmutable(left) && OH.isImmutable(right);
	}

}
