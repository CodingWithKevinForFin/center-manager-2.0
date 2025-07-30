package com.f1.tester.diff;

public interface Differ {

	DiffResult diff(String path, Object left, Object right, DiffSession session);

	boolean canDiff(Object left, Object right);
}
