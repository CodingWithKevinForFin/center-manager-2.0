package com.f1.utils.diff;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.f1.utils.Hasher;
import com.f1.utils.OH;
import com.f1.utils.concurrent.HasherMap;
import com.f1.utils.impl.BasicHasher;

public class SequenceDiffer<T> {

	public static boolean LEFT = true;
	public static boolean RIGHT = false;

	private static class Remaining<T> {

		private T[] vals;
		private int end;//exclusive
		private int start;//inclusive
		private boolean isForward = false;

		public Remaining(T[] vals) {
			this.vals = vals;
			this.start = 0;
			this.end = this.vals.length;
		}

		private void consume(int cnt) {
			if (isForward)
				start += cnt;
			else
				end -= cnt;
		}

		public void setIsForward(boolean isForward) {
			this.isForward = isForward;
		}
		public boolean isEmpty() {
			return end == start;
		}
		public T getAt(int n) {
			return this.vals[isForward ? (start + n) : (end - 1 - n)];
		}
		public int size() {
			return this.end - start;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < size(); i++) {
				if (i > 0)
					sb.append(',');
				sb.append(getAt(i));
			}
			return sb.toString();
		}

		public int getStartAt(int n) {
			return isForward ? start : (end - n);
		}
	}

	final private List<Block<T>> blocks;
	final private T[] lvals;
	final private T[] rvals;

	public SequenceDiffer(T xvalsIn[], T yvalsIn[]) {
		this(null, xvalsIn, yvalsIn);
	}
	public SequenceDiffer(Hasher<T> hasher, T lVals[], T rVals[]) {
		this.compator = hasher == null ? BasicHasher.INSTANCE : hasher;
		this.lvals = lVals;
		this.rvals = rVals;
		Remaining<T> l = new Remaining<T>(lVals);
		Remaining<T> r = new Remaining<T>(rVals);
		List<Block<T>> headBlock = new ArrayList<Block<T>>();
		List<Block<T>> tailBlock = new ArrayList<Block<T>>();
		l.setIsForward(true);
		r.setIsForward(true);
		findCommon(l, r, headBlock);
		l.setIsForward(false);
		r.setIsForward(false);
		findCommon(l, r, tailBlock);
		for (boolean isForward = true;; isForward = !isForward) {
			List<Block<T>> blocks = isForward ? headBlock : tailBlock;
			l.setIsForward(isForward);
			r.setIsForward(isForward);
			if (l.isEmpty() || r.isEmpty())
				break;
			findDiffs(l, r, blocks);
			if (l.isEmpty() || r.isEmpty())
				break;
			findCommon(l, r, blocks);
		}
		if (!r.isEmpty())
			headBlock.add(new Block(this, l.getStartAt(l.size()), 0, r.getStartAt(r.size()), r.size(), false));
		if (!l.isEmpty())
			headBlock.add(new Block(this, l.getStartAt(l.size()), l.size(), r.getStartAt(r.size()), 0, false));
		for (int i = tailBlock.size() - 1; i >= 0; i--)
			headBlock.add(tailBlock.get(i));
		this.blocks = headBlock;
	}
	private void findDiffs(Remaining<T> l, Remaining<T> r, List<Block<T>> sink) {
		final Block block = findDiffs(l, r);
		l.consume(block.leftSize);
		r.consume(block.rightSize);
		sink.add(block);
	}
	private void findCommon(Remaining<T> l, Remaining<T> r, List<Block<T>> sink) {
		int count = Math.min(l.size(), r.size());
		for (int i = 0; i < count; i++) {
			if (!eq(l.getAt(i), r.getAt(i))) {
				count = i;
				break;
			}
		}
		if (count > 0) {
			sink.add(new Block(this, l.getStartAt(count), count, r.getStartAt(count), count, true));
			l.consume(count);
			r.consume(count);
		}
	}

	private Block findDiffs(Remaining<T> l, Remaining<T> r) {
		int c = Math.min(l.size(), r.size());
		if (c > 0) {
			Map<T, Integer> lvals = new HasherMap<T, Integer>(this.compator);
			Map<T, Integer> rvals = new HasherMap<T, Integer>(this.compator);
			lvals.put(l.getAt(0), 0);
			rvals.put(r.getAt(0), 0);
			for (int i = 1; i < c; i++) {
				T rval = r.getAt(i);
				Integer lPos = lvals.get(rval);
				if (lPos != null)
					return new Block(this, l.getStartAt(lPos), lPos, r.getStartAt(i), i, false);
				T lval = l.getAt(i);
				Integer rPos = rvals.get(lval);
				if (rPos != null)
					return new Block(this, l.getStartAt(i), i, r.getStartAt(rPos), rPos, false);
				if (eq(lval, rval))
					return new Block(this, l.getStartAt(i), i, r.getStartAt(i), i, false);
				lvals.put(lval, i);
				rvals.put(rval, i);
			}
		}
		return new Block(this, l.getStartAt(l.size()), l.size(), r.getStartAt(r.size()), r.size(), false);
	}

	public List<Block<T>> getBlocks() {
		return this.blocks;
	}

	private final Hasher<T> compator;

	private boolean eq(T object, T object2) {
		return compator.areEqual(object, object2);
	}

	public static class Block<T> {
		private final int leftStart;
		private final int leftSize;
		private final int rightStart;
		private final int rightSize;
		private boolean isMatch;
		private SequenceDiffer<T> differ;

		public Block(SequenceDiffer<T> differ, int left, int leftSize, int right, int rightSize, boolean isMatch) {
			this.differ = differ;
			this.isMatch = isMatch;
			this.leftSize = leftSize;
			this.leftStart = left;
			this.rightSize = rightSize;
			this.rightStart = right;
			this.isMatch = isMatch;
		}
		public boolean areSame() {
			return isMatch;
		}
		public int getLeftLineNumber(int i) {
			OH.assertLt(i, leftSize);
			return leftStart + i;
		}
		public int getRightLineNumber(int i) {
			OH.assertLt(i, rightSize);
			return rightStart + i;
		}
		public T getLeft(int i) {
			return (T) differ.getLeftLineAt(getLeftLineNumber(i));
		}
		public T getRight(int i) {
			return (T) differ.getRightLineAt(getRightLineNumber(i));
		}
		public int getLeftCount() {
			return leftSize;
		}
		public int getRightCount() {
			return rightSize;
		}
		public int getLeftStart() {
			return leftStart;
		}
		public int getRightStart() {
			return rightStart;
		}
		public int getLeftEnd() {
			return leftStart + leftSize;
		}
		public int getRightEnd() {
			return rightStart + rightSize;
		}
		public int getMaxCount() {
			return Math.max(rightSize, leftSize);
		}
		public boolean hasLeftAndRight() {
			return leftSize > 0 && rightSize > 0;
		}
		public T getLeftOrNull(int n) {
			return n < leftSize ? getLeft(n) : null;
		}
		public T getRightOrNull(int n) {
			return n < rightSize ? getRight(n) : null;
		}
		public int getCount(boolean side) {
			return side == LEFT ? getLeftCount() : getRightCount();
		}
		public T getOrNull(boolean side, int n) {
			return side == LEFT ? getLeftOrNull(n) : getRightOrNull(n);
		}
		public T get(boolean side, int n) {
			return side == LEFT ? getLeft(n) : getRight(n);
		}
		public int getLineNumber(boolean side, int n) {
			return side == LEFT ? getLeftLineNumber(n) : getRightLineNumber(n);
		}

		@Override
		public String toString() {
			return leftStart + "-" + getLeftEnd() + " --> " + rightStart + "-" + getRightEnd();
		}

	}

	public T getRightLineAt(int i) {
		return rvals[i];
	}
	public T getLeftLineAt(int i) {
		return lvals[i];
	}
}
