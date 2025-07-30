package com.f1.tester.diff;

import java.util.ArrayList;
import java.util.List;
import com.f1.utils.TextMatcher;
import com.f1.utils.structs.Tuple2;

public class DiffSession {
	private static final double DEFAULT_DRIFT = .00001d;

	final private RootDiffer rootDiffer;
	final private List<Tuple2<TextMatcher, Differ>> overrides = new ArrayList<Tuple2<TextMatcher, Differ>>();
	private double drift = DEFAULT_DRIFT;

	public DiffSession(RootDiffer rootDiffer) {
		this.rootDiffer = rootDiffer;
	}

	public RootDiffer getRootDiffer() {
		return rootDiffer;
	}

	public String[] toString(String... parts) {
		return parts;
	}

	public void setDrift(double drift) {
		this.drift = drift;
	}

	public double getDrift() {
		return drift;
	}

	public void addDiffOverride(TextMatcher pathMatcher, Differ differ) {
		overrides.add(new Tuple2<TextMatcher, Differ>(pathMatcher, differ));
	}

	public Differ getOverride(String path, Object left, Object right) {
		for (Tuple2<TextMatcher, Differ> e : overrides)
			if (e.getA().matches(path) && e.getB().canDiff(left, right))
				return e.getB();
		return null;
	}

	public boolean canIgnore(String path) {
		for (Tuple2<TextMatcher, Differ> e : overrides)
			if (e.getA().matches(path) && e.getB().canDiff(null, null))
				return true;
		return false;
	}

}
