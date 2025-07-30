package com.f1.ami.center.triggers.agg;

import com.f1.ami.center.table.AmiRowImpl;
import com.f1.utils.concurrent.LinkedHasherSet;
import com.f1.utils.math.PrimitiveMath;
import com.f1.utils.math.PrimitiveMathManager;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedHelper;
import com.f1.utils.structs.table.derived.PauseStack;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public abstract class AmiTriggerAgg2 implements AmiTriggerAggCalc {

	final protected DerivedCellCalculator inner1, inner2;
	final private int position;
	private Object cachedValue;

	public AmiTriggerAgg2(int position, DerivedCellCalculator inner1, DerivedCellCalculator inner2) {
		this.inner1 = inner1;
		this.inner2 = inner2;
		this.position = position;
	}

	@Override
	public Object get(CalcFrameStack sf) {
		return cachedValue;
	}

	protected DerivedCellCalculator getInner1() {
		return this.inner1;
	}
	protected DerivedCellCalculator getInner2() {
		return this.inner2;
	}

	@Override
	public StringBuilder toString(StringBuilder sink) {
		sink.append(getMethodName()).append('(');
		getInner1().toString(sink);
		sink.append(',');
		getInner2().toString(sink);
		sink.append(')');
		return sink;
	}

	@Override
	public int getPosition() {
		return this.position;
	}

	@Override
	public boolean isConst() {
		return false;
	}

	@Override
	public boolean isReadOnly() {
		return true;
	}

	@Override
	public int getInnerCalcsCount() {
		return 2;
	}

	@Override
	public DerivedCellCalculator getInnerCalcAt(int n) {
		return n == 0 ? inner1 : inner2;
	}

	@Override
	abstract public Class<?> getReturnType();

	abstract public String getMethodName();

	@Override
	abstract public DerivedCellCalculator copy();

	final protected Object visitInsert(Object nuw1, Object nuw2, Object current, AmiRowImpl causingSourceRow, LinkedHasherSet<AmiRowImpl> sourceRows, Object aggregateHelper) {
		return cachedValue = calculateInsert(nuw1, nuw2, current, sourceRows, aggregateHelper);
	}
	final protected Object visitUpdate(Object old1, Object old2, Object nuw1, Object nuw2, Object current, AmiRowImpl causingSourceRow, LinkedHasherSet<AmiRowImpl> sourceRows,
			Object aggregateHelper) {
		return cachedValue = calculateUpdate(old1, old2, nuw1, nuw2, current, causingSourceRow, sourceRows, aggregateHelper);
	}
	final protected Object visitDelete(Object old1, Object old2, Object current, AmiRowImpl causingSourceRow, LinkedHasherSet<AmiRowImpl> sourceRows, Object aggregateHelper) {
		return cachedValue = calculateDelete(old1, old2, current, causingSourceRow, sourceRows, aggregateHelper);
	}

	abstract Object calculateInsert(Object nuw1, Object nuw2, Object current, LinkedHasherSet<AmiRowImpl> sourceRows, Object aggregateHelper);

	abstract Object calculateUpdate(Object old1, Object old2, Object nuw1, Object nuw2, Object current, AmiRowImpl causingSourceRow, LinkedHasherSet<AmiRowImpl> sourceRows,
			Object aggregateHelper);
	abstract Object calculateDelete(Object old1, Object old2, Object current, AmiRowImpl causingSourceRow, LinkedHasherSet<AmiRowImpl> sourceRows, Object aggregateHelper);

	protected PrimitiveMath getPrimitiveMathManager(DerivedCellCalculator inner) {
		PrimitiveMath manager = PrimitiveMathManager.INSTANCE.getNoThrow(inner.getReturnType());
		if (manager == null)
			throw new ExpressionParserException(inner.getPosition(), "Numeric type required for " + getMethodName() + "(...) method");
		return manager;
	}

	public boolean needsHelper() {
		return false;
	}

	public Object initHelper() {
		return null;
	}
	protected static double toDouble(Object o, boolean isFloat) {
		if (isFloat) {
			if (o instanceof Number)
				return ((Number) o).doubleValue();
			else
				return 0;
		} else {
			if (o instanceof Number)
				return ((Number) o).longValue();
			else
				return 0L;
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
