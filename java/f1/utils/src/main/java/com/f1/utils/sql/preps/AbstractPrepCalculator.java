package com.f1.utils.sql.preps;

import java.util.Arrays;
import java.util.List;

import com.f1.base.CalcFrame;
import com.f1.utils.AH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.mutable.Mutable;
import com.f1.utils.mutable.Mutable.Int;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedHelper;
import com.f1.utils.structs.table.derived.PauseStack;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.ReusableCalcFrameStack;

abstract public class AbstractPrepCalculator implements DerivedCellCalculator {

	final protected DerivedCellCalculator[] inners;
	final private int position;
	private boolean isVisited = false;
	protected final DerivedCellCalculator inner;
	private Mutable.Int pos;

	public AbstractPrepCalculator(int position, DerivedCellCalculator... inners) {
		this.inners = inners;
		if (this.inners.length == 0)
			throw new ExpressionParserException(position, getMethodName() + "(...) takes at least one parameter");
		this.inner = inners[0];
		this.position = position;
	}

	public void visitRows(ReusableCalcFrameStack sf, PrepRows values, Mutable.Int pos) {
		this.pos = pos;
		visit(sf, values);
		isVisited = true;
	}
	public void reset() {
		this.isVisited = false;
	}

	@Override
	final public Object get(CalcFrameStack sf) {
		return get(sf, pos.value);
	}

	abstract public Object get(CalcFrameStack sf, int pos);
	abstract protected void visit(ReusableCalcFrameStack sf, PrepRows values);
	abstract public String getMethodName();

	@Override
	abstract public Class<?> getReturnType();

	@Override
	abstract public DerivedCellCalculator copy();

	@Override
	public StringBuilder toString(StringBuilder sink) {
		sink.append(getMethodName()).append("(");
		for (int i = 0; i < this.inners.length; i++) {
			DerivedCellCalculator t = this.inners[i];
			if (i > 0)
				sink.append(',');
			t.toString(sink);
		}
		return sink.append(")");
	}

	@Override
	public int getPosition() {
		return position;
	}

	@Override
	public boolean isConst() {
		if (!isVisited)
			return false;
		for (DerivedCellCalculator i : inners)
			if (!i.isConst())
				return false;
		return true;
	}

	@Override
	public boolean isReadOnly() {
		return true;
	}
	@Override
	public boolean equals(Object other) {
		if (other == null || other.getClass() != this.getClass())
			return false;
		AbstractPrepCalculator o = (AbstractPrepCalculator) other;
		return AH.eq(inners, o.inners);
	}

	@Override
	public int hashCode() {
		return OH.hashCode(Arrays.hashCode(this.inners), OH.hashCode(getMethodName()));
	}

	@Override
	public String toString() {
		return getMethodName() + "(" + SH.join(',', inners) + ")";
	};
	protected DerivedCellCalculator[] copyInners() {
		DerivedCellCalculator[] r = this.inners.clone();
		for (int i = 0; i < r.length; i++)
			r[i] = r[i].copy();
		return r;
	}

	@Override
	public int getInnerCalcsCount() {
		return this.inners.length;
	}

	@Override
	public DerivedCellCalculator getInnerCalcAt(int n) {
		return this.inners[n];
	}

	public static class PrepRows {

		public PrepRows(Int pos, List<? extends CalcFrame> values) {
			this.pos = pos;
			this.values = values;
		}

		private Mutable.Int pos;
		private List<? extends CalcFrame> values;

		public int size() {
			return values.size();
		}
		public CalcFrame get(int pos) {
			this.pos.value = pos;
			return values.get(pos);
		}
		public boolean isEmpty() {
			return values.isEmpty();
		}
	}

	@Override
	public Object resume(PauseStack paused) {
		throw new IllegalStateException();
	}

	@Override
	public boolean isPausable() {
		return false;
	}
	@Override
	public boolean isSame(DerivedCellCalculator other) {
		return other.getClass() == this.getClass() && DerivedHelper.childrenAreSame(this, other);
	}

}