package com.f1.tester.diff;

import java.util.regex.Pattern;
import com.f1.utils.SH;

public class PatternDiffer implements Differ {

	@Override
	public DiffResult diff(String path, Object left, Object right, DiffSession session) {
		if (left instanceof Pattern) {
			if (((Pattern) left).matcher(SH.toString(right)).matches())
				return null;
			return new DiffResult(left, right, DifferConstants.LEFT_PATTERN_MISMATCH);
		} else if (right instanceof Pattern) {
			if (((Pattern) right).matcher(SH.toString(left)).matches())
				return null;
			return new DiffResult(left, right, DifferConstants.RIGHT_PATTERN_MISMATCH);
		}
		throw new IllegalStateException();
	}

	@Override
	public boolean canDiff(Object left, Object right) {
		return left instanceof Pattern || right instanceof Pattern;
	}

}
