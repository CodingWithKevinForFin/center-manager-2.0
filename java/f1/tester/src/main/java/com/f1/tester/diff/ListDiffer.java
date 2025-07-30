package com.f1.tester.diff;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.f1.utils.AH;
import com.f1.utils.CH;
import com.f1.utils.ManyToMany;
import com.f1.utils.SH;
import com.f1.utils.structs.Tuple2;

public class ListDiffer implements Differ {

	@Override
	public DiffResult diff(String path, Object left, Object right, DiffSession session) {
		final DiffResult r = new DiffResult(left, right, DifferConstants.LIST_MISMATCH);
		final Wrapper leftList = wrap(left);
		final Wrapper rightList = wrap(right);

		Set<Tuple2<String, String>> existingDiffs = new HashSet<Tuple2<String, String>>();
		ManyToMany<Integer, Integer> relation = diff(leftList, rightList, session);

		final int length = Math.max(leftList.getSize(), rightList.getSize());

		for (int i = 0; i < length; i++) {
			Set<Integer> rightValues = relation.getValues(i);
			Set<Integer> leftValues = relation.getKeys(i);
			if (rightValues != null && leftValues != null) {
				// everything lines up...
				if (rightValues.contains(i) || leftValues.contains(i)) {
					continue;
				} else {
					addMissaligned(r, existingDiffs, leftValues, CH.s(i), "");
					addMissaligned(r, existingDiffs, CH.s(i), rightValues, "");
				}

			} else if (rightValues == null && leftValues == null) {
				// both sides are missing from the other list, so diff the two
				DiffResult result = session.getRootDiffer().diff(path + "." + i, i < leftList.getSize() ? leftList.get(i) : null,
						i < rightList.getSize() ? rightList.get(i) : null, session);
				if (result != null)
					r.addChild(result.setKey(Integer.toString(i)));
			} else if (rightValues == null) {
				if (i < leftList.getSize())
					r.addChild(new DiffResult(leftList.get(i), DifferConstants.MISSING, DifferConstants.RIGHT_INDEX_ABSENT).setKey(Integer.toString(i)));
				addMissaligned(r, existingDiffs, leftValues, CH.s(i), "");
			} else if (leftValues == null) {
				if (i < rightList.getSize())
					r.addChild(new DiffResult(DifferConstants.MISSING, rightList.get(i), DifferConstants.LEFT_INDEX_ABSENT).setKey(Integer.toString(i)));
				addMissaligned(r, existingDiffs, CH.s(i), rightValues, "");
			}
		}
		if (r.getChildren().size() == 0)
			return null;
		return r;
	}

	private void addMissaligned(DiffResult diffResult, Set<Tuple2<String, String>> existing, Set<Integer> left, Set<Integer> right, String description) {
		Tuple2<String, String> tuple = toTuple(left, right);
		if (existing.add(tuple))
			diffResult.addChild(new DiffResult(tuple.getA(), tuple.getB(), DifferConstants.LIST_MISSALIGNED, description));

	}

	public Tuple2<String, String> toTuple(Set<Integer> left, Set<Integer> right) {
		return new Tuple2<String, String>("index " + SH.join(',', left), "index " + SH.join(',', right));
	}

	private Wrapper wrap(Object m) {
		if (m instanceof List)
			return new ListWrapper((List) m);
		if (m.getClass().isArray())
			return new ArrayWrapper((Object[]) m);
		throw new IllegalStateException(m.toString());
	}

	@Override
	public boolean canDiff(Object left, Object right) {
		return isList(left) && isList(right);
	}

	private boolean isList(Object o) {
		return o instanceof List || o.getClass().isArray();
	}

	private interface Wrapper {
		public Object get(int location);

		public int getSize();
	}

	static private class ListWrapper implements Wrapper {

		private List inner;

		public ListWrapper(List inner) {
			this.inner = inner;
		}

		@Override
		public Object get(int location) {
			return inner.get(location);
		}

		@Override
		public int getSize() {
			return inner.size();
		}
	}

	static private class ArrayWrapper implements Wrapper {

		private Object[] inner;

		public ArrayWrapper(Object[] inner) {
			this.inner = inner;
		}

		@Override
		public Object get(int location) {
			return inner[location];
		}

		@Override
		public int getSize() {
			return inner.length;
		}
	}

	public boolean eq(DiffSession session, Object l, Object r) {
		DiffResult result = session.getRootDiffer().diff("", l, r, session);
		return result == null;
	}

	public ManyToMany<Integer, Integer> diff(Wrapper l1, Wrapper l2, DiffSession session) {
		int p1 = 0, p2 = 0;
		final int s1 = l1.getSize(), s2 = l2.getSize();
		int matchedWith1[] = AH.fill(new int[l1.getSize()], -1);
		int matchedWith2[] = AH.fill(new int[l2.getSize()], -1);
		while (p1 < s1 || p2 < s2) {
			if (p1 < s1 && matchedWith1[p1] == -1) {
				Object o = l1.get(p1);
				for (int i : getClosest(p2, s2)) {
					if (eq(session, o, l2.get(i))) {
						matchedWith1[p1] = i;
						if (matchedWith2[i] != -1)
							matchedWith2[i] = p1;
						break;
					}
				}
			}
			if (p2 < s2 && matchedWith2[p2] == -1) {
				Object o = l2.get(p2);
				for (int i : getClosest(p1, s1)) {
					if (eq(session, o, l1.get(i))) {
						matchedWith2[p2] = i;
						if (matchedWith1[i] != -1)
							matchedWith1[i] = p2;
						break;
					}
				}
			}
			p1++;
			p2++;
		}
		ManyToMany<Integer, Integer> r = new ManyToMany<Integer, Integer>();
		for (int i = 0; i < matchedWith1.length; i++)
			if (matchedWith1[i] != -1)
				r.put(i, matchedWith1[i]);
		for (int i = 0; i < matchedWith2.length; i++)
			if (matchedWith2[i] != -1)
				r.put(matchedWith2[i], i);
		return r;
	}

	private static int[] getClosest(int loc, int size) {
		if (loc >= size)
			loc = size - 1;
		int[] r = new int[size];
		int back = loc - 1;
		int forward = loc;
		for (int i = 0; i < size; i++) {
			r[i] = loc + i;
			if (back < 0 || (i % 2 == 0 && forward < size)) {
				r[i] = forward;
				forward++;
			} else {
				r[i] = back;
				back--;
			}
		}
		return r;
	}

	public static void main(String a[]) {
		RootDiffer d = new RootDiffer();
		DiffResultReporter drr = new DiffResultReporter();
		System.out.println(drr.report(d.diff(CH.l(0, 1, 2, 3, 4, 5, 2), CH.l(0, 1, 2, 3, 4, 5, 2))));
		System.out.println(drr.report(d.diff(CH.l(2, 2, 3), CH.l(3, 2, 2))));
		System.out.println(drr.report(d.diff(CH.l(1, 2, 5, 3, 4, 6, 7, 8, 8, 8), CH.l(2, 1, 3, 4, 6, 7, 7, 7, 8))));
		System.out.println(drr.report(d.diff(CH.l(1, 2, 3, 4, 5), CH.l(1, 2, 3, 4, 5))));
		System.out.println(drr.report(d.diff(CH.l(1, 7, 3, 8, 5), CH.l(1, 2, 3, 4, 5))));
		System.out.println(drr.report(d.diff(CH.l(1, 3, 7, 8, 5), CH.l(1, 7, 3, 8, 5))));
		System.out.println(drr.report(d.diff(CH.l(0, 1, 2, 3, 4, 5), CH.l(1, 2, 3, 4, 5))));
		System.out.println(drr.report(d.diff(CH.l(1, 2, 3, 4, 5), CH.l(0, 1, 2, 3, 4, 5))));
	}
}
