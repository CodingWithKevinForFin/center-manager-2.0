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

public abstract class AmiTriggerAgg implements AmiTriggerAggCalc {

	final protected DerivedCellCalculator inner;
	final private int position;
	private Object cachedValue;

	public AmiTriggerAgg(int position, DerivedCellCalculator inner) {
		this.inner = inner;
		this.position = position;
	}

	@Override
	public Object get(CalcFrameStack sf) {
		return cachedValue;
	}

	protected DerivedCellCalculator getInner() {
		return this.inner;
	}

	@Override
	public StringBuilder toString(StringBuilder sink) {
		sink.append(getMethodName()).append('(');
		getInner().toString(sink);
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
		return 1;
	}

	@Override
	public DerivedCellCalculator getInnerCalcAt(int n) {
		return this.inner;
	}

	@Override
	abstract public Class<?> getReturnType();

	abstract public String getMethodName();

	@Override
	abstract public DerivedCellCalculator copy();

	final protected Object visitInsert(Object nuw, Object current, AmiRowImpl causingSourceRow, LinkedHasherSet<AmiRowImpl> sourceRows, Object aggregateHelper, CalcFrameStack sf) {
		return cachedValue = calculateInsert(nuw, current, causingSourceRow, sourceRows, aggregateHelper, sf);
	}
	final protected Object visitUpdate(Object old, Object nuw, Object current, AmiRowImpl causingSourceRow, LinkedHasherSet<AmiRowImpl> sourceRows, Object aggregateHelper,
			CalcFrameStack sf) {
		return cachedValue = calculateUpdate(old, nuw, current, causingSourceRow, sourceRows, aggregateHelper, sf);
	}
	final protected Object visitDelete(Object old, Object current, AmiRowImpl causingSourceRow, LinkedHasherSet<AmiRowImpl> sourceRows, Object aggregateHelper, CalcFrameStack sf) {
		return cachedValue = calculateDelete(old, current, causingSourceRow, sourceRows, aggregateHelper, sf);
	}

	Object calculateInsert(Object nuw, Object current, AmiRowImpl causingSourceRow, LinkedHasherSet<AmiRowImpl> sourceRows, Object aggregateHelper, CalcFrameStack sf) {
		return calculateInsert(nuw, current, sourceRows, aggregateHelper, sf);
	}
	abstract Object calculateInsert(Object nuw, Object current, LinkedHasherSet<AmiRowImpl> sourceRows, Object aggregateHelper, CalcFrameStack sf);
	abstract Object calculateUpdate(Object old, Object nuw, Object current, AmiRowImpl causingSourceRow, LinkedHasherSet<AmiRowImpl> sourceRows, Object aggregateHelper,
			CalcFrameStack sf);
	abstract Object calculateDelete(Object old, Object current, AmiRowImpl causingSourceRow, LinkedHasherSet<AmiRowImpl> sourceRows, Object aggregateHelper, CalcFrameStack sf);

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
