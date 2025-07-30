package com.f1.tester.diff;

public class NullDiffer implements Differ {

	@Override
	public DiffResult diff(String path, Object left, Object right, DiffSession session) {
		if (left == null)
			return new DiffResult(null, right, DifferConstants.LEFT_IS_NULL);
		if (right == null)
			return new DiffResult(left, null, DifferConstants.RIGHT_IS_NULL);
		throw new IllegalStateException();
	}

	@Override
	public boolean canDiff(Object left, Object right) {
		return left == null || right == null;
	}

}
