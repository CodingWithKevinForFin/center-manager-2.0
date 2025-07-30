package com.f1.tester.diff;

import java.util.ArrayList;
import java.util.List;
import com.f1.utils.OH;

public class RootDiffer implements Differ {
	private List<Differ> differs = new ArrayList<Differ>();

	public RootDiffer() {
		this(true);
	}

	public RootDiffer(boolean init) {
		if (init) {

			registerDiffer(new ListDiffer());
			registerDiffer(new MapDiffer());
			registerDiffer(new PrimitiveDiffer());
			registerDiffer(new StringDiffer());
			registerDiffer(new NumberDiffer());
			registerDiffer(new ValuedEnumDiffer());
			registerDiffer(new NullDiffer());
			registerDiffer(new PatternDiffer());
		}
	}

	public DiffResult diff(Object left, Object right) {
		return diff("", left, right, new DiffSession(this));
	}

	@Override
	public DiffResult diff(String path, Object left, Object right, DiffSession session) {
		if (OH.eq(left, right))
			return null;
		final Differ override = session.getOverride(path, left, right);
		if (override != null)
			return override.diff(path, left, right, session);
		for (final Differ differ : differs)
			if (differ.canDiff(left, right))
				return differ.diff(path, left, right, session);
		return new DiffResult(left, right, DifferConstants.TYPES_INCOMPATIBLE);
	}

	@Override
	public boolean canDiff(Object left, Object right) {
		return true;
	}

	public void registerDiffer(Differ differ) {
		this.differs.add(0, differ);
	}

}
