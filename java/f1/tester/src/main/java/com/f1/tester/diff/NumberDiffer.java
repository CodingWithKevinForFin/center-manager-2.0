package com.f1.tester.diff;

import com.f1.utils.OH;

public class NumberDiffer implements Differ {

	@Override
	public DiffResult diff(String path, Object l, Object r, DiffSession session) {
		final Number left, right;
		try {
			left = l instanceof Number ? (Number) l : (Number) OH.cast(l, r.getClass());
			right = r instanceof Number ? (Number) r : (Number) OH.cast(r, l.getClass());
		} catch (Exception e) {
			return new DiffResult(l, r, DifferConstants.TYPES_INCOMPATIBLE);
		}
		final Class<?> lc = left.getClass();
		final Class<?> rc = right.getClass();
		if (OH.isCoercable(rc, lc)) {
			if (equals(OH.cast(left, rc), right, session))
				return null;
		} else if (OH.isCoercable(lc, rc)) {
			if (equals(OH.cast(right, lc), left, session))
				return null;
		}
		return new DiffResult(left, right, DifferConstants.NUMBER_MISMATCH);
	}

	private boolean equals(Object left, Object right, DiffSession session) {
		if (left.equals(right))
			return true;
		if (left instanceof Double || left instanceof Float) {
			double lvalue = ((Number) left).doubleValue();
			double rvalue = ((Number) right).doubleValue();
			double diff = lvalue - rvalue;
			return OH.isBetween(diff, -session.getDrift(), session.getDrift());
		}
		return false;
	}

	@Override
	public boolean canDiff(Object left, Object right) {
		return left instanceof Number || right instanceof Number;
	}

}
