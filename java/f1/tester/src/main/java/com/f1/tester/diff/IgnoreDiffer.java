package com.f1.tester.diff;

public class IgnoreDiffer implements Differ {

	@Override
	public DiffResult diff(String path, Object left, Object right, DiffSession session) {
		return null;
	}

	@Override
	public boolean canDiff(Object left, Object right) {
		return true;
	}

}
