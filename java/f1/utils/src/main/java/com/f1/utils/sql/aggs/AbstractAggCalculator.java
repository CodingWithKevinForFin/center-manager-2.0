package com.f1.utils.sql.aggs;

import java.util.Arrays;
import java.util.List;

import com.f1.base.CalcFrame;
import com.f1.utils.AH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.math.PrimitiveMath;
import com.f1.utils.math.PrimitiveMathManager;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedHelper;
import com.f1.utils.structs.table.derived.PauseStack;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.ReusableCalcFrameStack;

abstract public class AbstractAggCalculator implements AggCalculator {

	final protected DerivedCellCalculator[] inners;
	final private int position;
	private boolean isVisited = false;
	protected final DerivedCellCalculator inner;

	public AbstractAggCalculator(int position, DerivedCellCalculator... inners) {
		this.inners = inners;
		if (this.inners.length == 0)
			throw new ExpressionParserException(position, getMethodName() + "(...) takes at least one parameter");
		this.inner = inners[0];
		this.position = position;
	}

	public void visitRows(ReusableCalcFrameStack sf, List<? extends CalcFrame> values) {
		visit(sf, values);
		isVisited = true;
	}
	public void reset() {
		this.isVisited = false;
	}

	abstract public Object get(CalcFrameStack sf);
	abstract protected void visit(ReusableCalcFrameStack sf, List<? extends CalcFrame> values);
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
		// Need to return false because this is not correct for realtime applications
		return false;
	}

	@Override
	public boolean isReadOnly() {
		return true;
	}
	@Override
	public boolean equals(Object other) {
		if (other == null || other.getClass() != this.getClass())
			return false;
		AbstractAggCalculator o = (AbstractAggCalculator) other;
		return OH.eq(getMethodName(), o.getMethodName()) && AH.eq(inners, o.inners);
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

	public void getInnerCalcs(java.util.List<DerivedCellCalculator> sink) {
		for (DerivedCellCalculator i : this.inners)
			sink.add(i);
	};

	@Override
	public int getInnerCalcsCount() {
		return this.inners.length;
	}

	@Override
	public DerivedCellCalculator getInnerCalcAt(int n) {
		return this.inners[n];
	}

	protected PrimitiveMath getPrimitiveMathManager(DerivedCellCalculator inner) {
		PrimitiveMath manager = PrimitiveMathManager.INSTANCE.getNoThrow(inner.getReturnType());
		if (manager == null)
			throw new ExpressionParserException(inner.getPosition(), "Numeric type required for " + getMethodName() + "(...) method");
		return manager;
	}

	public boolean getOrderingMatters() {
		return false;
	}

	public abstract void setValue(Object object);

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