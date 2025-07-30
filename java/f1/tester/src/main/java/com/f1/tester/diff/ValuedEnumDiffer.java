package com.f1.tester.diff;

import com.f1.base.ValuedEnum;
import com.f1.utils.OH;
import com.f1.utils.SH;

public class ValuedEnumDiffer implements Differ {

	@Override
	public DiffResult diff(String path, Object left, Object right, DiffSession session) {
		String leftString = toString(left);
		String rightString = toString(right);
		if (left instanceof ValuedEnum && right instanceof ValuedEnum && left.getClass() != right.getClass())
			return new DiffResult(left, right, DifferConstants.ENUM_TYPE_MISMATCH);
		if (OH.eq(leftString, rightString))
			return null;
		return new DiffResult(left, right, DifferConstants.ENUM_MISMATCH);
	}

	private String toString(Object o) {
		if (o instanceof ValuedEnum)
			return SH.toString(((ValuedEnum) o).getEnumValue());
		return o.toString();
	}

	@Override
	public boolean canDiff(Object left, Object right) {
		return left instanceof ValuedEnum || right instanceof ValuedEnum;
	}

}
